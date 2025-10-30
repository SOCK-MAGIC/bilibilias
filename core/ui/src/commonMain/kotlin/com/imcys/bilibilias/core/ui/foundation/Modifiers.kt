package com.imcys.bilibilias.core.ui.foundation

import androidx.compose.ui.Modifier
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun Modifier.ifThen(
    condition: Boolean,
    modifier: Modifier.Companion.() -> Modifier?
): Modifier {
    return if (condition) this.then(modifier(Modifier.Companion) ?: Modifier) else this
}