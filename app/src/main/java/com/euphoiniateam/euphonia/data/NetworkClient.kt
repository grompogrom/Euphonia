package com.euphoiniateam.euphonia.data

import android.util.Log
import com.euphoiniateam.euphonia.BuildConfig
import com.euphoiniateam.euphonia.data.source.StaveApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

object NetworkClient {
    val client = OkHttpClient()
        .newBuilder()
        .build()
}

object NetworkService {

    private val BASE_URL = BuildConfig.ServerIP
    init {
        Log.d("NetworkService", "base url: $BASE_URL")
    }
    private val retrofit = Retrofit.Builder()
        .client(NetworkClient.client)
        .baseUrl(BASE_URL)
        .build()

    val euphoniaApi = retrofit.create<StaveApi>()
}
