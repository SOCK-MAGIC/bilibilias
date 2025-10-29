package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState

@Composable
fun PlayerScreen(
    viewModel: EpisodePlayerViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.mediampPlayer.playbackState.collectAsStateWithLifecycle()
    val currentPositionMillis by viewModel.mediampPlayer.currentPositionMillis.collectAsStateWithLifecycle()
    val mediaProperties by viewModel.mediampPlayer.mediaProperties.collectAsStateWithLifecycle()

    println(currentPositionMillis.toString() + " bo1")
    LaunchedEffect(viewModel.isFullscreen) {
        viewModel.mediampPlayer.seekTo(currentPositionMillis)

        println(currentPositionMillis.toString() + " bo2")
    }

    PlayerLifecycleHandler(viewModel.mediampPlayer)
    PlayerContent(
        uiState = uiState,
        viewModel = viewModel,
        playbackState = playbackState,
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
    viewModel: EpisodePlayerViewModel,
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    playbackState: PlaybackState,
    danmakuHostState: DanmakuHostState,
    expanded: Boolean,
    onClickFullScreen: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            when (uiState) {
                is PlayerUiState.Error -> {}
                PlayerUiState.Loading -> {}
                is PlayerUiState.Success -> {
                    LaunchedEffect(Unit) {
                        viewModel.playUri(uiState.uris)
                    }

                    EpisodeVideo(
                        mediampPlayer = mediampPlayer,
                        playerControllerState = playerControllerState,
                        title = "hello",
                        expanded = expanded,
                        onClickFullScreen = onClickFullScreen,
                        danmakuHostState = danmakuHostState,
                        playbackState = playbackState,
                        onBack = onBack,
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