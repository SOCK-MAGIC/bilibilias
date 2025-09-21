package com.imcys.bilibilias.logic.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.datasource.api.BilibiliLoginApi
import com.imcys.bilibilias.core.datastore.AsPreferencesDataSource
import com.imcys.bilibilias.core.datastore.CookieJarDataSource
import com.imcys.bilibilias.core.datastore.MediaCacheDataSource
import com.imcys.bilibilias.core.datastore.model.EpisodeMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCacheMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCachePartMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCacheSave
import com.imcys.bilibilias.core.datastore.model.MetadataKey
import com.imcys.bilibilias.core.domain.GetDmUseCase
import com.imcys.bilibilias.core.domain.GetEpisodeInfoUseCase
import com.imcys.bilibilias.core.domain.MediaSourceUseCase
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.SelectedEpisodeContext
import com.imcys.bilibilias.core.domain.model.TrackInfo
import com.imcys.bilibilias.core.flow.FlowRestarter
import com.imcys.bilibilias.core.flow.restartable
import com.imcys.bilibilias.core.http.downloader.HttpDownloader
import com.imcys.bilibilias.core.http.downloader.model.DownloadId
import com.imcys.bilibilias.core.result.Result.Error
import com.imcys.bilibilias.core.result.Result.Loading
import com.imcys.bilibilias.core.result.Result.Success
import com.imcys.bilibilias.core.result.asResult
import com.imcys.bilibilias.logic.stateInViewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val applicationScope: CoroutineScope,
    private val httpDownloader: HttpDownloader,
    private val mediaCacheStorage: MediaCacheDataSource,
    private val getEpisodeInfoUseCase: GetEpisodeInfoUseCase,
    private val mediaSourceUseCase: MediaSourceUseCase,
    private val getDmUseCase: GetDmUseCase,
    private val preferences: AsPreferencesDataSource,
    private val api: BilibiliLoginApi,
    private val cookieJar: CookieJarDataSource,
) : ViewModel() {
    val selfInfoUiState = preferences.userData
        .map { preferences ->
            preferences.selfInfo?.let { SelfInfoUiState.Success(it) } ?: SelfInfoUiState.Guest
        }
        .stateInViewModelScope(SelfInfoUiState.Loading)
    val searchQuery: StateFlow<String> =
        savedStateHandle.getStateFlow(SEARCH_QUERY, getDefaultSearchQuery())

    private val restarter = FlowRestarter()
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                getEpisodeInfoUseCase(query)
                    .asResult()
                    .map { result ->
                        when (result) {
                            is Success -> {
                                val data = result.data
                                if (data == null) {
                                    SearchResultUiState.LoadFailed("解析失败")
                                } else {
                                    SearchResultUiState.Success(
                                        episodeCacheListState = data,
                                        episodeInfo = data.episodeInfo,
                                        episodes = data.episodes,
                                    )
                                }
                            }

                            is Error -> SearchResultUiState.LoadFailed(
                                result.exception.message ?: "Unknown error"
                            )

                            is Loading -> SearchResultUiState.Loading
                        }
                    }
            }
        }
            .restartable(restarter)
            .stateInViewModelScope(SearchResultUiState.Loading)

    private val currentSelectEpisode = MutableStateFlow<SelectedEpisodeContext?>(null)
    val mediaSourceSelectedUiState: StateFlow<MediaSourceSelectedUiState> =
        currentSelectEpisode.filterNotNull()
            .map { request ->
                (searchResultUiState.value as SearchResultUiState.Success).episodeInfo
                mediaSourceUseCase(request)
            }
            .asResult()
            .map { result ->
                when (result) {
                    is Success -> MediaSourceSelectedUiState.Success(result.data)
                    is Error -> MediaSourceSelectedUiState.LoadFailed(result.exception.message)
                    is Loading -> MediaSourceSelectedUiState.Loading
                }
            }.stateInViewModelScope(MediaSourceSelectedUiState.Loading)

    fun requestCache(request: EpisodeCacheRequest) {
        applicationScope.launch {
            val state = searchResultUiState.value
            if (state is SearchResultUiState.Success) {
                val episodeCacheState = state.episodes[request.index - 1]
                episodeCacheState.episodeAliasId
                val bvid = episodeCacheState.episodeId
                val cid = episodeCacheState.episodeSubId
                val title = episodeCacheState.title

                val id = cacheDanmu(episodeCacheState)

                val metadata = EpisodeMetadata(
                    bvid,
                    cid,
                    title
                )
                val mediaCacheSave = MediaCacheSave(
                    metadata,
                    MediaCacheMetadata(emptyList(), extra = mapOf(MetadataKey.ASS_FILE to id))
                )
                mediaCacheStorage.cacheEpisode(mediaCacheSave)

                cacheTrack(request.videoTrack, metadata)
                cacheTrack(request.audioTrack, metadata)
            }
        }
    }

    fun setSelectedEpisode(request: SelectedEpisodeContext) {
        currentSelectEpisode.value = request
    }

    fun onSearchTriggered(query: String) {}

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun restartSearch() {
        restarter.restart()
    }

    fun onLogout() {
        applicationScope.launch {
            api.exit()
            preferences.setSelfInfo(null)
            cookieJar.clearCookies()
        }
    }

    suspend fun cachePartMetadata(metadata: EpisodeMetadata, downloadId: DownloadId) {
        mediaCacheStorage.updateMediaCacheMetadata(
            metadata,
            MediaCachePartMetadata(downloadId.value)
        )
    }

    private suspend fun cacheTrack(track: TrackInfo?, metadata: EpisodeMetadata) {
        track?.let {
            val downloadId = httpDownloader.download(it.urls.random())
            cachePartMetadata(metadata, downloadId)
        }
    }

    private suspend fun cacheDanmu(episode: EpisodeCacheState): String {
        val danmuRequest = DanmuRequest(
            episode.episodeAliasId,
            episode.episodeSubId,
            episode.duration,
            episode.title,
            episode.width,
            episode.height
        )
        return getDmUseCase(danmuRequest)
    }

    private fun getDefaultSearchQuery(): String {
        return if (BuildConfig.DEBUG) {
            getSampleSearchQueries().random()
        } else {
            ""
        }
    }

    private fun getSampleSearchQueries() = listOf(
//        "BV1qW4y1k7yh",
//        "【《牧神记》 第1话 天黑别出门-哔哩哔哩国创】https://b23.tv/ep836727",
//        "https://www.bilibili.com/bangumi/play/ss48415",
        "https://www.bilibili.com/video/BV1fnYczYEsi/"
    )
}

internal expect val SEARCH_QUERY: String