package com.euphoiniateam.euphonia.ui.creation.synthesia

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.domain.models.Note
import com.euphoiniateam.euphonia.ui.piano.PianoOctave


@Composable
@SuppressLint("MagicNumber")
fun SynthesiaView(
    state: SynthesiaConfig,
    handler: SynthesiaHandler,
    modifier: Modifier = Modifier
) {


    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {




    }
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxHeight(0.2f)

        ) {
            Piano(state)
        }
    }

}

@Composable
fun Piano(
    state: SynthesiaConfig,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier



    ) {
        repeat(state.octaveCount) {
            PianoOctave(
                whiteWidth = 20.dp,
                blackWidth = 15.dp,
                blackHeight = 0.6f,
                onKeyDown = { _ ->},
                onKeyUp = { _ ->},
                modifier = Modifier

            )
        }
    }
}


@Preview
@Composable
fun SynthesiaViewPrev() {
    MaterialTheme {
        SynthesiaView(
            SynthesiaConfig(
                listOf(
                    Note(5, 9, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(0, 0, 0.25f, 0.0f),
                    Note(0, 0, 0.0f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f),
                    Note(5, 8, 0.25f, 0.0f)
                )
            ),
            SynthesiaHandler(SynthesiaConfig()),
            modifier = Modifier.fillMaxSize()
        )
    }
}


class SynthesiaConfig(
    val initialNotes: List<Note> = emptyList(),
    val octaveCount: Int = 3,
    val colorForWhiteNotes: Color = Color.Green,
    val colorForBlackNotes: Color = Color.Blue
)
