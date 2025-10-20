package com.imcys.bilibilias.core.ass

class Studio(
    private val options: RenderOptions,
    private val danmakus: List<Danmaku>,
) {
    fun generate(): List<Subtitle> {
        return subtitles()
    }

    private fun subtitles(): List<Subtitle> {
        val scroll = Collision(options.lineCount)
        val top = Collision(options.lineCount)
        val bottom = Collision(options.lineCount)

        val subtitles = mutableListOf<Subtitle>()
        for (comment in danmakus) {
            val display = when (comment.mode) {
                Mode.SCROLL -> Scroll(options, comment)
                Mode.BOTTOM -> Bottom(options, comment)
                Mode.TOP -> Top(options, comment)
            }

            val collision = when (comment.mode) {
                Mode.SCROLL -> scroll
                Mode.BOTTOM -> bottom
                Mode.TOP -> top
            }

            val (line_index, waiting_offset) = collision.detect(comment)

            if (waiting_offset > options.dropOffset) {
                continue
            }

            display.lineIndex = line_index
            collision.update(display.leaveTime, line_index, waiting_offset)
            val offset = waiting_offset + options.customOffset
            val subtitle = Subtitle(comment, display, offset)
            subtitles.add(subtitle)
        }
        return subtitles
    }
}


/**
 * @property tuneDuration 微调时长
 * @property dropOffset 丢弃偏移
 * @property bottomMargin 底部边距
 * @property customOffset 自定偏移
 */
class RenderOptions(
    val screenWidth: Int,
    val screenHeight: Int,
    val fontName: String,
    val baseFontSize: Int,
    val lineCount: Int,
    val layoutAlgorithm: LayoutAlgorithm,
    val tuneDuration: Long,
    val dropOffset: Int,
    val bottomMargin: Int,
    val customOffset: Int,
) {
    companion object {
        private const val DEFAULT_SCREEN_WIDTH = 1920
        private const val DEFAULT_SCREEN_HEIGHT = 1080
        private const val DEFAULT_FONT_NAME = "sans-serif"
        private const val DEFAULT_BASE_FONT_SIZE = 32
        val Default = RenderOptions(
            screenWidth = DEFAULT_SCREEN_WIDTH,
            screenHeight = DEFAULT_SCREEN_HEIGHT,
            fontName = DEFAULT_FONT_NAME,
            baseFontSize = DEFAULT_BASE_FONT_SIZE,
            lineCount = DEFAULT_SCREEN_HEIGHT / DEFAULT_BASE_FONT_SIZE,
            layoutAlgorithm = LayoutAlgorithm.SYNC,
            tuneDuration = 0,
            dropOffset = 2,
            bottomMargin = 0,
            customOffset = 0,
        )
    }
}

enum class LayoutAlgorithm {
    SYNC, ASYNC;
}