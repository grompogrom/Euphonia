package com.euphoiniateam.euphonia.domain

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun getMidFileNamesFromResults(): ArrayList<String> {
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "results")
    dir.mkdirs()
    val midiFilesNames = ArrayList<String>()
    dir.listFiles()?.forEach { file ->
        if (file.isFile && file.extension.equals("mid", ignoreCase = true)) {
            midiFilesNames.add(file.name)
        }
    }
    return midiFilesNames
}

fun getMidFileNamesFromPiano(): ArrayList<String> {
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "piano")
    dir.mkdirs()
    val midiFilesNames = ArrayList<String>()
    dir.listFiles()?.forEach { file ->
        if (file.isFile && file.extension.equals("mid", ignoreCase = true)) {
            midiFilesNames.add(file.name)
        }
    }
    return midiFilesNames
}

fun saveMidiFileToPianoDir(file: File, fileName: String){
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "piano")
    dir.mkdirs()
    val inputStream: InputStream = file.inputStream()
    val outputStream = FileOutputStream(File(dir, fileName))
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

fun saveMidiFileToResultsDir(file: File, fileName: String){
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "results")
    dir.mkdirs()
    val inputStream: InputStream = file.inputStream()
    val outputStream = FileOutputStream(File(dir, fileName))
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}
