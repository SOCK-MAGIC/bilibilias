package com.imcys.bilibilias.feature.videoplaayer.di

import com.imcys.bilibilias.feature.videoplaayer.EpisodePlayerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.openani.mediamp.MediampPlayer

actual val EpisodePlayerModule: Module = module {
    viewModel {
        EpisodePlayerViewModel(it[0], get(), get())
    }
    single<MediampPlayer> { MediampPlayer(Unit) }
}