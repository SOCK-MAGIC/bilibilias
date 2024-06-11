package com.imcys.bilibilias.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.imcys.bilibilias.core.common.download.DefaultConfig
import com.imcys.bilibilias.core.datastore.preferences.AsPreferencesDataSource
import com.imcys.bilibilias.feature.common.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class DefaultSettingsComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val asPreferencesDataSource: AsPreferencesDataSource
) : SettingsComponent, BaseViewModel<UserEditEvent, UserEditableSettings>(componentContext) {

    @Composable
    override fun models(events: Flow<UserEditEvent>): UserEditableSettings {
        var fileStoragePath by remember { mutableStateOf<String?>(null) }
        var fileNamingRule by remember { mutableStateOf<String?>(null) }
        var autoMerge by remember { mutableStateOf(false) }
        var autoImport by remember { mutableStateOf(false) }
        var shouldAppcenter by remember { mutableStateOf(false) }
        var command by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            asPreferencesDataSource.userData.collect {
                fileStoragePath = it.fileStoragePath
                fileNamingRule = it.fileNamingRule
                autoMerge = it.autoMerge
                autoImport = it.autoImport
                shouldAppcenter = it.shouldAppcenter
                command = it.command
            }
        }
        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is UserEditEvent.onChangeAutoImport ->
                        asPreferencesDataSource.setAutoImportToBilibili(event.state)

                    is UserEditEvent.onChangeAutoMerge ->
                        asPreferencesDataSource.setAutoMerge(event.state)

                    is UserEditEvent.onChangeCommand ->
                        asPreferencesDataSource.setCommand(command)

                    is UserEditEvent.onChangeFileNamingRule ->
                        asPreferencesDataSource.setFileNameRule(event.rule)

                    is UserEditEvent.onChangeStoragePath ->
                        asPreferencesDataSource.setFileStoragePath(event.path)

                    is UserEditEvent.onChangeWill ->
                        asPreferencesDataSource.setShouldAppcenter(event.state)
                }
            }
        }
        return UserEditableSettings(
            fileStoragePath ?: DefaultConfig.DEFAULT_STORE_PATH,
            fileNamingRule ?: DefaultConfig.DEFAULT_NAMING_RULE,
            autoMerge,
            autoImport,
            command ?: DefaultConfig.DEFAULT_COMMAND,
            shouldAppcenter
        )
    }

    @AssistedFactory
    interface Factory : SettingsComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
        ): DefaultSettingsComponent
    }
}
