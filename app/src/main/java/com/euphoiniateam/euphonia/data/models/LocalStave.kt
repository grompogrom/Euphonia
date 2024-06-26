package com.euphoiniateam.euphonia.data.models

import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.domain.models.Stave

data class LocalStave(
    val tempo: Int,
    val tonal: Int,
    val initialNotes: List<Note>,
    val generatedNotes: List<Note>
)

data class LocalNote(
    val pitch: Int,
    val note: Int,
    val duration: Float,
    val beat: Float
)

fun LocalStave.toStave(): Stave {
    return Stave(
        tempo = this.tempo,
        tonal = this.tonal,
        initialNotes = this.initialNotes,
        generatedNotes = this.generatedNotes
    )
}

fun Stave.toLocalStave(): LocalStave {
    return LocalStave(
        tempo = this.tempo,
        tonal = this.tonal,
        initialNotes = this.initialNotes,
        generatedNotes = this.generatedNotes
    )
}

fun LocalNote.toNote(): Note {
    return Note(
        pitch = this.pitch,
        note = this.note,
        duration = this.duration,
        beat = this.beat
    )
}

fun Note.toLocalNote(): LocalNote {
    return LocalNote(
        pitch = this.pitch,
        note = this.note,
        duration = this.duration,
        beat = this.beat
    )
}
