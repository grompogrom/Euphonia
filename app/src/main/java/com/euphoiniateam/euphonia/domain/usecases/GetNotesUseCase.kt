package com.euphoiniateam.euphonia.domain.usecases

import android.net.Uri
import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.domain.repos.NotesRepository

class GetNotesUseCase(private val notesRepository: NotesRepository) {
    suspend fun execute(uri: Uri): List<Note>? {
        return notesRepository.getNotes(uri)
    }
}
