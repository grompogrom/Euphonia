package com.euphoiniateam.euphonia.data.repos

import android.net.Uri
import com.euphoiniateam.euphonia.data.models.RemoteConvertRequest
import com.euphoiniateam.euphonia.data.source.convert.ConvertDataSource
import com.euphoiniateam.euphonia.domain.ConvertingException
import com.euphoiniateam.euphonia.domain.repos.AudioToMidiRepository

class AudioToMidiRepositoryImpl(private val dataSource: ConvertDataSource): AudioToMidiRepository {
    override suspend fun convertMp3ToMidi(mp3: Uri): Uri {
        try {
            val convertedResult = dataSource.convert(
                RemoteConvertRequest(mp3)
            )
            return convertedResult.uri
        } catch (e: ConvertingException){
            throw e
        }
        catch (e: Exception) {
            throw ConvertingException("Undefined error while generation", e)
        }
    }
}