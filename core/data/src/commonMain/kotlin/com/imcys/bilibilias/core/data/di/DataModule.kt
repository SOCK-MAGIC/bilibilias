package com.imcys.bilibilias.core.data.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.imcys.bilibilias.core.data.DataStoreMediaCacheDataSource
import com.imcys.bilibilias.core.data.MediaCacheDataSource
import com.imcys.bilibilias.core.data.model.MediaCacheSave
import com.imcys.bilibilias.core.data.util.ErrorMonitor
import com.imcys.bilibilias.core.data.util.StateErrorMonitor
import com.imcys.bilibilias.core.datastore.asDataStoreSerializer
import com.imcys.bilibilias.core.datastore.new
import com.imcys.bilibilias.core.datastore.resolveDataStoreFile
import com.imcys.bilibilias.core.di.applicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.builtins.ListSerializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DataModule = module {
    singleOf(::StateErrorMonitor) bind ErrorMonitor::class
    single<MediaCacheDataSource> {
        DataStoreMediaCacheDataSource(
            store = DataStoreFactory.new(
                serializer = ListSerializer(MediaCacheSave.serializer()).asDataStoreSerializer { emptyList() },
                corruptionHandler = ReplaceFileCorruptionHandler { emptyList() },
                produceFile = { resolveDataStoreFile("media_cache_storage") },
                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
            )
        )
    }
}