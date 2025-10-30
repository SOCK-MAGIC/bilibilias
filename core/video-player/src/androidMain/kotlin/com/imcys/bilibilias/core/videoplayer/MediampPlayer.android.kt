package com.imcys.bilibilias.core.videoplayer

import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import org.openani.mediamp.MediampPlayer

actual suspend fun MediampPlayer.playUri(uris: List<String>) {
    val exoPlayer = impl as ExoPlayer
    val provider = MediaSourceProvider()
    val source = provider.create(uris)
    exoPlayer.setMediaSource(source)
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