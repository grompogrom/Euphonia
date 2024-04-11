package com.euphoiniateam.euphonia.domain.models

data class Note(
    val pitch: Int,
    val note: Int,
    val duration: Float,
    val beat: Float
)
