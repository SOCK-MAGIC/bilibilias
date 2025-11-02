package com.imcys.bilibilias.core.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * 保持屏幕常亮。
 */
@Composable
actual fun KeepScreenOn() {
    val view = LocalView.current

    DisposableEffect(Unit) {
        view.keepScreenOn = true

        onDispose {
            // 恢复默认行为
            view.keepScreenOn = false
        }
    }
}