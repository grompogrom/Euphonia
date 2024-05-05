package com.euphoiniateam.euphonia.ui

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow

class MidiPlayer {
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var currentUri: Uri? = null
    val playerState = MutableStateFlow(false)

    init {
        mediaPlayer.setOnCompletionListener { playerState.tryEmit(false) }
    }

    fun initWithTrack(context: Context, midiUri: Uri) {
        currentUri = midiUri
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, midiUri)
        mediaPlayer.prepare()
        Log.d("AAA", "Player prepared")
    }

    fun play(context: Context, midiUri: Uri) {
        initWithTrack(context, midiUri)
        playerState.tryEmit(true)
        mediaPlayer.start()
        Log.d("AAA", "Player started. Current uri: $midiUri")
    }
    fun stop() {
        mediaPlayer.stop()
        playerState.tryEmit(false)
        Log.d("AAA", "Player stopped")
    }

    fun release() = mediaPlayer.release()
}