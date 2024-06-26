package com.euphoiniateam.euphonia.tools

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
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

fun getUriForFileNameFromPiano(context: Context, fileName: String): Uri? {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "piano"
    )
    val file = File(dir, fileName)
    return if (file.exists()) FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    ) else null
}

fun getUriForFileNameFromResults(context: Context, fileName: String): Uri? {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "results"
    )
    val file = File(dir, fileName)
    return if (file.exists()) FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    ) else null
}

fun saveMidiFileToPianoDir(contentResolver: ContentResolver, uri: Uri, fileName: String) {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "piano"
    )
    dir.mkdirs()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "audio/midi")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}/piano")
    }
    val insertUri: Uri? = contentResolver.insert(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    if (insertUri != null) {
        contentResolver.openOutputStream(insertUri).use { outputStream ->
            outputStream?.write(
                contentResolver.openInputStream(uri)?.readAllBytes() ?: byteArrayOf()
            )
        }
    } else {
        throw IllegalStateException("Unable to create new file in MediaStore")
    }
}

fun saveMidiFileToResultsDir(contentResolver: ContentResolver, uri: Uri, fileName: String) {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "results"
    )
    dir.mkdirs()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "audio/midi")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}/results")
    }
    val insertUri: Uri? = contentResolver.insert(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    if (insertUri != null) {
        contentResolver.openOutputStream(insertUri).use { outputStream ->
            outputStream?.write(
                contentResolver.openInputStream(uri)?.readAllBytes() ?: byteArrayOf()
            )
        }
    } else {
        throw IllegalStateException("Unable to create new file in MediaStore")
    }
}

fun saveMidiFileToCache(context: Context, inputStream: InputStream, fileName: String): Uri? {
    val cacheDir = context.applicationContext.externalCacheDir
    val outputStream = FileOutputStream(File(cacheDir, fileName))
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    val savedFile = File(cacheDir, fileName)
    return if (savedFile.exists())
        FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            savedFile
        )
    else null
}

fun getMicroOutputFile(applicationContext: Context): File {
    val cacheDir = applicationContext.externalCacheDir
    val audioFileName = "AUDIO_MICRO.ogg"
    return File(cacheDir, audioFileName)
}
