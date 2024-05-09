package com.euphoiniateam.euphonia.ui.creation.synthesia

import androidx.compose.runtime.mutableStateListOf
import com.euphoiniateam.euphonia.domain.models.Note

class SynthesiaHandler(synthesiaConfig: SynthesiaConfig) {

    private val notes = mutableStateListOf<Note>()

    init {
        updateNotes(synthesiaConfig.initialNotes)
    }

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
    }

}