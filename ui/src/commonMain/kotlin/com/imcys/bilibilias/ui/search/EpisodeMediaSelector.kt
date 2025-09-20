package com.imcys.bilibilias.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.TrackInfo

class EpisodeMediaSelector(
    private val onCacheRequestCallback: (EpisodeCacheRequest) -> Unit
) {
    var showMediaSelector by mutableStateOf(false)
        private set

    var selectedVideoTrack by mutableStateOf<TrackInfo?>(null)
        private set

    var selectedAudioTrack by mutableStateOf<TrackInfo?>(null)
        private set

    private var currentEpisodeIndex = 0

    fun openDialog(episodeIndex: Int) {
        currentEpisodeIndex = episodeIndex
        selectedVideoTrack = null
        selectedAudioTrack = null
        showMediaSelector = true
    }

    fun dismissDialog() {
        showMediaSelector = false
    }

    fun onVideoTrackSelected(track: TrackInfo?) {
        selectedVideoTrack = track
    }

    fun onAudioTrackSelected(track: TrackInfo?) {
        selectedAudioTrack = track
    }

    fun onConfirmSelection() {
        if (selectedVideoTrack == null && selectedAudioTrack == null) {
            dismissDialog()
            return
        }
        val episodeCacheRequest = EpisodeCacheRequest(
            currentEpisodeIndex,
            selectedVideoTrack,
            selectedAudioTrack
        )
        onCacheRequestCallback(episodeCacheRequest)
        dismissDialog()
    }
}