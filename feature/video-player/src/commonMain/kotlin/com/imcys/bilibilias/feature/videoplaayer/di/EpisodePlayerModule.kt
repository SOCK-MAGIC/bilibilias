package com.imcys.bilibilias.feature.videoplaayer.di

import com.imcys.bilibilias.core.videoplayer.di.VideoPlayerModule
import com.imcys.bilibilias.feature.videoplaayer.EpisodePlayerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val EpisodePlayerModule: Module = module {
    viewModel {
        EpisodePlayerViewModel(
            aid = it[0],
            bvid = it[1],
            cid = it[2],
            mediaCacheStorage = get(),
            api = get(),
            playerFactory = get(),
            audioManager = get(),
            brightnessManager = get(),
        )
    }
    includes(VideoPlayerModule)
    includes(EpisodePlayerInternalModule)
}

internal expect val EpisodePlayerInternalModule: Module
