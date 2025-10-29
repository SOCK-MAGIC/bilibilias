package com.imcys.bilibilias.feature.videoplaayer.di// In: androidMain/your/package/AndroidMediampPlayerFactory.kt

import android.content.Context
import org.openani.mediamp.MediampPlayer
import kotlin.coroutines.CoroutineContext

class AndroidMediaPlayerFactory(
    private val context: Context
) : MediaPlayerFactory {

    override fun create(parentCoroutineContext: CoroutineContext): MediampPlayer {
        return MediampPlayer(
            context = context,
            parentCoroutineContext = parentCoroutineContext
        )
    }
}