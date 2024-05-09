package com.euphoiniateam.euphonia.ui.creation.synthesia

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.domain.models.Note


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

        ) {
            Piano(state)
        }
    }

}

@Composable
fun Key(
    color: Color,
    modifier: Modifier = Modifier,
) {
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.Gray else color,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .background(
                backgroundColor,
                RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
            )

    ) {}
}

@Composable
fun PianoOctave(
    whiteWidth: Dp = 120.dp,
    blackWidth: Dp = 47.dp,
    whiteHeight: Float = 1f,
    blackHeight: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier
        ) {
            repeat(7) {
                Key(
                    color = Color.White,
                    modifier = Modifier
                        .width(whiteWidth)
                        .fillMaxHeight(whiteHeight),
                )
            }
        }
        Row(
            modifier = modifier.padding(horizontal = whiteWidth - blackWidth / 2),
            horizontalArrangement = Arrangement.spacedBy(whiteWidth - blackWidth)
        ) {
            repeat(6) {
                if (it != 2) {
                    Key(
                        color = Color.Black,
                        modifier = Modifier
                            .width(blackWidth)
                            .fillMaxHeight(blackHeight),
                    )
                } else
                    Spacer(modifier = Modifier.width(blackWidth))
            }
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
                whiteHeight = 0.15f,
                blackHeight = 0.1f,
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
