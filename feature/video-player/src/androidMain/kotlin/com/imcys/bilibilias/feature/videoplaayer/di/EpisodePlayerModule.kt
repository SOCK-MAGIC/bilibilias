package com.imcys.bilibilias.feature.videoplaayer.di

import com.imcys.bilibilias.feature.videoplaayer.EpisodePlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val EpisodePlayerModule: Module = module {
    viewModel {
        EpisodePlayerViewModel(
            aid = it[0],
            bvid = it[1],
            cid = it[2],
            mediaCacheStorage = get(),
            api = get(),
            playerFactory = get(),
        )
    }
    factory<MediaPlayerFactory> {
        AndroidMediaPlayerFactory(androidContext())
    }
}