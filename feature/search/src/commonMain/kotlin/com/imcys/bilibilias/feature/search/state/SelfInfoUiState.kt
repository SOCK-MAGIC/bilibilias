package com.imcys.bilibilias.feature.search.state

import com.imcys.bilibilias.core.datastore.model.SelfInfo

sealed interface SelfInfoUiState {
    data object Loading : SelfInfoUiState
    data object Guest : SelfInfoUiState
    data class Success(val selfInfo: SelfInfo) : SelfInfoUiState
}
