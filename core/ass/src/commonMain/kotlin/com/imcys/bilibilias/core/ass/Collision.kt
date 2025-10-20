package com.imcys.bilibilias.core.ass

import androidx.collection.mutableIntListOf
import kotlin.math.ceil
import kotlin.math.max

/**
 * 碰撞处理
 */
class Collision(lineCount: Int) {
    private val leaves = IntArray(lineCount)

    /**
     * 碰撞检测
     * 返回行号和时间偏移
     */
    fun detect(display: Comment): Pair<Int, Int> {
        val beyonds = mutableIntListOf()
        for ((index, leave) in leaves.withIndex()) {
            val beyond = display.start - leave
            if (beyond >= 0) {
                return index to 0
            }
            beyonds.add(beyond)
        }

        // 所有行都没有空间了，那么找出哪一行能在最短时间内让出空间
        var max = Int.MIN_VALUE
        beyonds.forEach {
            max = max(max, it)
        }

        val soon = max
        val lineIndex = beyonds.indexOf(soon)
        val offset = -soon
        return lineIndex to offset
    }

    /**
     * 更新碰撞信息
     */
    fun update(leave: Long, lineIndex: Int, offset: Int) {
        leaves[lineIndex] = ceil((leave + offset).toDouble()).toInt()
    }
}
