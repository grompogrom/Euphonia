package com.euphoiniateam.euphonia.ui.creation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.databinding.FragmentCreation2Binding

class CreationFragment : Fragment() {

    private lateinit var viewModel: CreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                            modifier = Modifier.fillMaxSize())
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(CreationViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
    }


    @Composable
    fun Stave(
        staveConfig: StaveConfig,
        modifier: Modifier = Modifier
    ) {
        Surface(
            color = Color(0xFF36343B),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .padding(16.dp)
        ) {
            StaveView(state = staveConfig)
        }
    }

    @Composable
    fun ButtonsSection(
        modifier: Modifier = Modifier,
        onRegenerateClick: ()-> Unit
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
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = { Icon(Icons.Default.ArrowBack, null) },
                    text = { Text(text = "Exit") },
                )
                ExtendedFloatingActionButton(
                    onClick = onRegenerateClick,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = { Icon(Icons.Default.Refresh, null) },
                    text = { Text(text = "Remake") },
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
                        onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Done, null)
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.PlayArrow, null)
                    }
                }
                ExtendedFloatingActionButton(
                    onClick = {  },

                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text(text = "Generate") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun Screen(
        viewModel: CreationViewModel ,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier=modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Stave(
                staveConfig = viewModel.staveConfig,
                modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(500.dp))
            ButtonsSection(
                onRegenerateClick = { viewModel.updateStave() },
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
                {}
            )
        }
    }

    @Preview
    @Composable
    fun StavePrev() {
        MaterialTheme {
            Surface(
                modifier=Modifier.fillMaxSize(),
                color= Color.Black
            ) {

                Stave( StaveConfig())
            }
        }
    }

}