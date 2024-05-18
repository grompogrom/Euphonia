package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri

interface GenerationRepository {

    // use for first and additional generation
    suspend fun generateNew(prompt: Uri): Uri

    // use for regeneration
    suspend fun regenerateLast(): Uri
}
