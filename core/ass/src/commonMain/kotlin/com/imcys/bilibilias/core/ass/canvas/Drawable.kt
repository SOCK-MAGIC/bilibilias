package com.imcys.bilibilias.core.ass.canvas

import com.imcys.bilibilias.core.ass.Danmu

/**
 * 代表一个可以被绘制的实体。
 * 弹幕的开始绘制时间由其包含的 danmu.timelineS 决定。
 *
 * @property danmu 弹幕数据本身。
 * @property duration 弹幕在屏幕上显示的总时长。
 * @property styleName 弹幕的绘制样式名称。
 * @property effect 弹幕的绘制特效 (移动或固定)。
 */
data class Drawable(
    val danmu: Danmu,
    val duration: Double,
    val styleName: String,
    val effect: DrawEffect
)

/**
 * 用于表示二维坐标点，比使用 Pair<Int, Int> 更具可读性。
 */
data class Point(val x: Int, val y: Int)

/**
 * 定义绘制的“特效”。
 */
sealed interface DrawEffect {
    /**
     * 移动特效，包含起点和终点坐标。
     */
    data class Move(val start: Point, val end: Point) : DrawEffect

    /**
     * 固定位置特效，没有额外数据。
     */
    object Fixed : DrawEffect
}