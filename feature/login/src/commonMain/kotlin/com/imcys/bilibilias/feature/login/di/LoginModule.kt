package com.imcys.bilibilias.feature.login.di

import com.imcys.bilibilias.feature.login.CookieStateMachine
import com.imcys.bilibilias.feature.login.LoginViewModel
import com.imcys.bilibilias.feature.login.QrCodeLoginStateMachine
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val LoginModule = module {
    factoryOf(::CookieStateMachine)
    factoryOf(::QrCodeLoginStateMachine)
    viewModelOf(::LoginViewModel)
}