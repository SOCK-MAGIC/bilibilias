package com.imcys.bilibilias.core.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import org.openani.mediamp.InternalForInheritanceMediampApi
import org.openani.mediamp.features.PlaybackSpeed

/**
 * Creates and remembers a [PlaybackSpeedControllerState].
 *
 * This function correctly handles the lifecycle of the state holder and launches a coroutine
 * to collect playback speed updates from the [playbackSpeed] source.
 *
 * @param playbackSpeed The underlying service or interface to control playback speed.
 * @param initialSpeedList The initial list of available playback speeds. It is recommended to
 *   provide a stable list (e.g., a constant or a value wrapped in `remember`).
 * @return A remembered instance of [PlaybackSpeedControllerState].
 */
@Composable
fun rememberPlaybackSpeedControllerState(
    playbackSpeed: PlaybackSpeed,
    initialSpeedList: List<Float> = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 3f)
): PlaybackSpeedControllerState {
    val state = remember(playbackSpeed, initialSpeedList) {
        PlaybackSpeedControllerState(
            playbackSpeed = playbackSpeed,
            speedList = initialSpeedList
        )
    }

    LaunchedEffect(playbackSpeed) {
        playbackSpeed.valueFlow
            .distinctUntilChanged()
            .collect { newSpeed ->
                state.currentSpeed = newSpeed
            }
    }

    return state
}

/**
 * A state holder for managing playback speed logic.
 *
 * This class should be created and remembered using the [rememberPlaybackSpeedControllerState] composable function.
 *
 * @param playbackSpeed The underlying service or interface to control playback speed.
 * @param speedList The list of available playback speeds.
 */
@Stable
class PlaybackSpeedControllerState(
    private val playbackSpeed: PlaybackSpeed,
    val speedList: List<Float>,
) {
    var currentSpeed by mutableFloatStateOf(playbackSpeed.value)
        internal set

    /**
     * The index of the current speed in the `speedList`.
     * If the `currentSpeed` is not found in the list, this falls back to the index of `1.0f`.
     * This ensures the UI always has a valid index to highlight.
     * Returns -1 if 1.0f is also not in the list (which is disallowed by init checks).
     */
    val currentIndex: Int by derivedStateOf {
        speedList.indexOf(currentSpeed).takeIf { it != -1 }
            ?: speedList.indexOf(1f)
    }

    init {
        require(speedList.isNotEmpty()) { "Playback speed list must not be empty" }
        require(speedList.contains(1.0f)) { "Playback speed list must contain 1.0f, but was $speedList" }
        require(
            speedList.zipWithNext()
                .all { (a, b) -> a < b }) { "Playback speed list must be strictly monotonic increasing" }
    }

    /**
     * Set playback speed to the next available speed in the list.
     * If the current speed is not in the list, it sets it to the nearest higher speed.
     */
    fun speedUp() {
        val nextSpeed = if (currentIndex == -1) {
            // Current speed is not in the list, find the first speed greater than it.
            speedList.firstOrNull { it > currentSpeed } ?: speedList.last()
        } else if (currentIndex < speedList.size - 1) {
            // Move to the next index.
            speedList[currentIndex + 1]
        } else {
            // Already at the highest speed, do nothing.
            return
        }
        playbackSpeed.set(nextSpeed)
    }

    /**
     * Set playback speed to the previous available speed in the list.
     * If the current speed is not in the list, it sets it to the nearest lower speed.
     */
    fun speedDown() {
        val prevSpeed = if (currentIndex == -1) {
            // Current speed is not in the list, find the first speed smaller than it.
            speedList.lastOrNull { it < currentSpeed } ?: speedList.first()
        } else if (currentIndex > 0) {
            // Move to the previous index.
            speedList[currentIndex - 1]
        } else {
            // Already at the lowest speed, do nothing.
            return
        }
        playbackSpeed.set(prevSpeed)
    }

    fun setSpeed(index: Int) {
        require(index in speedList.indices) {
            "Speed index is out of range, index: $index, size: ${speedList.size}"
        }
        playbackSpeed.set(speedList[index])
    }

    fun setSpeed(value: Float) {
        playbackSpeed.set(value)
    }

    fun reset() {
        playbackSpeed.set(1.0f)
    }
}

@OptIn(InternalForInheritanceMediampApi::class)
object NoOpPlaybackSpeedController : PlaybackSpeed {
    override val value: Float = 1f
    override val valueFlow: Flow<Float> = flowOf(1f)

    override fun set(speed: Float) {
    }
}