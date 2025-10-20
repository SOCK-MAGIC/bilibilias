package com.imcys.bilibilias.core.ass

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.math.roundToInt
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudioTest {
    private lateinit var options: RenderOptions
    private lateinit var danmakus: List<Danmaku>

    @BeforeTest
    fun setUp() {
        options = RenderOptions.Default
        danmakus = loadDanmakusFromTestResource("sample.xml")
    }

    /**
     * Loads and parses danmaku comments from a test resource file.
     * This function is robust against malformed data entries.
     */
    private fun loadDanmakusFromTestResource(fileName: String): List<Danmaku> {
        val fileContent = this::class.java.classLoader.getResource(fileName)?.readText()
        requireNotNull(fileContent) { "Test resource '$fileName' could not be found." }

        val danmakuRoot = XML {
            xmlVersion = XmlVersion.XML10
        }.decodeFromString<Root>(fileContent)

        return danmakuRoot.danmakus.mapNotNull { danmakuEntry ->
            val parts = danmakuEntry.p.split(",")
            if (parts.size < 4) {
                return@mapNotNull null
            }

            try {
                val (startStr, styleStr, sizeRatioStr, colorStr) = parts
                Danmaku.fromRawData(
                    start = startStr.toDouble().roundToInt(),
                    style = styleStr.toInt(),
                    color = colorStr.toInt(),
                    content = danmakuEntry.content,
                    sizeRatio = sizeRatioStr.toInt()
                )
            } catch (e: NumberFormatException) {
                null
            }
        }
    }

    @Test
    fun `generate should produce a large list of subtitles from loaded danmakus`() {
        val studio = Studio(options, danmakus)
        val subtitles = studio.generate()
        assertTrue(subtitles.size > 2000, "Generated subtitles count should be over 2000")
    }

    @Test
    fun `generate should create subtitles with positioning for fixed danmakus`() {
        val expectedFixedCount = danmakus.count { it.mode == Mode.TOP || it.mode == Mode.BOTTOM }


        val studio = Studio(options, danmakus)
        val subtitles = studio.generate()

        val actualFixedCount = subtitles.count { it.toString().contains("pos") }
        assertEquals(expectedFixedCount, actualFixedCount)
    }
}