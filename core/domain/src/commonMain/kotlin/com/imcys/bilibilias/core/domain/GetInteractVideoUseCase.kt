package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.InteractiveChoiceDetails

class GetInteractVideoUseCase(
    private val api: BilibiliApi
) {
    /**
     * 模块id映射到node，模块id是不会重复的，但是模块内的cid是会与其他模块内的cid重复的，为了防止重复下载
     */
    private val edgeIdToNodeMap: HashMap<Long, Node> = HashMap()

    /**
     * cid映射到node
     */
    private val cidToNodeMap: HashMap<Long, Node> = HashMap()
    suspend operator fun invoke(aid: Long, graphVersion: Int, rootCid: Long) {
        val processingQueue: ArrayDeque<TraversalItem> = ArrayDeque()

        processingQueue.addLast(TraversalItem(edgeId = 0L, contentId = rootCid, level = 0))

        while (processingQueue.isNotEmpty()) {
            val currentItem = processingQueue.removeFirst()
            val edgeId = currentItem.edgeId
            val contentId = currentItem.contentId
            val currentLevel = currentItem.level

            if (edgeId in edgeIdToNodeMap) {
                continue
            }

            // Fetch data for the current edge/module
            val edgeInfo: InteractiveChoiceDetails?
            try {
                edgeInfo = getSteinEdgeData(aid, graphVersion, edgeId)
            } catch (e: Exception) {
                // Handle API error: log, skip node, or rethrow as a specific exception
                println("Error fetching data for edgeId $edgeId: ${e.message}")
                // Depending on desired behavior, you might 'continue' here
                // or propagate the error. For now, let's assume skipping.
                continue
            }
            edgeInfo ?: continue
            val node = Node(
                cid = contentId,
                edgeId = edgeId,
                title = edgeInfo.title,
                level = currentLevel
            )

            edgeIdToNodeMap[edgeId] = node
            // Only add to cidToNodeMap if it's not already pointing to a different node instance
            // or if the policy is to overwrite/update.
            // If CIDs are truly reusable and should point to THE SAME node instance,
            // you might need to check cidToNodeMap first.
            // For now, assume a new Node instance per unique edgeId.
            cidToNodeMap[contentId] = node


            val questions = edgeInfo.edges.questions
            if (questions.isEmpty()) {
                node.isLeafNode = true
            } else {
                val childNodeLevel = currentLevel + 1
                for (question in questions) {
                    for (choice in question.choices) {
                        val nextEdgeId = choice.id
                        val nextContentId = choice.cid

                        // Add to queue only if not already processed.
                        // The check `nextEdgeId in edgeIdToNodeMap` at the start of the loop
                        // will handle nodes already fully processed.
                        // To avoid adding the same child multiple times to the queue if multiple paths
                        // lead to it before it's processed, you might need an additional "isQueued" set,
                        // or rely on the `edgeIdToNodeMap` check at the start of the loop.
                        // For simplicity, the current check is fine for correctness.
                        if (nextEdgeId !in edgeIdToNodeMap) {
                            processingQueue.addLast(
                                TraversalItem(
                                    nextEdgeId,
                                    nextContentId,
                                    childNodeLevel
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getSteinEdgeData(
        aid: Long,
        graphVersion: Int,
        edgeId: Long
    ): InteractiveChoiceDetails? {
        return api.getInteractiveChoiceOutcome(aid, graphVersion, edgeId)
    }
}

private data class TraversalItem(val edgeId: Long, val contentId: Long, val level: Int)

/**
 * @property level 当前节点所在的第几层
 */
data class Node(
    val cid: Long,
    val edgeId: Long,
    val title: String,
    val level: Int,
    var isLeafNode: Boolean = false
)