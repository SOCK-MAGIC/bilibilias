package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.ui.foundation.DarkStatusBarAppearance
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.progress.rememberMediaProgressSliderState
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import org.openani.mediamp.MediampPlayer

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
        expanded = viewModel.isFullscreen,
        mediampPlayer = viewModel.mediampPlayer,
        playerControllerState = viewModel.playerControllerState,
        onClickFullScreen = viewModel::toggleFullScreen
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    danmakuHostState: DanmakuHostState,
    expanded: Boolean,
    onClickFullScreen: () -> Unit,
    onBack: () -> Unit,
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
                    onClickFullScreen = onClickFullScreen,
                    danmakuHostState = danmakuHostState,
                    onBack = back,
                    progressSliderState = progressSliderState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .statusBarsPadding()
                )
            }
        }
    }
}