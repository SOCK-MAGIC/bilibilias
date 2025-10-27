package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.VideoPlayer
import com.imcys.bilibilias.core.videoplayer.VideoScaffold
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerBar
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerDefaults
import com.imcys.bilibilias.core.videoplayer.bar.PlayerTopBar
import com.imcys.bilibilias.core.videoplayer.gesture.PlayerGestureHost
import com.imcys.bilibilias.core.videoplayer.gesture.rememberGestureIndicatorState
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.togglePause

@Composable
fun EpisodeVideo(
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    title: String,
    expanded: Boolean,
    isPlaying: Boolean,
    onClickFullScreen: () -> Unit = {},
) {
    VideoScaffold(
        expanded = expanded,
        controllerState = playerControllerState,
        topBar = {
            PlayerTopBar(
                title = { Text(title) },
                actions = {},
                onBack = {}
            )
        },
        video = {
            VideoPlayer(
                player = mediampPlayer,
                modifier = Modifier
                    //                    .ifThen(statusBarHeight != 0.dp) {
                    //                        offset(x = -statusBarHeight / 2, y = 0.dp)
                    //                    }
                    .matchParentSize(),
            )
        },
        danmakuHost = {},
        gestureHost = {
            val gestureIndicatorState = rememberGestureIndicatorState()
            PlayerGestureHost(
                controllerState = playerControllerState,
                indicatorState = gestureIndicatorState,
                enableSwipeToSeek = true,
            )
        },
        bottomBar = {
            PlayerControllerBar(
                expanded = false,
                startActions = {
                    PlayerControllerDefaults.PlaybackIcon(
                        isPlaying = { isPlaying },
                        onClick = { mediampPlayer.togglePause() },
                    )
                },
                progressIndicator = { },
                progressSlider = { },
                danmakuEditor = { },
                endActions = {
                    PlayerControllerDefaults.FullscreenIcon(
                        expanded,
                        onClickFullscreen = onClickFullScreen,
                    )
                }
            )
        }
    )
}
