package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.MediaTrack
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
                    val playbackInfo =
                        api.getUgcPlayUrl(
                            episodeCacheState.episodeId,
                            episodeCacheState.episodeSubId
                        )

                    playbackInfo.dash?.let { dashData ->
                        MediaAsset(
                            videoStreams = dashData.video.mapToTrackInfoWithQuality(VideoQualities.all),
                            audioStreams = dashData.audio.mapToTrackInfoWithQuality(AudioQualities.all)
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

                PGC -> {
                    val playUrl = api.getPgcPlayUrl(
                        episodeCacheState.episodeId,
                        episodeCacheState.episodeSubId
                    )

                    val dash = playUrl.videoInfo.dash
                    MediaAsset(
                        videoStreams = dash.video.mapToTrackInfoWithQuality(VideoQualities.all),
                        audioStreams = dash.audio.mapToTrackInfoWithQuality(AudioQualities.all)
                            .sortedByDescending { it.streamId }
                    )
                }

                PUGV -> TODO()
            }
        }
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
