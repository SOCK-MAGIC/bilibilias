package com.imcys.bilibilias.core.ass

import com.imcys.bilibilias.core.ass.canvas.CanvasConfig

/**
 * 代表一个弹幕的数据结构
 *
 * @property timelineS 弹幕出现的时间点（秒）
 * @property content 弹幕的文本内容
 * @property type 弹幕类型 (滚动, 顶部, 底部等)
 * @property fontsize 弹幕的字号。注意：虽然这里有字号，但实际渲染时我们通常使用 CanvasConfig 的全局字号，
 *                   以避免在调节分辨率时字体大小发生不一致的变化。
 * @property rgb 弹幕的颜色
 */
data class Danmu(
    var timelineS: Double,
    val content: String,
    val fontsize: Int,
    val rgb: Rgb,
    var type: DanmuType = DanmuType.FLOAT
) {
    /**
     * 规则：汉字算一个全宽（计为3），ASCII字符算2/3宽（计为2）
     */
    fun calculateRenderedWidth(config: CanvasConfig): Double {
        val basePoints = content.sumOf { ch ->
            if (ch.code < 128) 2 else 3
        }

        val pts = config.fontSize * basePoints / 3

        return pts.toDouble() * config.widthRatio
    }
}

/**
 * 弹幕的类型
 */
enum class DanmuType {
    FLOAT,
    TOP,
    BOTTOM,
    REVERSE;

    companion object {
        fun valueOf(num: Int): DanmuType {
            return when (num) {
                1 -> FLOAT
                4 -> BOTTOM
                5 -> TOP
                6 -> REVERSE
                // 对于无效的输入，Kotlin 的惯用做法是抛出异常。
                // IllegalArgumentException 是最适合这种情况的异常类型。
                // 这完美地对应了 Rust 中 bail! 宏返回一个 Err 的行为。
                else -> throw IllegalArgumentException("未知的弹幕类型：$num")
            }
        }
    }
}

/**
 * 用于表示 RGB 颜色，比使用 Triple<Int, Int, Int> 更具可读性和类型安全
 */
data class Rgb(val r: Int, val g: Int, val b: Int)