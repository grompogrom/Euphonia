package com.euphoiniateam.euphonia.ui.piano

import android.content.pm.ActivityInfo
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentPianoBinding
import com.euphoiniateam.euphonia.ui.creation.StaveView
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

private val notes = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
private val blackKeys = arrayOf(1, 3, 6, 8, 10)
private var noteLengthData : MutableList<NoteLenght> = mutableListOf()

class PianoFragment : Fragment() {

    private val viewModel: PianoViewModel by viewModels {
        PianoViewModel.provideFactory(
            requireContext()
        )
    }
    private var binding: FragmentPianoBinding? = null
    private var sndPool: SoundPool = SoundPool.Builder().setMaxStreams(5).build()
    private var noteMap: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        binding = FragmentPianoBinding.inflate(inflater, container, false)
//        initKeyboard(inflater, container)
        initKeyboard2()
        initOverlay()
        initButtons()
        initStave()
        observeScreenState()
        return binding!!.root
    }

    private fun onKeyboardKeyDown(octave: Int, pitch: Int) {
        noteMap[pianoKey(notes[pitch], octave)]?.let {
            sndPool.play(
                it, 1F,
                1F, 1 ,0, 1.0f)
        }?.let {
            NoteLenght(pitch, octave,
                it
            )
        }?.let { noteLengthData.add(it) }


        viewModel.onPushKey(
            PianoEvent(
                -1L,
                System.currentTimeMillis(),
                pitch,
                octave
            )
        )
    }

    private fun onKeyboardKeyUp(octave: Int, pitch: Int) {
        viewModel.onRealiseKey(pitch, octave)
        for(l in 0 until noteLengthData.size) {
            if(noteLengthData[l].keyNum == pitch && noteLengthData[l].pitch == octave){
                slowVolumeDown(noteLengthData[l].player)
                noteLengthData.removeAt(l)
            }
        }
    }

    fun slowVolumeDown(streamId : Int){
        var V = 1F
        thread {
            while(V > 0.1F){
                V -= 0.1F
                sndPool.setVolume(streamId,V,V)
                Thread.sleep(50)
            }
            sndPool.stop(streamId)
        }
    }

    private fun initKeyboard2() {
        val octaveCount = 3
        for (octaveN in 0 until octaveCount) {
            for (pitchN in 0 until 12) {
                noteMap[pianoKey(notes[pitchN], octaveN)] = sndPool.load(
                    requireContext(),
                    pianoKey(notes[pitchN], octaveN),
                    1
                )
            }
        }
        binding?.composePiano?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    PianoKeyboard(
                        onKeyDown = { octave: Int, pitch: Int -> onKeyboardKeyDown(octave, pitch) },
                        onKeyUp = { octave: Int, pitch: Int -> onKeyboardKeyUp(octave, pitch) }
                    )
                }
            }
        }
    }

    private fun initOverlay() {
        binding?.overviewComposeView?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    val recordingState by viewModel.screenState.collectAsState()
                    PianoOverview(
                        recordState = recordingState.recordingState,
                        onExitClick = { viewModel.exit { findNavController().navigateUp() } },
                        onRecordClick = {
                            viewModel.startRecord()
                        },
                        onStopRecordClick = {
                            viewModel.stopRecord(requireContext())
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }

    private fun navigateToCreationScreen(uri: Uri) {
        val bundle = Bundle()
        bundle.putString("uri", uri.toString())
        findNavController().navigate(R.id.action_pianoFragment_to_creationFragment, bundle)
    }

    private fun initButtons() {
        binding?.buttonsComposeView?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    ButtonSection(
                        isPlaying = viewModel.screenState.value.isPlayingResult,
                        onPlayClick = { viewModel.onPlayPush(requireContext()) },
                        onApplyClick = { viewModel.applyRecord(::navigateToCreationScreen) },
                        onRemakeClick = { viewModel.remake() }
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
                            state = viewModel.staveConfig,
                        )
                    }
                }
            }
        }
        // TODO: init stave compose view
    }

    fun observeScreenState() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.screenState.collect { state ->
                        when (state.recordingState) {
                            PianoState.RECORDING -> {}
                            PianoState.NO_RECORD -> {
                                showPiano()
                            }

                            PianoState.AFTER_RECORD -> {
                                showRecordResult()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showRecordResult() {
        binding?.overviewComposeView?.visibility = View.GONE
        binding?.composePiano?.visibility = View.GONE
        binding?.buttonsComposeView?.visibility = View.VISIBLE
        binding?.staveComposeView?.visibility = View.VISIBLE
    }

    private fun showPiano() {
        binding?.overviewComposeView?.visibility = View.VISIBLE
        binding?.composePiano?.visibility = View.VISIBLE
        binding?.buttonsComposeView?.visibility = View.GONE
        binding?.staveComposeView?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }
}

private fun pianoKey(key: String, octave: Int): Int {
    var resource: Int

    if (octave == 1) { // C5
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
    } else if (octave == 0) { // C4
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
