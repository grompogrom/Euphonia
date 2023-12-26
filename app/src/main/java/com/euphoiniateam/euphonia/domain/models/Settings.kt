package com.euphoiniateam.euphonia.domain.models

data class Settings(
    val history: Boolean = true,
    val recording_audio: Boolean = true,
    val recording_stave:Boolean = true,
    val piano_size: Float = 7f,
    val stave_size: Float = 5f,
    val showing_stave: Boolean = true
)