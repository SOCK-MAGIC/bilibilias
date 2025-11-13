package com.imcys.bilibilias

import com.imcys.bilibilias.mp.BuildKonfig

object BuildConfig {
    val APPLICATION_ID: String = BuildKonfig.packageName

    val DEBUG: Boolean = BuildKonfig.debugBuild

}