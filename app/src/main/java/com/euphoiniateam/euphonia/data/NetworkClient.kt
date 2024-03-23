package com.euphoiniateam.euphonia.data

import com.euphoiniateam.euphonia.data.datasources.stave.StaveApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

object NetworkClient {
    val client = OkHttpClient()
        .newBuilder()
        .build()
}

object NetworkService {
    private val BASE_URL = "http://176.106.243.220:8008/"
    private val retrofit = Retrofit.Builder()
        .client(NetworkClient.client)
        .baseUrl(BASE_URL)
        .build()

    val  euphoniaApi = retrofit.create<StaveApi>()
}