package com.imcys.bilibilias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
        super.onCreate(savedInstanceState)

//        val notifier = get<Notifier>()
//        notifier.postNotifications()
        enableEdgeToEdge(
            // 透明状态栏
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            // 透明导航栏
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarColorEffect()

            val appState = rememberAsAppState(
                errorMonitor,
                asBackStack = backStackViewModel.asBackStack,
            )
            CompositionLocalProvider {
                AsTheme(false) {
                    AsApp(appState, entryProviderBuilders)
                }
            }
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
