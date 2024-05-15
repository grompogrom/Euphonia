package com.euphoiniateam.euphonia.ui.creation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.repos.GenerationRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.NotesRepositoryImpl
import com.euphoiniateam.euphonia.data.source.stave.StaveCache
import com.euphoiniateam.euphonia.data.source.stave.StaveRemoteDataSourceImp
import com.euphoiniateam.euphonia.domain.GenerationException
import com.euphoiniateam.euphonia.domain.repos.GenerationRepository
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import com.euphoiniateam.euphonia.ui.MidiPlayer
import com.euphoiniateam.euphonia.ui.creation.stave.StaveConfig
import com.euphoiniateam.euphonia.ui.creation.stave.StaveHandler
import com.euphoiniateam.euphonia.ui.creation.synthesia.SynthesiaConfig
import com.euphoiniateam.euphonia.ui.creation.synthesia.SynthesiaHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Stable
class CreationViewModel(
    private val generationRepository: GenerationRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    val staveConfig = StaveConfig()
    val staveHandler = StaveHandler(staveConfig)
    val synthesiaConfig = SynthesiaConfig()
    val synthesiaHandler = SynthesiaHandler(synthesiaConfig)
    var currentTrackState = MutableStateFlow(Uri.EMPTY)
    var screenState by mutableStateOf(CreationScreenState())
    private val midiPlayer: MidiPlayer = MidiPlayer()

    val staveChosen = false

    init {
        viewModelScope.launch {
            midiPlayer.playerState.collect {
                screenState = screenState.copy(isPlaying = it)
            }
        }
    }

    // TODO: move current uri from fragment to VM as field or state param
    // TODO: use UseCases instead repos
    // TODO: move stave config to screenState
    fun updateStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = generationRepository.generateStave()
            staveHandler.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    fun setCurrentUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            midiPlayer.initWithTrack(context, uri)
            currentTrackState.emit(uri)
        }
    }

    fun generateNewPart(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            screenState = try {
                val newMidi = generationRepository.generateMidi(uri, 5)

                val notes = notesRepository.getNotes(newMidi)
                notes?.let { staveHandler.updateNotes(it) }
                setCurrentUri(context, newMidi)
                currentTrackState.emit(newMidi)
                screenState.copy(isLoading = false)
            } catch (e: GenerationException) {
                screenState.copy(isLoading = false, error = "Unexpected server error")
            }
        }
    }

    private fun loadStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = generationRepository.getStave()
            staveHandler.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    fun togglePlayPause(context: Context) {
        if (screenState.isPlaying) {
            midiPlayer.stop()
        } else {
            midiPlayer.play(context, currentTrackState.value)
        }
    }

    fun getNotes(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val initialNotes = notesRepository.getNotes(uri)
            if (initialNotes != null) {
                if (staveChosen)
                    staveHandler.updateNotes(initialNotes)
                else
                    synthesiaHandler.updateNotes(initialNotes)
            }
            screenState = screenState.copy(isLoading = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        midiPlayer.release()
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CreationViewModel(
                    generationRepository = GenerationRepositoryImpl(
                        StaveCache(context.dataStore),
                        StaveRemoteDataSourceImp(context)
                    ),
                    notesRepository = NotesRepositoryImpl(
                        context
                    )
                )
            }
        }
    }
}
