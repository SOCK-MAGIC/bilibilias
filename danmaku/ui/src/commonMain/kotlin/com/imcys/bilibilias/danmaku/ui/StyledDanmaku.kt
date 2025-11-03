package com.imcys.bilibilias.danmaku.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.imcys.bilibilias.danmaku.ui.config.DanmakuStyle
import kotlin.math.floor
import kotlin.uuid.Uuid

/**
 * DanmakuState holds all params which [Canvas] needs to draw a danmaku text.
 */
@Immutable
data class StyledDanmaku(
    val presentation: DanmakuPresentation,
    internal val measurer: TextMeasurer,
    internal val baseStyle: TextStyle,
    internal val style: DanmakuStyle,
    internal val enableColor: Boolean,
    internal val isDebug: Boolean
) : SizeSpecifiedDanmaku {
    private val danmakuText = presentation.danmaku.run {
        val seconds = playTimeMillis.toFloat().div(1000)
        if (isDebug) "$text (${floor((seconds / 60)).toInt()}:${
            (seconds % 60).toString().padStart(2, '0')
        })"
        else text
    }

    private val solidTextLayout = measurer.measure(
        text = danmakuText,
        style = baseStyle.merge(
            style.styleForText(
                color = if (enableColor) {
                    Color(0xFF_00_00_00L or presentation.danmaku.color.toUInt().toLong())
                } else Color.White,
            ).copy(textDecoration = if (presentation.isSelf) TextDecoration.Underline else null),
        ),
        overflow = TextOverflow.Clip,
        maxLines = 1,
        softWrap = false,
    )

    private val borderTextLayout = if (style.strokeWidth == 0f) null else measurer.measure(
        text = danmakuText,
        style = baseStyle.merge(style.styleForBorder())
            .copy(textDecoration = if (presentation.isSelf) TextDecoration.Underline else null),
        overflow = TextOverflow.Clip,
        maxLines = 1,
        softWrap = false,
    )

    private var imageBitmap: ImageBitmapWithOffset? = null

    override val danmakuWidth: Int = solidTextLayout.size.width.coerceAtLeast(1)
    override val danmakuHeight: Int = solidTextLayout.size.height.coerceAtLeast(1)

    internal fun DrawScope.draw(screenPosX: Float, screenPosY: Float) {
        val cachedImage = imageBitmap ?: createDanmakuImageBitmap(solidTextLayout, borderTextLayout)
            .also { imageBitmap = it }

        drawImage(cachedImage.bitmap, Offset(screenPosX, screenPosY) + cachedImage.offset)
    }
}

class ImageBitmapWithOffset(
    val bitmap: ImageBitmap,
    val offset: Offset,
)

/**
 * Create image snapshot of danmaku text.
 */
internal expect fun createDanmakuImageBitmap(
    solidTextLayout: TextLayoutResult,
    borderTextLayout: TextLayoutResult?,
): ImageBitmapWithOffset

internal fun Canvas.paintIfNotEmpty(layout: TextLayoutResult) {
    if (layout.size.width != 0 && layout.size.height != 0) {
        TextPainter.paint(this, layout)
    }
}

internal fun dummyDanmaku(
    measurer: TextMeasurer,
    baseStyle: TextStyle,
    style: DanmakuStyle,
    dummyText: String = "dummy 占位 攟 の \uD83D\uDE04"
): StyledDanmaku {
    return StyledDanmaku(
        presentation = DanmakuPresentation(
            DanmakuInfo(
                Uuid.random().toHexString(),
                DanmakuServiceId("dummy"), "1",
                DanmakuContent(
                    0L, 0,
                    dummyText, DanmakuLocation.NORMAL,
                ),
            ),
            isSelf = false,
        ),
        measurer = measurer,
        baseStyle = baseStyle,
        style = style,
        enableColor = false,
        isDebug = false,
    )
}