package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.Dash
import com.imcys.bilibilias.core.datasource.model.MediaTrack
import com.imcys.bilibilias.core.datasource.model.UgcPlayUrl
import com.imcys.bilibilias.core.datastore.model.AudioQualities
import com.imcys.bilibilias.core.datastore.model.Quality
import com.imcys.bilibilias.core.datastore.model.VideoQualities
import com.imcys.bilibilias.core.domain.model.MediaAsset
import com.imcys.bilibilias.core.domain.model.SelectedEpisodeContext
import com.imcys.bilibilias.core.domain.model.TrackInfo
import com.imcys.bilibilias.core.model.VideoType.PGC
import com.imcys.bilibilias.core.model.VideoType.PUGV
import com.imcys.bilibilias.core.model.VideoType.UGC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaSourceUseCase(
    private val api: BilibiliApi
) {
    suspend operator fun invoke(
        request: SelectedEpisodeContext
    ): MediaAsset {
        return withContext(Dispatchers.IO) {
            val episodeCacheState = request.cacheState
            when (request.videoType) {
                UGC -> {
                    val playbackInfo = api.getUgcPlayUrl(
                        bvid = episodeCacheState.episodeId,
                        cid = episodeCacheState.episodeSubId
                    )
                    playbackInfo.dash?.let { dashData -> createMediaAssetFromDash(dashData) }
                        ?: playbackInfo.durl?.let { durlData -> createMediaAssetFromDurl(durlData) }
                        ?: throw MediaSourceNotFoundException(
                            "No DASH or DURL stream found for UGC video: bvid=${episodeCacheState.episodeId}, cid=${episodeCacheState.episodeSubId}"
                        )
                }

                PGC -> {
                    val playUrl = api.getPgcPlayUrl(
                        episodeCacheState.episodeId,
                        episodeCacheState.episodeSubId
                    )
                    createMediaAssetFromDash(playUrl.videoInfo.dash, sortAudioById = true)
                }

                PUGV -> TODO("Support for PUGV video type is not yet implemented.")
            }
        }
    }

    /**
     * 从 DASH 数据模型创建 MediaAsset。
     * @param dashData DASH 数据源。
     * @param sortAudioById 是否需要按 ID 降序排序音频轨道（PGC视频需要）。
     */
    private fun createMediaAssetFromDash(
        dashData: Dash,
        sortAudioById: Boolean = false
    ): MediaAsset {
        val videoStreams = dashData.video.mapToTrackInfoWithQuality(VideoQualities.all)
        var audioStreams = dashData.audio.mapToTrackInfoWithQuality(AudioQualities.all)

        if (sortAudioById) {
            audioStreams = audioStreams.sortedByDescending { it.streamId }
        }

        return MediaAsset(
            videoStreams = videoStreams,
            audioStreams = audioStreams
        )
    }

    /**
     * 从 DURL 数据模型创建 MediaAsset (适用于旧的 UGC 视频)。
     */
    private fun createMediaAssetFromDurl(durlData: List<UgcPlayUrl.Durl>): MediaAsset {
        val videoStreams = durlData.map { video ->
            TrackInfo(
                streamId = null,
                trackLabel = "默认",
                urls = video.backupUrl,
                codecs = null,
            )
        }
        return MediaAsset(
            videoStreams = videoStreams,
            audioStreams = emptyList()
        )
    }

    private fun List<MediaTrack>.mapToTrackInfoWithQuality(qualities: List<Quality>): List<TrackInfo> {
        return mapNotNull { track ->
            qualities.firstOrNull { it.id == track.id }?.let {
                TrackInfo(
                    it.id,
                    it.description,
                    track.primaryBackupUrls + track.secondaryBackupUrls,
                    track.codecs
                )
            }
        }
    }
}

class MediaSourceNotFoundException(message: String) : Exception(message)
