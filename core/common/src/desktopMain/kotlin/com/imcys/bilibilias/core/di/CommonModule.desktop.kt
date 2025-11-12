package com.imcys.bilibilias.core.di

import com.imcys.bilibilias.core.platform.AppDirs
import com.imcys.bilibilias.core.platform.DesktopAppDirs
import com.imcys.bilibilias.core.storage.DesktopMediaStoreAccess
import com.imcys.bilibilias.core.storage.MediaStoreAccess
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val CommonModule: Module = module {
    factoryOf(::DesktopMediaStoreAccess) bind MediaStoreAccess::class
    singleOf(::DesktopAppDirs) bind AppDirs::class
}