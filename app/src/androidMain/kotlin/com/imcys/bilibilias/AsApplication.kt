package com.imcys.bilibilias

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.util.DebugLogger
import com.imcys.bilibilias.core.ktor.client.createHttpClient
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.platform.AppDirs
import com.imcys.bilibilias.core.platform.createDirectories
import com.imcys.bilibilias.work.Sync
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AsApplication : Application(), SingletonImageLoader.Factory, KoinComponent {
    private val appDirs: AppDirs by inject()
    override fun onCreate() {
        super.onCreate()

        val defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            logger<AsApplication>().error(e) { "!!! FATAL !!!" }
            defaultUEH?.uncaughtException(t, e)
        }

        initKoin {
            androidContext(this@AsApplication)
            modules(commonModules())
            workManagerFactory()
        }
        Sync.initialize(this)
        BuildConfig.initDirectory()
        appDirs.createDirectories()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .components { add(KtorNetworkFetcherFactory(createHttpClient())) }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}