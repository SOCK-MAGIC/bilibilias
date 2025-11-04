package com.imcys.bilibilias.core.videoplayer.bar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 播放器顶部导航栏
 */
/**
 * 播放器顶部导航栏, 经过优化以提高可重用性和代码清晰度.
 *
 * @param title 顶部栏的标题内容.
 * @param onBackClick 导航图标（返回箭头）被点击时的回调.
 * @param modifier 要应用于此 TopAppBar 的 Modifier.
 * @param actions 顶部栏尾部的操作图标.
 * @param contentColor 应用于标题、导航图标和操作图标的统一颜色.
 * @param windowInsets 应用于此 TopAppBar 的窗口 insets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopBar(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    TopAppBar(
        title = {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                title()
            }
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

        },
        actions = {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                actions()
            }
        },
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}