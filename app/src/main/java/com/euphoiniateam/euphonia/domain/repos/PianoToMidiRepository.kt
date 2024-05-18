package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri
import com.euphoiniateam.euphonia.ui.piano.PianoEvent

interface PianoToMidiRepository {
    fun convertDefault(recordData: MutableList<PianoEvent>, bpm: Float, density: Int): Uri
    fun convert(recordData: MutableList<PianoEvent>, bpm: Int, density: Int = 120): Uri
}
