package com.euphoiniateam.euphonia.domain.usecases

import android.net.Uri
import com.euphoiniateam.euphonia.domain.repos.StaveRepository

class GenerateNewPartUseCase(
    private val staveRepository: StaveRepository,
) {
    suspend fun execute(prompt: Uri, notesToGenCount: Int): Uri {
        return staveRepository.generateMidi(prompt, notesToGenCount)
    }
}
