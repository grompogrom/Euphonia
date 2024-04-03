package com.euphoiniateam.euphonia.ui.creation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.dataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveCache
import com.euphoiniateam.euphonia.data.datasources.stave.StaveRemoteDataSourceImp
import com.euphoiniateam.euphonia.data.repos.NotesRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.StaveRepositoryImpl
import com.euphoiniateam.euphonia.domain.GenerationException
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import com.euphoiniateam.euphonia.domain.repos.StaveRepository
import com.euphoiniateam.euphonia.ui.history.MusicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class CreationViewModel(
    private val staveRepository: StaveRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {
    val staveConfig = StaveConfig()
    var currentTrackState = MutableStateFlow(Uri.EMPTY)
    var screenState by mutableStateOf(CreationScreenState())
    private var mediaPlayer: MediaPlayer? = null
    init {
        loadStave()
    }

    // TODO: move current uri from fragment to VM as field or state param
    // TODO: use UseCases instead repos
    // TODO: move stave config to screenState
    fun updateStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = staveRepository.generateStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }

    private fun setCurrentUri(uri: Uri) {
        viewModelScope.launch {
            currentTrackState.emit(uri)
        }
    }

    fun generateNewPart(uri: Uri) {
        Log.d("AAA", "generateNewPart invoked")
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            screenState = try {
                val newMidi = staveRepository.generateMidi(uri, 5)

                val notes = notesRepository.getNotes(newMidi)
                notes?.let { staveConfig.updateNotes(it) }
                setCurrentUri(newMidi)
                currentTrackState.emit(newMidi)
                screenState.copy(isLoading = false)
            } catch (e: GenerationException) {
                Log.e("AAA", null, e)
                screenState.copy(isLoading = false, error = "Unexpected server error")
            }
        }
    }

    private fun loadStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = staveRepository.getStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }
    fun togglePlayPause(context: Context) {
        screenState = if (screenState.isPlaying) {
            mediaPlayer?.pause()
            screenState.copy(isPlaying = false)
        } else {
            playSong(context)
            screenState.copy(isPlaying = true)
        }
    }

    fun getNotes(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val initial_notes = notesRepository.getNotes(uri)
            if (initial_notes != null) {
                staveConfig.updateNotes(initial_notes)
            }
            screenState = screenState.copy(isLoading = false)
        }
    }

    private fun playSong(context: Context) {
        val songName = MusicData.songName
        val midiFile = File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Euphonia"), songName)
        val midiUri = Uri.fromFile(midiFile)
        mediaPlayer = MediaPlayer.create(context, midiUri)
        mediaPlayer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CreationViewModel(
                    staveRepository = StaveRepositoryImpl(
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
