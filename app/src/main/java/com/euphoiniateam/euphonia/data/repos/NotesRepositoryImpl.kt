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

        sequencer.sequence?.tracks?.forEachIndexed { index, track ->
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                Log.d("aaa", "Tick ${event.tick}, message: ${event.message}")
                when (val message = event.message) {
                    is ShortMessage -> {
                        val command = message.command
                        val data1 = message.data1
                        val data2 = message.data2
                        Log.d(
                            "bbb",
                            "Tick ${event.tick}, command: $command, data1: $data1, data2: $data2"
                        )
                    }

                    else -> {}
                }
            }.take(10).toList()
        }

        return sequencer.sequence?.toNotes()
    }

    private fun jp.kshoji.javax.sound.midi.Sequence.toNotes(): List<Note> {
        return tracks.flatMap { track ->
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                var beatStart: Float = 0.0f
                when (val message = event.message) {
                    is ShortMessage -> {
                        val command = message.command
                        val midinote = message.data1
                        val amp = message.data2
                        val note = (midinote - 24) % 12

                        if (command == ShortMessage.NOTE_ON && amp.toDouble() != 0.0) {
                             beatStart = (event.tick / resolution.toDouble())
                                .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                                .toFloat()


                        }
                        if (command == ShortMessage.NOTE_OFF) {
                            val beatEnd = (event.tick / resolution.toDouble())
                                .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                                .toFloat()
                            Log.d("note", note.toString() + "  " + (beatEnd - beatStart).toString())
                            return@map Note(note, beatEnd - beatStart)
                        }
                    }
                }
                null
            }.filterNotNull()
        }
    }

}