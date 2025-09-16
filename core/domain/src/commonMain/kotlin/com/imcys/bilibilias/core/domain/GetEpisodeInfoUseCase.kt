package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.datasource.model.Season
import com.imcys.bilibilias.core.datastore.MediaCacheDataSource
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheStatus
import com.imcys.bilibilias.core.domain.model.EpisodeInfo
import com.imcys.bilibilias.core.flow.flowFromSuspend
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.model.VideoType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetEpisodeInfoUseCase(
    private val mediaCacheStorage: MediaCacheDataSource,
    private val getIdFromTextUseCase: GetIdFromTextUseCase,
    private val api: BilibiliApi,
) {
    private val logger = logger<GetEpisodeInfoUseCase>()

    // curl 'https://api.bilibili.com/pgc/player/web/v2/playurl?avid=40452040&cid=29608643908&qn=127&fnver=0&fnval=4048&fourk=1'
    //-b 'xxx'
    //-H 'referer: https://www.bilibili.com'
    suspend operator fun invoke(query: String): Flow<EpisodeCacheListState?> {
        return when (val result = getIdFromTextUseCase(query)) {
            is GetIdFromTextUseCase.MatchResult.Bv -> bv(result.id)
            is GetIdFromTextUseCase.MatchResult.Av -> TODO()
            is GetIdFromTextUseCase.MatchResult.Http -> fetchEpisodesViaRedirect(result.text)
            is GetIdFromTextUseCase.MatchResult.Ep -> ep(result.id, true)
            is GetIdFromTextUseCase.MatchResult.Ss -> ep(result.id, false)
            GetIdFromTextUseCase.MatchResult.Empty -> flowOf(null)
        }
    }

    private fun ep(id: String, isEp: Boolean): Flow<EpisodeCacheListState?> {
        val seasonDetails = flowFromSuspend {
            if (isEp) {
                api.getSeasonDetailsByEpisodeId(id)
            } else {
                api.getSeasonDetailsBySeasonId(id)
            }
        }

        return seasonDetails.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->
            val episodeBvids = detail.episodes.map { it.bvid }.toSet()

            val cachedItemsByCid = mediaCacheStorage.listFlow.first()
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
                    index = episode.title.toIntOrNull() ?: (index + 1),
                    title = episode.showTitle,
                    cacheStatus = cacheStatus,
                )
            }
            EpisodeCacheListState(
                episodeInfo = detail.toEpisodeInfo(),
                episodes = states,
            )
        }
    }

    private suspend fun fetchEpisodesViaRedirect(text: String): Flow<EpisodeCacheListState?> {
        val redirectUrl = api.getRedirectUrl(text)
        return this(redirectUrl)
    }

    private fun bv(id: String): Flow<EpisodeCacheListState> {
        val detailFlow = flowFromSuspend { api.getVideoInfoDetail(id) }

        return detailFlow.combine(mediaCacheStorage.listFlow) { detail, cachedItemsList ->

            val cachedItemsByCid = mediaCacheStorage.listFlow.first()
                .filter { it.origin.bvid == detail.bvid }
                .associateBy { it.origin.cid }

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
                )
            }
            EpisodeCacheListState(
                episodeInfo = detail.toEpisodeInfo(),
                episodes = states,
            )
        }
    }

    private fun BiliVideoData.toEpisodeInfo(): EpisodeInfo {
        return EpisodeInfo(
            title = title,
            desc = desc,
            cover = pic,
            videoType = if (redirectUrl == null) VideoType.UGC else VideoType.PGC,
        )
    }

    private fun Season.toEpisodeInfo(): EpisodeInfo {
        return EpisodeInfo(
            title = seasonTitle,
            desc = evaluate,
            cover = cover,
            videoType = VideoType.PGC
        )
    }
}