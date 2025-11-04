package com.imcys.bilibilias.core.videoplayer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 用于修复获取时间线获取为 null 的问题
 */
object TimelineState {
    internal val _durationMillis = MutableStateFlow(TIME_UNSET)
    val durationMillis: StateFlow<Long> = _durationMillis.asStateFlow()
    const val TIME_UNSET: Long = Long.MIN_VALUE + 1
}