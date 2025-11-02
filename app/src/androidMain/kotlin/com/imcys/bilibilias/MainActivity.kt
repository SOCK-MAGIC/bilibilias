package com.imcys.bilibilias

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.EntryProviderScope
import com.imcys.bilibilias.core.data.util.ErrorMonitor
import com.imcys.bilibilias.core.designsystem.theme.AsTheme
import com.imcys.bilibilias.core.navigation.AsBackStackViewModel
import com.imcys.bilibilias.core.navigation.AsNavKey
import com.imcys.bilibilias.ui.AsApp
import com.imcys.bilibilias.ui.rememberAsAppState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainActivity : ComponentActivity(), KoinComponent {

    private val errorMonitor: ErrorMonitor = get()
    private val backStackViewModel: AsBackStackViewModel by viewModel()
    private val entryProviderBuilders: EntryProviderScope<AsNavKey>.() -> Unit = get()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
//            SystemBarAppearanceController()

            val appState = rememberAsAppState(
                errorMonitor,
                asBackStack = backStackViewModel.asBackStack,
            )
            CompositionLocalProvider {
                AsTheme {
                    AsApp(appState, entryProviderBuilders)
                }
            }
        }
    }

    @Composable
    private fun SystemBarAppearanceController(isDark: Boolean) {
        val view = LocalView.current
        // SideEffect 会在每次 Composable 成功重组后运行
        SideEffect {
            val window = (view.context as Activity).window
            // WindowInsetsControllerCompat 是用于控制窗口 Insets 和系统栏外观的正确 API
            val insetsController = WindowCompat.getInsetsController(window, view)

            // true = 状态栏图标为浅色 (用于深色背景)
            // false = 状态栏图标为深色 (用于浅色背景)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }
    @Composable
    fun SystemBarColorEffect(
        isDark: Boolean = false,
    ) {
        // Set statusBarStyle & navigationBarStyle
        val activity = LocalActivity.current as? ComponentActivity
        if (activity != null) {
            LaunchedEffect(activity, isDark) {
                if (isDark) {
                    activity.enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                    )
                } else {
                    activity.enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ),
                        navigationBarStyle = SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ),
                    )
                }
            }
        }
    }
}
