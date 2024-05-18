package com.euphoiniateam.euphonia.ui.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.repos.GenerationRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.data.source.stave.StaveCache
import com.euphoiniateam.euphonia.data.source.stave.StaveRemoteDataSourceImp
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.models.VKUser
import com.euphoiniateam.euphonia.domain.repos.GenerationRepository
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository
import com.euphoiniateam.euphonia.domain.usecases.GetDefaultSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.GetSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.SaveSettingsUseCase
import com.euphoiniateam.euphonia.tools.ProfileMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val generationRepository: GenerationRepository
) : ViewModel() {
    private val getDefaultSettingsUseCase = GetDefaultSettingsUseCase()
    private val getSettingsUseCase by lazy { GetSettingsUseCase(settingsRepository = repository) }
    private val saveSettingsUseCase by lazy { SaveSettingsUseCase(settingsRepository = repository) }
    val settingsStateFlow = MutableStateFlow(Settings())
    var screenStateFlow = MutableStateFlow(
        VkLoadingState.EMPTY
    )

    val userStateFlow: MutableStateFlow<VKUser?> = MutableStateFlow(null)
    val friendsStateFlow: MutableStateFlow<List<VKUser>?> = MutableStateFlow(null)
    private var userProfile: VKUser? = null
    private var friendsProfiel: List<VKUser>? = null

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsConfig = getSettingsUseCase.execute()
            settingsStateFlow.tryEmit(settingsConfig)
        }
    }

    fun processProfile(
        getFriends: () -> List<VKUser>?,
        getProfile: () -> VKUser?,
        onSuccess: (Uri) -> Unit,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            userStateFlow.collect {
                userProfile = it
                friendsProfiel?.let {
                    endProfileProcess(context, onSuccess)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            screenStateFlow.emit(VkLoadingState.LOADING)

            getFriends()
            getProfile()
            friendsStateFlow.collect {
                friendsProfiel = it
                userProfile?.let {
                    endProfileProcess(context, onSuccess)
                }
            }
        }
    }

    fun endProfileProcess(context: Context, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch {
            try {
                val prompt = ProfileMapper.map(
                    context,
                    userProfile!!.firstName,
                    userProfile!!.lastName,
                    friendsProfiel!!.map { it.id.toString() },
                    userProfile!!.id
                )
                val midi = generationRepository.generateNewNoIncludedPrompt(prompt)
                onSuccess(midi)
                screenStateFlow.emit(VkLoadingState.SUCCESS)
            } catch (e: Exception) {
                screenStateFlow.emit(VkLoadingState.EMPTY)
                Log.e("SettingsViewModel", "error", e)
            }
            screenStateFlow.emit(VkLoadingState.EMPTY)
        }
    }

    fun saveSettings(
        history: Boolean,
        recordingAudio: Boolean,
        recordingStave: Boolean,
        pianoSize: Float,
        notesAmount: Float,
        staveOn: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val params = Settings(
                history,
                recordingAudio,
                recordingStave,
                pianoSize,
                notesAmount,
                staveOn
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
                    repository = SettingsRepositoryImpl(context),
                    generationRepository = GenerationRepositoryImpl(
                        StaveCache(context.dataStore),
                        StaveRemoteDataSourceImp(context)
                    ),
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
