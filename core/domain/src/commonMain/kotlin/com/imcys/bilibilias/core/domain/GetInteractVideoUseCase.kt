package com.imcys.bilibilias.core.domain

import androidx.collection.mutableLongObjectMapOf
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.InteractiveChoiceDetails
import com.imcys.bilibilias.core.logging.logger

class GetInteractVideoUseCase(
    private val api: BilibiliApi
) {
    /**
     * 模块id映射到node，模块id是不会重复的，但是模块内的cid是会与其他模块内的cid重复的，为了防止重复下载
     */
    private val edgeIdToNodeMap = mutableLongObjectMapOf<Node>()

    fun getSortedNodes(): List<Node> {
        return buildSet {
            edgeIdToNodeMap.forEachValue {
                add(it)
            }
        }
            .distinctBy { it.cid }
            .sortedBy { it.edgeId }
    }

    private val logger = logger<GetInteractVideoUseCase>()
    suspend fun invoke(aid: Long, bvid: String, rootCid: Long) {
        val graphVersion = getGraphVersion(aid, rootCid)

        val processingQueue: ArrayDeque<TraversalItem> = ArrayDeque()

        processingQueue.addLast(TraversalItem(edgeId = 0L, contentId = rootCid, level = 0))

        while (processingQueue.isNotEmpty()) {
            val currentItem = processingQueue.removeFirst()
            val edgeId = currentItem.edgeId
            val cid = currentItem.contentId
            val currentLevel = currentItem.level

            if (edgeId in edgeIdToNodeMap) {
                continue
            }

            val edgeInfo: InteractiveChoiceDetails?
            try {
                edgeInfo = getInteractiveChoiceOutcome(aid, bvid, graphVersion, edgeId)
            } catch (e: Exception) {
                logger.error(e) { "Error fetching data for edgeId $edgeId" }
                continue
            }

            edgeInfo ?: continue
            val node = Node(
                cid = cid,
                edgeId = edgeId,
                title = edgeInfo.title,
                level = currentLevel,
                isLeafNode = edgeInfo.isLeaf,
                width = edgeInfo.edges.dimension.width,
                height = edgeInfo.edges.dimension.height,
            )

            edgeIdToNodeMap[edgeId] = node

            val questions = edgeInfo.edges.questions
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

    private suspend fun getInteractiveChoiceOutcome(
        aid: Long,
        bvid: String,
        graphVersion: Int,
        edgeId: Long
    ): InteractiveChoiceDetails? {
        return api.getInteractiveChoiceOutcome(aid, bvid, graphVersion, edgeId)
    }

    private suspend fun getGraphVersion(aid: Long, cid: Long): Int {
        return api.getPlayerInfo(aid, cid).interaction.graphVersion
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
    val isLeafNode: Boolean,
    val width: Int,
    val height: Int,
)