package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.model.VideoType

data class EpisodeCacheListState(
    val episodeInfo: EpisodeInfo,
    val episodes: List<EpisodeCacheState>,
)

data class EpisodeInfo(
    val title: String,
    val desc: String,
    val cover: String,
    val videoType: VideoType,
)

data class EpisodeCacheState(
    val episodeId: String,
    val episodeSubId: Long,
    val episodeAliasId: Long,
    val title: String,
    val index: Int,
    val duration: Int,
    val width: Int,
    val height: Int,
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