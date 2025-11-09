package com.imcys.bilibilias.core.videoplayer

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import org.openani.mediamp.MediampPlayer

actual suspend fun MediampPlayer.playUri(uris: List<String>) {
    val exoPlayer = impl as ExoPlayer
    val sources = uris.map {
        val mediaItem = MediaItem.fromUri(it)
        ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(mediaItem)
    }
    val mediaSource = MergingMediaSource(*sources.toTypedArray())
    exoPlayer.setMediaSource(mediaSource)
    exoPlayer.addListener(object : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            if (!timeline.isEmpty) {
                val durationMs = exoPlayer.duration
                TimelineState._durationMillis.value = durationMs
            }
        }
    })
    exoPlayer.prepare()
}