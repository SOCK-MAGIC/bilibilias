package com.imcys.bilibilias.core.videoplayer

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.exoplayer.ExoPlayerMediampPlayer
import org.openani.mediamp.exoplayer.compose.ExoPlayerMediampPlayerSurface

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    player: MediampPlayer,
    modifier: Modifier
) {
    val isPreviewing by rememberUpdatedState(LocalInspectionMode.current)

    if (isPreviewing) {
        Box(modifier)
    } else {
        ExoPlayerMediampPlayerSurface(player as ExoPlayerMediampPlayer, modifier) {
            controllerAutoShow = false
            useController = false
            controllerHideOnTouch = false
            subtitleView?.apply {
                this.setStyle(
                    CaptionStyleCompat(
                        Color.WHITE,
                        0x000000FF,
                        0x00000000,
                        CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                        Color.BLACK,
                        Typeface.DEFAULT,
                    ),
                )
            }
            setControllerVisibilityListener(
                ControllerVisibilityListener { visibility ->
                    if (visibility == View.VISIBLE) {
                        hideController()
                    }
                },
            )
        }
    }
}