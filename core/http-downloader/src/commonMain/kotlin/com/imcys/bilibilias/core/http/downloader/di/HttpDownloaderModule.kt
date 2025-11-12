package com.imcys.bilibilias.core.http.downloader.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.imcys.bilibilias.core.datastore.asDataStoreSerializer
import com.imcys.bilibilias.core.datastore.new
import com.imcys.bilibilias.core.datastore.resolveDataStoreFile
import com.imcys.bilibilias.core.di.applicationScope
import com.imcys.bilibilias.core.http.downloader.HttpDownloader
import com.imcys.bilibilias.core.http.downloader.KtorPersistentHttpDownloader
import com.imcys.bilibilias.core.http.downloader.model.DownloadState
import com.imcys.bilibilias.core.ktor.client.createHttpClient
import com.imcys.bilibilias.core.logging.logger
import com.imcys.bilibilias.core.platform.AppDirs
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.builtins.ListSerializer
import org.koin.dsl.module

val HttpDownloaderModule = module {
    single<HttpDownloader> {
        KtorPersistentHttpDownloader(
            dataStore = DataStoreFactory.new(
                ListSerializer(DownloadState.serializer()).asDataStoreSerializer { emptyList() },
                produceFile = {
                    resolveDataStoreFile(
                        get<AppDirs>().dataStoreDir,
                        "ktor_persistent_http_downloader"
                    )
                },
                corruptionHandler = ReplaceFileCorruptionHandler { emptyList() },
                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
            ),
            client = createHttpClient {
                defaultRequest {
                    header(HttpHeaders.Referrer, "https://www.bilibili.com")
                }
                Logging {
                    level = LogLevel.HEADERS
                    logger = object : Logger {
                        private val logger = logger<HttpDownloader>()

                        override fun log(message: String) {
                            logger.info { message }
                        }
                    }
                }
            },
            fileSystem = SystemFileSystem,
            baseSaveDir = get<AppDirs>().defaultBaseMediaCacheDir.path,
        )
    }
}