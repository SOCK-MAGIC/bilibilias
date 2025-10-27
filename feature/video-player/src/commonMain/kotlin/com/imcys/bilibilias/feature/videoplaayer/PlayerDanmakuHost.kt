package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.core.danmaku.DanmakuHost
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import org.openani.mediamp.MediampPlayer

@Composable
fun PlayerDanmakuHost(
    player: MediampPlayer,
    danmakuHostState: DanmakuHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(player, danmakuHostState) {
        player.playbackState.collect {
            danmakuHostState.setPaused(!it.isPlaying)
        }
    }

    DanmakuHost(danmakuHostState, modifier)
}