package com.imcys.bilibilias.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideJson() = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}
