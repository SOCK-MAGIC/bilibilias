package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.local.MediaCacheDataSource
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheStatus
import com.imcys.bilibilias.core.domain.model.PgcId
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
    operator fun invoke(pgcId: PgcId): Flow<EpisodeCacheListState?> {
        val seasonDetails = flowFromSuspend {
            when (pgcId) {
                is PgcId.Ep -> api.getSeasonDetailsByEpisodeId(pgcId.id)
                is PgcId.Ss -> api.getSeasonDetailsBySeasonId(pgcId.id)
            }
        }

        return seasonDetails.combine(mediaCacheStorage.allCachesFlow) { detail, cachedItemsList ->
                val episodeBvids = detail.episodes.map { it.bvid }.toSet()

                val cachedItemsByCid = cachedItemsList
                    .filter { cachedItem -> cachedItem.key.bvid in episodeBvids }
                    .associateBy { it.key.cid }

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
        }
    }
}