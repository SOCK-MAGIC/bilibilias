package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.core.datastore.MediaCacheDataSource
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.result.Result
import com.imcys.bilibilias.core.result.asResult
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.playUri
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import com.imcys.bilibilias.feature.videoplaayer.di.MediaPlayerFactory
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class EpisodePlayerViewModel(
    private val compositeVideoId: String,
    private val mediaCacheStorage: MediaCacheDataSource,
    playerFactory: MediaPlayerFactory,
) : ViewModel() {
    @OptIn(ExperimentalAtomicApi::class)
    private val playbackTriggered = AtomicBoolean(false)
    val mediampPlayer = playerFactory.create(viewModelScope.coroutineContext)
    val playerControllerState = PlayerControllerState()
    val danmakuHostState = DanmakuHostState()

    var isFullscreen by mutableStateOf(false)
        private set

    @OptIn(ExperimentalAtomicApi::class)
    val uiState = flow {
        val (bvid, cid) = parseVideoIdentifier(compositeVideoId)
            ?: throw IllegalArgumentException("Invalid video identifier format: $compositeVideoId")

        val cache = mediaCacheStorage.findCache(bvid, cid)
            ?: throw NoSuchElementException("Video not found in cache for ID: $compositeVideoId")
        val title = cache.origin.title
        logger.debug { title }
        val uris = cache.metadata.metadata.map { it.filePath.toString() }
        emit(PlayerUiState.Success(title, uris))
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> PlayerUiState.Error(
                    result.exception.message ?: "An unknown error occurred"
                )

                Result.Loading -> PlayerUiState.Loading
            }
        }.onEach { state ->
            if (state is PlayerUiState.Success &&
                playbackTriggered.compareAndSet(expectedValue = false, newValue = true)
            ) {
                playUri(state.uris)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState.Loading
        )

    suspend fun playUri(uris: List<String>) {
        mediampPlayer.playUri(uris)
    }

    fun toggleFullScreen() {
        isFullscreen = !isFullscreen
    }

    override fun onCleared() {
        viewModelScope.launch(NonCancellable + CoroutineName("EpisodePlayerViewModel#onCleared")) {
            withContext(Dispatchers.Main) {
                mediampPlayer.stopPlayback()
            }
        }
    }

    private fun parseVideoIdentifier(identifier: String): VideoIdentifier? {
        val parts = identifier.split('-')
        if (parts.size != 2) {
            logger.warn { "Invalid identifier format: '$identifier'. Expected 'bvid-cid'." }
            return null
        }

        val bvid = parts[0]
        val cid = parts[1].toLongOrNull() ?: run {
            logger.warn { "Failed to parse numeric part from identifier: '$identifier'" }
            return null
        }
        return VideoIdentifier(bvid, cid)
    }

    data class VideoIdentifier(val bvid: String, val cid: Long)
    companion object {
        private val logger = logger<EpisodePlayerViewModel>()
    }
}