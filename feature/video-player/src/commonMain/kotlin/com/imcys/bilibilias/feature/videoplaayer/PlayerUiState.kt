package com.imcys.bilibilias.feature.videoplaayer

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Success(
        val uris: List<String>,
    ) : PlayerUiState

    data class Error(val message: String) : PlayerUiState
}