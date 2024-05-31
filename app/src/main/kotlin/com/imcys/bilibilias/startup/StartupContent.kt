package com.imcys.bilibilias.startup

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.imcys.bilibilias.feature.login.LoginContent
import com.imcys.bilibilias.feature.splash.SplashContent
import com.imcys.bilibilias.ui.AsApp
import com.imcys.bilibilias.ui.AsAppState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StartupContent(
    component: StartupComponent,
    appState: AsAppState
) {
    Children(stack = component.stack) {
        when (val child = it.instance) {
            is StartupComponent.Child.LoginChild -> LoginContent(
                component = child.component,
                onNavigationToRoot = component::onRootClicked
            )

            is StartupComponent.Child.RootChild -> AsApp(
                appState = appState,
                component = child.component
            )

            is StartupComponent.Child.SplashChild -> SplashContent(
                component = child.component,
                onNavigationToLogin = component::onLoginClicked,
                onNavigationToRoot = component::onRootClicked
            )
        }
    }
}
