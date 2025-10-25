package com.imcys.bilibilias.core.videoplayer

import org.openani.mediamp.MediampPlayer

expect suspend fun MediampPlayer.playUri(uris: List<String>)