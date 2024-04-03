package com.euphoiniateam.euphonia.data.datasources.stave

import android.net.Uri
import com.euphoiniateam.euphonia.data.datamodels.RemoteNote
import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackResponse
import kotlin.random.Random

class StaveApiMock() : StaveRemoteDataStore {
    override suspend fun getData(): RemoteStave {
        return RemoteStave(
            tempo = Random.nextInt(),
            tonal = Random.nextInt(),
            initialNotes = genRandomNotes(5),
            generatedNotes = genRandomNotes(Random.nextInt(3, 12))
        )
    }

    override suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse {
        return RemoteTrackResponse(Uri.EMPTY)
    }

    fun genRandomNotes(count: Int) = List(count) {
        RemoteNote(Random.nextInt(0, 9),Random.nextInt(0, 9), 0.3f,0.3f,)
    }
}
