package com.euphoiniateam.euphonia.data.source.stave

import com.euphoiniateam.euphonia.data.models.RemoteStave
import com.euphoiniateam.euphonia.data.models.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse

interface StaveRemoteDataStore {
    suspend fun getData(): RemoteStave

    suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse
}
