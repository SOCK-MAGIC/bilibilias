package com.imcys.bilibilias.core.ass

import kotlin.time.Duration.Companion.seconds

class Subtitle(
    private val danmaku: Danmaku,
    private val display: Layout,
    private val offset: Int
) {
    fun start() = danmaku.start + offset
    fun end() = start() + display.duration
    fun color() = danmaku.color
    fun position(): Pair<Point, Point> {
        return display.horizontal() to display.vertical()
    }

    fun startMarkup(): String {
        val seconds = start().seconds
        return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
            val nanos = nanoseconds.toString()
            hours.toString() + ":" +
                    minutes.toString().padStart(2, '0') + ":" +
                    seconds.toString().padStart(2, '0') + "." +
                    if (nanos.length >= 2) nanos.take(2) else nanos.padStart(2, '0')

        }
    }

    fun endMarkup(): String {
        val seconds = end().seconds
        return seconds.toComponents { hours, minutes, seconds, nanoseconds ->
            val nanos = nanoseconds.toString()
            hours.toString() + ":" +
                    minutes.toString().padStart(2, '0') + ":" +
                    seconds.toString().padStart(2, '0') + "." +
                    if (nanos.length >= 2) nanos.take(2) else nanos.padStart(2, '0')
        }
    }

    fun color_markup(): String {
        return color().toString()
    }

    fun border_markup(): String {
        return ""
    }

    fun font_size_markup(): String {
        if (display.isScaled) {
            return "\\fs" + display.fontSize
        }
        return ""
    }

    fun style_markup(): String {
        val (p1, p2) = position()
        if (danmaku.mode == Mode.SCROLL) {
            return "\\move(${p1.x}, ${p1.y}, ${p2.x}, ${p2.y})"
        }
        return "\\a6\\pos(${p1.x}, ${p1.y})"
    }

    fun layer_markup(): String {
        if (danmaku.mode != Mode.SCROLL) {
            return "-2"
        }
        return "-3"
    }

    fun content_markup(): String {
        val markUp = style_markup() + color_markup() + border_markup() + font_size_markup()
        val content = correct_typos(danmaku.content)
        return "{$markUp}$content"
    }

    fun correct_typos(text: String): String {
        return text.replace("\n", "\\N")
            .replace("&gt;", ">")
            .replace("&lt;", "<")
    }

    override fun toString(): String {
        return "Dialogue: ${layer_markup()},${startMarkup()},${endMarkup()},Danmaku,,0000,0000,0000,,${content_markup()}\n"
    }
}