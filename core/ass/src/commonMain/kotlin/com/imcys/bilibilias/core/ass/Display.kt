package com.imcys.bilibilias.core.ass

import kotlin.math.ceil

interface Layout {
    val options: RenderOptions
    val comment: Comment
    var lineIndex: Int

    /**
     * 按用户自定义的字体大小来缩放
     */
    val fontSize: Int
        get() {
            return ceil(options.baseFontSize * comment.sizeRatio).toInt()
        }

    /**
     * 整条字幕宽度
     */
    val width: Int
        get() {
            val charCount = maxLength / 2
            return ceil(options.baseFontSize * charCount * 1.0).toInt()
        }

    /**
     * 整条字幕高度
     */
    val height: Int
        get() = comment.content.lines().size * options.baseFontSize

    /**
     * 整条字幕的显示时间
     */
    val duration: Long
        get() {
            val base = 3 + options.tuneDuration
            val effectiveBase = if (base <= 0) 0 else base
            val charCount = maxLength / 2

            return when {
                charCount < 6 -> effectiveBase + 1
                charCount < 12 -> effectiveBase + 2
                else -> effectiveBase + 3
            }
        }

    /**
     * 离开碰撞时间
     */
    val leaveTime: Long
        get() = comment.start + duration

    /**
     * 字体是否被缩放过
     */
    val isScaled: Boolean
        get() = comment.sizeRatio != 1.0f

    /**
     * 最长的行字符数
     */
    val maxLength: Int
        get() = comment.content.lines().maxOfOrNull { it.displayLength() } ?: 0

    fun center(): Point {
        val x = options.screenWidth / 2
        val y = options.screenHeight / 2

        return Point(x, y)
    }

    /**
     * 出现和消失的垂直坐标位置
     */
    fun vertical(): Point {
        val y = options.screenHeight.floorDiv(2)
        return Point(y, y)
    }

    /**
     * 出现和消失的垂直坐标位置
     */
    fun horizontal(): Point {
        val x = options.screenWidth.floorDiv(2)
        return Point(x, x)
    }

    fun String.displayLength(): Int {
        return sumOf { char ->
            // Character.UnicodeBlock.of(char) 可以获取字符所属的 Unicode 区块
            when (Character.UnicodeBlock.of(char)) {
                // 列出常见的东亚（CJK）字符和全角符号区块
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
                Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
                Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
                Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS,
                Character.UnicodeBlock.GENERAL_PUNCTUATION,
                Character.UnicodeBlock.HIRAGANA,
                Character.UnicodeBlock.KATAKANA,
                Character.UnicodeBlock.BOPOMOFO,
                Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO,
                Character.UnicodeBlock.HANGUL_SYLLABLES,
                Character.UnicodeBlock.HANGUL_JAMO -> 2
                // 其他所有字符（主要是拉丁字母、数字等）计为 1
                else -> 1
            }
        }
    }
}

class Top(
    override val options: RenderOptions,
    override val comment: Comment,
) : Layout {
    override var lineIndex: Int = 0
    override fun vertical(): Point {
        val y = lineIndex * options.baseFontSize
        return Point(y, y)
    }
}

class Bottom(
    override val options: RenderOptions,
    override val comment: Comment,
) : Layout {
    override var lineIndex: Int = 0
    override fun vertical(): Point {
        val y =
            options.screenHeight - (lineIndex * options.baseFontSize) - height - options.bottomMargin

        return Point(y, y)
    }
}

class Scroll(
    override val options: RenderOptions,
    override val comment: Comment,
) : Layout {
    override var lineIndex: Int = 0
    override fun horizontal(): Point {
        val x1 = (options.screenWidth + width).floorDiv(2)
        val x2 = (-width).floorDiv(2)
        return Point(x1, x2)
    }

    override fun vertical(): Point {
        val baseFontSize = options.baseFontSize
        var y = (lineIndex + 1) * baseFontSize

        if (y < fontSize) {
            y = fontSize
        }
        return Point(y, y)
    }

    fun distance(): Int {
        val (x, y) = horizontal()
        return x - y
    }

    /**
     * 字幕的移动的速度
     */
    fun speed(): Int {
        var base = 12 + options.tuneDuration
        if (base <= 0) {
            base = 0
        }
        return ceil(1.0 * options.screenWidth / base).toInt()
    }

    fun sync_duration(): Long {
        return (distance() / speed()).toLong()
    }

    fun async_duration(): Long {
        val base = (6 + options.tuneDuration).coerceAtLeast(0L)

        val char_count = maxLength / 2

        val increment = when {
            char_count < 6 -> char_count
            char_count < 12 -> char_count / 2
            char_count < 24 -> char_count / 3
            else -> 10
        }

        return base + increment
    }

    override val duration: Long
        get() = when (options.layoutAlgorithm) {
            LayoutAlgorithm.SYNC -> sync_duration()
            LayoutAlgorithm.ASYNC -> async_duration()
        }
    override val leaveTime: Long
        get() = ((width / speed()) + comment.start).toLong()

}

data class Point(val x: Int, val y: Int)