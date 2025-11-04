package com.imcys.bilibilias.feature.videoplaayer.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.openani.mediamp.MediampPlayer

internal actual val EpisodePlayerInternalModule: Module = module {
    single<MediampPlayer> { MediampPlayer(Unit) }
}