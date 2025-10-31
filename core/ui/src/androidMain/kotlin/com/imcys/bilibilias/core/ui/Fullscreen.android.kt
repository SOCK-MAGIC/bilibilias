package com.imcys.bilibilias.core.ui

import android.content.pm.ActivityInfo
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun setRequestFullScreen(fullscreen: Boolean) {
    val activity = LocalActivity.current ?: return

    LaunchedEffect(fullscreen) {
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        if (fullscreen) {
            // 进入全屏模式
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            // 退出全屏模式
            insetsController.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // 确保在离开时恢复到非全屏状态
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)

            insetsController.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
actual fun setSystemBarVisible(visible: Boolean) {
    val activity = LocalActivity.current ?: return

    val insetsController =
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    val bitmask = WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()

    if (visible) {
        insetsController.show(bitmask)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    } else {
        insetsController.hide(bitmask)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}