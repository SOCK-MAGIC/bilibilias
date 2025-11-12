package com.imcys.bilibilias.core.di

import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

val CommonModule = module {
    single<CoroutineScope> {
        CoroutineScope(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                logger("ApplicationScope").warn(throwable) {
                    "Uncaught exception in coroutine $coroutineContext"
                }
            } + SupervisorJob() + Dispatchers.IO,
        )
    }
    includes(CommonInternalModule)
}
internal expect val CommonInternalModule: Module
val Scope.applicationScope: CoroutineScope
    get() = get()