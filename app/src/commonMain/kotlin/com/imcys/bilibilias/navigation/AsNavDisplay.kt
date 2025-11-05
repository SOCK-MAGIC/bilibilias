package com.imcys.bilibilias.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.imcys.bilibilias.core.navigation.AsBackStack
import com.imcys.bilibilias.core.navigation.AsNavKey

@Suppress("VisibleForTests", "NonSkippableComposable")
@Composable
fun AsNavDisplay(
    asBackStack: AsBackStack,
    entryProviderBuilders: EntryProviderScope<AsNavKey>.() -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = asBackStack.backStack,
        onBack = { asBackStack.popLast() },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
        entryProvider = entryProvider {
            entryProviderBuilders()
        },
        modifier = modifier,
    )
}