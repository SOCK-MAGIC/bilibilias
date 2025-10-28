package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.playUri
import org.openani.mediamp.MediampPlayer

class PlayerViewModel(val player: MediampPlayer) {

    val playerControllerState = PlayerControllerState()
    val danmakuHostState = DanmakuHostState()

    var isFullscreen by mutableStateOf(false)
        private set

    suspend fun playUri(uris: List<String>) {
        player.playUri(uris)
    }

    fun toggleFullScreen() {
        isFullscreen = !isFullscreen
    }
}