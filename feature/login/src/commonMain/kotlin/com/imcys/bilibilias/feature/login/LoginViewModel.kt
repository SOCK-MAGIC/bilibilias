package com.imcys.bilibilias.feature.login

import androidx.lifecycle.ViewModel

class LoginViewModel(
    val cookieStateMachine: CookieStateMachine,
    val qrCodeStateMachine: QrCodeLoginStateMachine,
) : ViewModel()