package com.imcys.bilibilias.core.videoplayer.bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.SubtitlesOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.imcys.bilibilias.core.videoplayer.progress.PlayerProgressSliderState

const val TAG_SPEED_SWITCHER_DROPDOWN_MENU = "SpeedSwitcherDropdownMenu"
const val TAG_DANMAKU_ICON_BUTTON = "DanmakuIconButton"
@Stable
object PlayerControllerDefaults {
    @Composable
    fun PlaybackIcon(
        isPlaying: () -> Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        IconButton(
            onClick = onClick,
            modifier,
        ) {
            if (isPlaying()) {
                Icon(Icons.Rounded.Pause, contentDescription = "Pause", Modifier.size(36.dp))
            } else {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", Modifier.size(36.dp))
            }
        }
    }

    /**
     * To turn danmaku on/off
     */
    @Composable
    fun DanmakuIcon(
        danmakuEnabled: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        IconButton(
            onClick = onClick,
            modifier.testTag(TAG_DANMAKU_ICON_BUTTON),
        ) {
            if (danmakuEnabled) {
                Icon(Icons.Rounded.Subtitles, contentDescription = "禁用弹幕")
            } else {
                Icon(Icons.Rounded.SubtitlesOff, contentDescription = "启用弹幕")
            }
        }
    }
//    @Composable
//    fun inVideoDanmakuTextFieldColors(): TextFieldColors {
//        return OutlinedTextFieldDefaults.colors(
//            unfocusedContainerColor = MaterialTheme.colorScheme.surface.stronglyWeaken(),
//            focusedContainerColor = MaterialTheme.colorScheme.surface.stronglyWeaken(),
//            unfocusedBorderColor = Color.Transparent,
//            focusedBorderColor = Color.Transparent,
//            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.slightlyWeaken(),
//            focusedTextColor = MaterialTheme.colorScheme.onSurface,
//        )
//    }
    /**
     * To enter/exit fullscreen
     */
    @Composable
    fun FullscreenIcon(
        isFullscreen: Boolean,
        onClickFullscreen: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        IconButton(
            onClick = onClickFullscreen,
            modifier,
        ) {
            if (isFullscreen) {
                Icon(
                    Icons.Rounded.FullscreenExit,
                    contentDescription = "Exit Fullscreen",
                    Modifier.size(32.dp)
                )
            } else {
                Icon(
                    Icons.Rounded.Fullscreen,
                    contentDescription = "Enter Fullscreen",
                    Modifier.size(32.dp)
                )
            }
        }
    }

    @Composable
    fun MediaProgressSlider(
        progressSliderState: PlayerProgressSliderState,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        showPreviewTimeTextOnThumb: Boolean = true,
    ) {
        com.imcys.bilibilias.core.videoplayer.progress.MediaProgressSlider(
            progressSliderState,
            enabled = enabled,
            showPreviewTimeTextOnThumb = showPreviewTimeTextOnThumb,
            modifier = modifier,
        )
    }
    /**
     * Set 1x, 2x playback speed.
     * @param optionsProvider The options to choose from. Note that when the value changes, it will not reflect in the UI.
     */
//    @Composable
//    fun SpeedSwitcher(
//        playbackSpeedControllerState: PlaybackSpeedControllerState,
//        modifier: Modifier = Modifier,
//        onExpandedChanged: (expanded: Boolean) -> Unit = {},
//    ) {
//        return OptionsSwitcher(
//            value = playbackSpeedControllerState.currentIndex,
//            onValueChange = { playbackSpeedControllerState.setSpeed(it) },
//            optionsProvider = { playbackSpeedControllerState.speedList.indices.toList() },
//            renderValue = { Text(remember(it) { "${playbackSpeedControllerState.speedList[it]}x" }) },
//            renderValueExposed = {
//                val speedValue = playbackSpeedControllerState.speedList[it]
//                Text(remember(speedValue) { if (speedValue == 1.0f) "倍速" else """${speedValue}x""" })
//            },
//            modifier,
//            properties = PlatformPopupProperties(
//                clippingEnabled = false,
//            ),
//            textButtonTestTag = TAG_SPEED_SWITCHER_TEXT_BUTTON,
//            dropdownMenuTestTag = TAG_SPEED_SWITCHER_DROPDOWN_MENU,
//            onExpandedChanged = onExpandedChanged,
//        )
//    }
}

/**
 * The controller bar of a video player. Usually at the bottom of the screen (the video player).
 *
 * See [PlayerControllerDefaults] for components.
 *
 * @param startActions [PlayerControllerDefaults.PlaybackIcon], [PlayerControllerDefaults.DanmakuIcon]
 * @param progressIndicator [MediaProgressIndicatorText]
 * @param progressSlider [MediaProgressSlider]
 * @param danmakuEditor [PlayerControllerDefaults.DanmakuTextField]
 * @param endActions [PlayerControllerDefaults.FullscreenIcon]
 * @param expanded Whether the controller bar is expanded.
 * If `true`, the [progressIndicator] and [progressSlider] will be shown on a separate row above. The bottom row will contain a [danmakuEditor].
 * If `false`, the entire bar will be only one row. [danmakuEditor] will be ignored.
 */
@Composable
fun PlayerControllerBar(
    expanded: Boolean,
    startActions: @Composable RowScope.() -> Unit,
    progressIndicator: @Composable RowScope.() -> Unit,
    progressSlider: @Composable RowScope.() -> Unit,
    danmakuEditor: @Composable RowScope.() -> Unit,
    endActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clickable(
                remember { MutableInteractionSource() },
                null,
                onClick = {}) // Consume touch event
            .padding(
                horizontal = if (expanded) 8.dp else 4.dp,
                vertical = if (expanded) 4.dp else 2.dp,
            ),
    ) {
        Column {
            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                Row(
                    Modifier
                        .padding(start = if (expanded) 8.dp else 4.dp)
                        .padding(vertical = if (expanded) 4.dp else 2.dp),
                ) {
                    progressIndicator()
                }
                if (expanded) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        progressSlider()
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (expanded) 8.dp else 4.dp),
        ) {
            // 播放 / 暂停按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                startActions()
            }

            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (expanded) {
                    ProvideTextStyle(MaterialTheme.typography.labelSmall) {
                        danmakuEditor()
                    }
                } else {
                    progressSlider()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                endActions()
            }
        }
    }
}