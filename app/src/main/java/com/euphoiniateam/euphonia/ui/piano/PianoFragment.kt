package com.euphoiniateam.euphonia.ui.piano

import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.euphoiniateam.euphonia.R
import kotlin.concurrent.thread

class PianoFragment : Fragment() {


    private lateinit var viewModel: PianoViewModel
    private val notes = arrayListOf("C", "D", "C#", "E", "D#", "F", "G", "F#", "A", "G#", "B", "A#")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_piano, container, false)
        val LLayout = rootView.findViewById<LinearLayout>(R.id.linear1)

        for(i in 0..1) {
            val pianoView : View = inflater.inflate(R.layout.piano, container, false)
            val octave: ConstraintLayout = pianoView.findViewById(R.id.octave)
            for(x in 0 until octave.childCount){
                (octave.getChildAt(x) as Button).setOnClickListener {
                    pianoKey(notes[x], i)
                }
            }
            LLayout.addView(pianoView, i)
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PianoViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun pianoKey(key : String, pitch : Int) : Unit {
        val resource : Int =
            when(key) {
                "C" -> R.raw.c
                "D" -> R.raw.d
                "C#" -> R.raw.cb
                "E" -> R.raw.e
                "D#" -> R.raw.db
                "F" -> R.raw.f
                "G" -> R.raw.g
                "F#" -> R.raw.fb
                "A" -> R.raw.a
                "G#" -> R.raw.gb
                "B" -> R.raw.b
                "A#" -> R.raw.ab
                else -> R.raw.c
            }
        thread(true) {
            val player = MediaPlayer.create(context, resource)
            player.start()
            player.setOnCompletionListener { mp ->
                mp.release()
            }
        }
        Log.d("PianoKey", key)
    }
}