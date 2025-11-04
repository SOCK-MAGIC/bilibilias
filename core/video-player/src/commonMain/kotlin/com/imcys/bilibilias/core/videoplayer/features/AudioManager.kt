package com.imcys.bilibilias.core.videoplayer.features

interface AudioManager {
    /**
     * @return 0..1
     */
    fun getVolume(streamType: StreamType): Float

    fun setVolume(streamType: StreamType, levelPercentage: Float)
}

enum class StreamType {
    MUSIC,
}