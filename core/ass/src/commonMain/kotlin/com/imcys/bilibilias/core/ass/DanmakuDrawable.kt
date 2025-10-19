package com.imcys.bilibilias.core.ass

data class DanmakuDrawable(
    val elem: DanmakuElem,
    /// 弹幕一共绘制的时间
    val duration: Int,
    /// 弹幕的绘制 style
    val styleName: String,
    /// 绘制的“特效”
    val effect: DrawEffect,
)

sealed interface DrawEffect {
    // {\move(635,25,-75,25)\c&H00FFFF}
    data class Move(val x1: Int, val y1: Int, val x2: Int, val y2: Int) : DrawEffect {
        override fun toString(): String {
            return "\\move($x1, $y1, $x2, $y2)"
        }
    }

    // {\pos(280,75)\c&H02F1FE}
    data class Fixed(val x: Int, val y: Int) : DrawEffect {
        override fun toString(): String {
            return "\\pos($x, $y)"
        }
    }
}