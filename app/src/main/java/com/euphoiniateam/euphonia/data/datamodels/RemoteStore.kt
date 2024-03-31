package com.euphoiniateam.euphonia.data.datamodels

import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.domain.models.Stave

data class RemoteStave(
    val tempo: Int,
    val tonal: Int,
    val initialNotes: List<RemoteNote>,
    val generatedNotes: List<RemoteNote>
)

data class RemoteNote(
    val pitch: Int,
    val note: Int,
    val duration: Float,
    val beat: Float
)

fun RemoteStave.toStave(): Stave {
    return Stave(
        tempo = this.tempo,
        tonal = this.tonal,
        initialNotes = this.initialNotes.map { it.toNote() },
        generatedNotes = this.generatedNotes.map { it.toNote() }
    )
}

fun Stave.toRemoteStave(): RemoteStave {
    return RemoteStave(
        tempo = this.tempo,
        tonal = this.tonal,
        initialNotes = this.initialNotes.map { it.toRemoteNote() },
        generatedNotes = this.generatedNotes.map { it.toRemoteNote() }
    )
}

fun RemoteNote.toNote(): Note {
    return Note(
        pitch = this.pitch,
        note = this.note,
        duration = this.duration,
        beat = this.beat
    )
}

fun Note.toRemoteNote(): RemoteNote {
    return RemoteNote(
        pitch = this.pitch,
        note = this.note,
        duration = this.duration,
        beat = this.beat
    )
}
