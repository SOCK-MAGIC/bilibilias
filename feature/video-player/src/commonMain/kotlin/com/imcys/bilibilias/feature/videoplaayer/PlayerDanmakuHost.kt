package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.danmaku.ui.DanmakuHost
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState

@Composable
fun PlayerDanmakuHost(
    isPaused: Boolean,
    danmakuHostState: DanmakuHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isPaused, danmakuHostState) {
        danmakuHostState.setPaused(isPaused)
    }

    DanmakuHost(danmakuHostState, modifier)
}