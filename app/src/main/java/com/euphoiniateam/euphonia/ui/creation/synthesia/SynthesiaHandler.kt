package com.euphoiniateam.euphonia.ui.creation.synthesia

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import com.euphoiniateam.euphonia.domain.models.Note

class SynthesiaHandler(synthesiaConfig: SynthesiaConfig) {

    private val notes = mutableStateListOf<Note>()

    init {
        updateNotes(synthesiaConfig.initialNotes)
    }

    val visibleNotes by derivedStateOf {
        if (notes.isNotEmpty()) {
            notes// maybe change
        }
        else {
            emptyList()
        }
    }

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
    }

}