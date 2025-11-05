package com.imcys.bilibilias.core.videoplayer.features

import android.content.Context
import android.provider.Settings
import android.view.WindowManager
import com.imcys.bilibilias.core.videoplayer.di.findActivity

internal class AndroidBrightnessManager(
    private val context: Context,
) : BrightnessManager {

    companion object {
        /** 系统亮度的最大值（通常是 255）。 */
        private const val MAX_SYSTEM_BRIGHTNESS = 255f
    }

    override fun getBrightness(): Float {
        val activity = context.findActivity() ?: return -1f
        val window = activity.window ?: return -1f
        val current = window.attributes.screenBrightness

        if (current == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            // no override, use system settings
            return Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
            ).toFloat() / MAX_SYSTEM_BRIGHTNESS
        }

        return current
    }

    override fun setBrightness(level: Float) {
        val activity = context.findActivity() ?: return
        val window = activity.window ?: return
        window.attributes.screenBrightness = level
        window.attributes = window.attributes
    }
}
//
//internal class AndroidBrightnessManager(
//    private val window: Window,
//    private val context: Context,
//) : BrightnessManager {
//
//    private val contentResolver = context.contentResolver
//
//    companion object {
//        private const val MAX_SYSTEM_BRIGHTNESS = 255f
//    }
//
//    override fun getBrightness(): Float {
//        val appOverrideBrightness = window.attributes.screenBrightness
//        if (appOverrideBrightness != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
//            return appOverrideBrightness
//        }
//        return try {
//            val systemBrightness = Settings.System.getInt(
//                contentResolver,
//                Settings.System.SCREEN_BRIGHTNESS
//            )
//            systemBrightness.toFloat() / MAX_SYSTEM_BRIGHTNESS
//        } catch (e: Settings.SettingNotFoundException) {
//            Log.w("BrightnessManager", "Could not read system brightness", e)
//            0.5f
//        }
//    }
//
//    override fun setBrightness(@FloatRange(from = 0.0, to = 1.0) level: Float) {
//        val clampedLevel = level.coerceIn(0.0f, 1.0f)
//
//        // 1. 创建属性副本并修改
//        val attributes = window.attributes.apply {
//            screenBrightness = clampedLevel
//        }
//
//        // 2. 使用 setAttributes() 来应用更改，这是更可靠的方式
//        window.setAttributes(attributes)
//    }
//
//    fun restoreSystemBrightness() {
//        // 1. 创建属性副本并修改
//        val attributes = window.attributes.apply {
//            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
//        }
//
//        // 2. 同样使用 setAttributes() 来应用更改
//        window.setAttributes(attributes)
//    }
//}