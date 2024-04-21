package com.euphoiniateam.euphonia.ui.piano

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PianoViewModel : ViewModel() {
    var screenState by mutableStateOf(
        PianoScreenState(
            PianoState.NO_RECORD
        )
    )

    fun startRecord(){
        screenState = screenState.copy()
    }

    fun stopRecord(){

    }

    fun exit(){

    }

    fun remake(){

    }

    fun applyRecord(){

    }
}

enum class PianoState{
    NO_RECORD(),
    RECORDING(),
    AFTER_RECORD()
}
