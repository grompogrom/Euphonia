package com.euphoiniateam.euphonia.domain.models

data class Stave(
    val tempo: Int,
    val tonal: Int,
    val initialNotes: List<Note>,
    val generatedNotes: List<Note>
)
