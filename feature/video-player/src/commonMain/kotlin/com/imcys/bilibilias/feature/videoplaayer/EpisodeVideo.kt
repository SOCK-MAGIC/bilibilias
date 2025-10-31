package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState
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
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onClickFullScreen: () -> Unit = {},
    windowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
) {
    val playbackState by mediampPlayer.playbackState.collectAsStateWithLifecycle()
    BackHandler(expanded) {
        if (expanded) {
            onClickFullScreen()
        }
    }
    setRequestFullScreen(expanded)
//    setSystemBarVisible(!expanded)
    AutoPauseEffect(mediampPlayer, playbackState)
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
                actions = {
                    IconButton({}) {
                        Icon(Icons.Default.Settings, null)
                    }
                },
                onBack = if (expanded) {
                    onClickFullScreen
                } else {
                    onBack
                },
//                windowInsets = WindowInsets()
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
        floatingBottomEnd = {
//            EpisodeVideoDefaults.FloatingFullscreenSwitchButton(
//            vm.videoScaffoldConfig.fullscreenSwitchMode,
//            isFullscreen = expanded,
//            onClickFullScreen,
//        )
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
        },
        modifier = modifier,
        contentWindowInsets = windowInsets,
    )
}

/**
 * 切后台自动暂停
 */
@Composable
private fun AutoPauseEffect(mediampPlayer: MediampPlayer, playbackState: PlaybackState) {
    var pausedVideo by rememberSaveable { mutableStateOf(true) }
    if (LocalInspectionMode.current) return

    val autoPauseTasker = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                if (playbackState.isPlaying) {
                    pausedVideo = true
                    autoPauseTasker.launch {
                        // 正在播放时, 切到后台自动暂停
                        mediampPlayer.pause()
                    }
                } else {
                    // 如果不是正在播放, 则不操作暂停, 当下次切回前台时, 也不要恢复播放
                    pausedVideo = false
                }
            } else if (event == Lifecycle.Event.ON_START && pausedVideo) {
                autoPauseTasker.launch {
                    // 切回前台自动恢复, 当且仅当之前是自动暂停的
                    mediampPlayer.resume()
                }
                pausedVideo = false
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
