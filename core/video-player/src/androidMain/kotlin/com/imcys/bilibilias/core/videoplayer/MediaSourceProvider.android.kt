package com.imcys.bilibilias.core.videoplayer

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

actual class MediaSourceProvider {

    fun create(url: String): MediaSource {
        return create(Uri.parse(url))
    }

    fun create(uri: Uri): MediaSource {
        val mediaItem = MediaItem.fromUri(uri)
        return ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(mediaItem)
    }

    fun create(uris: List<String>): MediaSource {
        val sources = uris.map { create(it) }
        return MergingMediaSource(*sources.toTypedArray())
    }
}