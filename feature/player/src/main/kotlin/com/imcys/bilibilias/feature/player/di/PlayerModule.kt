package com.imcys.bilibilias.feature.player.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PlayerModule {
    // @androidx.annotation.OptIn(UnstableApi::class)
    // @Provides
    // @Singleton
    // fun provideOkhttpDataSource(
    //     okhttpClient: OkHttpClient,
    // ): DataSource.Factory = OkHttpDataSource.Factory(okhttpClient)
    //     .setDefaultRequestProperties(mapOf("Referrer" to BILIBILI_URL))
    //     .setUserAgent(BROWSER_USER_AGENT)
    //     .setCacheControl(CacheControl.FORCE_CACHE)
}
