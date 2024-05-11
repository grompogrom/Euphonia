package com.euphoiniateam.euphonia.ui.creation.synthesia

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.euphoiniateam.euphonia.domain.models.Note

const val note4Duration = 0.55
const val note8Duration = 0.30
const val note16Duration = 0.10

const val note4Width = 300f
const val note8Width = 150f
const val note16Width = 75f

@Composable
@SuppressLint("MagicNumber")
fun SynthesiaView(
    state: SynthesiaConfig,
    handler: SynthesiaHandler,
    modifier: Modifier = Modifier
) {

    val whiteNotes = intArrayOf(0, 2, 4, 5, 7, 9, 11)
    val blackNotes = intArrayOf(1, 3, 6, 8, 10)
    Row {
        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {
            var deltaDraw by remember { mutableFloatStateOf(0f) }
            val colorForWhiteNotes = MaterialTheme.colorScheme.onPrimaryContainer
            val colorForBlackNotes = MaterialTheme.colorScheme.onPrimary
            val beginningPoint = 0f
            var scrollDistance = 0f

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .zIndex(100f)

            ) {
                Piano(state)
            }
            Canvas(
                modifier = modifier
                    .fillMaxSize()
                    .scrollable(
                        orientation = Orientation.Horizontal,
                        state = rememberScrollableState { delta ->
                            Log.d("delta", deltaDraw.toString())
                            Log.d("aaa", (beginningPoint - scrollDistance).toString())
                            if (deltaDraw + delta in (beginningPoint - scrollDistance)..0f)
                                deltaDraw += delta
                            delta
                        }
                    )

            ) {
                translate(deltaDraw, 0f) {
                    var prevNotesDuration = 0f
                    handler.visibleNotes.forEachIndexed { index, note ->

                        var durationWidth = 0f

                        if (note.duration >= note4Duration)
                            durationWidth = note4Width
                        else if (note.duration >= note8Duration)
                            durationWidth = note8Width
                        else if (note.duration >= note16Duration)
                            durationWidth = note16Width

                        if (note.note in whiteNotes) {
                            translate(
                                beginningPoint + prevNotesDuration,
                                state.whiteHeight.toPx() * note.pitch
                            ) {
                                drawRect(
                                    color = colorForWhiteNotes,
                                    size = Size(durationWidth, state.whiteHeight.toPx())
                                )
                            }
                            prevNotesDuration += durationWidth
                        } else if (note.note in blackNotes) {
                            translate(
                                beginningPoint + prevNotesDuration,
                                (
                                    state.whiteHeight.toPx() *
                                        note.pitch - (state.blackHeight / 2).toPx()
                                    ) + 0.8.dp.toPx()
                            ) {
                                drawRect(
                                    color = colorForBlackNotes,
                                    size = Size(durationWidth, state.blackHeight.toPx())
                                )
                            }
                            prevNotesDuration += durationWidth
                        }
                        scrollDistance = prevNotesDuration
                    }
                }
            }
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
            .background(
                backgroundColor,
                RoundedCornerShape(bottomStart = 5.dp, topStart = 5.dp)
            )

    ) {}
}

@Composable
fun PianoOctave(
    whiteHeight: Dp = 120.dp,
    blackHeight: Dp = 47.dp,
    whiteWidth: Float = 1f,
    blackWidth: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = modifier
        ) {
            repeat(7) {
                Key(
                    color = Color.White,
                    modifier = Modifier
                        .height(whiteHeight)
                        .padding(vertical = 0.5.dp)
                        .fillMaxWidth(whiteWidth),
                )
            }
        }
        Column(
            modifier = modifier
                .padding(vertical = whiteHeight + 0.5.dp - blackHeight / 2)
                .align(Alignment.BottomEnd),

            verticalArrangement = Arrangement.spacedBy(whiteHeight - blackHeight)

        ) {
            repeat(6) {
                if (it != 2) {
                    Key(
                        color = Color.Black,
                        modifier = Modifier
                            .height(blackHeight)
                            .fillMaxWidth(blackWidth),
                    )
                } else
                    Spacer(modifier = Modifier.height(blackHeight))
            }
        }
    }
}

@Composable
fun Piano(
    state: SynthesiaConfig,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier

    ) {
        repeat(state.octaveCount) {
            PianoOctave(
                whiteHeight = state.whiteHeight,
                blackHeight = state.blackHeight,
                whiteWidth = state.whiteWidth,
                blackWidth = state.blackWidth,
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
            SynthesiaConfig(),
            SynthesiaHandler(
                SynthesiaConfig(
                    listOf(
                        Note(1, 1, 0.2f, 0.0f),
                        Note(0, 0, 0.3f, 0.0f),
                        Note(1, 2, 0.45f, 0.0f),
                        Note(2, 3, 0.2f, 0.0f),
                        Note(2, 4, 0.24f, 0.0f),
                        Note(3, 5, 0.1f, 0.0f),
                        Note(4, 6, 0.05f, 0.0f),
                        Note(4, 7, 0.60f, 0.0f),
                        Note(5, 8, 0.80f, 0.0f),
                        Note(5, 9, 0.67f, 0.0f)
                    )
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

class SynthesiaConfig(
    val initialNotes: List<Note> = emptyList(),
    val octaveCount: Int = 3,
    val whiteHeight: Dp = 500.dp / (octaveCount * 7) - 2.dp,
    val blackHeight: Dp = 10.dp,
    val whiteWidth: Float = 0.2f,
    val blackWidth: Float = 0.1f,
)
