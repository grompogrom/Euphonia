package com.euphoiniateam.euphonia.ui.piano

data class PianoEvent(
    var elapseTime: Long,
    val pressTime: Long,
    val keyNum: Int,
    val pitch: Int
)
