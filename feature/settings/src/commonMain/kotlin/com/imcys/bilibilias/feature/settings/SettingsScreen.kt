package com.imcys.bilibilias.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alorma.compose.settings.ui.SettingsSwitch
import com.imcys.bilibilias.core.designsystem.component.BackButton
import com.imcys.bilibilias.core.io.SystemPath
import com.imcys.bilibilias.core.model.UserPreferences
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.preferences.collectAsState()

    SettingsContent(
        state = state,
        onBack = onBack,
        updateTryLook = viewModel::setTryLook,
        updateSubtitles = viewModel::setSubtitles,
        errorTip = viewModel::errorTip,
        logDir = viewModel.appDirs.logsDir
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: UserPreferences,
    onBack: () -> Unit = {},
    updateTryLook: (Boolean) -> Unit = {},
    updateSubtitles: (Boolean) -> Unit,
    errorTip: (String) -> Unit,
    logDir: SystemPath,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = { BackButton(onBack = onBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSwitch(
                state.setSubtitle,
                title = { Text("添加字幕") },
                subtitle = { Text("合并视频时添加字幕到视频") },
            ) {
                updateSubtitles(it)
            }

            SettingsSwitch(
                state = state.enableTryLook,
                icon = {
                    if (state.enableTryLook)
                        Icon(Icons.Outlined.Visibility, null)
                    else
                        Icon(Icons.Outlined.VisibilityOff, null)
                },
                title = {
                    Text("免登录1080P")
                },
                subtitle = {
                    Text("免登录查看1080P视频")
                }
            ) {
                updateTryLook(it)
            }
            ShareLogFileSection(logDir, errorTip)
        }
    }
}

@Composable
internal expect fun ShareLogFileSection(logDir: SystemPath, onTip: (String) -> Unit)