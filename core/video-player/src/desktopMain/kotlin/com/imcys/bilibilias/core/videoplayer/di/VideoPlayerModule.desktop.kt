package com.imcys.bilibilias.core.videoplayer.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.openani.mediamp.MediampPlayerFactory
import org.openani.mediamp.MediampPlayerFactoryLoader

actual val VideoPlayerModule: Module = module {
    single<MediampPlayerFactory<*>> {
        TODO("VLC")
//        MediampPlayerFactoryLoader.register()
//        MediampPlayerSurfaceProviderLoader.register()
        MediampPlayerFactoryLoader.first()
    }
}