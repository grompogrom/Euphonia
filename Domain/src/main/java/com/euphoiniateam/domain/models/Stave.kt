package com.euphoiniateam.domain.models

data class Stave(
    val tempo: Int,
    val key: Int,
    val notes: List<Note>
)
