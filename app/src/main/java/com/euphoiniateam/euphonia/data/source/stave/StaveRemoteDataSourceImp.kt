package com.euphoiniateam.euphonia.data.source.stave

import android.content.Context
import android.net.Uri
import com.euphoiniateam.euphonia.data.NetworkService
import com.euphoiniateam.euphonia.data.models.RemoteStave
import com.euphoiniateam.euphonia.data.models.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.models.RemoteTrackResponse
import com.euphoiniateam.euphonia.domain.UnexpectedServerResponseException
import com.euphoiniateam.euphonia.domain.WaitForGenerationTimeoutException
import com.euphoiniateam.euphonia.tools.saveMidiFileToCache
import java.io.File
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody

// TODO: make internal
// TODO: StaveApi прокинуть извне или подумать над DI
internal class StaveRemoteDataSourceImp(
    val context: Context
) : StaveRemoteDataSource {

    // TODO: вся цепочка с getData кажется бесполезной
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
            uri = saveMidiFileToCache(context, newTrackBytes.inputStream(), token) ?: Uri.EMPTY
        )
    }

    //
    suspend fun sendFileForGeneration(uri: Uri, meta: Map<String, String>): String {
        val file = createTempFile()
        context.contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        val requestBody = file.asRequestBody("audio/midi".toMediaTypeOrNull())
//        file.delete()

        return NetworkService.euphoniaApi
            .startGeneration(requestBody).body()?.string() ?: ""
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

                else -> throw UnexpectedServerResponseException(
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

    fun saveToCache(file: ByteArray): Uri {
        val cacheDir = context.cacheDir
        val newFile = File(cacheDir, "new.mid")
        newFile.writeBytes(file)
        newFile.createNewFile()
        return Uri.fromFile(newFile)
    }
}
