package com.euphoiniateam.euphonia.data.datasources.stave

import com.euphoiniateam.euphonia.data.datamodels.LocalStave

interface StaveLocalDataStore {
    suspend fun loadData(): LocalStave
    suspend fun saveData(stave:LocalStave)
}