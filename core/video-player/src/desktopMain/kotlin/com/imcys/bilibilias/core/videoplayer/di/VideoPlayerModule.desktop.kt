package com.imcys.bilibilias.core.videoplayer.di

import com.imcys.bilibilias.core.videoplayer.features.AudioManager
import com.imcys.bilibilias.core.videoplayer.features.BrightnessManager
import com.imcys.bilibilias.core.videoplayer.features.DesktopAudioManager
import com.imcys.bilibilias.core.videoplayer.features.DesktopBrightnessManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val VideoPlayerModule: Module = module {
    factoryOf(::DesktopAudioManager) bind AudioManager::class
    factoryOf(::DesktopBrightnessManager) bind BrightnessManager::class
}