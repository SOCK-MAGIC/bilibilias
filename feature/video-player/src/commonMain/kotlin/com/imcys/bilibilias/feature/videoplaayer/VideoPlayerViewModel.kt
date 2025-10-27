package com.imcys.bilibilias.feature.videoplaayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.core.datastore.MediaCacheDataSource
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.result.Result
import com.imcys.bilibilias.core.result.asResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class VideoPlayerViewModel(
    private val compositeVideoId: String,
    private val mediaCacheStorage: MediaCacheDataSource,
) : ViewModel() {
    private val logger = logger<VideoPlayerViewModel>()

    val uiState = flow {
        val (bvid, cid) = parseVideoIdentifier(compositeVideoId)
            ?: throw IllegalArgumentException("Invalid video identifier format: $compositeVideoId")

        val cache = mediaCacheStorage.findCache(bvid, cid)
            ?: throw NoSuchElementException("Video not found in cache for ID: $compositeVideoId")

        val uris = cache.metadata.metadata.map { it.filePath.toString() }
        emit(PlayerUiState.Success(uris))
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> PlayerUiState.Error(
                    result.exception.message ?: "An unknown error occurred"
                )

                Result.Loading -> PlayerUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUiState.Loading
        )

    private fun parseVideoIdentifier(identifier: String): VideoIdentifier? {
        val parts = identifier.split('-')
        if (parts.size != 2) {
            logger.warn { "Invalid identifier format: '$identifier'. Expected 'bvid-cid'." }
            return null
        }

        val bvid = parts[0]
        val cid = parts[1].toLongOrNull() ?: run {
            logger.warn { "Failed to parse numeric part from identifier: '$identifier'" }
            return null
        }
        return VideoIdentifier(bvid, cid)
    }

    data class VideoIdentifier(val bvid: String, val cid: Long)
}