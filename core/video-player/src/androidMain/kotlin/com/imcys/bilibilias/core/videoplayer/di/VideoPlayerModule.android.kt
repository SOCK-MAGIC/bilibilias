package com.imcys.bilibilias.core.videoplayer.di

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.imcys.bilibilias.core.videoplayer.features.AndroidAudioManager
import com.imcys.bilibilias.core.videoplayer.features.AndroidBrightnessManager
import com.imcys.bilibilias.core.videoplayer.features.AudioManager
import com.imcys.bilibilias.core.videoplayer.features.BrightnessManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val VideoPlayerModule: Module = module {
    factoryOf(::AndroidAudioManager) bind AudioManager::class
    factory<BrightnessManager> {
        AndroidBrightnessManager(androidContext())
    }
}

// TODO: move to common
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}