package com.euphoiniateam.euphonia.ui.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository
import com.euphoiniateam.euphonia.domain.usecases.GetDefaultSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.GetSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.SaveSettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val getDefaultSettingsUseCase = GetDefaultSettingsUseCase()
    private val getSettingsUseCase by lazy { GetSettingsUseCase(settingsRepository = repository) }
    private val saveSettingsUseCase by lazy { SaveSettingsUseCase(settingsRepository = repository) }
    val settingsLiveData = MutableLiveData(Settings())
    val isSavedLiveData: MutableLiveData<Boolean?> = MutableLiveData(false)

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO){
            val settingsConfig = getSettingsUseCase.execute()
            settingsLiveData.postValue(settingsConfig)
        }
    }

    fun saveSettings(
          history: Boolean,
          recording_audio: Boolean,
          recording_stave:Boolean,
          piano_size: Float,
          stave_size: Float,
          showing_stave: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val params = Settings(history, recording_audio, recording_stave, piano_size, stave_size, showing_stave)
            val result: Boolean = saveSettingsUseCase.execute(params)
            loadSettings()
        }
    }

    fun defaultSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val defaultSettings = getDefaultSettingsUseCase.execute()
            settingsLiveData.postValue(defaultSettings)
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