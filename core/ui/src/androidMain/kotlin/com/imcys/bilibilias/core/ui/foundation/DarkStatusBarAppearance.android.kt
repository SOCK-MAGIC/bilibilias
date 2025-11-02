package com.imcys.bilibilias.core.ui.foundation

import android.view.Window
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat

@Composable
actual fun DarkStatusBarAppearance() {
    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        // 通过视图获取窗口，比从 context 转换更安全
        val window: Window = activity?.window ?: return@DisposableEffect onDispose {}

        val insetsController = WindowCompat.getInsetsController(window, activity)

        val wasAppearanceLightStatusBars = insetsController.isAppearanceLightStatusBars

        // 设置为深色状态栏（图标为浅色）
        insetsController.isAppearanceLightStatusBars = false

        onDispose {
            insetsController.isAppearanceLightStatusBars = wasAppearanceLightStatusBars
        }
    }
}