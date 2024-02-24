package com.euphoiniateam.euphonia.data.datasources.stave

import com.euphoiniateam.euphonia.data.datamodels.RemoteStave

interface StaveRemoteDataStore {
    suspend fun getData(): RemoteStave
}
