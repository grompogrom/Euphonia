package com.euphoiniateam.euphonia.ui.creation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
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
import com.euphoiniateam.euphonia.ui.history.MusicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CreationViewModel(
    private val repository: StaveRepository,
    private val context: Context
) : ViewModel() {
    val staveConfig = StaveConfig()
    var screenState by mutableStateOf(CreationScreenState())
    private var mediaPlayer: MediaPlayer? = null
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

    private fun loadStave() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState = screenState.copy(isLoading = true)
            val newStave = repository.getStave()
            staveConfig.updateNotes(newStave.initialNotes + newStave.generatedNotes)
            screenState = screenState.copy(isLoading = false)
        }
    }
    fun togglePlayPause() {
        if (screenState.isPlaying) {
            mediaPlayer?.pause()
            screenState = screenState.copy(isPlaying = false)
        } else {
            playSong()
            screenState = screenState.copy(isPlaying = true)
        }
    }

    private fun playSong() {
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
                    repository = StaveRepositoryImpl(
                        StaveCache(context.dataStore),
                        StaveApi()
                    ),
                    context = context
                )
            }
        }
    }
}
