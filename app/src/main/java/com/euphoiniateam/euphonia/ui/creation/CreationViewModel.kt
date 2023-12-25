package com.euphoiniateam.euphonia.ui.creation

import androidx.lifecycle.ViewModel
import com.euphoiniateam.euphonia.domain.models.Note
import kotlin.random.Random

class CreationViewModel : ViewModel() {
    val staveConfig = StaveConfig()

    fun updateStave(){
        staveConfig.updateNotes(genRandomNotes())
    }

    fun genRandomNotes() = List(10) { Note(Random.nextInt(0, 9), 0.3f) }
}