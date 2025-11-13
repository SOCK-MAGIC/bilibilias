package com.imcys.bilibilias.core.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class MediaCacheSave(
    val key: EpisodeMetadata,
    val metadata: MediaCacheMetadata,
)
@Serializable
data class EpisodeMetadata(val aid: String, val bvid: String, val cid: Long)

@Serializable
data class MediaCacheMetadata(
    val metadata: List<MediaCachePartMetadata>,
    val createdAt: Instant = Clock.System.now(),
    val extra: Map<MetadataKey, String> = emptyMap(),
) {
    fun withExtra(other: Map<MetadataKey, String>): MediaCacheMetadata {
        return copy(
            extra = extra + other,
        )
    }
}

@Serializable
data class MediaCachePartMetadata(
    val fullPath: String
)

@Serializable
@JvmInline
value class MetadataKey(val value: String) {
    companion object {
        val ASS_FILE = MetadataKey("ass_file")
    }
}