package com.imcys.bilibilias

import com.imcys.bilibilias.core.data.di.DataModule
import com.imcys.bilibilias.core.datasource.di.DataSourceModule
import com.imcys.bilibilias.core.datasource.utils.WbiInitializer
import com.imcys.bilibilias.core.di.CommonModule
import com.imcys.bilibilias.core.domain.UseCaseModule
import com.imcys.bilibilias.core.ffmpeg.MediaProcessorModule
import com.imcys.bilibilias.core.http.downloader.HttpDownloader
import com.imcys.bilibilias.core.http.downloader.di.HttpDownloaderModule
import com.imcys.bilibilias.di.NavigationModule
import com.imcys.bilibilias.feature.cache.di.CacheModule
import com.imcys.bilibilias.feature.search.di.SearchModule
import com.imcys.bilibilias.feature.settings.di.SettingsModule
import com.imcys.bilibilias.feature.videoplaayer.di.EpisodePlayerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        if (config != null) {
            includes(config)
        } else {
            printLogger()
        }
        modules(platformModule())
        startCommonKoinModule(koin.get())
    }
}

fun KoinApplication.commonModules() = module {
    includes(
        NavigationModule,
        CommonModule,
        DataModule,
        DataSourceModule,
        MediaProcessorModule,
        UseCaseModule,
        HttpDownloaderModule,
        CacheModule,
        SearchModule,
        SettingsModule,
        EpisodePlayerModule,
    )
}

fun KoinApplication.startCommonKoinModule(
    coroutineScope: CoroutineScope,
): KoinApplication {
    coroutineScope.launch {
        koin.get<HttpDownloader>().init()
    }
    coroutineScope.launch {
        koin.get<WbiInitializer>().initialize()
    }
    return this
}

expect fun KoinApplication.platformModule(): Module