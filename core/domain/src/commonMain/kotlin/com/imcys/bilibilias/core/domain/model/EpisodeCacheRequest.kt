package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.model.VideoType

data class EpisodeCacheRequest(
    val cacheState: EpisodeCacheState,
    val videoType: VideoType,
)