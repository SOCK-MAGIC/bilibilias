package com.imcys.bilibilias.danmaku.api

data class DanmakuInfo(
    val id: String,
    val content: DanmakuContent,
) {
    val playTimeMillis get() = content.playTimeMillis
    val color get() = content.color
    val text get() = content.text
    val location get() = content.location
}

data class DanmakuContent(
    val playTimeMillis: Long, // in milliseconds
    val color: Int, // RGB
    val text: String,
    val location: DanmakuLocation,
)

enum class DanmakuLocation {
    TOP,
    BOTTOM,

    /**
     * Floating
     */
    NORMAL,
}