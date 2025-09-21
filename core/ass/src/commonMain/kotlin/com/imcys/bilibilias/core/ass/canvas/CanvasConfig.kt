package com.imcys.bilibilias.core.ass.canvas

import com.imcys.bilibilias.core.ass.Danmu
import com.imcys.bilibilias.core.ass.DanmuType
import com.imcys.bilibilias.core.ass.logger

/**
 * 弹幕画布的配置项。
 */
data class CanvasConfig(
    val duration: Double,
    val width: Int,
    val height: Int,
    val font: String,
    val fontSize: Int,
    val widthRatio: Double,
    val horizontalGap: Double,
    val laneSize: Int,
    val floatPercentage: Double,
    val bold: Boolean,
    val outline: Double,
    val timeOffset: Double,
    val alpha: Int,
    val bottomPercentage: Double = 0.25,
) {
    /**
     * 根据当前配置创建一个 Canvas 实例。
     * 替代 Rust 的 `impl Config { fn canvas(self) -> Canvas }`
     */
    fun toCanvas(): Canvas {
        val floatLanesCount = (floatPercentage * height / laneSize).toInt()
        val bottomLanesCount = (bottomPercentage * height / laneSize).toInt()

        return Canvas(
            config = this,
            floatLanes = MutableList(floatLanesCount) { null },
            bottomLanes = MutableList(bottomLanesCount) { null }
        )
    }
}

/**
 * 弹幕画布，负责管理弹幕轨道和布局。
 */
class Canvas(
    val config: CanvasConfig,
    private val floatLanes: MutableList<Lane?>,
    private val bottomLanes: MutableList<Lane?>
) {
    /**
     * 尝试将一条弹幕布局到画布上。
     * @param danmu 待处理的弹幕。
     * @return 如果成功布局，返回一个 Drawable 对象；如果弹幕被跳过，则返回 null。
     */
    fun draw(danmu: Danmu): Drawable? {
        danmu.timelineS += config.timeOffset
        if (danmu.timelineS < 0.0) {
            return null
        }

        return when (danmu.type) {
            DanmuType.FLOAT -> drawFloat(danmu)
            DanmuType.BOTTOM, DanmuType.TOP, DanmuType.REVERSE -> {
                // 不喜欢底部弹幕，直接转成 Float
                // 这是 feature 不是 bug
                danmu.type = DanmuType.FLOAT
                drawFloat(danmu)
            }
        }
    }

    private fun drawFloat(danmu: Danmu): Drawable? {
        // 使用 Pair<Double, Int> 存储 (timeNeeded, laneIndex)
        val collisions = mutableListOf<Pair<Double, Int>>()

        for ((index, lane) in floatLanes.withIndex()) {
            // 优先选择空闲的轨道
            if (lane == null) {
                return drawFloatInLane(danmu, index)
            }

            // 检查现有轨道的碰撞情况
            when (val col = lane.availableFor(danmu, config)) {
                is Collision.Separate, is Collision.NotEnoughTime -> {
                    return drawFloatInLane(danmu, index)
                }

                is Collision.Collide -> {
                    collisions.add(col.timeNeeded to index)
                }
            }
        }

        // 如果没有立即可以发射的轨道，检查是否可以通过微小延迟来发射
        if (collisions.isNotEmpty()) {
            // 按需要的延迟时间升序排序
            collisions.sortBy { it.first }
            val (timeNeeded, laneIndex) = collisions.first()

            // 只允许延迟 1 秒以内的弹幕
            if (timeNeeded < 1.0) {
                logger.info { "Delaying danmaku by ${String.format("%.2f", timeNeeded)}s" }
                danmu.timelineS += timeNeeded + 0.01 // 增加一个微小间隔
                return drawFloatInLane(danmu, laneIndex)
            }
        }

        logger.info { "Skipping danmaku: ${danmu.content}" }
        return null
    }

    private fun drawFloatInLane(danmu: Danmu, laneIndex: Int): Drawable {
        // 更新轨道状态
        floatLanes[laneIndex] = Lane.draw(danmu, config)

        val y = laneIndex * config.laneSize
        val length = danmu.calculateRenderedWidth(config)

        return Drawable(
            danmu = danmu,
            duration = config.duration,
            styleName = "Float",
            effect = DrawEffect.Move(
                start = Point(config.width, y),
                end = Point(-length.toInt(), y)
            )
        )
    }
}