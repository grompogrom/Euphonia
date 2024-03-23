package com.euphoiniateam.euphonia.data.datasources.stave

import com.euphoiniateam.euphonia.data.NetworkClient
import com.euphoiniateam.euphonia.data.NetworkService
import com.euphoiniateam.euphonia.data.datamodels.RemoteStave
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException

class StaveRemoteDataSourceImp: StaveRemoteDataStore {
    override suspend fun getData(): RemoteStave {
//        NetworkService.euphoniaApi.startGeneration()
    return RemoteStave(1,1, emptyList(), emptyList())
    }

    private suspend fun waitForResponse(){
        var response: ResponseBody? = null
        repeat(100) {
            try {
                response = NetworkService.euphoniaApi.getGenerated("token")
            } catch (e: HttpException) {
                if (e.code() != 202) {
                    throw e
                }
            }
        }
        response?.bytes()
    }
}