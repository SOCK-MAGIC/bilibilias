package com.imcys.bilibilias.core.videoplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

private val TopScrim = Brush.verticalGradient(
    0.0f to Color.Black.copy(0.6f),
    0.8f to Color.Transparent,
)
private val BottomScrim = Brush.verticalGradient(
    0.2f to Color.Transparent,
    1.0f to Color.Black.copy(0.6f),
)

private val standardEnter = fadeIn(tween())
private val standardExit = fadeOut(tween())

/**
 * 视频播放器框架, 可以自定义组合控制器等部分.
 *
 * 视频播放器框架由以下层级组成, 由上至下:
 *
 * - 悬浮消息: [floatingMessage], 例如正在缓冲
 * - 控制器: [topBar], [rhsBar] 和 [bottomBar]
 * - 手势: [gestureHost]
 * - 弹幕: [danmakuHost]
 * - 视频: [video]
 * - 右侧侧边栏: [rhsSheet]
 *
 * @param topBar [PlayerTopBar]
 * @param video [VideoPlayer]. video 不会接受到点击事件.
 * @param danmakuHost 为 `DanmakuHost` 留的区域
 * @param gestureHost 手势区域, 例如快进/快退, 音量调节等. See [PlayerGestureHost]
 * @param floatingMessage 悬浮消息, 例如正在缓冲. 将会对齐到中央
 * @param rhsBar 右侧控制栏, 锁定手势等.
 * @param bottomBar [PlayerControllerBar]
 * @param expanded 当前是否处于全屏模式. 全屏时此框架会 [Modifier.fillMaxSize], 否则会限制为一个 16:9 的框.
 */
@Composable
fun VideoScaffold(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets.safeContent,
    maintainAspectRatio: Boolean = !expanded,
    controllerState: PlayerControllerState,
    gestureLocked: Boolean = false,
    topBar: @Composable RowScope.() -> Unit = {},
    video: @Composable BoxScope.() -> Unit = {},
    danmakuHost: @Composable BoxScope.() -> Unit = {},
    gestureHost: @Composable BoxScope.() -> Unit = {},
    floatingMessage: @Composable BoxScope.() -> Unit = {},
    rhsButtons: @Composable ColumnScope.() -> Unit = {},
    gestureLock: @Composable ColumnScope.() -> Unit = {},
    bottomBar: @Composable RowScope.() -> Unit = {},
    detachedProgressSlider: @Composable () -> Unit = {},
    floatingBottomEnd: @Composable RowScope.() -> Unit = {},
    rhsSheet: @Composable () -> Unit = {},
    leftBottomTips: @Composable () -> Unit = {},
) {
    val controllerVisibility = controllerState.visibility
        .withGestureLocked(gestureLocked)
        .withExpanded(expanded)

    Box(
        modifier = modifier.then(
            if (expanded) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
        ),
        contentAlignment = Alignment.Center,
    ) {
        val aspectRatioModifier = if (maintainAspectRatio) {
            Modifier.aspectRatio(16f / 9f)
        } else {
            Modifier.fillMaxSize()
        }

        Box(modifier = aspectRatioModifier.background(Color.Black)) {
            VideoLayer(video)
            DanmakuLayer(contentWindowInsets, danmakuHost)
            GestureLayer(gestureHost)

            // [优化] 主 UI 覆盖层现在只负责协调，逻辑分发到更小的组件中
            UiOverlay(
                controllerState = controllerState,
                controllerVisibility = controllerVisibility,
                contentWindowInsets = contentWindowInsets,
                expanded = expanded,
                topBar = topBar,
                bottomBar = bottomBar,
                detachedProgressSlider = detachedProgressSlider,
                rhsButtons = rhsButtons,
                gestureLock = gestureLock,
                floatingBottomEnd = floatingBottomEnd,
                leftBottomTips = leftBottomTips,
                floatingMessage = floatingMessage,
            )

            Box(Modifier.matchParentSize().windowInsetsPadding(contentWindowInsets)) {
                rhsSheet()
            }
        }
    }
}

// ... VideoLayer, DanmakuLayer, GestureLayer 保持不变 ...

/**
 * UI 覆盖层的协调器，将不同的 UI 部分分发到各自的 Composable 中处理。
 */
@Composable
private fun BoxScope.UiOverlay(
    controllerState: PlayerControllerState,
    controllerVisibility: ControllerVisibility,
    contentWindowInsets: WindowInsets,
    expanded: Boolean,
    topBar: @Composable RowScope.() -> Unit,
    bottomBar: @Composable RowScope.() -> Unit,
    detachedProgressSlider: @Composable () -> Unit,
    rhsButtons: @Composable ColumnScope.() -> Unit,
    gestureLock: @Composable ColumnScope.() -> Unit,
    floatingBottomEnd: @Composable RowScope.() -> Unit,
    leftBottomTips: @Composable () -> Unit,
    floatingMessage: @Composable BoxScope.() -> Unit,
) {
    // [优化] 恢复流畅的动画效果
    val enterTransition = standardEnter
    val exitTransition = standardExit

    TopBarOverlay(
        visible = controllerVisibility.topBar,
        controllerState = controllerState,
        enter = enterTransition,
        exit = exitTransition,
        content = topBar,
    )

    BottomControlsOverlay(
        visible = controllerVisibility.bottomBar,
        sliderVisible = controllerVisibility.detachedSlider,
        expanded = expanded,
        controllerState = controllerState,
        insets = contentWindowInsets,
        enter = enterTransition,
        exit = exitTransition,
        bottomBar = bottomBar,
        detachedProgressSlider = detachedProgressSlider,
    )

    SideControlsOverlay(
        buttonsVisible = controllerVisibility.rhsBar,
        lockVisible = controllerVisibility.gestureLock,
        insets = contentWindowInsets,
        enter = enterTransition,
        exit = exitTransition,
        buttons = rhsButtons,
        lock = gestureLock,
    )

    FloatingElementsOverlay(
        bottomEndVisible = controllerVisibility.floatingBottomEnd && !expanded,
        bottomStartVisible = true, // leftBottomTips 总是可见
        insets = contentWindowInsets,
        enter = enterTransition,
        exit = exitTransition,
        bottomEndContent = floatingBottomEnd,
        bottomStartContent = leftBottomTips,
    )

    CenterMessageOverlay(
        insets = contentWindowInsets,
        content = floatingMessage,
    )
}

// [优化] 以下是将 UiOverlay 拆分后的独立组件

@Composable
private fun BoxScope.TopBarOverlay(
    visible: Boolean,
    controllerState: PlayerControllerState,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable RowScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.align(Alignment.TopCenter),
        enter = enter,
        exit = exit,
    ) {
        val alwaysOnRequester = rememberAlwaysOnRequester(controllerState, "topBar")
        Column(
            Modifier
                .fillMaxWidth()
                .background(TopScrim)
                .hoverToRequestAlwaysOn(alwaysOnRequester)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                    content()
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BoxScope.BottomControlsOverlay(
    visible: Boolean,
    sliderVisible: Boolean,
    expanded: Boolean,
    controllerState: PlayerControllerState,
    insets: WindowInsets,
    enter: EnterTransition,
    exit: ExitTransition,
    bottomBar: @Composable RowScope.() -> Unit,
    detachedProgressSlider: @Composable () -> Unit,
) {
    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
        val bottomAreaModifier = Modifier
            .windowInsetsPadding(insets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))

        AnimatedVisibility(
            visible = visible,
            enter = enter,
            exit = exit,
        ) {
            val alwaysOnRequester = rememberAlwaysOnRequester(controllerState, "bottomBar")
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(BottomScrim)
                    .hoverToRequestAlwaysOn(alwaysOnRequester)
                    .pointerInput(alwaysOnRequester) {
                        awaitEachGesture {
                            awaitFirstDown(); alwaysOnRequester.request()
                            try {
                                waitForUpOrCancellation()
                            } finally {
                                alwaysOnRequester.cancelRequest()
                            }
                        }
                    }
            ) {
                Spacer(Modifier.height(if (expanded) 12.dp else 6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().then(bottomAreaModifier),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompositionLocalProvider(LocalContentColor provides Color.White) {
                        bottomBar()
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = sliderVisible,
            enter = enter,
            exit = exit,
        ) {
            Row(Modifier.padding(horizontal = 4.dp, vertical = 12.dp).then(bottomAreaModifier)) {
                detachedProgressSlider()
            }
        }
    }
}

@Composable
private fun BoxScope.SideControlsOverlay(
    buttonsVisible: Boolean,
    lockVisible: Boolean,
    insets: WindowInsets,
    enter: EnterTransition,
    exit: ExitTransition,
    buttons: @Composable ColumnScope.() -> Unit,
    lock: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 16.dp)
            .windowInsetsPadding(insets.only(WindowInsetsSides.End)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(visible = buttonsVisible, enter = enter, exit = exit) { buttons() }
        AnimatedVisibility(visible = lockVisible, enter = enter, exit = exit) { lock() }
    }
}

@Composable
private fun BoxScope.FloatingElementsOverlay(
    bottomEndVisible: Boolean,
    bottomStartVisible: Boolean,
    insets: WindowInsets,
    enter: EnterTransition,
    exit: ExitTransition,
    bottomEndContent: @Composable RowScope.() -> Unit,
    bottomStartContent: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = bottomEndVisible,
        modifier = Modifier.align(Alignment.BottomEnd),
        enter = enter,
        exit = exit,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .windowInsetsPadding(insets.only(WindowInsetsSides.End)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                bottomEndContent()
            }
        }
    }

    if (bottomStartVisible) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .windowInsetsPadding(insets.only(WindowInsetsSides.Start + WindowInsetsSides.Bottom)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bottomStartContent()
        }
    }
}

@Composable
private fun BoxScope.CenterMessageOverlay(
    insets: WindowInsets,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(insets),
        contentAlignment = Alignment.Center,
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelSmall) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground.slightlyWeaken()) {
                content()
            }
        }
    }
}

@Composable
private fun VideoLayer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}, // 阻止点击事件穿透
        content = content
    )
}

@Composable
private fun DanmakuLayer(
    insets: WindowInsets,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .windowInsetsPadding(insets.only(WindowInsetsSides.Vertical)),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            content()
        }
    }
}

@Composable
private fun GestureLayer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Stable
private fun ControllerVisibility.withGestureLocked(gestureLocked: Boolean): ControllerVisibility {
    return if (gestureLocked) {
        copy(
            topBar = false,
            bottomBar = false,
            detachedSlider = false,
            rhsBar = false,
        )
    } else {
        this
    }
}

@Stable
private fun ControllerVisibility.withExpanded(isExpanded: Boolean): ControllerVisibility {
    return if (isExpanded) {
        copy(floatingBottomEnd = false)
    } else {
        this
    }
}

@Composable
private fun Color.slightlyWeaken(): Color {
    return copy(alpha = 0.618f)
}