package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.core.videoplayer.VideoPlayer
import com.imcys.bilibilias.core.videoplayer.VideoScaffold
import com.imcys.bilibilias.core.videoplayer.bar.PlayerTopBar
import com.imcys.bilibilias.core.videoplayer.rememberVideoControllerState
import org.openani.mediamp.MediampPlayer

@Composable
fun EpisodeVideo(playerState: MediampPlayer) {
    val videoControllerState = rememberVideoControllerState()
    VideoScaffold(
        expanded = true,
        controllerState = videoControllerState,
        topBar = {
            PlayerTopBar(
                title = if (true) {
                    { /*title()*/ }
                } else {
                    null
                },
                actions = {},
                onBack = {}
            )
        },
        video = {
            VideoPlayer(
                player = playerState,
                modifier = Modifier
                    //                    .ifThen(statusBarHeight != 0.dp) {
                    //                        offset(x = -statusBarHeight / 2, y = 0.dp)
                    //                    }
                    .matchParentSize(),
            )
        },
        danmakuHost = {},
        gestureHost = {

        },
    )
}
