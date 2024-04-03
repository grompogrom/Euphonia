package com.euphoiniateam

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.euphoiniateam.euphonia.data.datamodels.RemoteTrackRequest
import com.euphoiniateam.euphonia.data.datasources.stave.StaveRemoteDataSourceImp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StaveRemoteDataSourceTest() : Instrumentation() {
    lateinit var contextt: Context

    @Before
    fun setUp() {
        contextt = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun loadFileFromApi() {
        runBlocking {
            val source = StaveRemoteDataSourceImp(contextt)
//            try {
            val result = source.getFileFromServer("token")
            Log.d("AAA", result.toString())
            assert(true)
//            }  catch (e: Exception){
//                assert(false)
//            }
        }
    }

//    @Test
    fun sendFileForGeneration() = runBlocking {
        val source = StaveRemoteDataSourceImp(contextt)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val uri = getUriFromAsset(context, "input.mid")
        val token = source.sendFileForGeneration(uri!!, mapOf())
        Log.d("AAA", token)
        Unit
    }

//    @Test
    fun proceedMidiGeneration() {
        runBlocking {
            val source = StaveRemoteDataSourceImp(contextt)
            val remoteTrackRequest = RemoteTrackRequest(
                uri = getUriFromAsset(contextt, "input.mid")!!,
                countToGenerate = 10
            )
            val res = source.generate(remoteTrackRequest)
            Log.d("AAA", "result is ${res.uri}")
        }
    }

    @Test
    fun getUriFromAssetTest() {
        val assetUri = getUriFromAsset(contextt, "input.mid")!!
        val newUri = StaveRemoteDataSourceImp(contextt).saveToCache(assetUri.toFile().readBytes())
        Log.d("DDD", newUri.toFile().readBytes().toString())
//        assert(assetUri.toFile().readBytes().contentEquals(newUri.toFile().readBytes()))
        assert(
            newUri.toFile().readBytes().contentEquals(contextt.assets.open("input.mid").readBytes())
        )
    }

    @Throws(IOException::class)
    fun getUriFromAsset(context: Context, assetFileName: String): Uri? {
        val assetManager = context.assets
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        var tempFile: File? = null

        return try {
            inputStream = assetManager.open(assetFileName)
            tempFile = File.createTempFile("temp_asset", null, context.cacheDir)
            outputStream = FileOutputStream(tempFile)

            inputStream.copyTo(outputStream)

            Uri.fromFile(tempFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
            tempFile?.deleteOnExit()
        }
    }
}
