package com.imcys.bilibilias.core.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.jetbrains.annotations.VisibleForTesting

// TODO refine back behavior - perhaps take a lambda so that each screen / use site can customize back behavior?
// https://github.com/android/nowinandroid/issues/1934
//class AsBackStack(
//    private val startKey: AsNavKey,
//) {
//    internal var backStackMap: LinkedHashMap<AsNavKey, MutableList<AsNavKey>> =
//        linkedMapOf(
//            startKey to mutableStateListOf(startKey),
//        )
//
//    @VisibleForTesting
//    val backStack: SnapshotStateList<AsNavKey> = mutableStateListOf(startKey)
//
//    var currentTopLevelKey: AsNavKey by mutableStateOf(backStackMap.keys.last())
//        private set
//
//    val currentKey: AsNavKey
//        get() = backStackMap[currentTopLevelKey]!!.last()
//
//    fun navigate(key: AsNavKey) {
//        when {
//            // top level singleTop -> clear substack
//            key == currentTopLevelKey -> backStackMap[key] = mutableListOf(key)
//            // top level non-singleTop
//            key.isTopLevel -> {
//                // if navigating back to start destination, pop all other top destinations and
//                // store start destination substack
//                if (key == startKey) {
//                    val tempStack = mapOf(startKey to backStackMap[startKey]!!)
//                    backStackMap.clear()
//                    backStackMap.putAll(tempStack)
//                    // else either restore an existing substack or initiate new one
//                } else {
//                    backStackMap[key] = backStackMap.remove(key) ?: mutableListOf(key)
//                }
//            }
//            // not top level - add to current substack
//            else -> {
//                val currentStack = backStackMap.values.last()
//                // single top
//                if (currentStack.lastOrNull() == key) {
//                    currentStack.removeLastOrNull()
//                }
//                currentStack.add(key)
//            }
//        }
//        updateBackStack()
//    }
//
//    fun popLast(count: Int = 1) {
//        var popCount = count
//        var currentEntry = backStackMap.entries.last()
//        while (popCount > 0) {
//            val currentStack = currentEntry.value
//            if (currentStack.size == 1) {
//                // if current sub-stack only has one key, remove the sub-stack from the map
//                backStackMap.remove(currentEntry.key)
//                when {
//                    // throw if map is empty after pop
//                    backStackMap.isEmpty() -> error(popErrorMessage(count, currentEntry.key))
//                    // otherwise update currentEntry
//                    else -> currentEntry = backStackMap.entries.last()
//                }
//            } else {
//                // if current sub-stack has more than one key, just pop the last key off the sub-stack
//                currentStack.removeLastOrNull()
//            }
//            popCount--
//        }
//        updateBackStack()
//    }
//
//    private fun updateBackStack() {
//        backStack.apply {
//            clear()
//            backStack.addAll(
//                backStackMap.flatMap { it.value },
//            )
//        }
//
//        currentTopLevelKey = backStackMap.keys.last()
//    }
//
//    internal fun restore(map: LinkedHashMap<AsNavKey, MutableList<AsNavKey>>?) {
//        map ?: return
//        backStackMap.clear()
//        backStackMap.putAll(map)
//        updateBackStack()
//    }
//}


class AsBackStack(
    private val startKey: AsNavKey,
) {
    // 1. [核心修复] 使用一个 SnapshotStateList 来严格保证顶级目标的顺序
    private val topLevelKeysInOrder = mutableStateListOf(startKey)

    // 2. [核心修复] backStackMap 的值必须是 SnapshotStateList，以便 Compose 能够监听到内容的增删
    internal var backStackMap: MutableMap<AsNavKey, SnapshotStateList<AsNavKey>> =
        mutableStateMapOf(
            startKey to mutableStateListOf(startKey),
        )

    // `backStack` 用于提供一个扁平化的只读列表，如果需要的话
    @VisibleForTesting
    val backStack: SnapshotStateList<AsNavKey> = mutableStateListOf(startKey)

    // 3. currentTopLevelKey 现在从有序的列表中获取，100% 可靠
    var currentTopLevelKey: AsNavKey by mutableStateOf(topLevelKeysInOrder.last())
        private set

    // 4. currentKey 的 getter 现在可以正确地响应两种变化：
    //    a) currentTopLevelKey 的变化
    //    b) 当前页面栈 (SnapshotStateList) 的内容变化
    val currentKey: AsNavKey
        get() = backStackMap[currentTopLevelKey]!!.last()

    fun navigate(key: AsNavKey) {
        // --- 顶层导航 ---
        if (key.isTopLevel) {
            val existingStack = backStackMap[key]

            // singleTop 行为：如果导航到当前已选中的顶层目标，则清空其子栈
            if (key == currentTopLevelKey && existingStack != null) {
                existingStack.clear()
                existingStack.add(key)
            } else {
                // 如果目标已存在但未选中，或是一个全新的目标
                if (existingStack == null) {
                    // 创建新栈
                    backStackMap[key] = mutableStateListOf(key)
                }
                // [顺序管理] 将此 Key 移动到有序列表的末尾，表示它是最新的
                topLevelKeysInOrder.remove(key)
                topLevelKeysInOrder.add(key)
            }
            // 更新当前的顶级 Key
            currentTopLevelKey = key
        }
        // --- 子页面导航 ---
        else {
            val currentStack = backStackMap[currentTopLevelKey]
            checkNotNull(currentStack) { "Cannot navigate to sub-destination, current top-level stack is null." }

            // singleTop 行为：如果已在栈顶，则不重复添加
            if (currentStack.lastOrNull() != key) {
                currentStack.add(key)
            }
        }
        updateBackStack()
    }

    fun popLast(count: Int = 1) {
        repeat(count) {
            // 从有序列表中获取当前的顶级 Key
            val lastTopLevelKey =
                topLevelKeysInOrder.lastOrNull() ?: error(popErrorMessage(count, startKey))
            val currentStack = backStackMap[lastTopLevelKey]!!

            // 如果当前子栈只剩一个元素（即它自己），则移除整个顶级目标
            if (currentStack.size <= 1) {
                // 但不能移除最后一个顶级目标
                if (topLevelKeysInOrder.size > 1) {
                    topLevelKeysInOrder.removeLastOrNull()
                    backStackMap.remove(lastTopLevelKey)
                    // 更新 currentTopLevelKey 为新的最后一个
                    currentTopLevelKey = topLevelKeysInOrder.last()
                } else {
                    // 如果无法再 pop，可以抛出异常或静默失败
                    // error(popErrorMessage(count, currentEntry.key))
                    return // 停止 pop
                }
            }
            // 否则，只从当前子栈中移除最后一个元素
            else {
                currentStack.removeLastOrNull()
            }
        }
        updateBackStack()
    }

    private fun updateBackStack() {
        // 这个扁平化的 backStack 如果有其他地方使用，就需要更新
        backStack.apply {
            clear()
            // 按照 topLevelKeysInOrder 的顺序来 flatMap，保证最终顺序正确
            addAll(
                topLevelKeysInOrder.flatMap { key -> backStackMap[key]!! }
            )
        }
    }

    // restore 函数也需要调整以适应新的数据结构
    internal fun restore(map: LinkedHashMap<AsNavKey, MutableList<AsNavKey>>?) {
        map ?: return

        // 清空现有状态
        backStackMap.clear()
        topLevelKeysInOrder.clear()

        // 恢复状态，同时填充有序列表和 Map
        map.forEach { (key, stack) ->
            topLevelKeysInOrder.add(key)
            backStackMap[key] = mutableStateListOf<AsNavKey>().also { it.addAll(stack) }
        }

        // 更新当前 Key
        if (topLevelKeysInOrder.isNotEmpty()) {
            currentTopLevelKey = topLevelKeysInOrder.last()
        }

        updateBackStack()
    }
}
interface AsNavKey {
    val isTopLevel: Boolean
}

private fun popErrorMessage(count: Int, lastPopped: AsNavKey) =
    """
        Failed to pop $count entries. BackStack has been popped to an empty stack. Last
        popped key is $lastPopped.
    """.trimIndent()