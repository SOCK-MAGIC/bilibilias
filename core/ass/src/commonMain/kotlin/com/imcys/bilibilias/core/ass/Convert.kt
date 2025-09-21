package com.imcys.bilibilias.core.ass

import com.imcys.bilibilias.core.ass.canvas.CanvasConfig
import kotlinx.io.IOException
import kotlinx.io.files.Path
import kotlin.system.measureTimeMillis

/**
 * 将一个弹幕数据集合转换为 ASS 字幕格式并写入指定的输出。
 *
 * 这是整个转换流程的核心协调函数。
 *
 * @param I 输入的弹幕数据源，可以是任何可迭代的 Danmu 集合。
 * @param O 输出目标，可以是任何实现了 java.io.Writer 的对象 (如 FileWriter, StringWriter)。
 * @param dataProvider 包含 Danmu 对象的可迭代数据源。
 * @param title 字幕的标题。
 * @param output 写入 ASS 内容的目标 Writer。
 * @param canvasConfig 画布和弹幕的配置。
 * @param denylist 一个可选的字符串集合，任何内容包含其中任意字符串的弹幕都将被跳过。
 * @return 成功写入的弹幕数量。
 * @throws IOException 如果在写入输出时发生 I/O 错误。
 */
fun convert(
    dataProvider: List<Danmu>,
    title: String,
    output: Path,
    canvasConfig: CanvasConfig,
    denylist: Set<String>? = null
): Int {
    var count = 0
    measureTimeMillis {
        try {
            // 使用 .use 语句来确保 writer 在结束时被自动关闭，即使发生错误。
            // 这完美对应 Rust 中 AssWriter 离开作用域时自动 drop 的行为 (RAII)。
            AssWriter(output, title, canvasConfig).use { writer ->
                val canvas = canvasConfig.toCanvas()

                // 先按时间轴排序，对应 Rust 中的 sort_by
                val danmus = dataProvider.sortedBy { it.timelineS }

                for (danmu in danmus) {
                    // 检查屏蔽词列表
                    // `denylist?.any { ... } ?: false` 是一个安全且简洁的写法：
                    // 如果 denylist 不为 null，则执行 any 检查；如果为 null，则整个表达式结果为 false。
                    val isDenied = denylist?.any { s -> danmu.content.contains(s) } ?: false
                    if (isDenied) {
                        continue
                    }

                    // 尝试在画布上绘制弹幕
                    // canvas.draw() 返回 Drawable? (nullable)
                    // 使用 .let 作用域函数可以优雅地处理非空情况
                    canvas.draw(danmu)?.let { drawable ->
                        count++
                        writer.write(drawable)
                    }
                }
            }
        } catch (e: IOException) {
            // 如果 AssWriter 内部（如 writeHeader, write）抛出异常，在这里捕获并重新抛出，
            // 符合函数签名上的 @Throws 声明。
            logger.error { "An I/O error occurred during conversion: ${e.message}" }
            throw e
        }
    }

    // 使用 Duration 类可以更友好地格式化时间
//    val durationFormatted = java.time.Duration.ofMillis(elapsedTime).toString()
//        .substring(2).replace("S", "s").lowercase() // 格式化为类似 PT8.543S -> 8.543s
//    logger.info { "弹幕数量: $count, 耗时 $durationFormatted ($title)" }

    return count
}
