package com.euphoiniateam.euphonia.ui.piano

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentPianoBinding
import com.euphoiniateam.euphonia.ui.creation.StaveConfig
import com.euphoiniateam.euphonia.ui.creation.StaveView
import kotlinx.coroutines.launch

private val notes = arrayOf("C", "D", "C#", "E", "D#", "F", "G", "F#", "A", "G#", "B", "A#")
private val blackKeys = arrayOf(2, 4, 7, 9, 11)

class PianoFragment : Fragment() {

    private val viewModel: PianoViewModel by viewModels()
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
        initKeyboard(inflater, container)
        initOverlay()
        initButtons()
        initStave()
        observeScreenState()
        return binding!!.root
    }

    @SuppressLint("ClickableViewAccessibility") // FIXME
    private fun initKeyboard(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
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
                            if (x in blackKeys) {
                                v.background =
                                    ColorDrawable(
                                        resources.getColor(R.color.md_theme_dark_background)
                                    )
                            } else {
                                v.background = ColorDrawable(
                                    (resources.getColor(androidx.appcompat.R.color.material_grey_50))
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
                            viewModel.onPushKey(PianoEvent(-1L, System.currentTimeMillis(), x, i))
                        }

                        MotionEvent.ACTION_UP -> {
                            if (x in blackKeys) {
                                v.background =
                                    ColorDrawable(
                                        getColor(
                                            requireContext(),
                                            androidx.cardview.R.color.cardview_dark_background
                                        )
                                    )
                            } else {
                                v.background =
                                    getDrawable(requireContext(), R.drawable.piano_borders)
                            }

                            viewModel.onRealiseKey(x, i)
                        }

                        else -> {}
                    }
                    true
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
                        onExitClick = { viewModel.exit {} },
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
                        onPlayClick = { /*TODO*/ },
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
                            state = StaveConfig(), // use from vm
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
}

private fun pianoKey(key: String, pitch: Int): Int {
    var resource: Int

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
