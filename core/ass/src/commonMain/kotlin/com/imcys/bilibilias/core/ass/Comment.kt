package com.imcys.bilibilias.core.ass

@ConsistentCopyVisibility
data class Comment private constructor(
    val start: Int,
    val mode: Mode,
    val color: Color,
    val content: String,
    val sizeRatio: Float
) {
    companion object {
        private const val FONT_SIZE_DIVISOR = 25.0f

        /**
         * @param start 开始时间, 单位是秒
         * @param style 弹幕类型
         * @param color 弹幕颜色
         */
        fun fromRawData(
            start: Int,
            style: Int,
            color: Int,
            content: String,
            sizeRatio: Int
        ): Comment? {
            val danmakuMode = Mode.fromCode(style) ?: return null
            return Comment(
                start = start,
                mode = danmakuMode,
                color = Color(color),
                content = content,
                sizeRatio = sizeRatio / FONT_SIZE_DIVISOR
            )
        }
    }
}

enum class Mode {
    SCROLL,      // 普通弹幕
    BOTTOM,      // 底部弹幕
    TOP,         // 顶部弹幕
    ;

    companion object {
        fun fromCode(code: Int): Mode? {
            return when (code) {
                1, 2, 3, 6 -> SCROLL
                4 -> BOTTOM
                5 -> TOP
                else -> null
            }
        }
    }
}