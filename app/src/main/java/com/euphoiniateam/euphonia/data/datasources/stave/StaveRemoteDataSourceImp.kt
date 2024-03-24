package com.euphoiniateam.euphonia.data.datasources.stave

import android.content.Context
import android.net.Uri
import com.euphoiniateam.euphonia.data.NetworkClient
import com.euphoiniateam.euphonia.data.NetworkService
import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.File
import java.net.URI

class StaveRemoteDataSourceImp(val context: Context): StaveRemoteDataStore {

    override suspend fun getData(): RemoteStave {
//        NetworkService.euphoniaApi.startGeneration()
    return RemoteStave(1,1, emptyList(), emptyList())
    }

    suspend fun getFileFromServer(): Uri{
        var response: ResponseBody? = null
        for (i in 1..100) {
            try {
                response = NetworkService.euphoniaApi.getGenerated("token")
                break
            } catch (e: HttpException) {
                if (e.code() != 202) {
                    throw e
                }
                continue
            }
        }
        if (response != null) {
            return saveToCache(context, response.bytes())
        }
        throw Exception()
    }

    private fun saveToCache(context: Context, file: ByteArray): Uri {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "cache")
        file.createNewFile()
        return Uri.fromFile(file)
    }
}