package com.euphoiniateam.euphonia.data.source

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StaveApi {

    @GET("get/{token}/")
    suspend fun getGenerated(
        @Path("token") token: String
    ): Response<ResponseBody>

    @POST("generate/")
    suspend fun startGeneration(
        @Body midi: RequestBody,
        @Header("IncludePrompt") includePrompt: Int,
        @Header("CountToGen") countToGen: Int
    ): Response<ResponseBody>

    @GET("getc/{token}/")
    suspend fun getConverted(
        @Path("token") token: String
    ): Response<ResponseBody>

    @POST("convert/")
    suspend fun startConverting(
        @Body mp3: RequestBody
    ): Response<ResponseBody>
}
