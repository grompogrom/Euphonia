package com.euphoiniateam.euphonia.data.source.convert

import com.euphoiniateam.euphonia.data.models.RemoteConvertRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse

interface ConvertDataSource {
    suspend fun convert(mp3: RemoteConvertRequest): RemoteTrackResponse
}
