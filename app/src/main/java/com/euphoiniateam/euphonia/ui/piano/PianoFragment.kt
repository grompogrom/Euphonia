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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.ui.creation.StaveConfig
import com.euphoiniateam.euphonia.ui.creation.StaveView
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

        val noteView = rootView.findViewById<ComposeView>(R.id.stave_compose_view)
        val applyBtn = rootView.findViewById<Button>(R.id.button2)
        applyBtn.setOnClickListener {
            val action = PianoFragmentDirections.actionPianoFragmentToCreationFragment()
            findNavController().navigate(action)
        }
        noteView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    StaveView(state = StaveConfig(linesCount = 1))
                }
            }
        }


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