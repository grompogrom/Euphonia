package com.euphoiniateam.euphonia.ui.creation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveApiMock
import com.euphoiniateam.euphonia.data.datasources.stave.StaveCache
import com.euphoiniateam.euphonia.data.repos.StaveRepositoryImpl
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import com.euphoiniateam.euphonia.data.repos.NotesRepositoryImpl

import com.euphoiniateam.euphonia.domain.repos.StaveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreationViewModel(
    private val staveRepository: StaveRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    val staveConfig = StaveConfig()
    var screenState by mutableStateOf(CreationScreenState())
    init {

    }

    fun updateStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = staveRepository.generateStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    fun loadStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = staveRepository.getStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    fun getNotes(uri: Uri) {
        viewModelScope.launch (Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val initial_notes = notesRepository.getNotes(uri)
            if (initial_notes != null) {
                staveConfig.updateNotes(initial_notes)
            }
            screenState = screenState.copy(isLoading = false)
        }
    }


    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CreationViewModel(
                    staveRepository = StaveRepositoryImpl(
                        StaveCache(context.dataStore),
                        StaveApiMock()
                    ),
                    notesRepository = NotesRepositoryImpl(
                        context
                    )
                )
            }
        }
    }
}
