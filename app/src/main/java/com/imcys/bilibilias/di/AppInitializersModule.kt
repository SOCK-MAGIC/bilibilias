package com.imcys.bilibilias.di

import android.app.Application
import com.imcys.common.appinitializer.AppInitializers
import com.imcys.datastore.di.FastKVConfigAppInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 添加容器注入
 */
@Module
@InstallIn(SingletonComponent::class)
class AppInitializersModule {

    @Provides
    fun provideAppInitializers(application: Application): Set<AppInitializers> = setOf(
        FastKVConfigAppInitializer(),
    )
}
