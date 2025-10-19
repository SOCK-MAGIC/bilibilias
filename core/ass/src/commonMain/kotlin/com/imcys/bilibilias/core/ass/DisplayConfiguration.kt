package com.imcys.bilibilias.core.ass

import androidx.annotation.FloatRange
import androidx.annotation.IntRange

/**
 * Represents configuration settings for a display element.
 *
 * Using a `data class` provides useful generated methods like `copy()`, `equals()`, and `toString()`.
 */
class DisplayConfiguration(
    val font: FontSettings = FontSettings(),
    val layout: LayoutSettings = LayoutSettings(),
    val timing: TimingSettings = TimingSettings()
)

/**
 * Contains all settings related to text rendering.
 */
data class FontSettings(
    val fontName: String = "Microsoft YaHei",

    @IntRange(from = 1)
    val fontSize: Int = 38,

    @IntRange(from = 1)
    val secondaryFontSize: Int = 38,

    val isBold: Boolean = false,

    @FloatRange(from = 0.0)
    val outlineWidth: Float = 1.0f,

    @FloatRange(from = 0.0)
    val shadowRadius: Float = 0.0f
)

/**
 * Contains all settings related to screen layout and dimensions.
 */
data class LayoutSettings(
    @IntRange(from = 1)
    val width: Int = 1920,

    @IntRange(from = 1)
    val height: Int = 1080,

    @FloatRange(from = 0.0, to = 1.0)
    val displayAreaScale: Float = 1.0f,

    @FloatRange(from = 0.0, to = 1.0)
    val opacity: Float = 0.8f
)

/**
 * Contains settings for animation and display durations.
 * The 'InSeconds' suffix clarifies the unit of measurement.
 */
data class TimingSettings(
    @IntRange(from = 0)
    val rollTime: Int = 12 * 1000,

    @IntRange(from = 0)
    val fixedTime: Int = 5 * 1000
)

class DanmakuTrackManager(val tracks: Int) {
    private val trackData: MutableList<TimeLength> = MutableList(tracks) { TimeLength(-1, 0) }

    operator fun get(trackIndex: Int): TimeLength {
        require(trackIndex in 0 until tracks) { "Track index is out of bounds: $trackIndex" }
        return trackData[trackIndex]
    }

    operator fun set(trackIndex: Int, value: TimeLength) {
        require(trackIndex in 0 until tracks) { "Track index is out of bounds: $trackIndex" }
        trackData[trackIndex] = value
    }

    fun updateTrack(trackIndex: Int, time: Int, length: Int) {
        this[trackIndex] = TimeLength(time, length)
    }
}

data class TimeLength(val time: Int, val length: Int)