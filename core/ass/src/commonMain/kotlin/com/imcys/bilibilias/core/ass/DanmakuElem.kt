package com.imcys.bilibilias.core.ass

class DanmakuElem(
/// 弹幕出现位置（单位 ms）
    val progress: Int,

/// 弹幕类型
    val mode: DanmuType,

/// 弹幕颜色
    val color: Color,

/// 弹幕正文
    content: String,
) {
    val content = escapeText(content)
    private fun escapeText(text: String): String {
        val trimmedText = text.trim()

        return if (trimmedText.contains('\n')) {
            trimmedText.replace("\n", "\\N")
        } else {
            trimmedText
        }
    }

    fun length(config: DisplayConfiguration): Double {
        val pts = config.font.fontSize *
                content.sumOf { ch -> if (ch.code <= 127) 2 else 3 } / 2

        return pts.toDouble()
    }
}

// \c&H02F1FE
@JvmInline
value class Color(val value: Int) {
    override fun toString(): String {
        val r = ((value shr 16) and 0xFF).toString(16).padStart(2, '0')
        val g = ((value shr 8) and 0xFF).toString(16).padStart(2, '0')
        val b = (value and 0xFF).toString(16).padStart(2, '0')
        return "\\c&H$b$g$r"
    }
}

enum class DanmuType {
    Float,
    Top,
    Bottom,
    Reverse,
}
