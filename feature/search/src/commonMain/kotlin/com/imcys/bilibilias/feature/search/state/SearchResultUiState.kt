package com.imcys.bilibilias.feature.search.state

import com.imcys.bilibilias.core.domain.model.EpisodeCacheListState
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.EpisodeInfo

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState

    data class LoadFailed(val message: String) : SearchResultUiState

    data class Success(
        val episodeInfo: EpisodeInfo,
        val episodes: List<EpisodeCacheState>,
        val episodeCacheListState: EpisodeCacheListState,
    ) : SearchResultUiState
}