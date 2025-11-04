package com.imcys.bilibilias.core.videoplayer.features


interface BrightnessManager {
    /**
     * @return 0..1
     */
    fun getBrightness(): Float

    fun setBrightness(level: Float)
}