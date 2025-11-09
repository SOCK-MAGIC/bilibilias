package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.data.MediaCacheDataSource
import com.imcys.bilibilias.core.data.model.MediaCacheSave
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheStatus
import com.imcys.bilibilias.core.domain.model.toEpisodeInfo
import com.imcys.bilibilias.core.flow.flowFromSuspend
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
    operator fun invoke(aid: String? = null, bvid: String? = null): Flow<EpisodeCacheListState?> {
        val detailFlow = flowFromSuspend {
            when {
                aid != null -> api.getVideoDetailsByAid(aid.toLong())
                bvid != null -> api.getVideoDetailsByBvid(bvid)
                else -> null
            }
        }

        return detailFlow.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->
            if (detail != null) {
                val cachedItemsByCid = cachedItemsList
                    .filter { it.origin.bvid == detail.bvid }
                    .associateBy { it.origin.cid }
                if (detail.rights.isSteinGate) {
                    processInteractiveVideo(detail, cachedItemsByCid)
                } else {
                    processRegularVideo(detail, cachedItemsByCid)
                }
            } else null
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
        // TODO: 修改这个 use case
        getInteractVideoUseCase(detail.aid, detail.bvid, detail.cid)

        val states =
            getInteractVideoUseCase.getSortedNodes().mapIndexed { index, node ->
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
