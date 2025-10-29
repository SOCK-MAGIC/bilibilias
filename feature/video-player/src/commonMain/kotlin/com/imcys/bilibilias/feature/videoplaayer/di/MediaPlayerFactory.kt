package com.imcys.bilibilias.feature.videoplaayer.di

import org.openani.mediamp.MediampPlayer
import kotlin.coroutines.CoroutineContext

interface MediaPlayerFactory {
    fun create(parentCoroutineContext: CoroutineContext): MediampPlayer
}