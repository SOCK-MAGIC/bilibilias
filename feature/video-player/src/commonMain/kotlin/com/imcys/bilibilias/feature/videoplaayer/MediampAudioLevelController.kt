package com.imcys.bilibilias.feature.videoplaayer

import com.imcys.bilibilias.core.videoplayer.gesture.LevelController
import org.openani.mediamp.features.AudioLevelController
import org.openani.mediamp.features.toggleMute

/**
 * Delegation of [AudioLevelController], which allows observing volume state changes.
 */
class MediampAudioLevelController(
    private val controller: AudioLevelController,
    private val onVolumeStateChanged: (level: Float, mute: Boolean) -> Unit,
) : LevelController {
    override val level: Float get() = controller.volume.value

    val levelFlow = controller.volume
    val muteFlow = controller.isMute

    override val range: ClosedRange<Float> = 0f..controller.maxVolume

    override fun setLevel(level: Float) {
        val newLevel = level.coerceIn(range)
        controller.setVolume(newLevel)
        onVolumeStateChanged(newLevel, controller.isMute.value)
    }

    fun toggleMute() {
        val targetIsMute = !muteFlow.value
        controller.toggleMute()
        onVolumeStateChanged(level, targetIsMute)
    }
}