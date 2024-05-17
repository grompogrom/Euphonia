package com.euphoiniateam.euphonia.ui.piano

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.repos.NotesRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.PianoToMidiRepositoryImpl
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import com.euphoiniateam.euphonia.domain.repos.PianoToMidiRepository
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository
import com.euphoiniateam.euphonia.ui.MidiPlayer
import com.euphoiniateam.euphonia.ui.creation.stave.StaveConfig
import com.euphoiniateam.euphonia.ui.creation.stave.StaveHandler
import com.euphoiniateam.euphonia.ui.creation.synthesia.SynthesiaConfig
import com.euphoiniateam.euphonia.ui.creation.synthesia.SynthesiaHandler
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PianoViewModel(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository,
    private val pianoToMidiRepository: PianoToMidiRepository
) : ViewModel() {
    private var recordData: MutableList<PianoEvent> = mutableListOf()
    private var resultUri: Uri = Uri.EMPTY
    private val midiPlayer = MidiPlayer()
    val staveConfig = StaveConfig()
    val staveHandler = StaveHandler(staveConfig)
    val synthesiaConfig = SynthesiaConfig()
    val synthesiaHandler = SynthesiaHandler(synthesiaConfig)
    var screenStateFlow = MutableStateFlow(
        PianoScreenState(
            PianoState.NO_RECORD,
            false
        )
    )
    private var staveChosen = true
    val screenState: StateFlow<PianoScreenState>
        get() = screenStateFlow

    init {
        viewModelScope.launch {
            midiPlayer.playerState.collect {
                if (screenStateFlow.value.isPlayingResult != it) {
                    screenStateFlow.emit(screenStateFlow.value.copy(isPlayingResult = it))
                }
            }
        }
    }

    fun startRecord() {
        screenStateFlow.tryEmit(screenStateFlow.value.copy(recordingState = PianoState.RECORDING))
    }

    fun stopRecord(context: Context) {
        viewModelScope.launch {
            if (recordData.isNotEmpty()) {
                resultUri = pianoToMidiRepository.convert(recordData, 120,480)
                onRecordFinished()
                screenStateFlow.tryEmit(
                    screenStateFlow.value.copy(recordingState = PianoState.AFTER_RECORD)
                )
            } else {
                screenStateFlow.tryEmit(
                    screenStateFlow.value.copy(recordingState = PianoState.NO_RECORD)
                )
                Toast.makeText(context, "You played nothing)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun onRecordFinished() {
        val recognizedNotes = notesRepository.getNotes(resultUri)
        if (recognizedNotes != null) {
            if (staveChosen)
                staveHandler.updateNotes(recognizedNotes)
            else
                synthesiaHandler.updateNotes(recognizedNotes)
        }
    }

    fun exit(navigateUp: () -> Unit) {
        clearRecorded()
        navigateUp()
    }

    fun remake() {
        screenStateFlow.tryEmit(screenStateFlow.value.copy(recordingState = PianoState.NO_RECORD))
        clearRecorded()
    }

    fun applyRecord(navigate: (Uri) -> Unit) {
        navigate(resultUri)
    }

    fun onPushKey(event: PianoEvent) {
        if (screenStateFlow.value.recordingState == PianoState.RECORDING) {
            recordData.add(event)
        }
    }

    fun onPlayPush(context: Context) {
        resultUri?.let { midiPlayer.play(context, it) }
    }

    fun onStopPush() {
        midiPlayer.stop()
    }

    private fun clearRecorded() {
        midiPlayer.stop()
        recordData.clear()
        resultUri = Uri.EMPTY
        staveHandler.updateNotes(emptyList())
    }

    fun onRealiseKey(pitch: Int, octave: Int) {
        Log.d("toMidi", "recordData")
        if (screenStateFlow.value.recordingState == PianoState.RECORDING) {
            for (l in 0 until recordData.size) {
                if (recordData[l].elapseTime == -1L &&
                    recordData[l].keyNum == pitch &&
                    recordData[l].pitch == octave
                ) {
                    recordData[l].elapseTime = System.currentTimeMillis()
                }
            }
        }
    }

    fun setStaveChosen() {
        viewModelScope.launch(Dispatchers.IO) {
            staveChosen = settingsRepository.getSettings().showing_stave
        }
    }
    fun getStaveChosen(): Boolean {
        return staveChosen
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PianoViewModel(
                    notesRepository = NotesRepositoryImpl(context),
                    pianoToMidiRepository = PianoToMidiRepositoryImpl(context),
                    settingsRepository = SettingsRepositoryImpl(context)
                )
            }
        }
    }
}

enum class PianoState {
    NO_RECORD(),
    RECORDING(),
    AFTER_RECORD()
}
