package com.euphoiniateam.euphonia.data.models

import android.net.Uri

data class RemoteTrackResponse(
    val uri: Uri
)

data class RemoteTrackRequest(
    val uri: Uri,
    // does not affect now
    val countToGenerate: Int
)

data class RemoteConvertRequest(
    val uri: Uri
)
