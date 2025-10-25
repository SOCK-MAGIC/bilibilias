package com.imcys.bilibilias.logic.player

import androidx.lifecycle.ViewModel
import com.imcys.bilibilias.core.datastore.MediaCacheDataSource
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.result.Result
import com.imcys.bilibilias.core.result.asResult
import com.imcys.bilibilias.logic.stateInViewModelScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PlayerViewModel(
    private val compositeVideoId: String,
    private val mediaCacheStorage: MediaCacheDataSource,
) : ViewModel() {
    private val logger = logger<PlayerViewModel>()

    val uiState = flowOf(compositeVideoId)
        .flatMapLatest {
            val identifier = parseVideoIdentifier(it)
            if (identifier == null) {
                flowOf(PlayerUiState.Error("Invalid video identifier format."))
            } else {
                val cache = mediaCacheStorage.findCache(identifier.first, identifier.second)
                if (cache != null) {
                    flowOf(PlayerUiState.Success(cache))
                } else {
                    flowOf(PlayerUiState.Error("Invalid video identifier format."))
                }
            }
        }.asResult()
        .map { result ->
            when (result) {
                is Result.Error -> PlayerUiState.Error(result.exception.message ?: "Unknown error")
                Result.Loading -> PlayerUiState.Loading
                is Result.Success -> {
                    logger.debug { result.data.toString() }
                    result.data
                }
            }
        }
        .stateInViewModelScope(PlayerUiState.Loading)

    private fun parseVideoIdentifier(identifier: String): Pair<String, Long>? {
        return try {
            val parts = identifier.split('-')
            if (parts.size == 2) {
                val bvid = parts[0]
                val cid = parts[1].toLong()
                bvid to cid
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            logger.warn(e) { "Failed to parse video identifier: $identifier" }
            null
        }
    }
}