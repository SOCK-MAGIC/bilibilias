package com.imcys.bilibilias.core.videoplayer.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.SubtitlesOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.imcys.bilibilias.core.videoplayer.PlaybackSpeedControllerState
import com.imcys.bilibilias.core.videoplayer.PlayerControllerState
import com.imcys.bilibilias.core.videoplayer.progress.PlayerProgressSliderState
import com.imcys.bilibilias.core.videoplayer.progress.VerticalSlider
import kotlin.math.roundToInt

const val TAG_SPEED_SWITCHER_TEXT_BUTTON = "SpeedSwitcherTextButton"
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
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AudioIcon(
        volume: Float,
        isMute: Boolean,
        maxValue: Float,
        onClick: () -> Unit,
        onchange: (Float) -> Unit,
        controllerState: PlayerControllerState,
        modifier: Modifier = Modifier,
    ) {
        val hoverInteraction = remember { MutableInteractionSource() }
        val isHovered by hoverInteraction.collectIsHoveredAsState()

        val audioIconRequester = remember { Any() }

        LaunchedEffect(Unit) {
            snapshotFlow { isHovered }.collect {
                controllerState.setRequestAlwaysOn(audioIconRequester, isHovered)
            }
        }
        Box(
            modifier = modifier.hoverable(hoverInteraction),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val iconButton = @Composable {
                IconButton(
                    onClick = onClick,
                ) {
                    when {
                        isMute -> {
                            Icon(Icons.AutoMirrored.Rounded.VolumeOff, contentDescription = "静音")
                        }

                        volume < 0.33f -> {
                            Icon(Icons.AutoMirrored.Rounded.VolumeMute, contentDescription = "音量")
                        }

                        volume < 0.66f -> {
                            Icon(Icons.AutoMirrored.Rounded.VolumeDown, contentDescription = "音量")
                        }

                        else -> {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "音量")
                        }
                    }
                }
            }

            iconButton()

            Popup(
                alignment = Alignment.BottomCenter,
            ) {
                Surface(
                    modifier = Modifier
                        .hoverable(hoverInteraction)
                        .clip(shape = CircleShape),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedVisibility(
                            visible = isHovered && !isMute,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = volume.times(100).roundToInt().toString(),
                                    modifier = Modifier.padding(8.dp),
                                )
                                val colors = SliderDefaults.colors(
                                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface,
                                )
                                VerticalSlider(
                                    value = volume,
                                    onValueChange = onchange,
                                    modifier = Modifier.width(96.dp),
                                    thumb = {},
                                    colors = colors,
                                    track = { sliderState ->
                                        SliderDefaults.Track(
                                            colors = colors,
                                            enabled = true,
                                            sliderState = sliderState,
                                            thumbTrackGapSize = 0.dp,
                                        )
                                    },
                                    valueRange = 0f..maxValue,
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isHovered && !isMute,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            iconButton()
                        }
                    }
                }
            }
        }
    }

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
    @Composable
    fun SpeedSwitcher(
        playbackSpeedControllerState: PlaybackSpeedControllerState,
        modifier: Modifier = Modifier,
        onExpandedChanged: (expanded: Boolean) -> Unit = {},
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        return OptionsSwitcher(
            value = playbackSpeedControllerState.currentIndex,
            onValueChange = { playbackSpeedControllerState.setSpeed(it) },
            renderValue = { Text(remember(it) { "${playbackSpeedControllerState.speedList[it]}x" }) },
            renderValueExposed = {
                val speedValue = playbackSpeedControllerState.speedList[it]
                Text(remember(speedValue) { if (speedValue == 1.0f) "倍速" else """${speedValue}x""" })
            },
            modifier = modifier,
            properties = PopupProperties(
                clippingEnabled = false,
            ),
            textButtonTestTag = TAG_SPEED_SWITCHER_TEXT_BUTTON,
            dropdownMenuTestTag = TAG_SPEED_SWITCHER_DROPDOWN_MENU,
            options = playbackSpeedControllerState.speedList.indices.toList(),
            expanded = expanded,
            onExpandedChange = {
                expanded = it
                onExpandedChanged(it)
            },
        )
    }

    @Composable
    fun SpeedSwitcher(
        playbackSpeedControllerState: PlaybackSpeedControllerState,
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val options = remember(playbackSpeedControllerState.speedList) {
            playbackSpeedControllerState.speedList.indices.toList()
        }

        OptionsSwitcher(
            value = playbackSpeedControllerState.currentIndex,
            onValueChange = { index -> playbackSpeedControllerState.setSpeed(index) },
            options = options,
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            renderValue = { index ->
                val speedText = remember(index) {
                    "${playbackSpeedControllerState.speedList[index]}x"
                }
                Text(text = speedText)
            },
            renderValueExposed = { index ->
                val speedValue = playbackSpeedControllerState.speedList[index]
                val exposedText = remember(speedValue) {
                    if (speedValue == 1.0f) "倍速" else "${speedValue}x"
                }
                Text(text = exposedText)
            },
            modifier = modifier,
            properties = PopupProperties(
                clippingEnabled = false,
            ),
            textButtonTestTag = TAG_SPEED_SWITCHER_TEXT_BUTTON,
            dropdownMenuTestTag = TAG_SPEED_SWITCHER_DROPDOWN_MENU
        )
    }

    /**
     * @param T The type of the options.
     * @param value The currently selected option.
     * @param onValueChange Callback to be invoked when a new option is selected.
     * @param options The list of options to choose from. It is recommended to pass a stable list (e.g., using remember).
     * @param expanded Whether the dropdown menu is currently expanded.
     * @param onExpandedChange Callback to be invoked when the expanded state of the dropdown should change.
     * @param renderValue A composable lambda to render a single option in the dropdown list.
     * @param modifier Modifier to be applied to the layout.
     * @param renderValueExposed A composable lambda to render the currently selected value when the dropdown is collapsed. Defaults to `renderValue`.
     * @param enabled Controls the enabled state of the button.
     * @param properties Properties for the dropdown popup.
     * @param textButtonTestTag The test tag for the TextButton.
     * @param dropdownMenuTestTag The test tag for the DropdownMenu.
     */
    @Composable
    fun <T> OptionsSwitcher(
        value: T,
        onValueChange: (T) -> Unit,
        options: List<T>,
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        renderValue: @Composable (T) -> Unit,
        modifier: Modifier = Modifier,
        renderValueExposed: @Composable (T) -> Unit = renderValue,
        enabled: Boolean = true,
        properties: PopupProperties = PopupProperties(),
        textButtonTestTag: String = "textButton",
        dropdownMenuTestTag: String = "dropDownMenu",
    ) {
        Box(modifier, contentAlignment = Alignment.Center) {
            TextButton(
                onClick = { onExpandedChange(true) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = LocalContentColor.current,
                ),
                enabled = enabled,
                modifier = Modifier.testTag(textButtonTestTag),
            ) {
                renderValueExposed(value)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                properties = properties,
                modifier = Modifier.testTag(dropdownMenuTestTag),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            val color = if (value == option) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            }
                            CompositionLocalProvider(LocalContentColor provides color) {
                                renderValue(option)
                            }
                        },
                        onClick = {
                            onValueChange(option)
                            onExpandedChange(false) // Dismiss the menu after selection.
                        },
                    )
                }
            }
        }
    }
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