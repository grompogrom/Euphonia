package com.euphoiniateam.euphonia.tools

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.euphoiniateam.euphonia.ui.piano.PianoEvent
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import java.io.File

class ProfileMapper {
    private val notes: MutableList<PianoEvent> = mutableListOf()
    private var bpm: Int = NORMAL_BPM

    // Набор айдишников(только цифры)
    fun setFriendsId(mutableList: MutableList<String>) {
        val noteSize = if (mutableList.size > 100) 100 else mutableList.size
        for (i in 0 until noteSize) {
            if (mutableList[i].length % 2 != 0) {
                mutableList[i] = mutableList[i].substring(0, mutableList[i].length - 1)
            }
            for (x in 0 until mutableList[i].length step 2)
                notes.add(
                    PianoEvent(
                        100,
                        0,
                        mutableList[i][x].digitToInt(),
                        mutableList[i][x + 1].digitToInt() % 3
                    )
                )
        }
    }

    fun setUserId(id: String) {
        var tempId = id
        if (id.length % 2 != 0) {
            tempId = id.substring(0, id.length - 1)
        }
        for (i in tempId.indices step 2) {
            notes.add(
                PianoEvent(
                    100,
                    0,
                    tempId[i].digitToInt(),
                    tempId[i + 1].digitToInt() % 3
                )
            )
        }
    }

    fun setFriendsCount(count: Int) {
        bpm = when {
            count < MIN_BPM -> MIN_BPM
            count > MAX_BPM -> MAX_BPM
            else -> count
        }
    }

    fun userName(name: String, surname: String) {
        var username = name + surname
        val asciiStringBuilder = StringBuilder()
        for (char in username) {
            asciiStringBuilder.append(char.toInt())
        }
        username = asciiStringBuilder.toString()
        if (username.length % 2 != 0) {
            username = username.substring(0, username.length - 1)
        }

        for (i in username.indices step 2) {
            notes.add(
                PianoEvent(100, 0, username[i].digitToInt(), username[i + 1].digitToInt() % 3)
            )
        }
    }

    fun createMidiWithApi(context: Context): Uri {
        val file = File(context.applicationContext.externalCacheDir, "out.mid")
        val recordData = calibrateTime(notes)
        val noteTrack = MidiTrack()

        val tempo = Tempo()
        tempo.bpm = bpm.toFloat()

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

    private fun calibrateTime(recordData: MutableList<PianoEvent>): List<PianoEvent> {
        val zeroTime = recordData.sortedBy { it.pressTime }.first().pressTime
        return recordData.map {
            it.copy(
                elapseTime = it.elapseTime - zeroTime,
                pressTime = it.pressTime - zeroTime
            )
        }
    }

    companion object {
        val MAX_BPM = 240
        val MIN_BPM = 80
        val NORMAL_BPM = 140
    }
}
