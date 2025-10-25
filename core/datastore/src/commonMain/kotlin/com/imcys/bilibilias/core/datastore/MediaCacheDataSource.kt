package com.imcys.bilibilias.core.datastore

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.core.datastore.model.EpisodeMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCachePartMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCacheSave
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

interface MediaCacheDataSource {
    val listFlow: Flow<List<MediaCacheSave>>
    suspend fun findCache(bvid: String, cid: Long): MediaCacheSave?
    suspend fun delete(episodeMetadata: EpisodeMetadata): Boolean

    suspend fun cacheEpisode(episodeData: MediaCacheSave)

    suspend fun updateMediaCacheMetadata(
        targetEpisodeKey: EpisodeMetadata,
        newPartMetadata: MediaCachePartMetadata
    )
}

internal class DataStoreMediaCacheDataSource(
    private val store: DataStore<List<MediaCacheSave>>,
    private val clock: Clock = Clock.System,
) : MediaCacheDataSource {

    override val listFlow = store.data
    override suspend fun findCache(bvid: String, cid: Long): MediaCacheSave? {
        val saves = store.data.first()
        return saves.find { episode ->
            episode.origin.bvid == bvid && episode.origin.cid == cid
        }
    }

    override suspend fun cacheEpisode(episodeData: MediaCacheSave) {
        val itemWithTimestamp = episodeData.copy(
            metadata = episodeData.metadata.copy(createdAt = clock.now())
        )
        store.updateData { list ->
            list + itemWithTimestamp
        }
    }

    override suspend fun updateMediaCacheMetadata(
        targetEpisodeKey: EpisodeMetadata,
        newPartMetadata: MediaCachePartMetadata
    ) {
        store.updateData { currentList ->
            currentList.map { episode ->
                if (isSameEpisode(episode, targetEpisodeKey)) {
                    // This is the episode we want to update
                    val updatedPartMetadataList = episode.metadata.metadata + newPartMetadata
                    val updatedEpisodeMetadata = episode.metadata.copy(
                        metadata = updatedPartMetadataList,
                        createdAt = clock.now()
                    )
                    episode.copy(metadata = updatedEpisodeMetadata)
                } else {
                    // This is not the episode we're looking for, return it as is
                    episode
                }
            }
        }
    }

    override suspend fun delete(episodeMetadata: EpisodeMetadata): Boolean {
        var deleted = false
        val key = "${episodeMetadata.bvid}-${episodeMetadata.cid}" // For logging
        logger.debug { "Attempting to delete $key" }
        store.updateData { list ->
            val originalSize = list.size
            val newList = list.filterNot { isSameEpisode(it, episodeMetadata) }
            deleted = newList.size < originalSize
            if (deleted) {
                logger.info { "Deleted $key from cache" } // Info if successful
            } else {
                logger.debug { "$key not found in cache for deletion" }
            }
            newList
        }
        return deleted
    }

    private fun isSameEpisode(
        cache: MediaCacheSave,
        episodeMetadata: EpisodeMetadata
    ): Boolean {
        return cache.origin == episodeMetadata
    }


    companion object {
        private val logger = logger<DataStoreMediaCacheDataSource>()
    }
}