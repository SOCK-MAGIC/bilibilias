package com.imcys.bilibilias.feature.cache.di

import com.imcys.bilibilias.feature.cache.CacheViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CacheModule = module {
    viewModelOf(::CacheViewModel)
}