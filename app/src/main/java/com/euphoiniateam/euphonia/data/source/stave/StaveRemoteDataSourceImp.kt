package com.euphoiniateam.euphonia.data.source.stave

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.euphoiniateam.euphonia.data.NetworkService
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

    override suspend fun generate(track: RemoteTrackRequest): RemoteTrackResponse {
        Log.d("StaveRemoteDataSourceImpl", "generation request")
        val token = sendFileForGeneration(
            track.uri,
            track.countToGenerate,
            track.includePrompt,
        )
        val newTrackBytes = getFileFromServer(token)
        return RemoteTrackResponse(
            uri = saveMidiFileToCache(context, newTrackBytes.inputStream(), "outfile.mid") ?: Uri.EMPTY
        )
    }

    suspend fun sendFileForGeneration(
        uri: Uri,
        countToGen: Int,
        includePrompt: Boolean = true
    ): String {

        Log.d("StaveRemoteDataSourceImpl", "file is EMPTY ${uri == Uri.EMPTY}")
        // костыль ибо разные форматы при открытии файла и записи с пиаино
        val file: File =
            try {
                Uri.parse(uri.toString()).toFile()
            } catch (e: IllegalArgumentException) {
                context.contentResolver.openInputStream(uri).use { input ->
                    val tmp = createTempFile()
                    tmp.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                    tmp
                }
            }

        val requestBody = file.asRequestBody("audio/midi".toMediaTypeOrNull())

        return NetworkService.euphoniaApi
            .startGeneration(
                requestBody,
                if (includePrompt) 1 else 0,
                countToGen
            ).body()?.string() ?: ""
    }

    suspend fun getFileFromServer(token: String): ByteArray {
        var response: retrofit2.Response<ResponseBody>? = null
        for (i in 1..200) {
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
}
