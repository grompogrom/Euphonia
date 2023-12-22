package com.euphoiniateam.data

import com.euphoiniateam.domain.models.Stave

interface LocalDataStaveSource {
    suspend fun getStave(): Stave
}