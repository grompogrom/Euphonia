package com.euphoiniateam.euphonia.data.repos

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.euphoiniateam.euphonia.domain.repos.PianoToMidiRepository
import com.euphoiniateam.euphonia.ui.piano.PianoEvent
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import java.io.File
import kotlin.math.roundToLong

class PianoToMidiRepositoryImpl(
    private val context: Context
) : PianoToMidiRepository {
    override fun convert(recordData: MutableList<PianoEvent>, bpm: Int, density: Int): Uri {
        val tick = 60_000f / (bpm * density)
        val calibratedData = calibrateTime(recordData)
        val noteTrack = MidiTrack()

        val tempo = Tempo()
        tempo.bpm = bpm.toFloat()

        noteTrack.insertEvent(tempo)
        calibratedData.forEach {
            val push = (it.pressTime.toDouble() / tick).roundToLong()
            val release = (it.elapseTime.toDouble() / tick).roundToLong()
            val pitch = notesToMidiNotes(it.keyNum, it.pitch)
            val noteOn = NoteOn(push, 0, pitch, 100)
            val noteOff = NoteOff(release, 0, pitch, 0)

            noteTrack.insertEvent(noteOn)
            noteTrack.insertEvent(noteOff)
        }

        val tracks: MutableList<MidiTrack> = ArrayList()
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)

        val file = File(context.applicationContext.externalCacheDir, "out.mid")
        midi.writeToFile(file)
        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
    }

    private fun calibrateTime(recordData: MutableList<PianoEvent>): List<PianoEvent> {
        val zeroTime = recordData.minBy { it.pressTime }.pressTime
        return recordData.map {
            it.copy(
                elapseTime = it.elapseTime - zeroTime,
                pressTime = it.pressTime - zeroTime
            )
        }
    }

    fun createMidiWithApi(rawRecordData: MutableList<PianoEvent>, bmp: Float): Uri {
        val file = File(context.applicationContext.externalCacheDir, "out.mid")
        val recordData = calibrateTime(rawRecordData)
        val noteTrack = MidiTrack()

        val tempo = Tempo()
        tempo.bpm = bmp

        noteTrack.insertEvent(tempo)
        val noteCount = recordData.size
        for (i in 0 until noteCount) {
            val channel = 0
            val pitch = notesToMidiNotes(recordData[i].keyNum, recordData[i].pitch)
            val velocity = 100
            val tick = (i * 480).toLong()
            val duration: Long = recordData[i].elapseTime - recordData[i].pressTime
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
        }
        val tracks: MutableList<MidiTrack> = ArrayList()
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        midi.writeToFile(file)
        Toast.makeText(context, "saved to ${file.path}", Toast.LENGTH_LONG).show()
        return file.toUri()
    }

    private fun notesToMidiNotes(noteNum: Int, pitch: Int): Int {
        return when (pitch) {
            0 -> noteNum + 60
            1 -> noteNum + 72
            else -> noteNum + 48
        }
    }

    override fun convertDefault(
        recordData: MutableList<PianoEvent>,
        bpm: Float,
        density: Int
    ): Uri {
        return createMidiWithApi(recordData, bpm)
    }
}
