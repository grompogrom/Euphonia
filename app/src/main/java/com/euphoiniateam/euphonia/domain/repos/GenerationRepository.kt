package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri
import com.euphoiniateam.euphonia.domain.models.Stave

interface GenerationRepository {
    suspend fun getStave(): Stave

    suspend fun generateMidi(prompt: Uri, count: Int): Uri

    @Deprecated("Use generateMidi instead")
    suspend fun generateStave(): Stave
}
