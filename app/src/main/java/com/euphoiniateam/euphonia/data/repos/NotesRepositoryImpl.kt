package com.euphoiniateam.euphonia.data.repos

import android.content.Context
import android.net.Uri
import android.util.Log
import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.domain.repos.NotesRepository
import jp.kshoji.javax.sound.midi.MidiSystem
import jp.kshoji.javax.sound.midi.ShortMessage
import java.math.RoundingMode

class NotesRepositoryImpl(private val context: Context) :NotesRepository {

    override suspend fun getNotes(uri: Uri): List<Note>? {
        val stream = context.contentResolver?.openInputStream(uri)
        val sequencer = MidiSystem.getSequencer().apply {
            if (stream != null) {
                setSequence(stream)
            }
        }

        return sequencer.sequence?.toNotes()
    }

    private fun jp.kshoji.javax.sound.midi.Sequence.toNotes(): List<Note> {
        return tracks.flatMap { track ->
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                when (val message = event.message) {
                    is ShortMessage -> {
                        val command = message.command
                        val midinote = message.data1
                        val amp = message.data2
                        val note = (midinote - 24) % 12
                        if (command == ShortMessage.NOTE_ON && amp.toDouble() != 0.0) {
                            val beat = (event.tick / resolution.toDouble())
                            .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                            .toFloat()
                            Log.d("note", note.toString() + "  " + beat)
                            return@map Note(note, beat)
                        }
                    }
                }
                null
            }.filterNotNull()
        }
    }

}