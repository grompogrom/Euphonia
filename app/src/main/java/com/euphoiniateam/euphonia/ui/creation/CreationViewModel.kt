package com.euphoiniateam.euphonia.ui.creation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveApi
import com.euphoiniateam.euphonia.data.datasources.stave.StaveCache
import com.euphoiniateam.euphonia.data.repos.StaveRepositoryImpl
import com.euphoiniateam.euphonia.domain.repos.StaveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreationViewModel(
    private val repository: StaveRepository
) : ViewModel() {
    val staveConfig = StaveConfig()
    var screenState by mutableStateOf(CreationScreenState())
    init {
        loadStave()
    }

    fun updateStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = repository.generateStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    fun loadStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = repository.getStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CreationViewModel(
                    repository = StaveRepositoryImpl(
                        StaveCache(context.dataStore),
                        StaveApi()
                    )
                )
            }
        }
    }
}
