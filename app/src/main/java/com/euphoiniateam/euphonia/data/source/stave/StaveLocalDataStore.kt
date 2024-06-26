package com.euphoiniateam.euphonia.data.source.stave

import com.euphoiniateam.euphonia.data.models.LocalStave

interface StaveLocalDataStore {
    suspend fun loadData(): LocalStave
    suspend fun saveData(stave: LocalStave)
}
