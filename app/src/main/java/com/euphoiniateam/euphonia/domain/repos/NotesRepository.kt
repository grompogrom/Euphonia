package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri
import com.euphoiniateam.euphonia.domain.models.Note

interface NotesRepository {
    suspend fun getNotes(uri: Uri): List<Note>?
}
