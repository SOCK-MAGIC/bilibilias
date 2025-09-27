package com.imcys.bilibilias.logic.login

import androidx.lifecycle.ViewModel

class LoginViewModel(
    val cookieStateMachine: CookieStateMachine,
    val qrCodeStateMachine: QrCodeLoginStateMachine,
) : ViewModel()