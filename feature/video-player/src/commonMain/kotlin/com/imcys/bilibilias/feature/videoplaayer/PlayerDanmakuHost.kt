package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.danmaku.api.DanmakuEvent
import com.imcys.bilibilias.danmaku.api.DanmakuInfo
import com.imcys.bilibilias.danmaku.ui.DanmakuHost
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import com.imcys.bilibilias.danmaku.ui.DanmakuPresentation
import kotlinx.coroutines.flow.Flow

@Composable
fun PlayerDanmakuHost(
    isPaused: Boolean,
    currentPosition: Long,
    danmakuHostState: DanmakuHostState,
    danmakuEvent: Flow<DanmakuEvent>,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isPaused, danmakuHostState) {
        danmakuHostState.setPaused(isPaused)
    }
    LaunchedEffect(danmakuEvent, danmakuHostState) {
        danmakuEvent.collect { event ->
            when (event) {
                is DanmakuEvent.Add -> {
                    danmakuHostState.trySend(createDanmakuPresentation(event.danmaku))
                }

                is DanmakuEvent.Repopulate -> {
                    val presentations = event.list
                        .filter { it.text.any { c -> !c.isWhitespace() } }
                        .map { createDanmakuPresentation(it) }

                    danmakuHostState.repopulate(presentations, currentPosition)
                }
            }
        }
    }
    DanmakuHost(danmakuHostState, modifier)
}

private fun createDanmakuPresentation(
    data: DanmakuInfo,
) = DanmakuPresentation(data, false)
