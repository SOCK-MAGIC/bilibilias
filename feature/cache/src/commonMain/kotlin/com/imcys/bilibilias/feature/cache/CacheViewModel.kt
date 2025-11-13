package com.imcys.bilibilias.feature.cache

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eygraber.uri.toKmpUri
import com.imcys.bilibilias.core.data.MediaCacheRepository
import com.imcys.bilibilias.core.datasource.local.MediaCacheDataSource
import com.imcys.bilibilias.core.domain.GetCachedEpisodeStateUseCase
import com.imcys.bilibilias.core.domain.model.CacheEpisodeState
import com.imcys.bilibilias.core.ffmpeg.MediaProcessor
import com.imcys.bilibilias.core.ffmpeg.ProcessRequest
import com.imcys.bilibilias.core.ffmpeg.SubtitleMode
import com.imcys.bilibilias.core.ffmpeg.SubtitleTrack
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.model.MetadataKey
import com.imcys.bilibilias.core.storage.MediaStoreAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class CacheViewModel(
    private val multiplexer: MediaProcessor,
    private val mediaStoreAccess: MediaStoreAccess,
    private val getCachedEpisodeStateUseCase: GetCachedEpisodeStateUseCase,
    private val mediaCacheStorage: MediaCacheDataSource,
    private val applicationScope: CoroutineScope,
    private val mediaCacheRepository: MediaCacheRepository
) : ViewModel() {

    private val lock = MutableStateFlow(false)

    //    val canProcess = multiplexer.isRunning.map { !it }.stateInViewModelScope(true)
    val stateFlow = getCachedEpisodeStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val logger = logger<CacheViewModel>()
    fun onCombine(state: CacheEpisodeState) {
        if (lock.value) {
            logger.info { "Muxing task for ${state.episodeMetadata} rejected: Another muxing task is in progress." }
            return
        }
        lock.update { true }
        try {
            logger.info { "Attempting to combine media cache for episode: ${state.episodeMetadata}" }
            val assFile = state.mediaCacheMetadata.extra[MetadataKey.ASS_FILE]

            val filename = Clock.System.now().toEpochMilliseconds()
            val videoUri =
                mediaStoreAccess.createVideo(filename.toString(), "video/mp4", "BilibiliAs")
                    ?: run {
                        logger.warn { "Failed to create video file for episode: ${state.episodeMetadata}" }
                        lock.update { false }
                        return
                    }
            val subtitle = if (assFile != null) {
                listOf(
                    SubtitleTrack(
                        assFile.toKmpUri(),
                        "ZH-cn",
                        true
                    )
                )
            } else {
                emptyList()
            }
            val request = ProcessRequest(
                inputUris = state.mediaCacheMetadata.metadata.map {
                    it.fullPath.toKmpUri()
                },
                outputUri = videoUri,
                subtitleTracks = subtitle,
                subtitleMode = SubtitleMode.SOFT_SUB
            )

            applicationScope.launch {
                multiplexer.process(request)
            }.invokeOnCompletion {
                lock.update { false }
                it?.let {
                    logger.debug(it) { "Muxing task finished (or failed) for ${state.episodeMetadata}. Lock released." }
                }
            }
        } catch (e: Exception) {
            lock.update { false }
            logger.error(e) { "An unexpected error occurred before starting muxing for ${state.episodeMetadata}" }
        }
    }
    fun deleteEpisodeCache(state: CacheEpisodeState) {
        viewModelScope.launch {
            try {
                performDelete(state)
            } catch (e: Exception) {
                logger.error(e) { "Error deleting cache for episode: ${state.episodeMetadata}" }
            }
        }
    }

    private suspend fun performDelete(state: CacheEpisodeState) {
        mediaCacheStorage.delete(state.episodeMetadata)

        val filesDeletedSuccessfully = mediaCacheRepository.delete(state.mediaCacheMetadata)

        if (!filesDeletedSuccessfully) {
            logger.warn { "Record deleted, but failed to clean up all associated files for ${state.episodeMetadata}" }
        }
    }
}