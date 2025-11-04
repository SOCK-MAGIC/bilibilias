package com.imcys.bilibilias.core.videoplayer.features

import androidx.annotation.FloatRange

internal class DesktopBrightnessManager : BrightnessManager {
    override fun getBrightness(): Float {
        return 0f
    }

    override fun setBrightness(@FloatRange(from = 0.0, to = 1.0) level: Float) {
    }
}