package com.euphoiniateam.euphonia.ui.creation

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentCreation2Binding
import com.euphoiniateam.euphonia.domain.models.Note
import jp.kshoji.javax.sound.midi.MidiSystem
import jp.kshoji.javax.sound.midi.ShortMessage


class CreationFragment : Fragment() {

    private lateinit var viewModel: CreationViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var uri: Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreation2Binding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme()
                ) {
                    Screen(
                        viewModel = viewModel,
                        onExitClick = { navigateBack() },
                        modifier = Modifier.fillMaxSize(),

                    )
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, CreationViewModel.provideFactory(requireContext()))
            .get(CreationViewModel::class.java)


        uri = Uri.parse(arguments?.getString("uri"))
        mediaPlayer = MediaPlayer.create(requireContext(), uri)


        //val midiStream = JvmMidiSequencer::class.java.getResourceAsStream("/elise.mid")
        // val midiStream = JvmMidiSequencer::class.java.getResourceAsStream(stream.toString())
        val stream = context?.contentResolver?.openInputStream(uri)
        val sequencer = MidiSystem.getSequencer().apply {
            if (stream != null) {
                setSequence(stream)
            }
        }

        sequencer.sequence?.tracks?.forEachIndexed { index, track ->
            Log.d("aaa","Track $index")
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                Log.d("aaa", "Tick ${event.tick}, message: ${event.message}")
                when (val message = event.message) {
                    is ShortMessage -> {
                        val command = message.command
                        val data1 = message.data1
                        val data2 = message.data2
                        Log.d("bbb", "Tick ${event.tick}, command: $command, data1: $data1, data2: $data2")
                    }

                    else -> {}
                }
            }.take(10).toList()


        }

        val initial_notes = sequencer.sequence?.toNotes()?.slice(0..10)
        if (initial_notes != null) {
            for (note in initial_notes) {
                Log.d("note", note.toString())
            }
        }


                super.onViewCreated(view, savedInstanceState)

    }

    private fun jp.kshoji.javax.sound.midi.Sequence.toNotes(): List<Note> {
        return tracks.flatMap { track ->
            (0 until track.size()).asSequence().map { idx ->
                val event = track[idx]
                when (val message = event.message) {
                    is ShortMessage -> {
                        val command = message.command
                        val midinote = message.data1
                        val amp = message.data2
                        val note = (midinote - 24) % 12
                        if (command == ShortMessage.NOTE_ON && amp.toDouble() != 0.0) {
                            //val beat = (event.tick / resolution.toDouble())
                                //.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                                //.toDouble()
                            //return@map Note(beat, midinote, 0.25, amp / 127.0f)
                            return@map Note(note, 0.25f)
                        }
                    }
                }
                null
            }.filterNotNull()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    @Composable
    fun Stave(
        staveConfig: StaveConfig,
        modifier: Modifier = Modifier,
        isLoading: Boolean
    ) {
        var alpha by remember { mutableStateOf(0.5f) }

        LaunchedEffect(isLoading) {
            if (isLoading) {
                alpha = 1f
                animate(
                    initialValue = 1f,
                    targetValue = 0.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    block = { value, _ -> alpha = value }
                )
            } else {
                alpha = 1f
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .padding(16.dp)
                .graphicsLayer(alpha = alpha)
        ) {
            StaveView(state = staveConfig)
        }
    }

    @Composable
    fun ButtonsSection(
        modifier: Modifier = Modifier,
        onRegenerateClick: () -> Unit,
        onExitClick: () -> Unit
    ) {
        Row(
            modifier = modifier.padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 20.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = onExitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = { Icon(Icons.Default.ArrowBack, null) },
                    text = { Text(text = stringResource(R.string.btn_exit_creation_fragment)) },
                )
                ExtendedFloatingActionButton(
                    onClick = onRegenerateClick,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = { Icon(Icons.Default.Refresh, null) },
                    text = { Text(text = stringResource(R.string.btn_remake_creation_fragment)) },
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    FloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 10.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { }
                    ) {
                        Icon(Icons.Default.Check, null)
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        onClick = {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                            } else {
                                mediaPlayer.start()
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null)
                    }
                }
                ExtendedFloatingActionButton(
                    onClick = { },

                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text(text = stringResource(R.string.btn_generate_creation_fragment)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun Screen(
        viewModel: CreationViewModel,
        onExitClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Stave(
                staveConfig = viewModel.staveConfig,
                isLoading = viewModel.screenState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(500.dp)
            )
            ButtonsSection(
                onRegenerateClick = { viewModel.updateStave() },
                onExitClick = onExitClick,
                modifier = Modifier
            )
        }
    }

    @Preview
    @Composable
    fun ScreenPrev() {
        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {
//            Screen(
//                modifier = Modifier.fillMaxSize()
//            )
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun ButtonsSectionPrev() {
        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {
            ButtonsSection(
                Modifier.fillMaxWidth(),
                {},
                {}
            )
        }
    }

    @Preview
    @Composable
    fun StavePrev() {
        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 1f),
                color = MaterialTheme.colorScheme.background
            ) {

                Stave(StaveConfig(), isLoading = true)
            }
        }
    }
}
