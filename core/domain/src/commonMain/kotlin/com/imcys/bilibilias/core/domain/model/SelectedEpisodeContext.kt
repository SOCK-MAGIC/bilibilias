package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.model.VideoType

data class SelectedEpisodeContext(
    val cacheState: EpisodeCacheState,
    val videoType: VideoType,
)

data class EpisodeCacheRequest(
    val index: Int,
    val videoTrack: TrackInfo?,
    val audioTrack: TrackInfo?,
)