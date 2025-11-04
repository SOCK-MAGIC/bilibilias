package com.imcys.bilibilias.core.videoplayer.features

import android.content.Context
import androidx.core.content.getSystemService
import kotlin.math.roundToInt
import android.media.AudioManager as SystemAudioManager

internal class AndroidAudioManager(
    context: Context
) : AudioManager {
    private val StreamType.android: Int
        get() {
            return when (this) {
                StreamType.MUSIC -> SystemAudioManager.STREAM_MUSIC
            }
        }
    private val systemAudioManager: SystemAudioManager =
        requireNotNull(context.getSystemService<SystemAudioManager>()) {
            "AudioManager system service not found, please check the context."
        }

    override fun getVolume(streamType: StreamType): Float {
        return systemAudioManager.getStreamVolume(streamType.android).toFloat() /
                systemAudioManager.getStreamMaxVolume(streamType.android)
    }

    override fun setVolume(streamType: StreamType, levelPercentage: Float) {
        val max = systemAudioManager.getStreamMaxVolume(streamType.android)
        return systemAudioManager.setStreamVolume(
            streamType.android,
            (levelPercentage * max).roundToInt()
                .coerceIn(minimumValue = 0, maximumValue = max),
            0,
        )
    }
}