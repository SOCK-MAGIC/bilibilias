package com.imcys.bilibilias.core.domain.model

data class MediaAsset(
    val videoStreams: List<TrackInfo>,
    val audioStreams: List<TrackInfo>,
)

data class TrackInfo(
    val streamId: Int?,
    val trackLabel: String,
    val urls: List<String>,
    val codecs: String?,
)