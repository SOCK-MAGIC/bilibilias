package com.imcys.bilibilias.core.videoplayer.gesture

import androidx.annotation.UiThread
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.BrightnessLow
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imcys.bilibilias.core.ui.foundation.ifThen
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.BRIGHTNESS
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.FAST_BACKWARD
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.FAST_FORWARD
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.PAUSED_ONCE
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.RESUMED_ONCE
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.SEEKING
import com.imcys.bilibilias.core.videoplayer.gesture.GestureIndicatorState.State.VOLUME
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberGestureIndicatorState(): GestureIndicatorState = remember { GestureIndicatorState() }

@Stable
class GestureIndicatorState {
    internal enum class State {
        PAUSED_ONCE,
        RESUMED_ONCE,
        VOLUME,
        BRIGHTNESS,
        SEEKING,
        FAST_FORWARD,
        FAST_BACKWARD,
    }

    internal var visible: Boolean by mutableStateOf(false)
    internal var state: State? by mutableStateOf(null)
    internal var progressValue: Float by mutableFloatStateOf(0f)
    internal var deltaSeconds: Int by mutableIntStateOf(0)
    private var counter: Int = 0
    private inline fun startShow(
        state: State,
        setup: () -> Unit = {},
    ): Int {
        val ticket = ++counter
        setup()
        this.state = state
        visible = true
        return ticket
    }

    private inline fun show(
        state: State,
        setup: () -> Unit = {},
        action: () -> Unit
    ) {
        val ticket = ++counter
        try {
            setup()
            this.state = state
            visible = true
            action()
        } finally {
            if (this.counter == ticket && // no one changed the state after us
                this.state == state
            ) {
                visible = false
            }
        }
    }

    private companion object {
        private const val LONG: Long = 700
        private const val SHORT: Long = 500
    }

    @UiThread
    suspend fun showPausedLong() {
        show(PAUSED_ONCE) {
            delay(LONG)
        }
    }

    @UiThread
    suspend fun showResumedLong() {
        show(RESUMED_ONCE) {
            delay(LONG)
        }
    }

    @UiThread
    suspend fun showVolumeRange(currentRatio: Float) {
        show(VOLUME, setup = { progressValue = currentRatio }) {
            delay(SHORT)
        }
    }

    @UiThread
    suspend fun showBrightnessRange(currentRatio: Float) {
        show(BRIGHTNESS, setup = { progressValue = currentRatio }) {
            delay(SHORT)
        }
    }

    @UiThread
    suspend fun showSeeking(
        deltaSeconds: Int,
    ) {
        show(SEEKING, setup = { this.deltaSeconds = deltaSeconds }) {
            delay(SHORT)
        }
    }

    @UiThread
    fun startFastForward(): Int {
        startShow(FAST_FORWARD, setup = { })
        return counter
    }

    @UiThread
    fun stopFastForward(ticket: Int) {
        stopShow(ticket)
    }

    @UiThread
    fun startFastBackward(): Int {
        startShow(FAST_BACKWARD, setup = { })
        return counter
    }

    @UiThread
    fun stopFastBackward(ticket: Int) {
        stopShow(ticket)
    }

    private fun stopShow(ticket: Int) {
        if (ticket == this.counter) {
            visible = false
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerGestureHost(
    controllerState: PlayerControllerState,
    seekerState: SwipeSeekerState,
//    progressSliderState: PlayerProgressSliderState,
    indicatorState: GestureIndicatorState,
//    fastSkipState: FastSkipState?,
    enableSwipeToSeek: Boolean,
    audioController: LevelController,
    brightnessController: LevelController,
//    playbackSpeedControllerState: PlaybackSpeedControllerState?,
    modifier: Modifier = Modifier,
    family: GestureFamily = GestureFamily.TOUCH,    // todo 多平台触摸类型
    onTogglePauseResume: () -> Unit = {},
    onToggleFullscreen: () -> Unit = {},
    onExitFullscreen: () -> Unit = {},
    onToggleDanmaku: () -> Unit = {},
) {
    val onTogglePauseResumeState by rememberUpdatedState(onTogglePauseResume)

    BoxWithConstraints {
        Row(
            Modifier.align(Alignment.TopCenter)
                .systemGesturesPadding()
                .padding(top = 16.dp),
        ) {
            LaunchedEffect(seekerState.deltaSeconds) {
                if (seekerState.isSeeking) {
                    indicatorState.showSeeking(seekerState.deltaSeconds)
                }
            }
            GestureIndicator(indicatorState)
        }
        val maxHeight = maxHeight
        val adjustingVolumeOrBrightness =
            indicatorState.visible && (indicatorState.state == VOLUME || indicatorState.state == BRIGHTNESS)
        val adjustingForwardOrBackward =
            indicatorState.visible && (indicatorState.state == FAST_FORWARD || indicatorState.state == FAST_BACKWARD)

//   useDesktopGestureLayoutWorkaround = false,
//        clickToPauseResume = false,
//        clickToToggleController = true,
//        doubleClickToFullscreen = false,
//        doubleClickToPauseResume = true,
//        swipeToSeek = true,
//        swipeRhsForVolume = true,
//        swipeLhsForBrightness = true,
//        longPressForFastSkip = true,
//        volumeControllerOnBottomBar = false,
//        autoHideController = true,
//        mouseHoverForController = false,
        val focusManager by rememberUpdatedState(LocalFocusManager.current) // workaround for #288

        LaunchedEffect(controllerState.visibility, controllerState.alwaysOn) {
            if (controllerState.alwaysOn) return@LaunchedEffect
            if (controllerState.visibility.bottomBar) {
                delay(VIDEO_GESTURE_TOUCH_SHOW_CONTROLLER_DURATION)
                controllerState.toggleFullVisible(false)
            }
        }

        @Composable
        fun Modifier.combineClickableWithFamilyGesture() = combinedClickable(
            remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                focusManager.clearFocus()
                controllerState.toggleFullVisible()
            },
            onDoubleClick = remember(onToggleFullscreen) {
                { onTogglePauseResumeState() }
            },
        )
        Box(
            modifier
                .testTag("VideoGestureHost")
                .combineClickableWithFamilyGesture()
//                .ifThen(family.swipeToSeek && enableSwipeToSeek) {
//                    val swipeToSeekRequester =
//                        rememberAlwaysOnRequester(controllerState, "swipeToSeek")
//                    swipeToSeek(
//                        seekerState,
//                        Orientation.Horizontal,
//                        //调节音量/亮度时禁用水平seek
//                        enabled = !adjustingVolumeOrBrightness,
//                        onDragStarted = {
//                            if (controllerState.visibility.bottomBar) {
//                                swipeToSeekRequester.request()
//                            }
//                            controllerState.setRequestProgressBar(swipeToSeekRequester)
//                        },
//                        onDragStopped = {
//                            if (controllerState.visibility.bottomBar) {
//                                swipeToSeekRequester.cancelRequest()
//                            }
//                            controllerState.cancelRequestProgressBarVisible(swipeToSeekRequester)
//                            progressSliderState.finishPreview()
//                        },
//                    ) {
//                        progressSliderState.run {
//                            if (totalDurationMillis == 0L) return@run
//                            val offsetRatio =
//                                (currentPositionMillis + seekerState.deltaSeconds.times(1000)).toFloat() / totalDurationMillis
//                            previewPositionRatio(offsetRatio.coerceIn(0f, 1f))
//                        }
//                    }
//                }
//                .ifThen(family.keyboardLeftRightToSeek) {
//                    onKeyboardHorizontalDirection(
//                        onBackward = {
//                            seekerState.onSeek(-5)
//                        },
//                        onForward = {
//                            seekerState.onSeek(5)
//                        },
//                    )
//                }
//                .ifThen(family.keyboardUpDownForVolume) {
//                    audioController.let { controller ->
//                        onKey(ComposeKey.DirectionUp) {
//                            controller.increaseLevel(0.10f)
//                        }
//                        onKey(ComposeKey.DirectionDown) {
//                            controller.decreaseLevel(0.10f)
//                        }
//                    }
//                }
//                .ifThen(family.keyboardSpaceForPauseResume) {
//                    onKey(ComposeKey.Spacebar) {
//                        onTogglePauseResumeState()
//                    }
//                }
                .fillMaxSize(),
        ) {
            Row(
                Modifier.matchParentSize()
                    .systemGesturesPadding()
//                    .ifThen(family.longPressForFastSkip) {
//                        fastSkipState?.let {
//                            longPressFastSkip(it, SkipDirection.FORWARD)
//                        }
//                    },
            ) {
                Box(
                    Modifier
                        .ifThen(family.swipeLhsForBrightness) {
                            swipeLevelControlWithIndicator(
                                brightnessController,
                                ((maxHeight - 100.dp) / 40).coerceAtLeast(2.dp),
                                Orientation.Vertical,
                                indicatorState,
                                enabled = !seekerState.isSeeking && !adjustingForwardOrBackward,
                                step = 0.01f,
                                setup = {
                                    indicatorState.state = BRIGHTNESS
                                },
                            )
                        }
                        .weight(1f)
                        .fillMaxHeight(),
                )

                Box(Modifier.weight(1f).fillMaxHeight())

                Box(
                    Modifier
                        .ifThen(family.swipeRhsForVolume) {
                            swipeLevelControlWithIndicator(
                                audioController,
                                ((maxHeight - 100.dp) / 40).coerceAtLeast(2.dp),
                                Orientation.Vertical,
                                indicatorState,
                                enabled = !seekerState.isSeeking && !adjustingForwardOrBackward,
                                step = 0.05f,
                                setup = {
                                    indicatorState.state = VOLUME
                                },
                            )
                        }
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }

        // 状态栏区域响应点击手势
//            Box(
//                Modifier.fillMaxWidth()
//                    .ifThen(isSystemInFullscreen()) {
//                        windowInsetsTopHeight(WindowInsets.systemGestures)
//                    }
//                    .combineClickableWithFamilyGesture(),
//            )
    }
}

/**
 * 展示当前快进/快退秒数的指示器.
 *
 * `<< 00:00` / `>> 00:00`
 */
@Composable
fun GestureIndicator(
    state: GestureIndicatorState,
) {
    val shape = MaterialTheme.shapes.small
    val colors = MaterialTheme.colorScheme
    var lastDelta by remember(state) {
        mutableIntStateOf(state.deltaSeconds)
    }

    AnimatedVisibility(
        visible = state.visible,
        enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)),
        exit = fadeOut(tween(durationMillis = 500)),
        label = "SeekPositionIndicator",
    ) {
        Surface(
            Modifier.alpha(0.8f),
            color = colors.surface,
            shape = shape,
            shadowElevation = 1.dp,
            contentColor = colors.onSurface,
        ) {
            val iconSize = 36.dp
            ProvideTextStyle(MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) {
                Row(
                    Modifier.background(Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .height(iconSize),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Used by volume and brightness
                    val progressIndicator: @Composable () -> Unit = remember(state, colors) {
                        // This remember is needed because Compose does not remember lambdas
                        // and can cause performance problem in this fast-changing composable.
                        {
                            LinearProgressIndicator(
                                progress = { state.progressValue },
                                modifier = Modifier.width(80.dp),
                                color = colors.primary,
                                trackColor = colors.onSurface.copy(alpha = 0.5f),
                                drawStopIndicator = {},
                            )
                        }
                    }

                    when (state.state) {
                        RESUMED_ONCE -> {
                            Icon(
                                Icons.Rounded.PlayArrow, null,
                                Modifier.size(iconSize).background(Color.Transparent),
                            )
                        }

                        PAUSED_ONCE -> {
                            Icon(Icons.Rounded.Pause, null, Modifier.size(iconSize))
                        }

                        SEEKING -> {
                            val deltaDuration = state.deltaSeconds
                            // 记忆变为 0 之前的 delta, 这样在快进/快退结束后, 会显示上一次的 delta, 而不是显示 0
                            val duration = if (deltaDuration == 0) {
                                lastDelta
                            } else {
                                deltaDuration.also {
                                    lastDelta = deltaDuration
                                }
                            }

                            Icon(
                                if (duration > 0) {
                                    Icons.Rounded.FastForward
                                } else {
                                    Icons.Rounded.FastRewind
                                },
                                null,
                                Modifier.size(iconSize),
                            )
                            val text = renderTime(duration.absoluteValue)
                            Text(
                                text,
                                maxLines = 1,
                            )
                        }

                        VOLUME -> {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeUp, null,
                                Modifier.size(iconSize),
                            )
                            progressIndicator()
                        }

                        BRIGHTNESS -> {
                            Icon(
                                when (state.progressValue) {
                                    in 0.67..1.0 -> Icons.Rounded.BrightnessHigh
                                    in 0.33..0.67 -> Icons.Rounded.BrightnessMedium
                                    else -> Icons.Rounded.BrightnessLow
                                },
                                null,
                                Modifier.size(iconSize),
                            )
                            progressIndicator()
                        }

                        FAST_FORWARD -> {
                            Icon(Icons.Rounded.FastForward, null, Modifier.size(iconSize))
                        }

                        FAST_BACKWARD -> {
                            Icon(Icons.Rounded.FastRewind, null, Modifier.size(iconSize))
                        }

                        null -> {}
                    }
                }
            }
        }
    }
}

@Stable
private fun renderTime(seconds: Int): String {
    return "${(seconds / 60).fixToString(2)}:${(seconds % 60).fixToString(2)}"
}

private fun Int.fixToString(length: Int, prefix: Char = '0'): String {
    val str = this.toString()
    return if (str.length >= length) {
        str
    } else {
        prefix.toString().repeat(length - str.length) + str
    }
}

val VIDEO_GESTURE_MOUSE_MOVE_SHOW_CONTROLLER_DURATION = 3.seconds
val VIDEO_GESTURE_TOUCH_SHOW_CONTROLLER_DURATION = 3.seconds
