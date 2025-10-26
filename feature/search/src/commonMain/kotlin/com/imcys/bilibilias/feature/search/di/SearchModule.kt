package com.imcys.bilibilias.feature.search.di

import com.imcys.bilibilias.feature.search.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val SearchModule = module {
    viewModelOf(::SearchViewModel)
}