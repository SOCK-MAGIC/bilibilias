package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.openani.mediamp.compose.rememberMediampPlayer

@Composable
fun PlayerScreen(viewModel: VideoPlayerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val player = rememberMediampPlayer()
    val playerViewModel = rememberPlayerViewModel(player)
    PlayerContent(
        uiState,
        playerViewModel
    )
}

@Composable
fun PlayerContent(uiState: PlayerUiState, playerViewModel: PlayerViewModel) {
    Scaffold { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            when (uiState) {
                is PlayerUiState.Error -> {}
                PlayerUiState.Loading -> {}
                is PlayerUiState.Success -> {
                    LaunchedEffect(Unit) {
                        playerViewModel.playUri(uiState.uris)
                    }

                    EpisodeVideo(
                        mediampPlayer = playerViewModel.player,
                        playerControllerState = playerViewModel.playerControllerState,
                        title = "hello",
                        expanded = playerViewModel.isFullscreen,
                        onClickFullScreen = playerViewModel::toggleFullScreen,
                        danmakuHostState = playerViewModel.danmakuHostState,
                    )
                }
            }
        }
    }
}
