package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.data.MediaCacheDataSource
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.PgcId
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
                is PgcId.Ep -> api.getCourseInfo(id.id)
                is PgcId.Ss -> TODO()
            }
        }

        return detailFlow.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->
            val saves = cachedItemsList.filter { cacheSave ->
                detail.episodes.find { it.aid == cacheSave.origin.aid && it.cid == cacheSave.origin.cid } != null
            }

            val cachedItemsByCid = cachedItemsList
                .filter { it.origin.aid == 0L }
//                    .associateBy { it.origin.cid }


            null
        }
    }
}