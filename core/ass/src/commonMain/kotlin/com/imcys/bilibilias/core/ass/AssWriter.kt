package com.imcys.bilibilias.core.ass

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

class AssWriter(
    path: Path
) : AutoCloseable {
    private val sink = SystemFileSystem.sink(path).buffered()
    fun writerHeader(options: RenderOptions) {
        val head = """
            [Script Info]
            ScriptType: v4.00+
            Collisions: Normal
            PlayResX: ${options.screenWidth}
            PlayResY: ${options.screenHeight}

            [V4+ Styles]
            Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding
            Style: Default,${options.fontName},54,&H00FFFFFF,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0.00,0.00,1,2.00,0.00,2,30,30,120,0
            Style: Danmaku,${options.fontName},${options.baseFontSize},&H00FFFFFF,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0.00,0.00,1,1.00,0.00,2,30,30,30,0

            [Events]
            Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text

        """.trimIndent()

        sink.writeString(head)
    }

    fun writerBody(subtitle: Subtitle) {
        sink.writeString(subtitle.toString())
    }

    fun writerBody(subtitles: List<Subtitle>) {
        subtitles.forEach {
            sink.writeString(it.toString())
        }
    }
    override fun close() {
        sink.close()
    }
}