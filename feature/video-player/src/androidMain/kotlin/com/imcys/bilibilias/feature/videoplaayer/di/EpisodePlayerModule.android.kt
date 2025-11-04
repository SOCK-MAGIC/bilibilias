package com.imcys.bilibilias.feature.videoplaayer.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val EpisodePlayerInternalModule: Module = module {
    factory<MediaPlayerFactory> {
        AndroidMediaPlayerFactory(androidContext())
    }
}