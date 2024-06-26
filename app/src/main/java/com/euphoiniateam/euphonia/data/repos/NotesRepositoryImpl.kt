package com.euphoiniateam.euphonia.data.repos

import android.content.Context
import android.net.Uri
import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import java.math.RoundingMode
import jp.kshoji.javax.sound.midi.MidiSystem
import jp.kshoji.javax.sound.midi.ShortMessage

class NotesRepositoryImpl(private val context: Context) : NotesRepository {

    private val notesMap = mapOf(
        0 to intArrayOf(0),
        1 to intArrayOf(1, 2),
        2 to intArrayOf(3, 4),
        3 to intArrayOf(5),
        4 to intArrayOf(6, 7),
        5 to intArrayOf(8, 9),
        6 to intArrayOf(10, 11)
    )

    override suspend fun getNotes(uri: Uri): List<Note>? {
        val stream = context.contentResolver?.openInputStream(uri)
        val sequencer = MidiSystem.getSequencer().apply {
            if (stream != null) {
                setSequence(stream)
            }
        }
//        val initialNotes = sequencer.sequence?.toNotes()
//        if (initialNotes != null) {
//            for (note in initialNotes) {
//                Log.d("note", note.toString())
//            }
//        }

        return sequencer.sequence?.toNotes()
    }

    private fun jp.kshoji.javax.sound.midi.Sequence.toNotes(): List<Note> {
        return tracks.flatMap { track ->
            val inflight = mutableMapOf<Int, Note>()
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                when (val message = event.message) {
                    is ShortMessage -> {
                        var pitch = 0
                        val command = message.command
                        val midiNote = (message.data1 - 24) % 36
                        val noteNum = midiNote % 12
                        val amp = message.data2
                        for (key in notesMap.keys) {
                            if (notesMap[key]?.contains(noteNum) == true)
                                pitch = key
                        }
                        val beat = (event.tick / resolution.toDouble())
                            .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                            .toFloat()
                        when (command) {
                            ShortMessage.NOTE_ON -> {
                                val note = Note(pitch, midiNote, 0.25f, beat)
                                inflight[midiNote] = note
                            }
                            ShortMessage.NOTE_OFF -> {
                                val note = inflight.remove(midiNote)
                                return@map note?.let {
                                    it.copy(duration = beat - it.beat)
                                }
                            }
                        }
                    }
                }
                null
            }.filterNotNull()
        }
    }
}
