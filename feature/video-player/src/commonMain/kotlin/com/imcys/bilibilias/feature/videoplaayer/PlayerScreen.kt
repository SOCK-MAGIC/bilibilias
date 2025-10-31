package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.progress.rememberMediaProgressSliderState
import org.openani.mediamp.MediampPlayer

@Composable
fun PlayerScreen(
    viewModel: EpisodePlayerViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlayerLifecycleHandler(viewModel.mediampPlayer)

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

@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    danmakuHostState: DanmakuHostState,
    expanded: Boolean,
    onClickFullScreen: () -> Unit,
    onBack: () -> Unit,
    windowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
) {
    val progressSliderState = rememberMediaProgressSliderState(
        player = mediampPlayer,
        onPreview = {},
        onPreviewFinished = { mediampPlayer.seekTo(it) },
    )

    Scaffold(contentWindowInsets = WindowInsets(0.dp)) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
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
                        onBack = onBack,
                        progressSliderState = progressSliderState,
                        windowInsets = if (expanded) {
                            windowInsets
                        } else {
                            // 非全屏右边还有东西
                            // Consider #1923 平板横屏模式下播放器底栏和导航栏重合
                            windowInsets.only(WindowInsetsSides.Left + WindowInsetsSides.Vertical)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerLifecycleHandler(mediampPlayer: MediampPlayer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_START) {
//                mediampPlayer.resume()
//            }
//
//            if (event == Lifecycle.Event.ON_STOP) {
//                mediampPlayer.pause()
//            }
        }

        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}