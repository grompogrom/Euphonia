package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri

interface GenerationRepository {

    // use for first and additional generation
    fun setCountToGenerate(count: Int)

    suspend fun generateNew(prompt: Uri): Uri

    // use for regeneration
    suspend fun regenerateLast(): Uri

    suspend fun generateNewNoIncludedPrompt(prompt: Uri): Uri
}
