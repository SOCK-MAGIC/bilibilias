package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.danmaku.DanmakuHostState
import com.imcys.bilibilias.core.ui.setRequestFullScreen
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.VideoPlayer
import com.imcys.bilibilias.core.videoplayer.VideoScaffold
import com.imcys.bilibilias.core.videoplayer.bar.EpisodePlayerTitle
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerBar
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerDefaults
import com.imcys.bilibilias.core.videoplayer.bar.PlayerTopBar
import com.imcys.bilibilias.core.videoplayer.gesture.PlayerGestureHost
import com.imcys.bilibilias.core.videoplayer.gesture.rememberGestureIndicatorState
import com.imcys.bilibilias.core.videoplayer.progress.MediaProgressIndicatorText
import com.imcys.bilibilias.core.videoplayer.progress.PlayerProgressSliderState
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.togglePause

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EpisodeVideo(
    mediampPlayer: MediampPlayer,
    playerControllerState: PlayerControllerState,
    progressSliderState: PlayerProgressSliderState,
    danmakuHostState: DanmakuHostState,
    title: String,
    expanded: Boolean,
    onClickFullScreen: () -> Unit = {},
    onBack: () -> Unit,
) {
    BackHandler(expanded) {
        if (expanded) {
            onClickFullScreen()
        }
    }
    setRequestFullScreen(expanded)

    VideoScaffold(
        expanded = expanded,
        controllerState = playerControllerState,
        topBar = {
            PlayerTopBar(
                title = if (expanded) {
                    { EpisodePlayerTitle(title) }
                } else {
                    null
                },
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
                    val playbackState by mediampPlayer.playbackState.collectAsStateWithLifecycle()
                    PlayerControllerDefaults.PlaybackIcon(
                        isPlaying = { playbackState.isPlaying },
                        onClick = { mediampPlayer.togglePause() },
                    )
                },
                progressIndicator = { MediaProgressIndicatorText(progressSliderState) },
                progressSlider = {
                    PlayerControllerDefaults.MediaProgressSlider(
                        progressSliderState,
                        showPreviewTimeTextOnThumb = expanded,
                    )
                },
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
