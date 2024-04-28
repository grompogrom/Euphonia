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
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import com.euphoiniateam.euphonia.ui.creation.StaveConfig
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PianoViewModel(
    private val notesRepository: NotesRepository
) : ViewModel() {
    private val notePosMidi = arrayListOf(0, 2, 1, 4, 3, 5, 7, 6, 9, 8, 11, 10)
    private var recordData: MutableList<PianoEvent> = mutableListOf()
    private var resultUri: Uri? = null
    val staveConfig = StaveConfig()
    var screenState = MutableStateFlow(
        PianoScreenState(
            PianoState.NO_RECORD
        )
    )

    fun startRecord() {
        screenState.tryEmit(screenState.value.copy(recordingState = PianoState.RECORDING))
    }

    fun stopRecord(context: Context) {
        viewModelScope.launch{
            if (recordData.isNotEmpty()) {
                createMidiWithApi(context)
                onRecordFinished()
                screenState.tryEmit(screenState.value.copy(recordingState = PianoState.AFTER_RECORD))
            } else {
                screenState.tryEmit(screenState.value.copy(recordingState = PianoState.NO_RECORD))
                Toast.makeText(context, "You played nothing)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun onRecordFinished() {
        val recognizedNotes = notesRepository.getNotes(resultUri ?: Uri.EMPTY)
        if (recognizedNotes != null) {
            staveConfig.updateNotes(recognizedNotes)
        }
    }

    fun exit(navigateUp: () -> Unit) {
        navigateUp()
    }

    fun remake() {
        screenState.tryEmit(screenState.value.copy(recordingState = PianoState.NO_RECORD))
        recordData.clear()
        resultUri = null
        staveConfig.updateNotes(emptyList())
        // TODO: Clear piano stave
    }

    fun applyRecord(navigate: (Uri) -> Unit) {
        resultUri?.apply { navigate(this) }
    }

    fun onPushKey(event: PianoEvent) {
        if (screenState.value.recordingState == PianoState.RECORDING) {
            recordData.add(event)
        }
    }

    fun onRealiseKey(pitch: Int, key: Int) {
        if (screenState.value.recordingState == PianoState.RECORDING) {
            Log.d("AAA", "realise key")
            for (l in 0 until recordData.size) {
                if (recordData[l].elapseTime == -1L &&
                    recordData[l].keyNum == key &&
                    recordData[l].pitch == pitch
                ) {
                    recordData[l].elapseTime = System.currentTimeMillis()
                }
            }
        }
    }

    fun createMidiWithApi(context: Context) {
        val file = File(context.applicationContext.externalCacheDir, "out.mid")
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()
        val ts = TimeSignature()
        ts.setTimeSignature(
            4,
            4,
            TimeSignature.DEFAULT_METER,
            TimeSignature.DEFAULT_DIVISION
        )
        val tempo = Tempo()
        tempo.bpm = 228f

        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        val noteCount = recordData.size
        for (i in 0 until noteCount) {
            val channel = 0
            val pitch = notesToMidiNotes(recordData[i].keyNum, recordData[i].pitch)
            val velocity = 100
            val tick = (i * 480).toLong()
            val duration: Long = recordData[i].elapseTime - recordData[i].pressTime
            Log.d("aaa", duration.toString())
            var durTick: Int
            if (duration <= 100)
                durTick = 120
            else if (duration <= 300)
                durTick = 240
            else if (duration <= 600)
                durTick = 360
            else
                durTick = 480

            val noteOn = NoteOn(tick, channel, pitch, velocity)
            val noteOff = NoteOff((tick + durTick), channel, pitch, 0)

            noteTrack.insertEvent(noteOn)
            noteTrack.insertEvent(noteOff)
//            noteTrack.insertNote(channel, pitch, velocity, tick, duration)
        }
        val tracks: MutableList<MidiTrack> = ArrayList()
        // tracks.add(tempoTrack)
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        midi.writeToFile(file)
        resultUri = file.toUri()
        Toast.makeText(context, "saved to ${file.path}", Toast.LENGTH_LONG).show()
    }

    fun notesToMidiNotes(noteNum: Int, pitch: Int): Int {
        return if (pitch == 0) notePosMidi[noteNum] + 60
        else if (pitch == 1) notePosMidi[noteNum] + 72
        else notePosMidi[noteNum] + 48
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PianoViewModel(
                    notesRepository = NotesRepositoryImpl(
                        context
                    )
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
