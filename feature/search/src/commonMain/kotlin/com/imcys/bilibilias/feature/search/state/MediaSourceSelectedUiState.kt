package com.imcys.bilibilias.feature.search.state

import com.imcys.bilibilias.core.domain.model.MediaAsset

sealed interface MediaSourceSelectedUiState {
    data class Success(val asset: MediaAsset) : MediaSourceSelectedUiState
    data class LoadFailed(val message: String?) : MediaSourceSelectedUiState
    data object Loading : MediaSourceSelectedUiState
}