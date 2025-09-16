package com.imcys.bilibilias.core.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaTrack(
    @SerialName("base_url")
    val baseUrl: String,
    @SerialName("backup_url")
    val primaryBackupUrls: List<String>,
    @SerialName("backupUrl")
    val secondaryBackupUrls: List<String>,
    @SerialName("bandwidth")
    val bandwidth: Int,
    @SerialName("codecid")
    val codecid: Int,
    @SerialName("codecs")
    val codecs: String,
    @SerialName("frame_rate")
    val frameRate: String,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("sar")
    val sar: String,
    @SerialName("segment_base")
    val segmentBase: SegmentBase,
    @SerialName("start_with_sap")
    val startWithSap: Int,
    @SerialName("width")
    val width: Int,
) {
    @Serializable
    data class SegmentBase(
        @SerialName("index_range")
        val indexRange: String,
        @SerialName("initialization")
        val initialization: String,
    )
}