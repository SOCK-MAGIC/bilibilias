package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.model.EpisodeMetadata
import com.imcys.bilibilias.core.model.MediaCacheMetadata
import com.imcys.bilibilias.core.utils.FileStats

data class CacheEpisodeState(
    val episodeMetadata: EpisodeMetadata,
    val mediaCacheMetadata: MediaCacheMetadata,
    val fileStats: FileStats,
    val canPlay: Boolean,
    val canMux: Boolean,
)