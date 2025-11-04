package com.imcys.bilibilias.core.videoplayer.gesture

import androidx.annotation.MainThread
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import com.imcys.bilibilias.core.videoplayer.features.AudioManager
import com.imcys.bilibilias.core.videoplayer.features.BrightnessManager
import com.imcys.bilibilias.core.videoplayer.features.StreamType
import kotlinx.coroutines.CoroutineScope

interface LevelController {
    val level: Float

    val range: ClosedRange<Float>

    @MainThread
    fun setLevel(level: Float)
}

@MainThread
fun LevelController.increaseLevel(step: Float = 0.05f) {
    setLevel((level + step).coerceAtMost(range.endInclusive))
}

@MainThread
fun LevelController.decreaseLevel(step: Float = 0.05f) {
    setLevel((level - step).coerceAtLeast(range.start))
}

fun AudioManager.asLevelController(
    streamType: StreamType,
): LevelController = object : LevelController {
    override val level: Float
        get() = getVolume(streamType)

    override val range: ClosedRange<Float> = 0f..1f

    override fun setLevel(level: Float) {
        setVolume(streamType, level.coerceIn(range))
    }
}

fun BrightnessManager.asLevelController(): LevelController = object : LevelController {
    override val level: Float
        get() = getBrightness()

    override val range: ClosedRange<Float> = 0f..1f

    override fun setLevel(level: Float) {
        setBrightness(level.coerceIn(range))
    }
}

fun Modifier.swipeLevelControlWithIndicator(
    controller: LevelController,
    stepSize: Dp,
    orientation: Orientation,
    indicatorState: GestureIndicatorState,
    enabled: Boolean = true,
    step: Float = 0.05f,
    setup: () -> Unit = {}
): Modifier = swipeLevelControl(
    controller = controller,
    stepSize = stepSize,
    orientation = orientation,
    step = step,
    enabled = enabled,
    afterStep = {
        setup()
        indicatorState.progressValue = controller.level
    },
    onDragStarted = {
        indicatorState.visible = true
    },
    onDragStopped = {
        indicatorState.visible = false
    },
)

fun Modifier.swipeLevelControl(
    controller: LevelController,
    stepSize: Dp,
    orientation: Orientation,
    step: Float = 0.05f,
    enabled: Boolean = true,
    afterStep: (StepDirection) -> Unit = {},
    onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {},
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "swipeLevelControl"
        properties["controller"] = controller
        properties["stepSize"] = stepSize
        properties["orientation"] = orientation
    },
) {
    steppedDraggable(
        rememberSteppedDraggableState(
            stepSize = stepSize,
            onStep = { direction ->
                when (direction) {
                    StepDirection.FORWARD -> controller.increaseLevel(step)
                    StepDirection.BACKWARD -> controller.decreaseLevel(step)
                }
                afterStep(direction)
            },
        ),
        orientation = orientation,
        enabled = enabled,
        onDragStarted = onDragStarted,
        onDragStopped = onDragStopped,
    )

}