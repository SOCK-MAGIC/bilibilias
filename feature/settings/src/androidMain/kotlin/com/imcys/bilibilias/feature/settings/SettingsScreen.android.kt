package com.imcys.bilibilias.feature.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.io.toFile
import kotlinx.io.files.Path

@Composable
internal actual fun ShareLogFileSection(onTip: (String) -> Unit) {
    val context = LocalContext.current

    val shareAction = remember {
        {
            try {
                val logFile = getCurrentLogFile().toFile()
                if (logFile.exists()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        logFile
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    val chooser = Intent.createChooser(shareIntent, "分享日志文件")
                    context.startActivity(chooser)
                } else {
                    onTip("文件不存在")
                }
            } catch (e: ActivityNotFoundException) {
                onTip("没有应用可分享")
            } catch (e: Exception) {
                onTip("未知错误 ${e.message}")
            }
        }
    }


    Section(
        title = { Text("分享日志文件") },
        icon = { Icon(Icons.Outlined.Feedback, null) }
    ) {
        shareAction()
    }
}

private fun getCurrentLogFile(): Path {
    return BuildConfig.LOG_DIR.resolve("app.log")
}