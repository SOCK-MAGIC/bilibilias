package com.imcys.bilibilias.core.data.di

import com.imcys.bilibilias.core.data.MediaCacheRepository
import com.imcys.bilibilias.core.data.util.ErrorMonitor
import com.imcys.bilibilias.core.data.util.StateErrorMonitor
import kotlinx.io.files.SystemFileSystem
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DataModule = module {
    singleOf(::StateErrorMonitor) bind ErrorMonitor::class
    single { MediaCacheRepository(SystemFileSystem) }
}