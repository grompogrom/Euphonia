package com.euphoiniateam.euphonia.ui.creation.stave

data class StaveScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlaying: Boolean = false,
)
