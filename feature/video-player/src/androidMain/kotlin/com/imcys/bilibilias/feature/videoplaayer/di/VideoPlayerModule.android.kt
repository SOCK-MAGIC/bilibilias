package com.imcys.bilibilias.feature.videoplaayer.di

import com.imcys.bilibilias.feature.videoplaayer.VideoPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.openani.mediamp.MediampPlayer

actual val VideoPlayerModule: Module = module {
    viewModel {
        VideoPlayerViewModel(it[0], get(), get())
    }
    single<MediampPlayer> { MediampPlayer(androidContext()) }
}