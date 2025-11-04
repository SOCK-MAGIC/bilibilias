package com.imcys.bilibilias.core.videoplayer.features

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.FloatRange

internal class AndroidBrightnessManager(
    private val window: Window,
    private val context: Context,
) : BrightnessManager {

    private val contentResolver = context.contentResolver

    companion object {
        /** 系统亮度的最大值（通常是 255）。 */
        private const val MAX_SYSTEM_BRIGHTNESS = 255f
    }

    /**
     * 获取当前屏幕亮度，返回一个 0.0f 到 1.0f 之间的值。
     *
     * 首先检查应用内是否有亮度覆盖设置，如果没有，则读取系统亮度设置。
     */
    override fun getBrightness(): Float {
        val appOverrideBrightness = window.attributes.screenBrightness

        // 如果亮度值不是“跟随系统”，则直接返回应用内的设置值
        if (appOverrideBrightness != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            return appOverrideBrightness
        }

        // 否则，读取系统亮度设置
        return try {
            val systemBrightness = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            systemBrightness.toFloat() / MAX_SYSTEM_BRIGHTNESS
        } catch (e: Settings.SettingNotFoundException) {
            Log.d("BrightnessManager", "设置亮度失败", e)
            0.5f
        }
    }

    /**
     * 为当前窗口设置应用内亮度。
     *
     * @param level 亮度级别，范围必须在 0.0f 到 1.0f 之间。
     *              - 设置为 0.0f 表示最暗。
     *              - 设置为 1.0f 表示最亮。
     *              - 若要恢复跟随系统亮度，请调用 restoreSystemBrightness()。
     */
    override fun setBrightness(@FloatRange(from = 0.0, to = 1.0) level: Float) {
        val clampedLevel = level.coerceIn(0.0f, 1.0f)

        val attributes = window.attributes.apply {
            screenBrightness = clampedLevel
        }
        window.attributes = attributes
    }

    /**
     * 恢复窗口亮度，使其跟随系统设置。
     */
    fun restoreSystemBrightness() {
        val attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
        window.attributes = attributes
    }
}