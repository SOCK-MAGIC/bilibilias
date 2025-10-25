package com.imcys.bilibilias.logic.player

import com.imcys.bilibilias.core.datastore.model.MediaCacheSave

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Success(val cacheSave: MediaCacheSave) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}