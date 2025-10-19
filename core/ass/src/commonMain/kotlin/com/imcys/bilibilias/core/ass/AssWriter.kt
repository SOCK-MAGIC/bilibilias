package com.imcys.bilibilias.core.ass

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import java.util.Locale

class AssWriter(
    path: Path
) : AutoCloseable {
    private val sink = SystemFileSystem.sink(path).buffered()

    fun writerHeader(displayConfiguration: DisplayConfiguration) {
        val width = displayConfiguration.layout.width
        val height = displayConfiguration.layout.height
        val alpha =
            ((1 - displayConfiguration.layout.opacity) * 255).toInt().toString(16).padStart(2)
        val fontName = displayConfiguration.font.fontName
        val fontSize = displayConfiguration.font.fontSize
        val scFontSize = displayConfiguration.font.secondaryFontSize
        val primaryColor = "&H${alpha}FFFFFF"
        val backColor = "&H${alpha}000000"
        val bold = if (displayConfiguration.font.isBold) 1 else 0
        val outline = displayConfiguration.font.outlineWidth
        val shadow = displayConfiguration.font.shadowRadius
        val head = """
           |[Script Info]
           
           |ScriptType: v4.00+
           |Collisions: Normal
           |PlayResX: $width
           |PlayResY: $height
           |Timer: 100.0000
           |WrapStyle: 2
           |ScaledBorderAndShadow: yes
           
           |[V4+ Styles]
           |Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding

           |Style: R2L,${fontName},${fontSize},${primaryColor},&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,${outline},${shadow},7,0,0,0,1
           |Style: L2R,${fontName},${fontSize},${primaryColor},&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,${outline},${shadow},9,0,0,0,1
           |Style: TOP,${fontName},${fontSize},${primaryColor},&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,${outline},${shadow},8,0,0,0,1
           |Style: BTM,${fontName},${fontSize},${primaryColor},&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,${outline},${shadow},2,0,0,0,1
           |Style: SP,${fontName},${fontSize},&H00FFFFFF,&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,${outline},${shadow},7,0,0,0,1
           |Style: message_box,${fontName},${scFontSize},&H00FFFFFF,&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,0.0,0.7,7,0,0,0,1
           |Style: price,${fontName},${(scFontSize * 0.7).toInt()},&H00FFFFFF,&H00FFFFFF,&H00000000,${backColor},${bold},0,0,0,100.00,100.00,0.00,0.00,1,0.0,0.7,7,0,0,0,1
           
           |[Events]
           |Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
       """.trimIndent()
        sink.writeString(head)
    }

    fun writer(drawable: DanmakuDrawable) {
        val start = timePoint(drawable.elem.progress)
        val end = timePoint(drawable.elem.progress + drawable.duration)
        val style = drawable.styleName
        val effect = drawable.effect
        val color = drawable.elem.color
        val text = escapeText(drawable.elem.content)
        // {\pos(280,50)\c&H02F1FE}x7
        val dialogue = "Dialogue: 2,$start,$end,$style,,0,0,0,,{$effect$color}${text}"

        sink.writeString(dialogue)
    }

    override fun close() {
        sink.close()
    }

    private fun timePoint(point: Int): String {
        val second = point / 1000
        val hour = second / 3600
        val minutes = (second % 3600) / 60

        val left = second - (hour * 3600) - (minutes * 60)

        return String.format(Locale.CHINESE, "%d:%02d:%05.2f", hour, minutes, left)
    }

    private fun escapeText(text: String): String {
        val trimmedText = text.trim()

        return if (trimmedText.contains('\n')) {
            trimmedText.replace("\n", "\\N")
        } else {
            trimmedText
        }
    }
}