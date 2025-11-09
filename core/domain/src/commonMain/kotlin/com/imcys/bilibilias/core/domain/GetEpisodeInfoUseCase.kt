package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetEpisodeInfoUseCase(
    private val parseBilibiliId: ParseBilibiliIdUseCase,
    private val getPgcEpisodeInfo: GetPgcEpisodeInfoUseCase,
    private val getUgcEpisodeInfo: GetUgcEpisodeInfoUseCase,
    private val api: BilibiliApi,
) {
    private val logger = logger<GetEpisodeInfoUseCase>()

    suspend operator fun invoke(query: String): Flow<EpisodeCacheListState?> {
        return when (val result = parseBilibiliId(query)) {
            is ParseBilibiliIdUseCase.MatchResult.Av -> getUgcEpisodeInfo(aid = result.id)
            is ParseBilibiliIdUseCase.MatchResult.Bv -> getUgcEpisodeInfo(bvid = result.id)
            is ParseBilibiliIdUseCase.MatchResult.Ep -> getPgcEpisodeInfo(epId = result.id)
            is ParseBilibiliIdUseCase.MatchResult.Ss -> getPgcEpisodeInfo(ssId = result.id)
            is ParseBilibiliIdUseCase.MatchResult.ShortLink -> fetchEpisodesViaRedirect(result.url)

            is ParseBilibiliIdUseCase.MatchResult.Cheese,
            ParseBilibiliIdUseCase.MatchResult.NoMatch -> flowOf(null)
        }
    }

    private suspend fun fetchEpisodesViaRedirect(text: String): Flow<EpisodeCacheListState?> {
        val redirectUrl = api.getRedirectUrl(text)
        return this(redirectUrl)
    }
}