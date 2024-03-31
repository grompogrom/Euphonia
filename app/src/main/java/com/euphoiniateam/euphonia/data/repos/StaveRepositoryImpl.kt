package com.euphoiniateam.euphonia.data.repos

import android.net.Uri
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.datamodels.toLocalStave
import com.euphoiniateam.euphonia.data.datamodels.toStave
import com.euphoiniateam.euphonia.data.datasources.stave.StaveLocalDataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveRemoteDataStore
import com.euphoiniateam.euphonia.domain.GenerationException
import com.euphoiniateam.euphonia.domain.models.Stave
import com.euphoiniateam.euphonia.domain.repos.StaveRepository
import kotlinx.coroutines.delay

class StaveRepositoryImpl(
    private val localDataStore: StaveLocalDataStore,
    private val remoteDataStore: StaveRemoteDataStore
) : StaveRepository {

    override suspend fun getStave(): Stave {
        try {
            return localDataStore.loadData().toStave()
        } catch (e: Exception) {
            return Stave(0, 0, emptyList(), emptyList())
        }
    }

    override suspend fun generateMidi(prompt: Uri, count: Int): Uri {
        try {
            val remoteTrackRequest = remoteDataStore.generate(
                RemoteTrackRequest(prompt, 10)
            )
            return remoteTrackRequest.uri
        } catch (e: GenerationException) {
            throw e
        } catch (e: Exception) {
            throw GenerationException("Undefined error while generation", e)
        }
    }

    @Deprecated("Use generateMidi instead")
    override suspend fun generateStave(): Stave {
        val newStave = remoteDataStore.getData()
        delay(2000)
        val staveToCache = newStave.toStave().toLocalStave()
        localDataStore.saveData(staveToCache)

        return newStave.toStave()
    }
}
