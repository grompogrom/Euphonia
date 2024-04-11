package com.euphoiniateam.euphonia.ui.piano


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.euphoiniateam.euphonia.R
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import java.io.File


class PianoFragment : Fragment() {


    private lateinit var viewModel: PianoViewModel
    private val notes = arrayListOf("C", "D", "C#", "E", "D#", "F", "G", "F#", "A", "G#", "B", "A#")
    //private val notes = arrayListOf<Pair<String, Int>>()
    private val notePosMidi = arrayListOf(0, 2, 1, 4, 3, 5, 7, 6, 9, 8, 11, 10)
    private var noteMap : MutableMap<Int, Int> = mutableMapOf()
    private var sndPool : SoundPool = SoundPool.Builder().setMaxStreams(5).build()
    private var isRecording = false
    private lateinit var recordButton : Button
    private var recordData : MutableList<PianoPlayer> = mutableListOf()
    private var previousPressTime : Long = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_piano, container, false)
        val LLayout = rootView.findViewById<LinearLayout>(R.id.linear1)

        //createMidiFile(notess, "output.mid")

        recordButton = rootView.findViewById(R.id.record_button)
        recordButton.setOnClickListener {
            isRecording = !isRecording
            if(isRecording){
                recordButton.setBackgroundResource(android.R.drawable.presence_online)
            } else recordButton.setBackgroundResource(R.drawable.record_button)
            if(!isRecording && recordData.isNotEmpty()){
//                playRecord()
                createMidiWithApi()



            }

//            Toast.makeText(requireContext(), isRecording.toString(), Toast.LENGTH_SHORT).show()
        }
        for(i in 0..2) {
            val pianoView : View = inflater.inflate(R.layout.piano, container, false)
            val octave: ConstraintLayout = pianoView.findViewById(R.id.octave)
            for(x in 0 until octave.childCount){
                noteMap[pianoKey(notes[x],i)] = sndPool.load(
                    requireContext(),
                    pianoKey(notes[x],i), 1)
                (octave.getChildAt(x) as Button).setOnTouchListener { v, event ->
                    Log.d("dd", "dd")
                    when(event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if(x == 2 || x == 4 || x == 7 || x == 9 || x == 11) {
                                v.background =
                                    ColorDrawable(resources.getColor(R.color.md_theme_dark_background))
                            }
                            else {
                                v.background = ColorDrawable((resources.getColor(androidx.appcompat.R.color.material_grey_50)))
                            }
                            noteMap[pianoKey(notes[x], i)]?.let { it1 -> sndPool.play(it1, 1F,
                                1F, 1 ,0, 1.0f) }
                            if(isRecording) recordData.add(PianoPlayer(-1L, System.currentTimeMillis(), x, i))
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            if(x == 2 || x == 4 || x == 7 || x == 9 || x == 11) {
                                v.background =
                                    ColorDrawable(resources.getColor(androidx.cardview.R.color.cardview_dark_background))
                            }else {
                                v.background = (resources.getDrawable(R.drawable.piano_borders))
                            }
                            if(isRecording){
                                for(l in 0 until recordData.size){
                                    if(recordData[l].elapseTime == -1L && recordData[l].keyNum == x && recordData[l].pitch == i){
                                        recordData[l].elapseTime = System.currentTimeMillis()
                                    }
                                }
                            }
                            true
                        }

                        else -> {true}

                    }
                }
               /* (octave.getChildAt(x) as Button).setOnClickListener {
                    if(isRecording) {
                        val currentTimeMillis = SystemClock.elapsedRealtime()
                        val elapsedTimeMillis = if (previousPressTime != 0L) {
                            currentTimeMillis - previousPressTime
                        } else 0
                        previousPressTime = currentTimeMillis

                        recordData.add(PianoPlayer(elapsedTimeMillis, x, i))
                    }
                    noteMap[pianoKey(notes[x], i)]?.let { it1 -> sndPool.play(it1, 1F,
                        1F, 1 ,0, 1.0f) }
                }*/
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

    private fun pianoKey(key : String, pitch : Int) : Int {
//        Log.d("key", key)
        var resource : Int = R.raw.c

        if(pitch == 1) { //C5
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
        } else if(pitch == 0){ //C4
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
        } else{ //C6
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
        //Log.d("PianoKey", key)
        return resource
    }

    /*private fun playRecord(){
        thread(true){
            for(i in 0 until recordData.size){
                Log.d("sleep", recordData[i].second.toString())
                Thread.sleep(recordData[i].second)
                noteMap[pianoKey(notes[recordData[i].keyNum], recordData[i].pitch)]?.let { it1 -> sndPool.play(it1, 1F,
                    1F, 1 ,0, 1.0f) }
            }
            recordData.clear()
            previousPressTime = 0

        }

    }*/
    private fun createMidiWithApi() {
        for(i in 0 until recordData.size)Log.d("wtf", recordData[i].keyNum.toString() + " " + (recordData[i].elapseTime - recordData[i].pressTime).toString())
        val file = File(requireContext().applicationContext.externalCacheDir, "out.mid")
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()
        val ts = TimeSignature()
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
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

            noteTrack.insertNote(channel, pitch, velocity, tick, duration)
        }
        val tracks: MutableList<MidiTrack> = ArrayList()
        tracks.add(tempoTrack)
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        midi.writeToFile(file)
    }

    private fun notesToMidiNotes(noteNum : Int, pitch : Int) : Int{
        return if(pitch == 0) notePosMidi[noteNum] + 60
        else if (pitch == 1) notePosMidi[noteNum] + 72
        else notePosMidi[noteNum] + 48
    }
}