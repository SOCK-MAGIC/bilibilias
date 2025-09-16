package com.imcys.bilibilias.core.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dash(
    @SerialName("audio")
    val audio: List<MediaTrack>,
    @SerialName("dolby")
    val dolby: Dolby,
    @SerialName("duration")
    val duration: Int,
    @SerialName("flac")
    val flac: Flac? = null,
    @SerialName("video")
    val video: List<MediaTrack>,
) {
    val combinedAudioSources = buildList {
        addAll(audio)
        flac?.audio?.let { add(it) }
        dolby.audio?.let { addAll(it) }
    }

    @Serializable
    data class Dolby(
        @SerialName("audio")
        val audio: List<MediaTrack>? = null,
    )

    @Serializable
    data class Flac(
        @SerialName("audio")
        val audio: MediaTrack,
    )

    @Serializable
    data class SupportFormat(
        @SerialName("codecs")
        val codecs: List<String>?,
        @SerialName("display_desc")
        val displayDesc: String,
        @SerialName("format")
        val format: String,
        @SerialName("new_description")
        val newDescription: String,
        @SerialName("quality")
        val quality: Int,
        @SerialName("superscript")
        val superscript: String,
    )
}