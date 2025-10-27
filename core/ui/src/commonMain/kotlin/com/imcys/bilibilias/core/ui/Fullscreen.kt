package com.imcys.bilibilias.core.ui

import androidx.compose.runtime.Composable

/**
 * Request to set the fullscreen, landscape mode, hiding status bars and navigation bars.
 *
 * If [fullscreen] is `true`, the app will enter fullscreen mode.
 * Otherwise, the app go back to the users' preferred mode.
 *
 * Note that when [fullscreen] is `false`, the system bars will be visible,
 * but the app may be still in landscape mode if the user's system is in landscape mode.
 */
@Composable
expect fun setRequestFullScreen(fullscreen: Boolean)

//expect fun Context.setSystemBarVisible(window: PlatformWindowMP, visible: Boolean)

//@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
//@Composable
//inline fun isSystemInFullscreen(): Boolean = LocalPlatformWindow.current.isUndecoratedFullscreen