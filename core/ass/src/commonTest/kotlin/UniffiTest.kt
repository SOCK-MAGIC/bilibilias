import com.imcys.bilibilias.core.ass.Drawable
import com.imcys.bilibilias.core.ass.createDanmu
import kotlin.test.Test

class UniffiTest {

    //    private val setting = DanmuSetting(
//        duration = 15.0,
//        width = 1920u,
//        height = 1080u,
//        fontName = "黑体",
//        fontSize = 25u,
//        widthRatio = 1.2,
//        horizontalGap = 20.0,
//        laneSize = 32u,
//        floatPercentage = 0.5,
//        bottomPercentage = 0.3,
//        opacity = ((1.0 - 0.7) * 255.0).roundToInt().toUByte(),
//        bold = false,
//        outline = 0.8,
//        timeOffset = 0.0
//    )
//
//    @Test
//    fun test1() {
//
//
//    }
//
//    @Test
//    fun test2() {
//        buildList {
//            repeat(100) {
//                add(
//                    DanmuElement(
//                        timelineS = Random.nextInt(0, 600), // 随机时间，0到10分钟
//                        content = generateRandomString(Random.nextInt(5, 50)), // 随机内容，长度5到50
//                        type = DanmuMode.entries.random(), // 随机类型
//                        fontsize = Random.nextInt(18, 48).toUInt(), // 随机字号，18到48
//                        color = Random.nextInt(0, 0xFFFFFF).toUInt() // 随机颜色
//                    )
//                )
//            }
//        }
//    }
//
//    @Test
//    fun test3() {
//        val drawable = Drawable(
//            element = randomDanmu(),
//            duration = setting.duration,
//            styleName = "",
//            effect = DrawEffect.Move(0, 0, 100, 100)
//        )
//        val canvas = Canvas(setting)
//        canvas.draw(randomDanmu())
//    }
//
//    private fun randomDanmu() = DanmuElement(
//        timelineS = Random.nextInt(0, 600), // 随机时间，0到10分钟
//        content = generateRandomString(Random.nextInt(5, 50)), // 随机内容，长度5到50
//        type = DanmuMode.entries.random(), // 随机类型
//        fontsize = Random.nextInt(18, 48).toUInt(), // 随机字号，18到48
//        color = Random.nextInt(0, 0xFFFFFF).toUInt() // 随机颜色
//    )
//
//    private fun generateRandomString(length: Int): String {
//        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ('\u4e00'..'\u9fa5') // 包含中文字符
//        return (1..length)
//            .map { allowedChars.random() }
//            .joinToString("")
//    }
    @Test
    fun test4() {
        Drawable().danmu
        val danmu = createDanmu()
        danmu
    }
}