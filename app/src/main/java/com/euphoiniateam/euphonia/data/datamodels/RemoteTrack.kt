package com.euphoiniateam.euphonia.data.datamodels

import android.net.Uri

data class RemoteTrackResponse(
    val uri: Uri
)

data class RemoteTrackRequest(
    val uri: Uri,
    val countToGenerate: Int
)
