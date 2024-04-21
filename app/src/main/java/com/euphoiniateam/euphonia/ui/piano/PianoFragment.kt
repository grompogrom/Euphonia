package com.euphoiniateam.euphonia.ui.piano

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentPianoBinding
import com.euphoiniateam.euphonia.ui.creation.StaveConfig
import com.euphoiniateam.euphonia.ui.creation.StaveView
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import java.io.File

class PianoFragment : Fragment() {

    private val viewModel: PianoViewModel by viewModels()
    private var binding: FragmentPianoBinding? = null
    private val notes = arrayListOf("C", "D", "C#", "E", "D#", "F", "G", "F#", "A", "G#", "B", "A#")
    private val notePosMidi = arrayListOf(0, 2, 1, 4, 3, 5, 7, 6, 9, 8, 11, 10)
    private var noteMap: MutableMap<Int, Int> = mutableMapOf()
    private var sndPool: SoundPool = SoundPool.Builder().setMaxStreams(5).build()
    private var isRecording = false
    private lateinit var recordButton: Button
    private var recordData: MutableList<PianoPlayer> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        binding = FragmentPianoBinding.inflate(inflater, container, false)

        recordButton = binding!!.recordButton
        recordButton.setOnClickListener {
            isRecording = !isRecording
            if (viewModel.screenState.recordingState == PianoState.RECORDING) {
                recordButton.setBackgroundResource(android.R.drawable.presence_online)
            } else recordButton.setBackgroundResource(R.drawable.record_button)
            if (!isRecording && recordData.isNotEmpty()) {
                createMidiWithApi()
            }
        }
        for (i in 0..2) {
            val pianoView: View = inflater.inflate(R.layout.piano, container, false)
            val octave: ConstraintLayout = pianoView.findViewById(R.id.octave)
            for (x in 0 until octave.childCount) {
                noteMap[pianoKey(notes[x], i)] = sndPool.load(
                    requireContext(),
                    pianoKey(notes[x], i),
                    1
                )
                (octave.getChildAt(x) as Button).setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if (x == 2 || x == 4 || x == 7 || x == 9 || x == 11) {
                                v.background =
                                    ColorDrawable(
                                        resources.getColor(R.color.md_theme_dark_background)
                                    )
                            } else {
                                v.background = ColorDrawable(
                                    (
                                        resources.getColor(
                                            androidx.appcompat.R.color.material_grey_50
                                        )
                                        )
                                )
                            }
                            noteMap[pianoKey(notes[x], i)]?.let { it1 ->
                                sndPool.play(
                                    it1,
                                    1F,
                                    1F,
                                    1,
                                    0,
                                    1.0f
                                )
                            }
                            if (viewModel.screenState.recordingState == PianoState.RECORDING)
                                recordData.add(
                                    PianoPlayer(-1L, System.currentTimeMillis(), x, i)
                                )
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            if (x == 2 || x == 4 || x == 7 || x == 9 || x == 11) {
                                v.background =
                                    ColorDrawable(
                                        resources.getColor(
                                            androidx.cardview.R.color.cardview_dark_background
                                        )
                                    )
                            } else {
                                v.background = (resources.getDrawable(R.drawable.piano_borders))
                            }
                            if (viewModel.screenState.recordingState == PianoState.RECORDING) {
                                for (l in 0 until recordData.size) {
                                    if (recordData[l].elapseTime == -1L &&
                                        recordData[l].keyNum == x &&
                                        recordData[l].pitch == i
                                    ) {
                                        recordData[l].elapseTime = System.currentTimeMillis()
                                    }
                                }
                            }
                            true
                        }

                        else -> {
                            true
                        }
                    }
                }
            }
            binding!!.linear1.addView(
                pianoView,
                i,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                    .apply { gravity = Gravity.TOP }
            )
        }
        initOverlay()
        initButtons()
        initStave()
        return binding!!.root
    }

    private fun initOverlay() {
        binding?.overviewComposeView?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    PianoOverview(
                        recordState = viewModel.screenState.recordingState,
                        onExitClick = { viewModel.exit() },
                        onRecordClick = {
                            showRecordResult()
                            viewModel.startRecord()
                        },
                        onStopRecordClick = { viewModel.stopRecord() },
                        modifier = Modifier
                    )
                }
            }
        }
    }

    private fun initButtons() {
        binding?.buttonsComposeView?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    ButtonSection(
                        onPlayClick = { /*TODO*/ },
                        onApplyClick = { /*TODO*/ },
                        onRemakeClick = { showPiano() }
                    )
                }
            }
        }
    }

    private fun initStave() {
        binding?.staveComposeView?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        StaveView(
                            state = StaveConfig(),
                        )
                    }
                }
            }
        }
        // TODO: init stave compose view
    }

    private fun showRecordResult() {
        binding?.overviewComposeView?.visibility = View.GONE
        binding?.scrollView?.visibility = View.GONE
        binding?.buttonsComposeView?.visibility = View.VISIBLE
        binding?.staveComposeView?.visibility = View.VISIBLE
    }

    private fun showPiano() {
        binding?.overviewComposeView?.visibility = View.VISIBLE
        binding?.scrollView?.visibility = View.VISIBLE
        binding?.buttonsComposeView?.visibility = View.GONE
        binding?.staveComposeView?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }

    private fun pianoKey(key: String, pitch: Int): Int {
        var resource: Int = R.raw.c

        if (pitch == 1) { // C5
            resource =
                when (key) {
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
        } else if (pitch == 0) { // C4
            resource =
                when (key) {
                    "C" -> R.raw.c4
                    "D" -> R.raw.d4
                    "C#" -> R.raw.cb4
                    "E" -> R.raw.e4
                    "D#" -> R.raw.db4
                    "F" -> R.raw.f4
                    "G" -> R.raw.g4
                    "F#" -> R.raw.fb4
                    "A" -> R.raw.a4
                    "G#" -> R.raw.gb4
                    "B" -> R.raw.b4
                    "A#" -> R.raw.ab4
                    else -> R.raw.c4
                }
        } else { // C6
            resource =
                when (key) {
                    "C" -> R.raw.c6
                    "D" -> R.raw.d6
                    "C#" -> R.raw.cb6
                    "E" -> R.raw.e6
                    "D#" -> R.raw.db6
                    "F" -> R.raw.f6
                    "G" -> R.raw.g6
                    "F#" -> R.raw.fb6
                    "A" -> R.raw.a6
                    "G#" -> R.raw.gb6
                    "B" -> R.raw.b6
                    "A#" -> R.raw.ab6
                    else -> R.raw.c6
                }
        }
        return resource
    }

    private fun createMidiWithApi() {
        val file = File(requireContext().applicationContext.externalCacheDir, "out.mid")
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()
        val ts = TimeSignature()
        ts.setTimeSignature(
            4,
            4,
            TimeSignature.DEFAULT_METER,
            TimeSignature.DEFAULT_DIVISION
        )
        val tempo = Tempo()
        tempo.bpm = 228f

        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        val noteCount = recordData.size
        for (i in 0 until noteCount) {
            val channel = 0
            val pitch = notesToMidiNotes(recordData[i].keyNum, recordData[i].pitch)
            val velocity = 100
            val tick = (i * 480).toLong()
            val duration: Long = recordData[i].elapseTime - recordData[i].pressTime
            Log.d("aaa", duration.toString())
            var durTick: Int
            if (duration <= 100)
                durTick = 120
            else if (duration <= 300)
                durTick = 240
            else if (duration <= 600)
                durTick = 360
            else
                durTick = 480

            val noteOn = NoteOn(tick, channel, pitch, velocity)
            val noteOff = NoteOff((tick + durTick), channel, pitch, 0)

            noteTrack.insertEvent(noteOn)
            noteTrack.insertEvent(noteOff)
//            noteTrack.insertNote(channel, pitch, velocity, tick, duration)
        }
        val tracks: MutableList<MidiTrack> = ArrayList()
        // tracks.add(tempoTrack)
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        midi.writeToFile(file)
    }

    private fun notesToMidiNotes(noteNum: Int, pitch: Int): Int {
        return if (pitch == 0) notePosMidi[noteNum] + 60
        else if (pitch == 1) notePosMidi[noteNum] + 72
        else notePosMidi[noteNum] + 48
    }
}
