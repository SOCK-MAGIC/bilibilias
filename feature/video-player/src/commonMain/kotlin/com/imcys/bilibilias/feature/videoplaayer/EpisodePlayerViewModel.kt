package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.core.data.MediaCacheDataSource
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.flow.FlowRestarter
import com.imcys.bilibilias.core.flow.restartable
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.result.Result
import com.imcys.bilibilias.core.result.asResult
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.TimelineState
import com.imcys.bilibilias.core.videoplayer.TimelineState.TIME_UNSET
import com.imcys.bilibilias.core.videoplayer.features.AudioManager
import com.imcys.bilibilias.core.videoplayer.features.BrightnessManager
import com.imcys.bilibilias.core.videoplayer.playUri
import com.imcys.bilibilias.danmaku.api.DanmakuCollection
import com.imcys.bilibilias.danmaku.api.DanmakuContent
import com.imcys.bilibilias.danmaku.api.DanmakuEvent
import com.imcys.bilibilias.danmaku.api.DanmakuInfo
import com.imcys.bilibilias.danmaku.api.DanmakuLocation
import com.imcys.bilibilias.danmaku.api.DanmakuSession
import com.imcys.bilibilias.danmaku.api.TimeBasedDanmakuSession
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import com.imcys.bilibilias.danmaku.ui.config.DanmakuConfig
import com.imcys.bilibilias.feature.videoplaayer.di.MediaPlayerFactory
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openani.mediamp.features.PlaybackSpeed
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalAtomicApi::class)
class EpisodePlayerViewModel(
    private val aid: Long,
    private val bvid: String,
    private val cid: Long,
    private val mediaCacheStorage: MediaCacheDataSource,
    private val api: BilibiliApi,
    val audioManager: AudioManager?,
    val brightnessManager: BrightnessManager?,
    playerFactory: MediaPlayerFactory,
) : ViewModel() {
    @OptIn(ExperimentalAtomicApi::class)
    private val playbackTriggered = AtomicBoolean(false)
    val mediampPlayer = playerFactory.create(viewModelScope.coroutineContext)
    val playerControllerState = PlayerControllerState()
    private val danmakuConfig = mutableStateOf(DanmakuConfig(displayArea = 0.65f))
    val danmakuHostState = DanmakuHostState(danmakuConfig)

    private val refreshTrigger = FlowRestarter()
    var isFullscreen by mutableStateOf(false)
        private set
    var danmakuEnabled by mutableStateOf(true)
        private set

    val uiState = flow {
        val cache = mediaCacheStorage.findCache(bvid, cid)
            ?: throw NoSuchElementException("Video not found in cache for Bvid: $bvid, cid: $cid")

        val uris = cache.metadata.metadata.map { it.filePath.toString() }
        emit(PlayerUiState.Success(cache.origin.title, uris))
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
        .restartable(refreshTrigger)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState.Loading
        )
    private val danmakuCollectionFlow: Flow<DanmakuCollection> = TimelineState.durationMillis
        .filter { it != TIME_UNSET }
        .transformLatest { duration ->
            val dmSegMobile = api.dmSegMobile(aid, cid, (duration / 1000).toInt())
            val result = dmSegMobile.asSequence()
                .flatMap { it.elems }
                .sortedBy { it.progress }
                .map { elem ->
                    DanmakuInfo(
                        elem.idStr,
                        DanmakuContent(
                            elem.progress.toLong(),
                            elem.color,
                            elem.content,
                            when (elem.mode) {
                                5 -> DanmakuLocation.TOP
                                4 -> DanmakuLocation.BOTTOM
                                else -> DanmakuLocation.NORMAL
                            }
                        )
                    )
                }
            emit(TimeBasedDanmakuSession.create(sequence = result))
        }
        .flowOn(Dispatchers.IO)
    private val danmakuSessionFlow: Flow<DanmakuSession> =
        danmakuCollectionFlow.mapLatest { session ->
            session.at(
                progress = mediampPlayer.currentPositionMillis.map { it.milliseconds },
                playbackSpeed = { mediampPlayer.features[PlaybackSpeed]?.value ?: 1f },
                danmakuRegexFilterList = flowOf(),
            )
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
    val danmakuEventFlow: Flow<DanmakuEvent> = danmakuSessionFlow.flatMapLatest { it.events }

    fun reload() {
        // 必须重置播放标记，否则刷新后即使成功也不会再次触发 playUri
        playbackTriggered.store(false)
        refreshTrigger.restart()
    }

    suspend fun playUri(uris: List<String>) {
        mediampPlayer.playUri(uris)
    }

    fun toggleFullScreen() {
        isFullscreen = !isFullscreen
    }

    fun toggleDanmakuEnabled() {
        danmakuEnabled = !danmakuEnabled
    }

    fun requestRepopulate() {
        viewModelScope.launch {
            danmakuSessionFlow.first().requestRepopulate()
        }
    }

    override fun onCleared() {
        viewModelScope.launch(NonCancellable + CoroutineName("EpisodePlayerViewModel#onCleared")) {
            withContext(Dispatchers.Main) {
                mediampPlayer.stopPlayback()
            }
        }
    }

    private fun sanitizeDanmakuText(text: String): String? {
        if (text.isEmpty()) {
            return null
        }
        // 全部是空白或者控制字符不行
        val result = text
            .trim {
                it.isWhitespace() || it.isISOControl()
            }
            .filterNot { it.isISOControl() }
        if (result.isEmpty()) {
            return null
        }
        return result
    }

    companion object {
        private val logger = logger<EpisodePlayerViewModel>()
    }
}