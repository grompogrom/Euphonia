package com.euphoiniateam.euphonia.domain.models

// TODO: naming и сделать объект по умолчанию
data class Settings(
    val history: Boolean = true,
    val recordingAudio: Boolean = true,
    val recordingStave: Boolean = true,
    val pianoSize: Float = 7f,
    val notesAmount: Int = 5,
    val staveOn: Boolean = true
)
