package com.euphoiniateam.euphonia.domain.repos

import com.euphoiniateam.euphonia.domain.models.Stave

interface StaveRepository {
    suspend fun getStave(): Stave

    suspend fun generateStave(): Stave
}