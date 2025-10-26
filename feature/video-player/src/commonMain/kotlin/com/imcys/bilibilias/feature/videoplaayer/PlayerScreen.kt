package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.core.videoplayer.playUri
import org.openani.mediamp.compose.rememberMediampPlayer

@Composable
fun PlayerScreen(viewModel: VideoPlayerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    PlayerContent(uiState)
}

@Composable
fun PlayerContent(uiState: PlayerUiState) {
    val player = rememberMediampPlayer()

    Scaffold { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            when (uiState) {
                is PlayerUiState.Error -> {}
                PlayerUiState.Loading -> {}
                is PlayerUiState.Success -> {
                    LaunchedEffect(Unit) {
                        player.playUri(uiState.uris)
                    }

                    EpisodeVideo(
                        playerState = player
                    )
                }
            }
        }
    }
}
