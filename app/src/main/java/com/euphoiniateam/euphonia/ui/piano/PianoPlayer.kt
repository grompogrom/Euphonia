package com.euphoiniateam.euphonia.ui.piano

import android.media.SoundPool

data class PianoPlayer(
    var elapseTime : Long,
    val pressTime : Long,
    val keyNum : Int,
    val pitch : Int
)
