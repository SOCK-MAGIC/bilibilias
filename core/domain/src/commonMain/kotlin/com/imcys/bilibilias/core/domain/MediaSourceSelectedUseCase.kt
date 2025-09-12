package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.Cdn
import com.imcys.bilibilias.core.datasource.CdnType
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.datasource.model.VideoPlaybackInfo
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.EpisodeInfo
import com.imcys.bilibilias.core.domain.model.MediaStreamMetadata
import com.imcys.bilibilias.core.domain.model.Resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

typealias MediaStreamTransformCondition = (Map<Int, List<MediaStreamMetadata>>) -> MediaStreamMetadata

class MediaSourceSelectedUseCase(
    private val api: BilibiliApi,
) {
    suspend operator fun invoke(
        request: EpisodeCacheRequest
    ): EpisodeInfo {
        return withContext(Dispatchers.IO) {
            val episodeCacheState = request.cacheState
            val detailDeferred = async { api.getVideoInfoDetail(episodeCacheState.episodeId) }
            val playUrlDeferred =
                async {
                    api.getPlayUrl(
                        episodeCacheState.episodeId,
                        episodeCacheState.episodeSubId
                    )
                }

            val detail = detailDeferred.await()
            val playUrl = playUrlDeferred.await()

            val dash = playUrl.dash
            val durl = playUrl.durl

            when {
                dash != null -> processDashStream(dash, request, detail)
                durl != null -> processDurlStream(durl, detail)
                else -> throw Exception("No DASH or DURL streams found for bvid: ${detail.bvid}, cid: ${detail.cid}")
            }
        }
    }

    private fun processDurlStream(
        durl: List<VideoPlaybackInfo.Durl>,
        detail: BiliVideoData
    ): EpisodeInfo {
        val s = MediaStreamMetadata(
            0,
            durl.flatMap { it.backupUrl + it.url }
                .mapNotNull { Cdn.parse(it) }
                .filterNot { it.type == CdnType.MCDN })

        return EpisodeInfo(
            bvid = detail.bvid,
            cid = detail.cid,
            title = detail.title,
            urls = listOf(s),
        )
    }

    private fun processDashStream(
        dash: VideoPlaybackInfo.Dash,
        request: EpisodeCacheRequest,
        detail: BiliVideoData
    ): EpisodeInfo {
        val audioUrl =
            dash.combinedAudioSources.applyMediaStreamTransformation { streamMap ->
                streamMap.values.flatten().maxBy { it.id }
            }
        val videoUrl = dash.video.applyMediaStreamTransformation { streamMap ->
            val preferredResolutionMetadata = streamMap[request.videoQuality]
            preferredResolutionMetadata?.maxBy { it.codecId }
                ?: streamMap.values.flatten().maxWith(compareBy { it.id })
        }
        return EpisodeInfo(
            bvid = detail.bvid,
            cid = detail.cid,
            title = detail.title,
            urls = listOf(audioUrl, videoUrl)
        )
    }

    private fun List<VideoPlaybackInfo.AudioOrVideo>.applyMediaStreamTransformation(
        action: MediaStreamTransformCondition
    ): MediaStreamMetadata {
        val mediaStreamMetadataMap = this.map { playbackInfo ->
            val allBackupUrls = playbackInfo.primaryBackupUrls + playbackInfo.secondaryBackupUrls
            val cdnResources = allBackupUrls.mapNotNull { url -> Cdn.parse(url) }
                .filterNot { it.type == CdnType.MCDN }
            MediaStreamMetadata(
                playbackInfo.id,
                cdnResources,
                playbackInfo.codecid,
                Resolution.values().find { it.id == playbackInfo.id }?.displayName ?: ""
            )
        }.groupBy { it.id }
        return action(mediaStreamMetadataMap)
    }
}