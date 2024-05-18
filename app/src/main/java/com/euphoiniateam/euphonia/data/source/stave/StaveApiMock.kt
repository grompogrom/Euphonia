package com.euphoiniateam.euphonia.data.source.stave

import android.net.Uri
import com.euphoiniateam.euphonia.data.models.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse

class StaveApiMock() : StaveRemoteDataSource {

    override suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse {
        return RemoteTrackResponse(Uri.EMPTY)
    }
}
