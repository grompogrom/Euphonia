package com.euphoiniateam.euphonia.ui.creation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import com.euphoiniateam.euphonia.domain.models.Note
import kotlin.math.min

class StaveHandler(staveConfig: StaveConfig) {
    private val notes = mutableStateListOf<Note>()
    init {
        updateNotes(staveConfig.initialNotes)
    }

    val visibleNotes by derivedStateOf {
        if (notes.isNotEmpty()) {

            staveConfig.linesCount = (notes.size / staveConfig.lineNotesCount) +
                    (notes.size % staveConfig.lineNotesCount != 0).toInt()

            notes.subList(
                0,
                min(staveConfig.lineNotesCount * staveConfig.linesCount, notes.size)
            )
        } else {
            emptyList()
        }
    }
    private fun Boolean.toInt() = if (this) 1 else 0
    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
    }

}