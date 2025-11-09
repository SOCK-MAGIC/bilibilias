package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.datasource.model.Season
import com.imcys.bilibilias.core.model.VideoType

fun BiliVideoData.toEpisodeInfo(): EpisodeInfo {
    return EpisodeInfo(
        title = title,
        desc = desc,
        cover = pic,
        videoType = if (redirectUrl == null) VideoType.UGC else VideoType.PGC,
    )
}

fun Season.toEpisodeInfo(): EpisodeInfo {
    return EpisodeInfo(
        title = seasonTitle,
        desc = evaluate,
        cover = cover,
        videoType = VideoType.PGC
    )
}