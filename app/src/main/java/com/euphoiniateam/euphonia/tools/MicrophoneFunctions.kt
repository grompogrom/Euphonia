package com.euphoiniateam.euphonia.tools

import android.content.Context
import android.media.MediaRecorder
import android.widget.Toast
import java.io.File
import java.io.IOException

object MicrophoneFunctions {
    private var mediaRecorder: MediaRecorder? = null
    fun startRecording(applicationContext: Context, outputFile: File): Boolean {

        mediaRecorder = MediaRecorder(applicationContext).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.OGG)
            setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
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

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
}
