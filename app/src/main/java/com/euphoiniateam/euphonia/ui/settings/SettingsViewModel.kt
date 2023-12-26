package com.euphoiniateam.euphonia.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveApi
import com.euphoiniateam.euphonia.data.datasources.stave.StaveCache
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.StaveRepositoryImpl
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository
import com.euphoiniateam.euphonia.domain.usecases.GetDefaultSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.GetSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.SaveSettingsUseCase
import com.euphoiniateam.euphonia.ui.creation.CreationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val fragment = SettingsFragment()
    private val getDefaultSettingsUseCase = GetDefaultSettingsUseCase()
    private val getSettingsUseCase by lazy { GetSettingsUseCase(settingsRepository = repository) }
    private val saveSettingsUseCase by lazy { SaveSettingsUseCase(settingsRepository = repository) }

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO){
            fragment.switchHistory.isChecked = getSettingsUseCase.execute().history
            fragment.switchRecordingAudio.isChecked = getSettingsUseCase.execute().recording_audio
            fragment.switchRecordingStave.isChecked = getSettingsUseCase.execute().recording_stave
            fragment.sliderPianoSize.value = getSettingsUseCase.execute().piano_size
            fragment.sliderStaveSize.value = getSettingsUseCase.execute().stave_size
            fragment.switchShowingStave.isChecked = getSettingsUseCase.execute().showing_stave
        }
    }

    fun saveSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val param1 = fragment.switchHistory.isChecked
            val param2 = fragment.switchRecordingAudio.isChecked
            val param3 = fragment.switchRecordingStave.isChecked
            val param4 = fragment.sliderPianoSize.value
            val param5 = fragment.sliderStaveSize.value
            val param6 = fragment.switchShowingStave.isChecked

            val params = Settings(param1, param2, param3, param4, param5, param6)
            val result: Boolean = saveSettingsUseCase.execute(params)
        }
    }

    fun defaultSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val defaultSettings = getDefaultSettingsUseCase.execute()
            fragment.switchHistory.isChecked = defaultSettings.history
            fragment.switchRecordingAudio.isChecked = defaultSettings.recording_audio
            fragment.switchRecordingStave.isChecked = defaultSettings.recording_stave
            fragment.sliderPianoSize.value = defaultSettings.piano_size
            fragment.sliderStaveSize.value = defaultSettings.stave_size
            fragment.switchShowingStave.isChecked = defaultSettings.showing_stave
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    repository = SettingsRepositoryImpl(context)
                )
            }
        }
    }
}