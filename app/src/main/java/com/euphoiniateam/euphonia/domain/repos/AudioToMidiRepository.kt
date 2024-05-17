package com.euphoiniateam.euphonia.domain.repos

import android.net.Uri

interface AudioToMidiRepository {
    suspend fun convertMp3ToMidi(mp3: Uri): Uri
}
