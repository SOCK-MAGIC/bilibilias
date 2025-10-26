package com.imcys.bilibilias.feature.videoplaayer.di

import com.imcys.bilibilias.feature.videoplaayer.VideoPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val VideoPlayerModule = module {
    viewModel {
        VideoPlayerViewModel(it[0], get())
    }
}
