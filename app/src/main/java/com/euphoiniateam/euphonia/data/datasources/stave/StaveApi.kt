package com.euphoiniateam.euphonia.data.datasources.stave

import com.euphoiniateam.euphonia.data.datamodels.RemoteNote
import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import kotlin.random.Random

class StaveApi() : StaveRemoteDataStore {
    override suspend fun getData(): RemoteStave {
        return RemoteStave(
            tempo = Random.nextInt(),
            tonal = Random.nextInt(),
            initialNotes = genRandomNotes(5),
            generatedNotes = genRandomNotes(Random.nextInt(3, 12))
        )
    }

    fun genRandomNotes(count: Int) = List(count) { RemoteNote(Random.nextInt(0, 9), 0.3f) }
}
