package com.imcys.bilibilias.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.TrackInfo

class EpisodeMediaSelector(
    // Initial state or dependencies can be passed here if needed
    // For example, if currentEpisodeIndex is fixed when the dialog is created:
    initialEpisodeIndex: Int,
    private val onCacheRequestCallback: (EpisodeCacheRequest) -> Unit
) {
    var showMediaSelector by mutableStateOf(false)
        private set // Only allow modification through methods

    var selectedVideoTrack by mutableStateOf<TrackInfo?>(null)
        private set

    var selectedAudioTrack by mutableStateOf<TrackInfo?>(null)
        private set

    // If currentEpisodeIndex is dynamic and set when an episode is clicked,
    // you might pass it to openDialog() or have a separate setter.
    // For simplicity, let's assume it's set when the dialog is "prepared".
    private var currentEpisodeIndex: Int = initialEpisodeIndex

    fun openDialog(episodeIndex: Int) { // Or pass relevant episode data
        currentEpisodeIndex = episodeIndex // Update if it's dynamic
        // Reset previous selections when opening the dialog
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
        // You can add validation here if needed
        // e.g., if (selectedVideoTrack == null && selectedAudioTrack == null) { /* show error */ return }

        val episodeCacheRequest = EpisodeCacheRequest(
            currentEpisodeIndex,
            selectedVideoTrack,
            selectedAudioTrack
        )
        onCacheRequestCallback(episodeCacheRequest)
        dismissDialog() // Dismiss after confirming
    }
}