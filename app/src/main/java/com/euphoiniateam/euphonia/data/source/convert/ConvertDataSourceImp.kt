package com.euphoiniateam.euphonia.data.source.convert

import android.content.Context
import android.net.Uri
import com.euphoiniateam.euphonia.data.NetworkService
import com.euphoiniateam.euphonia.data.models.RemoteConvertRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse
import com.euphoiniateam.euphonia.domain.UnexpectedServerResponse
import com.euphoiniateam.euphonia.domain.WaitForGenerationTimeoutException
import com.euphoiniateam.euphonia.tools.saveMidiFileToCache
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody

class ConvertDataSourceImp(private val context: Context) : ConvertDataSource {
    override suspend fun convert(mp3: RemoteConvertRequest): RemoteTrackResponse {
        val token = sendFileForConverting(mp3.uri)
        val convertedTrackBytes = getFileFromServer(token)
        return RemoteTrackResponse(
            uri = saveMidiFileToCache(context, convertedTrackBytes.inputStream(), token) ?: Uri.EMPTY
        )
    }

    suspend fun getFileFromServer(token: String): ByteArray {
        var response: retrofit2.Response<ResponseBody>? = null
        for (i in 1..100) {
            response = NetworkService.euphoniaApi.getConverted(token)
            when (response.code()) {
                202 -> {
                    delay(100)
                }

                200 -> break

                else -> throw UnexpectedServerResponse(
                    code = response.code(),
                    body = response.body()?.string() ?: ""
                )
            }
        }
        if (response?.code() == 200 && response.body() != null) {

            return response.body()?.bytes()!!
        }
        throw WaitForGenerationTimeoutException("Server file not ready")
    }

    suspend fun sendFileForConverting(uri: Uri): String {
        val file = createTempFile()
        context.contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        val requestBody = file.asRequestBody("audio/mp3".toMediaTypeOrNull())
//        file.delete()

        return NetworkService.euphoniaApi
            .startConverting(requestBody).body()?.string() ?: ""
    }
}