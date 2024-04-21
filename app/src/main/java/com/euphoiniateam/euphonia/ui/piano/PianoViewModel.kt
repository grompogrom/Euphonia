package com.euphoiniateam.euphonia.ui.piano

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow

class PianoViewModel : ViewModel() {
    private val notePosMidi = arrayListOf(0, 2, 1, 4, 3, 5, 7, 6, 9, 8, 11, 10)
    var recordData: MutableList<PianoEvent> = mutableListOf()
    var resultUri: Uri? = null
    var screenState = MutableStateFlow(
        PianoScreenState(
            PianoState.NO_RECORD
        )
    )

    fun startRecord() {
        screenState.tryEmit(screenState.value.copy(recordingState = PianoState.RECORDING))
    }

    fun stopRecord(context: Context) {
        if (recordData.isNotEmpty()) {
            createMidiWithApi(context)
            screenState.tryEmit(screenState.value.copy(recordingState = PianoState.AFTER_RECORD))
        } else {
            screenState.tryEmit(screenState.value.copy(recordingState = PianoState.NO_RECORD))
            Toast.makeText(context, "You played nothing)", Toast.LENGTH_LONG).show()
        }
    }

    fun exit(navigateUp: () -> Unit) {
        navigateUp()
    }

    fun remake() {
        screenState.tryEmit(screenState.value.copy(recordingState = PianoState.NO_RECORD))
        recordData.clear()
        resultUri = null
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
}

enum class PianoState {
    NO_RECORD(),
    RECORDING(),
    AFTER_RECORD()
}
