package com.imcys.bilibilias.core.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcPlayUrl(
    @SerialName("accept_description")
    val acceptDescription: List<String>,
    @SerialName("accept_format")
    val acceptFormat: String,
    @SerialName("accept_quality")
    val acceptQuality: List<Int>,
    @SerialName("dash")
    val dash: Dash? = null,
    @SerialName("durl")
    val durl: List<Durl>? = null,
    @SerialName("format")
    val format: String,
    @SerialName("from")
    val from: String,
    @SerialName("last_play_cid")
    val lastPlayCid: Long,
    @SerialName("last_play_time")
    val lastPlayTime: Int,
    @SerialName("message")
    val message: String,
    @SerialName("quality")
    val quality: Int,
    @SerialName("result")
    val result: String,
    @SerialName("seek_param")
    val seekParam: String,
    @SerialName("seek_type")
    val seekType: String,
    @SerialName("support_formats")
    val supportFormats: List<Dash.SupportFormat>,
    @SerialName("timelength")
    val timeLength: Int,
    @SerialName("video_codecid")
    val videoCodecId: Int,
) {
    @Serializable
    data class Durl(
        @SerialName("backup_url")
        val backupUrl: List<String>,
        @SerialName("length")
        val length: Int,
        @SerialName("order")
        val order: Int,
        @SerialName("size")
        val size: Int,
        @SerialName("url")
        val url: String,
    )
}