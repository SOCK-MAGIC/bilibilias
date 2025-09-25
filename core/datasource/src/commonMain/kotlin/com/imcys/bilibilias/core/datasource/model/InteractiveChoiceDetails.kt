package com.imcys.bilibilias.core.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InteractiveChoiceDetails(
    @SerialName("buvid")
    val buvid: String? = null,
    @SerialName("edge_id")
    val edgeId: Int,
    @SerialName("edges")
    val edges: Edges,
    @SerialName("is_leaf")
    @Serializable(with = IntAsBooleanSerializer::class)
    val isLeaf: Boolean,
    @SerialName("preload")
    val preload: Preload,
    @SerialName("story_list")
    val storyList: List<Story>,
    @SerialName("title")
    val title: String
) {
    @Serializable
    data class Edges(
        @SerialName("dimension")
        val dimension: Dimension,
        @SerialName("questions")
        val questions: List<Question>,
    ) {
        @Serializable
        data class Dimension(
            @SerialName("height")
            val height: Int,
            @SerialName("rotate")
            val rotate: Int,
            @SerialName("sar")
            val sar: String,
            @SerialName("width")
            val width: Int
        )

        @Serializable
        data class Question(
            @SerialName("choices")
            val choices: List<Choice>,
            @SerialName("duration")
            val duration: Int,
            @SerialName("id")
            val id: Int,
            @SerialName("pause_video")
            val pauseVideo: Int,
            @SerialName("start_time_r")
            val startTimeR: Int,
            @SerialName("title")
            val title: String,
            @SerialName("type")
            val type: Int
        ) {
            @Serializable
            data class Choice(
                @SerialName("cid")
                val cid: Long,
                @SerialName("condition")
                val condition: String,
                @SerialName("id")
                val id: Long,
                @SerialName("is_default")
                val isDefault: Int? = null,
                @SerialName("native_action")
                val nativeAction: String,
                @SerialName("option")
                val option: String,
                @SerialName("platform_action")
                val platformAction: String
            )
        }
    }

    @Serializable
    data class Preload(
        @SerialName("video")
        val video: List<Video>
    ) {
        @Serializable
        data class Video(
            @SerialName("aid")
            val aid: Long,
            @SerialName("cid")
            val cid: Long
        )
    }

    @Serializable
    data class Story(
        @SerialName("cid")
        val cid: Long,
        @SerialName("cover")
        val cover: String,
        @SerialName("cursor")
        val cursor: Int,
        @SerialName("edge_id")
        val edgeId: Int,
        @SerialName("is_current")
        val isCurrent: Int? = null,
        @SerialName("node_id")
        val nodeId: Int,
        @SerialName("start_pos")
        val startPos: Int,
        @SerialName("title")
        val title: String
    )
}