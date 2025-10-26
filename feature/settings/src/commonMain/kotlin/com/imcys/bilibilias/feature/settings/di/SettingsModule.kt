package com.imcys.bilibilias.feature.settings.di

import com.imcys.bilibilias.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val SettingsModule = module {
    viewModelOf(::SettingsViewModel)
}