package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.datasource.CdnResource

data class EpisodeCacheListState(
    val episodeInfo: EpisodeInfo,
    val episodes: List<EpisodeCacheState>,
)

data class EpisodeInfo(
    val title: String,
    val desc: String,
    val cover: String,
)

data class EpisodeCacheState(
    val episodeId: String,
    val episodeSubId: Long,
    val episodeAliasId: Long,
    val title: String,
    val index: Int,
    val cacheStatus: EpisodeCacheStatus,
)

data class MediaStream(
    val id: Int,
    val description: String,
    val urls: List<String>,
    val codecId: Int = 0,
)

sealed interface EpisodeCacheStatus {

    /**
     * At least one cache is fully downloaded.
     */
    data object Cached : EpisodeCacheStatus

    /**
     * No cache is fully downloaded, but at least one cache is downloading.
     */
    data object Caching : EpisodeCacheStatus

    data object NotCached : EpisodeCacheStatus
}

/**
 * [codecId] audio always 0, video include 7/avc 12/hevc 13/av1
 */
data class MediaStreamMetadata(
    val id: Int,
    val backupUrl: List<CdnResource>,
    val codecId: Int = 0,
    val description: String = ""
)