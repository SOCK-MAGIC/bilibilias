package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetEpisodeInfoUseCase(
    private val parseBilibiliId: ParseBilibiliIdUseCase,
    private val getPgcEpisodeInfo: GetPgcEpisodeInfoUseCase,
    private val getUgcEpisodeInfo: GetUgcEpisodeInfoUseCase,
    private val getPugvEpisodeInfo: GetPugvEpisodeInfoUseCase,
    private val api: BilibiliApi,
) {
    suspend operator fun invoke(query: String): Flow<EpisodeCacheListState?> {
        return when (val result = parseBilibiliId(query)) {
            is ParseBilibiliIdUseCase.MatchResult.UgcMatch -> getUgcEpisodeInfo(result.id)
            is ParseBilibiliIdUseCase.MatchResult.PgcMatch -> getPgcEpisodeInfo(result.id)
            is ParseBilibiliIdUseCase.MatchResult.Cheese -> getPugvEpisodeInfo(result.id)
            ParseBilibiliIdUseCase.MatchResult.NoMatch -> flowOf(null)
        }
    }
}