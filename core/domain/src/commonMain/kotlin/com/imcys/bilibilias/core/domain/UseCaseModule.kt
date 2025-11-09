package com.imcys.bilibilias.core.domain

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val UseCaseModule = module {
    factoryOf(::GetCachedEpisodeStateUseCase)
    factoryOf(::GetEpisodeInfoUseCase)
    factoryOf(::MediaSourceUseCase)
    factoryOf(::ParseBilibiliIdUseCase)
    factoryOf(::GetDmUseCase)
    factoryOf(::GetPgcEpisodeInfoUseCase)
    factoryOf(::GetUgcEpisodeInfoUseCase)
    factoryOf(::GetPugvEpisodeInfoUseCase)
    factoryOf(::GetInteractVideoUseCase)
}