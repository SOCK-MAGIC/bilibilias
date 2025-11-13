package com.imcys.bilibilias.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.datasource.api.BilibiliLoginApi
import com.imcys.bilibilias.core.datasource.local.AsPreferencesDataSource
import com.imcys.bilibilias.core.datasource.local.CredentialsDataSource
import com.imcys.bilibilias.core.datasource.local.MediaCacheDataSource
import com.imcys.bilibilias.core.domain.GetDmUseCase
import com.imcys.bilibilias.core.domain.GetEpisodeInfoUseCase
import com.imcys.bilibilias.core.domain.MediaSourceUseCase
import com.imcys.bilibilias.core.domain.RedirectResolverUseCase
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.SelectedEpisodeContext
import com.imcys.bilibilias.core.domain.model.TrackInfo
import com.imcys.bilibilias.core.flow.FlowRestarter
import com.imcys.bilibilias.core.flow.restartable
import com.imcys.bilibilias.core.http.downloader.HttpDownloader
import com.imcys.bilibilias.core.http.downloader.model.DownloadId
import com.imcys.bilibilias.core.io.absolutePath
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.model.EpisodeMetadata
import com.imcys.bilibilias.core.model.MediaCacheMetadata
import com.imcys.bilibilias.core.model.MediaCachePartMetadata
import com.imcys.bilibilias.core.model.MediaCacheSave
import com.imcys.bilibilias.core.platform.AppDirs
import com.imcys.bilibilias.core.result.Result
import com.imcys.bilibilias.core.result.Result.Error
import com.imcys.bilibilias.core.result.Result.Loading
import com.imcys.bilibilias.core.result.Result.Success
import com.imcys.bilibilias.core.result.asResult
import com.imcys.bilibilias.feature.search.state.MediaSourceSelectedUiState
import com.imcys.bilibilias.feature.search.state.SearchResultUiState
import com.imcys.bilibilias.feature.search.state.SelfInfoUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val redirectResolverUseCase: RedirectResolverUseCase,
    private val appDirs: AppDirs,
    private val credentialStore: CredentialsDataSource,
) : ViewModel() {
    val selfInfoUiState = preferences.userData
        .map { preferences ->
            preferences.selfInfo?.let { SelfInfoUiState.Success(it) } ?: SelfInfoUiState.Guest
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SelfInfoUiState.Loading
        )
    val searchQuery: StateFlow<String> =
        savedStateHandle.getStateFlow(SEARCH_QUERY, getDefaultSearchQuery())

    private val restarter = FlowRestarter()
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.isBlank()) {
                return@flatMapLatest flowOf(SearchResultUiState.EmptyQuery)
            }

            flowOf(
                if (isBilibiliShortLink(query)) {
                    redirectResolverUseCase.resolveUrl(query)
                } else {
                    query
                }
            )
                .flatMapLatest { finalQuery ->
                    getEpisodeInfoUseCase(finalQuery)
                }
                .asResult()
                .map { result -> result.toSearchResultUiState() }
                .catch { exception ->
                    emit(SearchResultUiState.LoadFailed("请求失败: ${exception.message}"))
                }
        }
            .restartable(restarter)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SearchResultUiState.EmptyQuery,
            )
    private val currentSelectEpisode = MutableStateFlow<SelectedEpisodeContext?>(null)
    val mediaSourceSelectedUiState: StateFlow<MediaSourceSelectedUiState> =
        currentSelectEpisode.filterNotNull()
            .combine(searchResultUiState) { selection, searchResult ->
                if (searchResult is SearchResultUiState.Success) {
                    selection to searchResult
                } else {
                    null
                }
            }
            .filterNotNull()
            .map { (selection, _) ->
                mediaSourceUseCase(selection)
            }
            .asResult()
            .map { result ->
                when (result) {
                    is Success -> MediaSourceSelectedUiState.Success(result.data)
                    is Error -> MediaSourceSelectedUiState.LoadFailed(result.exception.message)
                    is Loading -> MediaSourceSelectedUiState.Loading
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MediaSourceSelectedUiState.Loading
            )

    fun requestCache(request: EpisodeCacheRequest) {
        val currentState = searchResultUiState.value
        if (currentState !is SearchResultUiState.Success) {
            return
        }

        applicationScope.launch {
            currentState.episodes.getOrNull(request.index - 1)?.let { episodeCacheState ->
                val key = EpisodeMetadata(
                    aid = episodeCacheState.episodeAliasId.toString(),
                    bvid = episodeCacheState.episodeId,
                    cid = episodeCacheState.episodeSubId,
                )

                val mediaCacheSave = MediaCacheSave(
                    key = key,
                    metadata = MediaCacheMetadata(emptyList())
                )
                mediaCacheStorage.save(mediaCacheSave)

                // 并行启动音视频轨道缓存
                launch { cacheTrack(request.videoTrack, key) }
                launch { cacheTrack(request.audioTrack, key) }
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
//            api.exit()
            preferences.setSelfInfo(null)
            credentialStore.clearCredentials()
        }
    }

    private suspend fun cachePartMetadata(key: EpisodeMetadata, downloadId: DownloadId) {
        val path = appDirs.defaultBaseMediaCacheDir.resolve(downloadId.value).absolutePath
        val save = MediaCacheSave(key, MediaCacheMetadata(listOf(MediaCachePartMetadata(path))))
        mediaCacheStorage.save(save)
    }

    private fun isBilibiliShortLink(query: String): Boolean {
        val containsShortDomain = "b23.tv" in query
        if (!containsShortDomain) return false

        val isAlreadyLongLink = "/av" in query || "/BV1" in query
        return !isAlreadyLongLink
    }

    private suspend fun cacheTrack(track: TrackInfo?, key: EpisodeMetadata) {
        track?.urls?.randomOrNull()?.let { url ->
            val downloadId = httpDownloader.download(url)
            cachePartMetadata(key, downloadId)
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
//        "https://www.bilibili.com/video/BV1fnYczYEsi/",
//        "BV1nQoTY6ELm"
//        "https://www.bilibili.com/video/BV1UE411y7Wy/",
        ""
    )
}

private fun Result<EpisodeCacheListState?>.toSearchResultUiState(): SearchResultUiState {
    return when (this) {
        is Success -> {
            data?.let {
                SearchResultUiState.Success(
                    episodeCacheListState = it,
                    episodeInfo = it.episodeInfo,
                    episodes = it.episodes,
                )
            } ?: SearchResultUiState.LoadFailed("解析失败，未获取到有效数据")
        }

        is Error -> SearchResultUiState.LoadFailed(exception.message ?: "未知错误")
        is Loading -> SearchResultUiState.Loading
    }
}

private const val SEARCH_QUERY: String = "android.intent.extra.TEXT"