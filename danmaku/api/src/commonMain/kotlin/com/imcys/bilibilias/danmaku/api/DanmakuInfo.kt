package com.imcys.bilibilias.danmaku.api

data class DanmakuInfo(
    val id: String,
    val serviceId: DanmakuServiceId,
    val senderId: String,
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

/**
 * 表示一个弹幕源.
 *
 * ### 实现细节
 *
 * 这是一个 value class 而不是 sealed class: 为了未来扩展和旧代码的兼容性. 但请尽量使用 [DanmakuServiceId.Companion] 中的"常量", 而不要使用构造器.
 *
 * 如果添加了新的 service, 请同时更新 UI `renderDanmakuServiceId`.
 */
@JvmInline
value class DanmakuServiceId(
    val value: String,
) {
    companion object {
        // Sort alphabetically
        val Bilibili = DanmakuServiceId("Bilibili")
    }
}