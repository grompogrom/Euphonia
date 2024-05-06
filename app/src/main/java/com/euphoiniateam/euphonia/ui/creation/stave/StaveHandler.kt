package com.euphoiniateam.euphonia.ui.creation.stave

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import com.euphoiniateam.euphonia.domain.models.Note
import kotlin.math.min

class StaveHandler(staveConfig: StaveConfig) {
    private var linesCount: Int = 3
    private val notes = mutableStateListOf<Note>()
    init {
        updateNotes(staveConfig.initialNotes)
    }

    val visibleNotes by derivedStateOf {
        if (notes.isNotEmpty()) {

            linesCount = (notes.size / staveConfig.lineNotesCount) +
                (notes.size % staveConfig.lineNotesCount != 0).toInt()

            notes.subList(
                0,
                min(staveConfig.lineNotesCount * linesCount, notes.size)
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

    public fun getLinesCount(): Int {
        return linesCount
    }
}
