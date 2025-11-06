package com.imcys.bilibilias.feature.videoplaayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imcys.bilibilias.core.ui.foundation.KeepScreenOn
import com.imcys.bilibilias.core.ui.setRequestFullScreen
import com.imcys.bilibilias.core.videoplayer.PlaybackSpeedControllerState
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.VideoPlayer
import com.imcys.bilibilias.core.videoplayer.VideoScaffold
import com.imcys.bilibilias.core.videoplayer.bar.EpisodePlayerTitle
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerBar
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerDefaults
import com.imcys.bilibilias.core.videoplayer.bar.PlayerControllerDefaults.SpeedSwitcher
import com.imcys.bilibilias.core.videoplayer.bar.PlayerTopBar
import com.imcys.bilibilias.core.videoplayer.gesture.GestureFamily
import com.imcys.bilibilias.core.videoplayer.gesture.LevelController
import com.imcys.bilibilias.core.videoplayer.gesture.PlayerGestureHost
import com.imcys.bilibilias.core.videoplayer.gesture.rememberGestureIndicatorState
import com.imcys.bilibilias.core.videoplayer.gesture.rememberSwipeSeekerState
import com.imcys.bilibilias.core.videoplayer.progress.MediaProgressIndicatorText
import com.imcys.bilibilias.core.videoplayer.progress.PlayerProgressSliderState
import com.imcys.bilibilias.core.videoplayer.rememberAlwaysOnRequester
import com.imcys.bilibilias.danmaku.api.DanmakuEvent
import com.imcys.bilibilias.danmaku.ui.DanmakuHostState
import kotlinx.coroutines.flow.Flow
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
    danmakuEnabled: Boolean,
    danmakuHostState: DanmakuHostState,
    danmakuEventFlow: Flow<DanmakuEvent>,
    onToggleDanmaku: () -> Unit,
    audioController: LevelController,
    brightnessController: LevelController,
    playbackSpeedControllerState: PlaybackSpeedControllerState?,
    title: String,
    expanded: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    gestureFamily: GestureFamily = GestureFamily.TOUCH, // todo
    onToggleFullScreen: () -> Unit = {},
) {
    val playbackState by mediampPlayer.playbackState.collectAsStateWithLifecycle()
    if (playbackState.isPlaying) {
        KeepScreenOn()
    }

    setRequestFullScreen(expanded)

    AutoPauseEffect(mediampPlayer, playbackState)
    VideoScaffold(
        expanded = expanded,
        controllerState = playerControllerState,
        topBar = {
            PlayerTopBar(
                title = {
                    if (expanded) {
                        EpisodePlayerTitle(title)
                    }
                },
                actions = {
                    IconButton({}) {
                        Icon(Icons.Default.Settings, null)
                    }
                },
                onBackClick = onBack,
                contentColor = Color.White
            )
        },
        video = {
            VideoPlayer(
                player = mediampPlayer,
                modifier = Modifier
                    .matchParentSize(),
            )
        },
        gestureHost = {
            val swipeSeekerState = rememberSwipeSeekerState(constraints.maxWidth) {
                mediampPlayer.skip(it * 1000L)
            }
            val videoPropertiesState by mediampPlayer.mediaProperties.collectAsState(null)
            val enableSwipeToSeek by remember {
                derivedStateOf {
                    // todo 时长无法获取
                    videoPropertiesState?.let { it.durationMillis != 0L } == true
                    true
                }
            }
            val gestureIndicatorState = rememberGestureIndicatorState()

            PlayerGestureHost(
                controllerState = playerControllerState,
                indicatorState = gestureIndicatorState,
                enableSwipeToSeek = enableSwipeToSeek,
                seekerState = swipeSeekerState,
                audioController = audioController,
                brightnessController = brightnessController,
//                onTogglePauseResume = { mediampPlayer.togglePause() },
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
                    PlayerControllerDefaults.DanmakuIcon(danmakuEnabled, onToggleDanmaku)
                    val audioLevelController = audioController as? MediampAudioLevelController
                    if (expanded && audioLevelController != null && gestureFamily == GestureFamily.MOUSE) {
                        val level by audioLevelController.levelFlow.collectAsState()
                        val isMute by audioLevelController.muteFlow.collectAsState()

                        PlayerControllerDefaults.AudioIcon(
                            level,
                            isMute = isMute,
                            maxValue = audioLevelController.range.endInclusive,
                            onClick = {
                                audioLevelController.toggleMute()
                            },
                            onchange = {
                                audioLevelController.setLevel(it)
                            },
                            controllerState = playerControllerState,
                        )
                    }
                },
                progressIndicator = { MediaProgressIndicatorText(progressSliderState) },
                progressSlider = {
                    PlayerControllerDefaults.MediaProgressSlider(
                        progressSliderState,
                        showPreviewTimeTextOnThumb = expanded,
                    )
                },
                endActions = {
                    val alwaysOnRequester =
                        rememberAlwaysOnRequester(playerControllerState, "speedSwitcher")

                    var isSpeedSwitcherExpanded by rememberSaveable { mutableStateOf(false) }
                    LaunchedEffect(expanded) {
                        if (expanded) {
                            alwaysOnRequester.request()
                        } else {
                            alwaysOnRequester.cancelRequest()
                        }
                    }
                    if (expanded) {
                        playbackSpeedControllerState?.also { controller ->
                            SpeedSwitcher(
                                playbackSpeedControllerState = controller,
                                expanded = isSpeedSwitcherExpanded,
                                onExpandedChange = { isSpeedSwitcherExpanded = it },
                            )
                        }
                    }
                    PlayerControllerDefaults.FullscreenIcon(
                        expanded,
                        onClickFullscreen = onToggleFullScreen,
                    )
                },
                danmakuEditor = { }
            )
        },
        danmakuHost = {
            AnimatedVisibility(
                danmakuEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlayerDanmakuHost(
                    isPaused = !playbackState.isPlaying,
                    currentPosition = mediampPlayer.getCurrentPositionMillis(),
                    danmakuHostState = danmakuHostState,
                    danmakuEvent = danmakuEventFlow,
                )
            }
        },
        modifier = modifier,
    )
}

/**
 * 切后台自动暂停
 */
@Composable
private fun AutoPauseEffect(mediampPlayer: MediampPlayer, playbackState: PlaybackState) {
    if (LocalInspectionMode.current) return

    var wasPausedAutomatically by remember { mutableStateOf(false) }

    val autoPauseScope = rememberCoroutineScope()
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val isPlaying = playbackState.isPlaying

            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (isPlaying) {
                        // 标记为“自动暂停”，以便返回时恢复
                        wasPausedAutomatically = true
                        autoPauseScope.launch {
                            mediampPlayer.pause()
                        }
                    } else {
                        // 如果是用户手动暂停的，则不标记
                        wasPausedAutomatically = false
                    }
                }

                Lifecycle.Event.ON_START -> {
                    // 只有在之前是“自动暂停”的情况下才恢复播放
                    if (wasPausedAutomatically) {
                        autoPauseScope.launch {
                            mediampPlayer.resume()
                        }
                        // 恢复后重置标记
                        wasPausedAutomatically = false
                    }
                }

                else -> { /* Do nothing for other events */
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
