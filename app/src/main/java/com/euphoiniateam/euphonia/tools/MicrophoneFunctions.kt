package com.euphoiniateam.euphonia.tools

import android.content.Context
import android.media.MediaRecorder
import android.widget.Toast
import java.io.File
import java.io.IOException

class MicrophoneFunctions() {
    private var mediaRecorder: MediaRecorder? = null
    fun startRecording(applicationContext: Context): Boolean {
        val outputFile = getOutputFile(applicationContext)
        if (outputFile == null) {
            Toast.makeText(
                applicationContext,
                "Failed to create audio file",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        mediaRecorder = MediaRecorder(applicationContext).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()
        return true
    }

    fun stopRecording(applicationContext: Context) {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Toast.makeText(applicationContext, "Recording stopped", Toast.LENGTH_SHORT).show()
    }
    private fun getOutputFile(applicationContext: Context): File? {
        val cacheDir = applicationContext.cacheDir
        val timeStamp = System.currentTimeMillis()
        val audioFileName = "AUDIO_$timeStamp.mp3"
        return File(cacheDir, audioFileName)
    }
}
