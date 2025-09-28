package com.imcys.bilibilias.core.ffmpeg

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val MediaMultiplexerModule: Module = module {
    singleOf(::FfmpegMediaMultiplexer) bind MediaMultiplexer::class
}