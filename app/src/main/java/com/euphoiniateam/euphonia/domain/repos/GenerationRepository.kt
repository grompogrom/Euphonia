package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri
import com.euphoiniateam.euphonia.domain.models.Stave

interface GenerationRepository {

    suspend fun generateMidi(prompt: Uri, count: Int): Uri

    // TODO: используется, но не делает полезной работы
    @Deprecated("Use generateMidi instead")
    suspend fun generateStave(): Stave
}
