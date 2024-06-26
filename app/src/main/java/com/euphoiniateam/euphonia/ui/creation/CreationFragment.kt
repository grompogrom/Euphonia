package com.euphoiniateam.euphonia.ui.creation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.ui.MidiFile

class CreationFragment : Fragment() {

    private lateinit var viewModel: CreationViewModel
    private lateinit var uri: Uri
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, CreationViewModel.provideFactory(requireContext()))
            .get(CreationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_creation2, container, false)
        root.findViewById<ComposeView>(R.id.compose_view).apply {
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setStaveChosen()

        val userMidiFile = arguments?.getSerializable("midiFile", MidiFile::class.java)

        userMidiFile?.let {
            uri = Uri.parse(userMidiFile.uri)
            viewModel.setCurrentUri(requireContext(), uri)
            viewModel.getNotes(uri)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    companion object {
        const val URI_KEY = "midiFile"
    }

    @Composable
    fun Screen(
        viewModel: CreationViewModel,
        onExitClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        var songName by remember { mutableStateOf(TextFieldValue("")) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enter Song Name") },
                text = {
                    TextField(
                        value = songName,
                        onValueChange = { songName = it },
                        label = { Text("Song Name") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.saveGeneratedToStorage(
                                context.contentResolver,
                                uri,
                                "${songName.text}.mid"
                            )
                            Toast.makeText(
                                context,
                                "Saved as ${songName.text}",
                                Toast.LENGTH_LONG
                            ).show()
                            showDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (viewModel.getStaveChosen()) {
                Stave(
                    staveConfig = viewModel.staveConfig,
                    staveHandler = viewModel.staveHandler,
                    isLoading = viewModel.screenState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(500.dp)
                )
            } else {
                Synthesia(
                    synthesiaConfig = viewModel.synthesiaConfig,
                    synthesiaHandler = viewModel.synthesiaHandler,
                    isLoading = viewModel.screenState.isLoading,
                    modifier = Modifier
                        .requiredHeight(500.dp)
                )
            }
            ButtonsSection(
                onRegenerateClick = { viewModel.regenerateLastPart(context) },
                onExitClick = onExitClick,
                onPlayClick = { viewModel.togglePlayPause(context) },
                onSaveClick = {
                    showDialog = true
                },
                onShareClick = {
                    viewModel.shareFile(context)
                },
                onGenerateClick = { viewModel.generateNewPart(context, uri) },
                isPlaying = viewModel.screenState.isPlaying,
                modifier = Modifier

            )
        }
    }
}
