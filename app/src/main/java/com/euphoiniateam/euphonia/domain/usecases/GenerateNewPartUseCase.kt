package com.euphoiniateam.euphonia.domain.usecases

import android.net.Uri
import com.euphoiniateam.euphonia.domain.repos.GenerationRepository

class GenerateNewPartUseCase(
    private val generationRepository: GenerationRepository,
) {
    suspend fun execute(prompt: Uri, notesToGenCount: Int): Uri {
        return generationRepository.generateMidi(prompt, notesToGenCount)
    }
}
