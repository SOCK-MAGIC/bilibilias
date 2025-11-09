package com.imcys.bilibilias.core.domain

import androidx.collection.mutableLongObjectMapOf
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.InteractiveChoiceDetails
import com.imcys.bilibilias.core.logging.logger

/**
 * 获取Bilibili互动视频所有节点信息的无状态UseCase。
 * 它通过遍历互动视频的节点图来收集所有可达的分支信息。
 */
class GetInteractVideoUseCase(
    private val api: BilibiliApi
) {
    private val logger = logger<GetInteractVideoUseCase>()

    /**
     * @param aid 视频的AID
     * @param bvid 视频的BVID
     * @param rootCid 起始节点的CID
     * @return 一个包含所有节点信息的列表，按edgeId排序。
     */
    suspend operator fun invoke(aid: Long, bvid: String, rootCid: Long): List<Node> {
        val edgeIdToNodeMap = mutableLongObjectMapOf<Node>()

        val graphVersion = getGraphVersion(aid, rootCid)
        val processingQueue: ArrayDeque<TraversalItem> = ArrayDeque()

        // 从根节点开始遍历
        processingQueue.addLast(TraversalItem(edgeId = 0L, contentId = rootCid, level = 0))

        while (processingQueue.isNotEmpty()) {
            val currentItem = processingQueue.removeFirst()
            val edgeId = currentItem.edgeId
            val cid = currentItem.contentId
            val currentLevel = currentItem.level

            // 如果节点已经处理过，则跳过
            if (edgeId in edgeIdToNodeMap) {
                continue
            }

            val edgeInfo = try {
                getInteractiveChoiceOutcome(aid, bvid, graphVersion, edgeId)
            } catch (e: Exception) {
                logger.error(e) { "获取互动视频节点失败，edgeId: $edgeId" }
                null
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

            edgeIdToNodeMap.put(edgeId, node)

            // 如果不是叶子节点，则将其所有子选项加入处理队列
            if (!edgeInfo.isLeaf) {
                val childNodeLevel = currentLevel + 1
                for (question in edgeInfo.edges.questions) {
                    for (choice in question.choices) {
                        val nextEdgeId = choice.id
                        if (nextEdgeId !in edgeIdToNodeMap) {
                            processingQueue.addLast(
                                TraversalItem(nextEdgeId, choice.cid, childNodeLevel)
                            )
                        }
                    }
                }
            }
        }

        edgeIdToNodeMap
        return buildSet {
            edgeIdToNodeMap.forEachValue { add(it) }
        }
            .distinctBy { it.cid }
            .sortedBy { it.edgeId }
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