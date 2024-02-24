package com.euphoiniateam.domain

import com.euphoiniateam.domain.models.Stave

interface NotesRepository {
    fun save(stave: Stave): Boolean
    fun load(): Stave
}
