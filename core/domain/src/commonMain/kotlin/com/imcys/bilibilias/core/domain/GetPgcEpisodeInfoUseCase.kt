package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.data.MediaCacheDataSource
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheStatus
import com.imcys.bilibilias.core.domain.model.toEpisodeInfo
import com.imcys.bilibilias.core.flow.flowFromSuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * 专门用于获取 PGC (番剧、影视等) 内容的剧集信息。
 */
class GetPgcEpisodeInfoUseCase(
    private val mediaCacheStorage: MediaCacheDataSource,
    private val api: BilibiliApi,
) {
    operator fun invoke(epId: String? = null, ssId: String? = null): Flow<EpisodeCacheListState?> {
        val seasonDetails = flowFromSuspend {
            when {
                epId != null -> api.getSeasonDetailsByEpisodeId(epId)
                ssId != null -> api.getSeasonDetailsBySeasonId(ssId)
                else -> null
            }
        }

        return seasonDetails.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->
            if (detail != null) {
                val episodeBvids = detail.episodes.map { it.bvid }.toSet()

                val cachedItemsByCid = cachedItemsList
                    .filter { cachedItem -> cachedItem.origin.bvid in episodeBvids }
                    .associateBy { it.origin.cid }

                val states = detail.episodes.mapIndexed { index, episode ->
                    val cid = episode.cid
                    val cacheStatus = if (cachedItemsByCid.containsKey(cid)) {
                        EpisodeCacheStatus.Cached
                    } else {
                        EpisodeCacheStatus.NotCached
                    }
                    EpisodeCacheState(
                        episodeId = episode.bvid,
                        episodeSubId = cid,
                        episodeAliasId = episode.aid,
                        index = index + 1,
                        title = episode.showTitle,
                        cacheStatus = cacheStatus,
                        duration = episode.duration,
                        width = episode.dimension.width,
                        height = episode.dimension.height,
                    )
                }
                EpisodeCacheListState(
                    episodeInfo = detail.toEpisodeInfo(),
                    episodes = states,
                )
            } else null
        }
    }
}