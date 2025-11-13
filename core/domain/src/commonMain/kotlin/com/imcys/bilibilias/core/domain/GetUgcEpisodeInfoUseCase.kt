package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.local.MediaCacheDataSource
import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheStatus
import com.imcys.bilibilias.core.domain.model.UgcId
import com.imcys.bilibilias.core.domain.model.toEpisodeInfo
import com.imcys.bilibilias.core.flow.flowFromSuspend
import com.imcys.bilibilias.core.model.MediaCacheSave
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * 专门用于获取 UGC (用户投稿视频) 内容的剧集信息。
 */
class GetUgcEpisodeInfoUseCase(
    private val mediaCacheStorage: MediaCacheDataSource,
    private val api: BilibiliApi,
    private val getInteractVideoUseCase: GetInteractVideoUseCase
) {
    operator fun invoke(ugcId: UgcId): Flow<EpisodeCacheListState?> {
        val detailFlow = flowFromSuspend {
            when (ugcId) {
                is UgcId.Aid -> api.getVideoDetailsByAid(ugcId.id.toLong())
                is UgcId.Bvid -> api.getVideoDetailsByBvid(ugcId.id)
            }
        }

        return detailFlow.combine(mediaCacheStorage.allCachesFlow) { detail, cachedItemsList ->
            val cachedItemsByCid = cachedItemsList
                .filter { it.key.bvid == detail.bvid }
                .associateBy { it.key.cid }
            if (detail.rights.isSteinGate) {
                processInteractiveVideo(detail, cachedItemsByCid)
            } else {
                processRegularVideo(detail, cachedItemsByCid)
            }
        }
    }

    /**
     * 处理普通多P视频的逻辑。
     */
    private fun processRegularVideo(
        detail: BiliVideoData,
        cachedItemsByCid: Map<Long, MediaCacheSave>
    ): EpisodeCacheListState {
        val states = detail.pages.map { page ->
            val cid = page.cid
            val cacheStatus = if (cachedItemsByCid.containsKey(cid)) {
                EpisodeCacheStatus.Cached
            } else {
                EpisodeCacheStatus.NotCached
            }
            EpisodeCacheState(
                episodeId = detail.bvid,
                episodeSubId = cid,
                episodeAliasId = detail.aid,
                index = page.page,
                title = page.part,
                cacheStatus = cacheStatus,
                duration = page.duration.toInt(),
                width = detail.dimension.width,
                height = detail.dimension.height,
            )
        }
        return EpisodeCacheListState(
            episodeInfo = detail.toEpisodeInfo(),
            episodes = states,
        )
    }

    /**
     * 处理互动视频的逻辑。
     */
    private suspend fun processInteractiveVideo(
        detail: BiliVideoData,
        cachedItemsByCid: Map<Long, MediaCacheSave>
    ): EpisodeCacheListState {
        val nodes = getInteractVideoUseCase(detail.aid, detail.bvid, detail.cid)

        val states = nodes.mapIndexed { index, node ->
            val cid = node.cid
            val cacheStatus = if (cachedItemsByCid.containsKey(cid)) {
                EpisodeCacheStatus.Cached
            } else {
                EpisodeCacheStatus.NotCached
            }
            EpisodeCacheState(
                episodeId = detail.bvid,
                episodeSubId = cid,
                episodeAliasId = detail.aid,
                index = index + 1,
                title = node.title,
                cacheStatus = cacheStatus,
                duration = 0,
                width = node.width,
                height = node.height,
            )
        }
        return EpisodeCacheListState(
            episodeInfo = detail.toEpisodeInfo(),
            episodes = states,
        )
    }
}
