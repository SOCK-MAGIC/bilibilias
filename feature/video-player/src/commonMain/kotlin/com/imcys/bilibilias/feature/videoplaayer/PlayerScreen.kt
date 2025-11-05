package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.ui.foundation.DarkStatusBarAppearance
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.features.AudioManager
import com.imcys.bilibilias.core.videoplayer.features.BrightnessManager
import com.imcys.bilibilias.core.videoplayer.features.StreamType
import com.imcys.bilibilias.core.videoplayer.gesture.NoOpLevelController
import com.imcys.bilibilias.core.videoplayer.gesture.asLevelController
import com.imcys.bilibilias.core.videoplayer.progress.rememberMediaProgressSliderState
import com.imcys.bilibilias.core.videoplayer.rememberPlaybackSpeedControllerState
import com.imcys.bilibilias.danmaku.api.DanmakuEvent
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import kotlinx.coroutines.flow.Flow
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.features.AudioLevelController
import org.openani.mediamp.features.PlaybackSpeed

@Suppress("NonSkippableComposable")
@Composable
fun PlayerScreen(
    viewModel: EpisodePlayerViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlayerContent(
        uiState = uiState,
        onBack = onBack,
        danmakuHostState = viewModel.danmakuHostState,
        danmakuEventFlow = viewModel.danmakuEventFlow,
        expanded = viewModel.isFullscreen,
        mediampPlayer = viewModel.mediampPlayer,
        playerControllerState = viewModel.playerControllerState,
        onClickFullScreen = viewModel::toggleFullScreen,
        danmakuEnabled = viewModel.danmakuEnabled,
        onToggleDanmaku = viewModel::toggleDanmakuEnabled,
        audioManager = viewModel.audioManager,
        brightnessManager = viewModel.brightnessManager,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    danmakuHostState: DanmakuHostState,
    danmakuEventFlow: Flow<DanmakuEvent>,
    expanded: Boolean,
    audioManager: AudioManager?,
    brightnessManager: BrightnessManager?,
    onClickFullScreen: () -> Unit,
    onBack: () -> Unit,
    danmakuEnabled: Boolean,
    onToggleDanmaku: () -> Unit,
) {
    DarkStatusBarAppearance()
    val progressSliderState = rememberMediaProgressSliderState(
        player = mediampPlayer,
        onPreview = {},
        onPreviewFinished = { mediampPlayer.seekTo(it) },
    )
    val back = {
        if (expanded) {
            onClickFullScreen()
        } else {
            mediampPlayer.stopPlayback()
            onBack()
        }
    }
    BackHandler(onBack = back)

    val scope = rememberCoroutineScope()
    val playbackSpeedControllerState =
        mediampPlayer.features[PlaybackSpeed]?.let { rememberPlaybackSpeedControllerState(it) }

    Column {
        when (uiState) {
            is PlayerUiState.Error -> {}
            PlayerUiState.Loading -> {}
            is PlayerUiState.Success -> {
                EpisodeVideo(
                    mediampPlayer = mediampPlayer,
                    playerControllerState = playerControllerState,
                    title = uiState.title,
                    expanded = expanded,
                    onToggleFullScreen = onClickFullScreen,
                    danmakuHostState = danmakuHostState,
                    danmakuEnabled = danmakuEnabled,
                    onToggleDanmaku = onToggleDanmaku,
                    danmakuEventFlow = danmakuEventFlow,
                    onBack = back,
                    progressSliderState = progressSliderState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .statusBarsPadding(),
                    audioController = audioManager?.asLevelController(StreamType.MUSIC)
                        ?: mediampPlayer.features[AudioLevelController]?.let {
                            MediampAudioLevelController(it, { _, _ -> })
                        } ?: NoOpLevelController,
                    brightnessController = brightnessManager?.asLevelController()
                        ?: NoOpLevelController,
                    playbackSpeedControllerState = playbackSpeedControllerState,
                )
            }
        }
    }
}