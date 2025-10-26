package com.imcys.bilibilias.logic

import com.imcys.bilibilias.logic.login.CookieStateMachine
import com.imcys.bilibilias.logic.login.LoginViewModel
import com.imcys.bilibilias.logic.login.QrCodeLoginStateMachine
import com.imcys.bilibilias.logic.setting.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val LogicModule = module {
    factoryOf(::CookieStateMachine)
    factoryOf(::QrCodeLoginStateMachine)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SettingsViewModel)
}