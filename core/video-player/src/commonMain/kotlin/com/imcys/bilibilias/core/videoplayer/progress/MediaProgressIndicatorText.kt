package com.imcys.bilibilias.core.videoplayer.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

const val TAG_MEDIA_PROGRESS_INDICATOR_TEXT = "MediaProgressIndicatorText"

/**
 * 显示媒体进度文本，例如 "01:23 / 15:40"。
 *
 * 为了防止文本因数字变化而“抖动”，此组件在背景中渲染了一个不可见的、
 * 使用最宽字符的占位文本来固定组件宽度。
 */
@Composable
fun MediaProgressIndicatorText(
    state: PlayerProgressSliderState,
    modifier: Modifier = Modifier
) {
    // 1. 分离业务逻辑：首先确定需要显示哪个时间点
    val displayPositionMillis = if (state.isPreviewing) {
        (state.displayPositionRatio * state.totalDurationMillis).roundToLong()
    } else {
        state.currentPositionMillis
    }

    // 2. 将毫秒转换为秒。这是 remember 的关键，使得计算更精细化。
    val currentSeconds = displayPositionMillis / 1000
    val totalSeconds = state.totalDurationMillis / 1000

    // 3. 计算实际显示的文本。
    //    现在 remember 的 key 是 currentSeconds，这意味着这个代码块
    //    最多每秒执行一次，而不是每一帧，极大地提升了性能。
    val text = remember(currentSeconds, totalSeconds) {
        renderProgressText(currentSeconds, totalSeconds)
    }

    // 4. 计算占位文本。
    //    它只依赖于总时长，所以只会在总时长变化时（通常只有一次）被计算。
    val reserveText = remember(totalSeconds) {
        renderReserveText(totalSeconds)
    }

    // 将描边样式提取为变量，使代码更清晰
    val borderTextStyle = LocalTextStyle.current.copy(
        color = Color.DarkGray,
        drawStyle = Stroke(
            miter = 3f,
            width = 2f,
            join = StrokeJoin.Round
        )
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        // 不可见的占位文本，用于撑开宽度
        Text(
            text = reserveText,
            modifier = Modifier.alpha(0f)
        )
        // 带描边的文本（作为背景边框）
        Text(
            text = text,
            style = borderTextStyle,
        )
        // 可见的文本
        Text(
            text = text,
            modifier = Modifier.testTag(TAG_MEDIA_PROGRESS_INDICATOR_TEXT),
        )
    }
}

/**
 * 渲染完整的进度文本，例如 "01:23 / 15:40"。
 */
@Stable
internal fun renderProgressText(currentSecs: Long, totalSecs: Long?): String {
    if (totalSecs == null || totalSecs <= 0) {
        return formatDuration(currentSecs, 0) + " / --:--"
    }
    val currentFormatted = formatDuration(currentSecs, totalSecs)
    val totalFormatted = formatDuration(totalSecs, totalSecs)
    return "$currentFormatted / $totalFormatted"
}

/**
 * 将秒数格式化为 "HH:MM:SS" 或 "MM:SS" 格式的字符串。
 * 具体格式取决于媒体的总时长。
 *
 * @param seconds 要格式化的秒数。
 * @param totalSeconds 媒体总秒数，用于决定是否显示小时。
 */
@Stable
private fun formatDuration(seconds: Long, totalSeconds: Long): String {
    val duration = seconds.seconds
    val totalDuration = totalSeconds.seconds

    return duration.toComponents { hours, minutes, secs, _ ->
        val showHours = totalDuration.inWholeHours > 0

        if (showHours) {
            val h = hours.toString().padStart(2, '0')
            val m = minutes.toString().padStart(2, '0')
            val s = secs.toString().padStart(2, '0')
            "$h:$m:$s"
        } else {
            val totalMinutes = duration.inWholeMinutes
            val m = totalMinutes.toString().padStart(2, '0')
            val s = secs.toString().padStart(2, '0')
            "$m:$s"
        }
    }
}

@Stable
private fun renderReserveText(totalSecs: Long?): String {
    val actualText = renderProgressText(totalSecs ?: 0, totalSecs)
    return actualText.replace(Regex("\\d"), "8")
}