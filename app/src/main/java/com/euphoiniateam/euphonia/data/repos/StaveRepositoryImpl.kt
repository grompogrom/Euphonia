package com.euphoiniateam.euphonia.data.repos

import com.euphoiniateam.euphonia.data.datamodels.toLocalStave
import com.euphoiniateam.euphonia.data.datamodels.toStave
import com.euphoiniateam.euphonia.data.datasources.stave.StaveLocalDataStore
import com.euphoiniateam.euphonia.data.datasources.stave.StaveRemoteDataStore
import com.euphoiniateam.euphonia.domain.models.Stave
import com.euphoiniateam.euphonia.domain.repos.StaveRepository
import kotlinx.coroutines.delay

class StaveRepositoryImpl(
    private val localDataStore: StaveLocalDataStore,
    private val remoteDataStore: StaveRemoteDataStore
): StaveRepository{

    override suspend fun getStave(): Stave {
        return localDataStore.loadData().toStave()
    }

    override suspend fun generateStave(): Stave {
        val newStave = remoteDataStore.getData()
        delay(2000)
        val staveToCache = newStave.toStave().toLocalStave()
        localDataStore.saveData(staveToCache)

        return newStave.toStave()
    }

}