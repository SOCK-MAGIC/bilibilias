package com.imcys.bilibilias.core.data

import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.model.MediaCacheMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.IOException
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path

class MediaCacheRepository(
    private val fileSystem: FileSystem,
) {
    private val logger = KotlinLogging.logger("MediaCacheRepository")

    /**
     * 根据给定的元数据，删除所有关联的缓存文件。
     * @param cacheMetadata 描述要删除文件的元数据对象。
     * @return 如果所有文件都成功删除，则返回 true。
     */
    fun delete(cacheMetadata: MediaCacheMetadata): Boolean {
        val allPaths = getAllFilePaths(cacheMetadata)
        val failures = allPaths.count { !deleteFile(it) }

        if (failures == 0) {
            logger.info { "Successfully deleted all files for metadata created at ${cacheMetadata.createdAt}" }
        } else {
            logger.warn { "$failures/${allPaths.size} files failed to delete for metadata created at ${cacheMetadata.createdAt}" }
        }

        return failures == 0
    }

    /**
     * 从元数据对象中提取所有相关的文件路径。
     */
    private fun getAllFilePaths(cacheMetadata: MediaCacheMetadata): List<Path> {
        return buildList {
            cacheMetadata.metadata.forEach { add(Path(it.fullPath)) }
            cacheMetadata.extra[ASS_FILE]?.let { add(Path(it)) }
        }
    }

    /**
     * 删除单个文件，并处理异常。
     */
    private fun deleteFile(path: Path): Boolean {
        return try {
            if (fileSystem.exists(path)) {
                fileSystem.delete(path)
                logger.debug { "Deleted: $path" }
            } else {
                logger.warn { "File not found, skipping delete: $path" }
            }
            true
        } catch (e: IOException) {
            logger.error(e) { "Failed to delete file: $path" }
            false
        }
    }
}