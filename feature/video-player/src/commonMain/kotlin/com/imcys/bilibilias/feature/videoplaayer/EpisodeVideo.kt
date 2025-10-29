package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import com.imcys.bilibilias.core.ui.setRequestFullScreen
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.VideoPlayer
import com.imcys.bilibilias.core.videoplayer.VideoScaffold
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerBar
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerDefaults
import com.imcys.bilibilias.core.videoplayer.bar.PlayerTopBar
import com.imcys.bilibilias.core.videoplayer.gesture.PlayerGestureHost
import com.imcys.bilibilias.core.videoplayer.gesture.rememberGestureIndicatorState
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.togglePause

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EpisodeVideo(
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    playbackState: PlaybackState,
    danmakuHostState: DanmakuHostState,
    title: String,
    expanded: Boolean,
    onClickFullScreen: () -> Unit = {},
    onBack: () -> Unit,
) {

    BackHandler {
        if (expanded) {
            onClickFullScreen()
        } else {
            onBack()
        }
    }

    setRequestFullScreen(expanded)

    VideoScaffold(
        expanded = expanded,
        controllerState = playerControllerState,
        topBar = {
            PlayerTopBar(
                title = { Text(title) },
                actions = {},
                onBack = if (expanded) {
                    onClickFullScreen
                } else {
                    onBack
                }
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
        danmakuHost = {
            PlayerDanmakuHost(mediampPlayer, danmakuHostState)
        },
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
                expanded = expanded,
                startActions = {
                    PlayerControllerDefaults.PlaybackIcon(
                        isPlaying = { playbackState.isPlaying },
                        onClick = { mediampPlayer.togglePause() },
                    )
                },
                progressIndicator = { },
                progressSlider = { },
                endActions = {
                    PlayerControllerDefaults.FullscreenIcon(
                        expanded,
                        onClickFullscreen = onClickFullScreen,
                    )
                },
                danmakuEditor = { }
            )
        }
    )
}
