package com.euphoiniateam.euphonia.ui.settings

import android.content.Context
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val getDefaultSettingsUseCase = GetDefaultSettingsUseCase()
    private val getSettingsUseCase by lazy { GetSettingsUseCase(settingsRepository = repository) }
    private val saveSettingsUseCase by lazy { SaveSettingsUseCase(settingsRepository = repository) }
    val settingsStateFlow = MutableStateFlow(Settings())
    val isSavedStateFlow: MutableStateFlow<Boolean?> = MutableStateFlow(false)
    var screenStateFlow = MutableStateFlow(
        VkLoadingState.EMPTY
    )

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsConfig = getSettingsUseCase.execute()
            settingsStateFlow.tryEmit(settingsConfig)
        }
    }

    fun processProfile() {
        viewModelScope.launch {
            screenStateFlow.emit(VkLoadingState.LOADING)
            delay(5000)
            screenStateFlow.emit(VkLoadingState.SUCCESS)
        }
    }

    fun endProfileProcess() {
        viewModelScope.launch {
            screenStateFlow.emit(VkLoadingState.EMPTY)
        }
    }

    fun saveSettings(
        history: Boolean,
        recording_audio: Boolean,
        recording_stave: Boolean,
        piano_size: Float,
        stave_size: Float,
        showing_stave: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val params = Settings(
                history,
                recording_audio,
                recording_stave,
                piano_size,
                stave_size,
                showing_stave
            )
            val result: Boolean = saveSettingsUseCase.execute(params)
            loadSettings()
        }
    }

    fun defaultSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val defaultSettings = getDefaultSettingsUseCase.execute()
            settingsStateFlow.tryEmit(defaultSettings)
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

enum class VkLoadingState {
    EMPTY,
    LOADING,
    SUCCESS
}
