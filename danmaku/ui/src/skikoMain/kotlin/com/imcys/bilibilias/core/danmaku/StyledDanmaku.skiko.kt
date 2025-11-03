package com.imcys.bilibilias.danmaku.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Surface
import kotlin.math.max

internal actual fun createDanmakuImageBitmap(
    solidTextLayout: androidx.compose.ui.text.TextLayoutResult,
    borderTextLayout: androidx.compose.ui.text.TextLayoutResult?
): ImageBitmapWithOffset {
    // We must ensure the size is at least 1x1, otherwise there may be an exception, see #1838.
    val width = max(borderTextLayout?.size?.width ?: 0, solidTextLayout.size.width).coerceAtLeast(1)
    val height =
        max(borderTextLayout?.size?.height ?: 0, solidTextLayout.size.height).coerceAtLeast(1)
    val extraMargin = height shr 1
    val extraMarginFloat = extraMargin.toFloat()

    val destSurface = Surface.makeRasterN32Premul(width + extraMargin * 2, height + extraMargin * 2)
    val destCanvas = destSurface.canvas.asComposeCanvas()

    destCanvas.translate(extraMarginFloat, extraMarginFloat)
    borderTextLayout?.let { destCanvas.paintIfNotEmpty(it) }
    destCanvas.paintIfNotEmpty(solidTextLayout)

    return ImageBitmapWithOffset(
        destSurface.makeImageSnapshot().toComposeImageBitmap().apply { prepareToDraw() },
        Offset(-extraMarginFloat, -extraMarginFloat),
    )
}