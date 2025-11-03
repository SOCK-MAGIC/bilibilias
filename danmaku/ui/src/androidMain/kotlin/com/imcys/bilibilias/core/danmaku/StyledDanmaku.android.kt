package com.imcys.bilibilias.core.danmaku

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.max
import android.graphics.Canvas as AndroidCanvas

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

    val destBitmap = Bitmap.createBitmap(
        width + extraMargin * 2,
        height + extraMargin * 2,
        Bitmap.Config.ARGB_8888,
    )
    val destCanvas = Canvas(AndroidCanvas(destBitmap))

    destCanvas.translate(extraMarginFloat, extraMarginFloat)
    borderTextLayout?.let { destCanvas.paintIfNotEmpty(it) }
    destCanvas.paintIfNotEmpty(solidTextLayout)

    return ImageBitmapWithOffset(
        destBitmap.asImageBitmap().apply { prepareToDraw() },
        Offset(-extraMarginFloat, -extraMarginFloat),
    )
}