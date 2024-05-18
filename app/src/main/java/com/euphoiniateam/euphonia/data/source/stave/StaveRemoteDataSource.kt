package com.euphoiniateam.euphonia.data.source.stave

import com.euphoiniateam.euphonia.data.models.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse

interface StaveRemoteDataSource {

    suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse
}
