package com.imcys.bilibilias.ui.player

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import org.openani.mediamp.MediampPlayer

/**
 * 一个用于创建 MediaSource 实例的单例工厂对象。
 */
@OptIn(UnstableApi::class)
object MediaSourceFactory {

    /**
     * 从多个 URI 创建一个组合的 MediaSource。
     * 这个函数非常灵活，可以通过传入不同的 dataSourceFactory 来支持本地文件、网络流等。
     *
     * @param uris 媒体资源的 URI 列表。
     * @param dataSourceFactory 用于创建数据源的工厂，例如 FileDataSource.Factory() 或 OkHttpDataSource.Factory()。
     * @return 一个组合了所有轨道的 MediaSource。
     */
    fun createCombinedMediaSource(
        uris: List<String>,
        dataSourceFactory: DataSource.Factory,
    ): MediaSource {
        // ProgressiveMediaSource.Factory 只需要创建一次
        val progressiveMediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

        val mediaSources = uris.map { uri ->
            progressiveMediaSourceFactory.createMediaSource(MediaItem.fromUri(uri))
        }.toTypedArray()

        return MergingMediaSource(*mediaSources)
    }
}

/**
 * 为 ExoPlayer 准备合并后的媒体轨道并开始缓冲。
 *
 * @param uris 媒体资源的 URI 列表。
 * @param dataSourceFactory 用于创建数据源的工厂。
 */
@OptIn(UnstableApi::class)
fun ExoPlayer.prepareWithTracks(
    uris: List<String>,
    dataSourceFactory: DataSource.Factory,
) {
    // 1. 使用工厂创建 MediaSource
    val combinedSource = MediaSourceFactory.createCombinedMediaSource(uris, dataSourceFactory)

    // 2. 设置给播放器并准备
    this.setMediaSource(combinedSource)
    this.prepare()
}

/**
 * [MediampPlayer] 的便捷扩展函数，内部会获取 ExoPlayer 实例并准备媒体。
 *
 * @param uris 媒体资源的 URI 列表。
 */

actual fun MediampPlayer.prepareWithTracks(
    uris: List<String>,
) {
    (this.impl as? ExoPlayer)?.prepareWithTracks(uris, FileDataSource.Factory())
}


