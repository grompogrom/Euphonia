package com.euphoiniateam.euphonia.data.repos

import android.net.Uri
import android.util.Log
import com.euphoiniateam.euphonia.data.models.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.source.stave.StaveLocalDataStore
import com.euphoiniateam.euphonia.data.source.stave.StaveRemoteDataSource
import com.euphoiniateam.euphonia.domain.GenerationException
import com.euphoiniateam.euphonia.domain.repos.GenerationRepository

class GenerationRepositoryImpl(
    private val localDataStore: StaveLocalDataStore,
    private val remoteDataStore: StaveRemoteDataSource
) : GenerationRepository {
    private var currentMidiSource: Uri? = null
    private var generatedMidiSource: Uri? = null
    private var countToGenerate: Int = 5

    override fun setCountToGenerate(count: Int) {
        val candidate = minOf(count, 20)
        countToGenerate = maxOf(candidate, 2)
    }
    override suspend fun generateNew(prompt: Uri): Uri {
        if (currentMidiSource == null) {
            currentMidiSource = prompt
            generatedMidiSource = null
        }
        Log.d("StaveRemoteDataSourceImpl", "new uri is EMPTY ${prompt == Uri.EMPTY}")

        Log.d("StaveRemoteDataSourceImpl", "current uri is EMPTY ${currentMidiSource == Uri.EMPTY}")
        return generateMidi(generatedMidiSource ?: currentMidiSource!!, true)
    }

    override suspend fun regenerateLast(): Uri {
        val tmp = generatedMidiSource
        try {
            generatedMidiSource = null
            val result = generateMidi(currentMidiSource ?: Uri.EMPTY, true)
            return result
        } catch (e: Exception) {
            generatedMidiSource = tmp
            throw e
        }
    }

    override suspend fun generateNewNoIncludedPrompt(prompt: Uri): Uri {
        if (currentMidiSource == null) {
            currentMidiSource = prompt
            generatedMidiSource = null
        }
        return generateMidi(generatedMidiSource ?: currentMidiSource!!, false, 20)
    }

    private suspend fun generateMidi(
        prompt: Uri,
        includePrompt: Boolean,
        count: Int = countToGenerate
    ): Uri {
        try {
            val remoteTrackResponse = remoteDataStore.generate(
                RemoteTrackRequest(prompt, count, includePrompt)
            )
            onSuccessGeneration(remoteTrackResponse.uri)
            return remoteTrackResponse.uri
        } catch (e: GenerationException) {
            throw e
        } catch (e: Exception) {
            Log.e("GenerationRepositoryImpl", "due generation", e)
            throw GenerationException("Undefined error while generation", e)
        }
    }

    private fun onSuccessGeneration(newUri: Uri) {
        generatedMidiSource?.let {
            currentMidiSource = generatedMidiSource
        }
        generatedMidiSource = newUri
    }
}
