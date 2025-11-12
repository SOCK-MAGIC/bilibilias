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

class GetPugvEpisodeInfoUseCase(
    private val api: BilibiliApi,
    private val mediaCacheStorage: MediaCacheDataSource,
) {

    operator fun invoke(id: PgcId): Flow<EpisodeCacheListState?> {
        val detailFlow = flowFromSuspend {
            when (id) {
                is PgcId.Ep -> TODO()
                is PgcId.Ss -> api.getCourseInfo(id.id)
            }
        }

        return detailFlow.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->
            val cachedItemsMap = cachedItemsList.associateBy {
                it.origin.aid to it.origin.cid
            }
            val cacheStates = detail.episodes.map { episode ->
                val isCached = cachedItemsMap.containsKey(episode.aid to episode.cid)
                val cacheStatus = if (isCached) {
                    EpisodeCacheStatus.Cached
                } else {
                    EpisodeCacheStatus.NotCached
                }
                // id 是 epid
                EpisodeCacheState(
                    episodeId = episode.id.toString(),
                    episodeSubId = episode.cid,
                    episodeAliasId = episode.aid,
                    title = episode.title,
                    index = episode.index,
                    duration = episode.duration,
                    width = 1920,
                    height = 1080,
                    cacheStatus = cacheStatus
                )
            }

            EpisodeCacheListState(detail.toEpisodeInfo(), cacheStates)
        }
    }
}