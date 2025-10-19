package com.imcys.bilibilias.core.ass

import kotlin.math.roundToInt

class Canvas(
    private val displayConfiguration: DisplayConfiguration,
) {
    private val rollTracks =
        DanmakuTrackManager(displayConfiguration.layout.height / displayConfiguration.font.fontSize)
    private val fixedArray =
        DanmakuTrackManager(displayConfiguration.layout.height / displayConfiguration.font.fontSize)

    fun draw(elem: DanmakuElem): DanmakuDrawable? {
        val layout = displayConfiguration.layout
        val timing = displayConfiguration.timing
        val halfElemLength = elem.length(displayConfiguration) / 2

        return when (elem.mode) {
            DanmuType.Float, DanmuType.Reverse -> {
                val y = getPositionY(elem) ?: return null
                if (y >= layout.height) return null

                val (x1, x2, style) = if (elem.mode == DanmuType.Reverse) {
                    // L2R effect
                    Triple(
                        -halfElemLength.roundToInt(),
                        layout.width + halfElemLength.roundToInt(),
                        "L2R"
                    )
                } else {
                    // R2L effect
                    Triple(
                        layout.width + halfElemLength.roundToInt(),
                        -halfElemLength.roundToInt(),
                        "R2L"
                    )
                }

                DanmakuDrawable(
                    elem,
                    timing.rollTime,
                    style,
                    DrawEffect.Move(x1, y, x2, y)
                )
            }

            DanmuType.Top, DanmuType.Bottom -> {
                val isTop = elem.mode == DanmuType.Top
                val y = getFixedY(elem, isTop)

                if (y != null && y < layout.height) {
                    val x = displayConfiguration.layout.width / 2
                    val style = if (isTop) "TOP" else "BTM"
                    DanmakuDrawable(
                        elem,
                        displayConfiguration.timing.fixedTime,
                        style,
                        DrawEffect.Fixed(x, y)
                    )
                } else {
                    null
                }
            }
        }
    }

    fun getPositionY(elem: DanmakuElem): Int? {
        val velocity =
            elem.length(displayConfiguration) / displayConfiguration.timing.rollTime

        var bestRow = 0
        var bestBias = Double.MIN_VALUE

        val appearTime = elem.progress
        val textLength = elem.length(displayConfiguration).roundToInt()
        val fontSize = displayConfiguration.font.fontSize

        for (i in 0 until rollTracks.tracks) {
            val previous = rollTracks[i]
            val previousAppearTime = previous.time
            val previousLength = previous.length

            if (previousAppearTime < 0) {
                rollTracks.updateTrack(i, appearTime, textLength)
                return 1 + i * fontSize
            }

            val previousVelocity =
                (previousLength + displayConfiguration.layout.width) / displayConfiguration.timing.rollTime
            val deltaVelocity = velocity - previousVelocity
            val deltaX = (appearTime - previousAppearTime) *
                    previousVelocity - (previousLength + textLength) / 2
            if (deltaX < 0) {
                continue
            }
            if (deltaVelocity <= 0) {
                rollTracks.updateTrack(i, appearTime, textLength)
                return 1 + i * fontSize
            }
            val deltaTime = deltaX / deltaVelocity
            val bias = appearTime - previousAppearTime - deltaTime
            val tCatch = previousAppearTime + deltaTime
            val distancePrev = previousVelocity * (tCatch - previousAppearTime)
            if (distancePrev > displayConfiguration.layout.width) {
                rollTracks.updateTrack(i, appearTime, textLength)
                return 1 + i * fontSize
            }
            if (bias > 0) {
                rollTracks.updateTrack(i, appearTime, textLength)
                return 1 + i * fontSize
            } else if (bestRow == 0 || bias > bestBias) {
                bestBias = bias
                bestRow = i
            }
        }
        if (bestRow > 0) {
            rollTracks.updateTrack(bestRow, appearTime, textLength)
            return 1 + bestRow * fontSize
        }
        return null
    }

    fun getFixedY(elem: DanmakuElem, fromTop: Boolean): Int? {
        var bestBias = -1

        val rowRange = if (fromTop) {
            1..fixedArray.tracks
        } else {
            fixedArray.tracks downTo 1
        }

        for (i in rowRange) {
            val rowIndex = i - 1
            val (previousAppearTime, _) = fixedArray[rowIndex]

            val deltaTime = elem.progress - previousAppearTime

            if (previousAppearTime < 0 || deltaTime > displayConfiguration.timing.fixedTime) {
                fixedArray.updateTrack(rowIndex, elem.progress, 0)

                val fontSize = displayConfiguration.font.fontSize
                return if (fromTop) {
                    rowIndex * fontSize + 1
                } else {
                    displayConfiguration.layout.height - fontSize * (fixedArray.tracks - rowIndex) + 1
                }
            } else if (deltaTime > bestBias) {
                bestBias = deltaTime
            }
        }

        return null
    }
}