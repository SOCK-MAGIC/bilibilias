package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.playUri
import org.openani.mediamp.MediampPlayer

@Composable
fun rememberPlayerViewModel(player: MediampPlayer): PlayerViewModel {
    return remember(player) { PlayerViewModel(player) }
}

class PlayerViewModel(val player: MediampPlayer) {

    var title by mutableStateOf("")
    val playerControllerState = PlayerControllerState()
    val isPlaying = player.getCurrentPlaybackState().isPlaying

    var isFullscreen by mutableStateOf(false)
    suspend fun playUri(uris: List<String>) {
        player.playUri(uris)
    }

    fun toggleFullScreen() {
        isFullscreen = !isFullscreen
    }
}