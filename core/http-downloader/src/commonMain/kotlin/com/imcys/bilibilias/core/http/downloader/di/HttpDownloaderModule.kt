package com.imcys.bilibilias.core.http.downloader.di

import org.koin.dsl.module

val HttpDownloaderModule = module {
//    single<HttpDownloader> {
//        KtorPersistentHttpDownloader(
//            dataStore = DataStoreFactory.new(
//                ListSerializer(DownloadState.serializer()).asDataStoreSerializer { emptyList() },
//                produceFile = { resolveDataStoreFile("ktor_persistent_http_downloader") },
//                corruptionHandler = ReplaceFileCorruptionHandler { emptyList() },
//                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
//            ),
//            client = createHttpClient {
//                defaultRequest {
//                    header(HttpHeaders.Referrer, "https://www.bilibili.com")
//                }
//                Logging {
//                    level = LogLevel.HEADERS
//                    logger = object : io.ktor.client.plugins.logging.Logger {
//                        private val logger =
//                            logger<HttpDownloader>()
//
//                        override fun log(message: String) {
//                            logger.info { message }
//                        }
//                    }
//                }
//            },
//            fileSystem = SystemFileSystem,
//            baseSaveDir = Path(BuildConfig.MEDIA_DOWNLOAD),
//        )
//    }
}