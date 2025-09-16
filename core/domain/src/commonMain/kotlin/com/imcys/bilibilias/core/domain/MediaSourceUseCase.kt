package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datastore.model.AudioQuality
import com.imcys.bilibilias.core.datastore.model.Resolution
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.MediaAsset
import com.imcys.bilibilias.core.domain.model.TrackInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaSourceUseCase(
    private val api: BilibiliApi
) {
    suspend operator fun invoke(
        request: EpisodeCacheRequest
    ): MediaAsset {
        return withContext(Dispatchers.IO) {
            val episodeCacheState = request.cacheState
            val playbackInfo =
                api.getPlayUrl(episodeCacheState.episodeId, episodeCacheState.episodeSubId)

            playbackInfo.dash?.let { dashData ->
                MediaAsset(
                    videoStreams = dashData.video.mapNotNull { video ->
                        Resolution.videoResolutions.firstOrNull { it.id == video.id }
                            ?.let { resolution ->
                                TrackInfo(
                                    resolution.id,
                                    resolution.displayName,
                                    video.primaryBackupUrls + video.secondaryBackupUrls,
                                    codecs = video.codecs
                                )
                            }
                    },
                    audioStreams = dashData.audio.mapNotNull { audio ->
                        AudioQuality.fromCode(audio.id)?.let { audioQuality ->
                            TrackInfo(
                                audioQuality.code,
                                audioQuality.description,
                                audio.primaryBackupUrls + audio.secondaryBackupUrls,
                                audio.codecs,
                            )
                        }
                    }
                )
            } ?: playbackInfo.durl?.let { durlData ->
                MediaAsset(
                    videoStreams = durlData.map { video ->
                        TrackInfo(
                            streamId = null,
                            trackLabel = "未知",
                            urls = video.backupUrl,
                            codecs = null,
                        )
                    },
                    audioStreams = emptyList()
                )
            }
            ?: throw MediaSourceNotFoundException("No DASH or DURL stream found for bvid: ${episodeCacheState.episodeId}, cid: ${episodeCacheState.episodeSubId}")
        }
    }
}

class MediaSourceNotFoundException(message: String) : Exception(message)
