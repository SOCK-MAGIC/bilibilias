package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.domain.model.GetDmUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val UseCaseModule = module {
    singleOf(::GetCachedEpisodeStateUseCase)
    singleOf(::GetEpisodeInfoUseCase)
    singleOf(::MediaSourceUseCase)
    singleOf(::GetIdFromTextUseCase)
    singleOf(::GetDmUseCase)
}