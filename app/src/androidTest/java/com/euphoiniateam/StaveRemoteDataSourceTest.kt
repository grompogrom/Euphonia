package com.euphoiniateam

import android.content.Context
import com.euphoiniateam.euphonia.data.datasources.stave.StaveRemoteDataSourceImp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mock


class StaveRemoteDataSourceTest() {
    val MID_TOKEN = "1d0c7efc-706c-493b-880d-8ab81ddb2b5e"

    @Mock
    private lateinit var mockContext: Context

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadFileFromApi(){
        val source = StaveRemoteDataSourceImp(mockContext)
        runTest {
            val result = source.getFileFromServer()
            println(result)
        }
    }

}