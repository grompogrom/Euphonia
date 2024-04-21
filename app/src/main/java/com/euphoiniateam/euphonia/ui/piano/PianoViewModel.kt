package com.euphoiniateam.euphonia.ui.piano

import androidx.lifecycle.ViewModel

class PianoViewModel : ViewModel() {
    // TODO: Implement the ViewModel
}

enum class PianoScreenState{
    NO_RECORD(),
    RECORDING(),
    AFTER_RECORD()
}
