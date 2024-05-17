package com.euphoiniateam.euphonia.tools

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// TODO: положим в объект все функции?

fun getMidFileNamesFromResults(): ArrayList<String> {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "results"
    )
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
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "piano"
    )
    dir.mkdirs()
    val midiFilesNames = ArrayList<String>()
    dir.listFiles()?.forEach { file ->
        if (file.extension.equals("mid", ignoreCase = true)) {
            midiFilesNames.add(file.name)
        }
    }
    return midiFilesNames
}

fun getUriForFileNameFromPiano(fileName: String): Uri? {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "piano"
    )
    val file = File(dir, fileName)
    return if (file.exists()) Uri.fromFile(file) else null
}

fun getUriForFileNameFromResults(fileName: String): Uri? {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "results"
    )
    val file = File(dir, fileName)
    return if (file.exists()) Uri.fromFile(file) else null
}

fun saveMidiFileToPianoDir(contentResolver: ContentResolver, uri: Uri, fileName: String) {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "piano"
    )
    dir.mkdirs()
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(File(dir, fileName))
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

fun saveMidiFileToResultsDir(contentResolver: ContentResolver, uri: Uri, fileName: String) {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "results"
    )
    dir.mkdirs()
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(File(dir, fileName))
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

fun saveMidiFileToCache(context: Context, inputStream: InputStream, fileName: String): Uri? {
    val cacheDir = context.cacheDir
    val outputStream = FileOutputStream(File(cacheDir, fileName))
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    val savedFile = File(cacheDir, fileName)
    return if (savedFile.exists()) Uri.fromFile(savedFile) else null
}
