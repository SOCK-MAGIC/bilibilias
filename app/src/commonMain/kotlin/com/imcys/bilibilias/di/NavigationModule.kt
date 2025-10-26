package com.imcys.bilibilias.di

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.imcys.bilibilias.core.navigation.AsBackStack
import com.imcys.bilibilias.core.navigation.AsBackStackViewModel
import com.imcys.bilibilias.core.navigation.AsNavKey
import com.imcys.bilibilias.feature.search.SearchScreen
import com.imcys.bilibilias.feature.videoplaayer.PlayerScreen
import com.imcys.bilibilias.feature.videoplaayer.VideoPlayerViewModel
import com.imcys.bilibilias.navigation.CacheRoute
import com.imcys.bilibilias.navigation.LoginRoute
import com.imcys.bilibilias.navigation.PlayerRoute
import com.imcys.bilibilias.navigation.SearchRoute
import com.imcys.bilibilias.navigation.SettingRoute
import com.imcys.bilibilias.navigation.TopLevelDestination
import com.imcys.bilibilias.ui.cache.CacheScreen
import com.imcys.bilibilias.ui.login.LoginScreen
import com.imcys.bilibilias.ui.setting.SettingsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val NavigationModule = module {
    viewModelOf(::AsBackStackViewModel)
    single { AsBackStack(TopLevelDestination.SEARCH.key) }
    single<SerializersModule> {
        SerializersModule {
            polymorphic(AsNavKey::class) {
                subclass(SearchRoute::class)
                subclass(CacheRoute::class)
                subclass(LoginRoute::class)
                subclass(SettingRoute::class)
            }
        }
    }
    single<EntryProviderBuilder<AsNavKey>.() -> Unit> {
        {
            val backStack: AsBackStack = get()
            entry<SearchRoute> {
                SearchScreen(
                    navigationToLogin = { backStack.navigate(LoginRoute) },
                    navigationToPlayer = {},
                    navigationToSettings = { backStack.navigate(SettingRoute) }
                )
            }
            entry<CacheRoute> {
                CacheScreen(
                    navigationToPlayer = { backStack.navigate(PlayerRoute(it)) },
                )
            }
            entry<LoginRoute> {
                LoginScreen(
                    onShowSnackbar = { _, _ -> true },
                    onBack = { backStack.popLast() }
                )
            }
            entry<SettingRoute> {
                SettingsScreen(
                    onBack = { backStack.popLast() }
                )
            }
            entry<PlayerRoute> {
                val playerViewModel: VideoPlayerViewModel =
                    koinViewModel(parameters = { parametersOf(it.id) })
                PlayerScreen(playerViewModel)
            }
        }
    }
}