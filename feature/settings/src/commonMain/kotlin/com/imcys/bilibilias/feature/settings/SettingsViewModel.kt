package com.imcys.bilibilias.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.bilibilias.core.data.util.ErrorMonitor
import com.imcys.bilibilias.core.datastore.AsPreferencesDataSource
import com.imcys.bilibilias.core.datastore.model.Codecs
import com.imcys.bilibilias.core.datastore.model.Resolution
import com.imcys.bilibilias.core.datastore.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val asPreferencesDataSource: AsPreferencesDataSource,
    private val errorMonitor: ErrorMonitor,
) : ViewModel() {
    val preferences = asPreferencesDataSource.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences.DEFAULT
        )

    fun setTryLook(enable: Boolean) {
        viewModelScope.launch {
            asPreferencesDataSource.setTryLookEnabled(enable)
        }
    }

    fun setDecoderCodecPriorityList(newCodecs: List<Codecs>) {
        viewModelScope.launch {
            asPreferencesDataSource.setDecoderCodecPriorityList(newCodecs)
        }
    }

    fun setVideoResolutions(newResolutions: List<Resolution>) {
        viewModelScope.launch {
            asPreferencesDataSource.setVideoResolutions(newResolutions)
        }
    }

    fun setAudioResolutions(newResolutions: List<Resolution>) {
        viewModelScope.launch {
            asPreferencesDataSource.setAudioResolutions(newResolutions)
        }
    }

    fun setSubtitles(enable: Boolean) {
        viewModelScope.launch {
            asPreferencesDataSource.setSubtitles(enable)
        }
    }
    fun errorTip(message: String) {
        errorMonitor.addMessageByString(message)
    }
}