package com.imcys.bilibilias.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.logic.player.PlayerUiState
import com.imcys.bilibilias.logic.player.PlayerViewModel
import kotlinx.coroutines.launch
import org.openani.mediamp.compose.MediampPlayerSurface
import org.openani.mediamp.compose.rememberMediampPlayer

@Composable
fun PlayerScreen(playerViewModel: PlayerViewModel) {
    val uiState by playerViewModel.uiState.collectAsState()
    PlayerContent(uiState)
}

@Composable
fun PlayerContent(uiState: PlayerUiState) {
    val player = rememberMediampPlayer()
    val scope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            when (uiState) {
                is PlayerUiState.Error -> {}
                PlayerUiState.Loading -> {}
                is PlayerUiState.Success -> {
                    Button(
                        onClick = {
                            scope.launch {
                                player.prepareWithTracks(uiState.cacheSave.metadata.metadata.map { it.filePath.toString() })
                            }
                        }
                    ) {
                        Text("Play")
                    }

                    MediampPlayerSurface(player, Modifier.fillMaxSize())
                }
            }
        }
    }
}
