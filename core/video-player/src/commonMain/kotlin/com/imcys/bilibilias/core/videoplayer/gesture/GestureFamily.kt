package com.imcys.bilibilias.core.videoplayer.gesture

import androidx.compose.runtime.Immutable

@Immutable
enum class GestureFamily(
    val useDesktopGestureLayoutWorkaround: Boolean,
    val clickToPauseResume: Boolean,
    val clickToToggleController: Boolean,
    val doubleClickToFullscreen: Boolean,
    val doubleClickToPauseResume: Boolean,
    val swipeToSeek: Boolean,
    val swipeRhsForVolume: Boolean,
    val swipeLhsForBrightness: Boolean,
    val longPressForFastSkip: Boolean,
    val scrollForVolume: Boolean,
    val autoHideController: Boolean,
    val volumeControllerOnBottomBar: Boolean,
    val keyboardSpaceForPauseResume: Boolean = true,
    val keyboardUpDownForVolume: Boolean = true,
    val keyboardLeftRightToSeek: Boolean = true,
    val mouseHoverForController: Boolean = true, // not supported on mobile
    val keyboardControlFullscreen: Boolean = true,
    val keyboardControlSpeed: Boolean = true,
    val keyboardToggleDanmaku: Boolean = true,
) {
    TOUCH(
        useDesktopGestureLayoutWorkaround = false,
        clickToPauseResume = false,
        clickToToggleController = true,
        doubleClickToFullscreen = false,
        doubleClickToPauseResume = true,
        swipeToSeek = true,
        swipeRhsForVolume = true,
        swipeLhsForBrightness = true,
        longPressForFastSkip = true,
        volumeControllerOnBottomBar = false,
        scrollForVolume = false,
        autoHideController = true,
        mouseHoverForController = false,
    ),
    MOUSE(
        useDesktopGestureLayoutWorkaround = true,
        clickToPauseResume = true,
        clickToToggleController = false,
        doubleClickToFullscreen = true,
        doubleClickToPauseResume = false,
        swipeToSeek = false,
        swipeRhsForVolume = false,
        swipeLhsForBrightness = false,
        longPressForFastSkip = false,
        scrollForVolume = true,
        autoHideController = false,
        volumeControllerOnBottomBar = true,
    )
}
