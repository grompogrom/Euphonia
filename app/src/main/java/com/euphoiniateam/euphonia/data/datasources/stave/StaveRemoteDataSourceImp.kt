package com.euphoiniateam.euphonia.data.datasources.stave

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.euphoiniateam.euphonia.data.NetworkService
import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackResponse
import java.io.File
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody

class StaveRemoteDataSourceImp(
    val context: Context
) : StaveRemoteDataStore {

    override suspend fun getData(): RemoteStave {
        return RemoteStave(1, 1, emptyList(), emptyList())
    }

    override suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse {
        val token = sendFileForGeneration(
            track.uri,
            mapOf("count" to track.countToGenerate.toString())
        )
        val newTrackBytes = getFileFromServer(token)
        return RemoteTrackResponse(
            uri = saveToCache(newTrackBytes)
        )
    }

    suspend fun sendFileForGeneration(uri: Uri, meta: Map<String, String>): String {
        val file = uri.toFile()
        val requestBody = file.asRequestBody("audio/midi".toMediaTypeOrNull())
        val response = NetworkService.euphoniaApi
            .startGeneration(requestBody).body()?.string() ?: ""

        return response
    }

    suspend fun getFileFromServer(token: String): ByteArray {
        var response: retrofit2.Response<ResponseBody>? = null
        for (i in 1..100) {
            response = NetworkService.euphoniaApi.getGenerated(token)
            when (response.code()) {
                202 -> {
                    delay(100)
                }
                200 -> break
                else -> throw Exception()
            }
        }
        if (response?.code() == 200 && response.body() != null) {

            return response.body()?.bytes()!!
        }
        throw Exception("NOT FOUND FILE")
    }

    fun saveToCache(file: ByteArray): Uri {
        val cacheDir = context.cacheDir
        val newFile = File(cacheDir, "new.mid")
        newFile.writeBytes(file)
        newFile.createNewFile()
        return Uri.fromFile(newFile)
    }
}
