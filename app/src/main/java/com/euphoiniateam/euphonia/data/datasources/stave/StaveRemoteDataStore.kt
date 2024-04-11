package com.euphoiniateam.euphonia.data.datasources.stave

import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackResponse

interface StaveRemoteDataStore {
    suspend fun getData(): RemoteStave

    suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse
}
