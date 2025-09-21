package com.imcys.bilibilias.core.ass.canvas

import com.imcys.bilibilias.core.ass.Danmu

/**
 * 表示两条弹幕之间可能发生的碰撞情况。
 */
sealed interface Collision {
    /** 两条弹幕会逐渐远离，不会碰撞。 */
    data class Separate(val closestDis: Double) : Collision

    /** 第二条弹幕速度更快，但没有足够的时间追上第一条。 */
    data class NotEnoughTime(val closestDis: Double) : Collision

    /** 两条弹幕会发生碰撞，需要推迟发射以避免。 */
    data class Collide(val timeNeeded: Double) : Collision
}

/**
 * 表示一个弹幕轨道（或槽位），记录了最后一条弹幕的信息以用于碰撞检测。
 *
 * @property lastShootTime 最后一条弹幕的发射时间。
 * @property lastLength 最后一条弹幕的计算长度。
 */
data class Lane(
    val lastShootTime: Double,
    val lastLength: Double
) {
    /**
     * 检查当前轨道是否可以发射另一条弹幕。
     * @param other 准备发射的新弹幕。
     * @param config 画布配置。
     * @return 碰撞检测的结果。
     */
    fun availableFor(other: Danmu, config: CanvasConfig): Collision {
        val duration = config.duration
        val width = config.width.toDouble()
        val gap = config.horizontalGap

        // 当前轨道上的最后一条弹幕（即“我”）
        val t1 = lastShootTime
        val l1 = lastLength

        // 准备发射的新弹幕
        val t2 = other.timelineS
        val l2 = other.calculateRenderedWidth(config)

        // 计算两条弹幕的速度
        val v1 = (width + l1) / duration
        val v2 = (width + l2) / duration

        val deltaT = t2 - t1
        // 当 t2 时刻，第一条弹幕的尾部距离屏幕左侧的距离
        val deltaX = v1 * deltaT - l1

        // 如果新弹幕发射时，前一条弹幕还没完全进入屏幕，或者间距不足
        return if (deltaX < gap) {
            if (l2 <= l1) {
                // 新弹幕更短（或等长），因此速度更慢（或等速），永远追不上。
                // 只需等待前一条弹幕前进足够的距离即可。
                Collision.Collide(timeNeeded = (gap - deltaX) / v1)
            } else {
                // 新弹幕更长，速度更快，会追上。
                // 计算需要延迟多久才能保证在前一条弹幕消失时，新弹幕的头部不会越过安全线。
                val timeNeeded = (duration - (width - gap) / v2) - deltaT
                Collision.Collide(timeNeeded = timeNeeded)
            }
        } else {
            // 新弹幕发射时，前一条弹幕已经完全进入屏幕且间距足够。
            if (l2 <= l1) {
                // 新弹幕更慢，永远追不上，可以安全发射。
                Collision.Separate(closestDis = deltaX - gap)
            } else {
                // 追击问题：新弹幕更快，需要计算是否会在屏幕内追上。
                // 计算前一条弹幕消失时（t1 + T），新弹幕的位置。
                val pos = v2 * (duration - deltaT)
                if (pos < (width - gap)) {
                    // 在前一条弹幕消失时，新弹幕还未到达终点安全线，不会碰撞。
                    Collision.NotEnoughTime(closestDis = (width - gap) - pos)
                } else {
                    // 会在屏幕内追上，计算需要延迟多久才能避免。
                    Collision.Collide(timeNeeded = (pos - (width - gap)) / v2)
                }
            }
        }
    }

    companion object {
        /**
         * 根据一个滚动弹幕创建新的轨道状态。
         */
        fun draw(danmu: Danmu, config: CanvasConfig): Lane {
            return Lane(
                lastShootTime = danmu.timelineS,
                lastLength = danmu.calculateRenderedWidth(config)
            )
        }

        /**
         * 根据一个固定弹幕（如顶部或底部）创建新的轨道状态。
         * 固定弹幕不参与滚动碰撞检测，因此长度可以记为 0。
         */
        fun drawFixed(danmu: Danmu): Lane {
            return Lane(
                lastShootTime = danmu.timelineS,
                lastLength = 0.0
            )
        }
    }
}