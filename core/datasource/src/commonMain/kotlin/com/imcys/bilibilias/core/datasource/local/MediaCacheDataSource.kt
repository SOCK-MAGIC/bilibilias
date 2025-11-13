package com.imcys.bilibilias.core.datasource.local

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.model.EpisodeMetadata
import com.imcys.bilibilias.core.model.MediaCacheSave
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface MediaCacheDataSource {
    val allCachesFlow: Flow<List<MediaCacheSave>>
    suspend fun get(key: EpisodeMetadata): MediaCacheSave?

    suspend fun save(cache: MediaCacheSave)

    suspend fun delete(key: EpisodeMetadata)
}

internal class DataStoreMediaCacheDataSource(
    private val store: DataStore<List<MediaCacheSave>>,
) : MediaCacheDataSource {

    override val allCachesFlow = store.data
    override suspend fun get(key: EpisodeMetadata): MediaCacheSave? {
        return store.data.first().find { it.key == key }
    }

    override suspend fun save(cache: MediaCacheSave) {
        store.updateData { currentList ->
            val indexToUpdate = currentList.indexOfFirst { it.key == cache.key }

            if (indexToUpdate != -1) {
                logger.info { "Updating cache for key: ${cache.key}" }
                currentList.toMutableList().apply { this[indexToUpdate] = cache }
            } else {
                logger.info { "Adding new cache for key: ${cache.key}" }
                currentList + cache
            }
        }
    }

    override suspend fun delete(key: EpisodeMetadata) {
        store.updateData { currentList ->
            val indexToDelete = currentList.indexOfFirst { it.key == key }

            if (indexToDelete != -1) {
                logger.info { "Deleting cache for key: $key" }
                currentList.toMutableList().apply { removeAt(indexToDelete) }
            } else {
                logger.warn { "Attempted to delete non-existent cache for key: $key" }
                currentList
            }
        }
    }
    companion object {
        private val logger = logger<DataStoreMediaCacheDataSource>()
    }
}