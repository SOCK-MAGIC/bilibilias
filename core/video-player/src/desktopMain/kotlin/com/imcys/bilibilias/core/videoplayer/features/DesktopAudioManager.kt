package com.imcys.bilibilias.core.videoplayer.features

internal class DesktopAudioManager : AudioManager {
    override fun getVolume(streamType: StreamType): Float {
        return 0f
    }

    override fun setVolume(streamType: StreamType, levelPercentage: Float) {
    }
}