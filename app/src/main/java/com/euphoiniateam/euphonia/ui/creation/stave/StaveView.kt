package com.euphoiniateam.euphonia.ui.creation.stave

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.domain.models.Note

const val note4Duration = 0.55
const val note8Duration = 0.30
const val note16Duration = 0.10
@Composable
@SuppressLint("MagicNumber")
fun StaveView(
    state: StaveConfig,
    handler: StaveHandler,
    modifier: Modifier = Modifier
) {

    var deltaDraw by remember { mutableFloatStateOf(0f) }
    val whiteNotes = intArrayOf(2, 4, 5, 7, 9, 11)
    val blackNotes = intArrayOf(1, 3, 6, 8, 10)

    val note4 = painterResource(R.drawable.note_1_4)
    val note4C = painterResource(R.drawable.note_1_4_c)

    val note8 = painterResource(R.drawable.note_1_8)
    val note8C = painterResource(R.drawable.note_1_8_c)

    val note16 = painterResource(R.drawable.note_1_16)
    val note16C = painterResource(R.drawable.note_1_16_c)

    val sharp = painterResource(R.drawable.sharp)
    val scriptKey = painterResource(R.drawable.notekey)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    if (deltaDraw + delta in -425.0 * (handler.getLinesCount() - 2)..0.0) // change
                        deltaDraw += delta
                    delta
                }
            )
    ) {
        val lineHeight = 2.dp.toPx()
        val lineWidth = size.width - lineHeight
        val topMargin = 50.dp.toPx()
        val staveLinesDelta = 15.5.dp.toPx()
        val verticalOffset = 80.dp.toPx()

        val noteLinesHeight = staveLinesDelta * 4

        translate(0f, deltaDraw) {
            // draw lines for stave
            repeat(handler.getLinesCount()) { line ->
                val lineDelta = if (line != 0)
                    (verticalOffset + noteLinesHeight) * line else 0f
                repeat(5) {
                    drawLine(
                        color = Color.White,
                        strokeWidth = lineHeight,
                        start = Offset(
                            lineHeight,
                            topMargin + lineDelta + staveLinesDelta * it
                        ),
                        end = Offset(
                            lineWidth,
                            topMargin + lineDelta + staveLinesDelta * it
                        )
                    )
                }
                drawLine(
                    color = Color.White,
                    strokeWidth = lineHeight,
                    start = Offset(
                        lineWidth,
                        topMargin + line * (verticalOffset + noteLinesHeight) - lineHeight / 2
                    ),
                    end = Offset(
                        lineWidth,
                        topMargin + line * (verticalOffset + noteLinesHeight) +
                            noteLinesHeight + lineHeight / 2
                    )
                )
                drawLine(
                    color = Color.White,
                    strokeWidth = lineHeight,
                    start = Offset(
                        lineHeight,
                        topMargin + line * (verticalOffset + noteLinesHeight) - lineHeight / 2
                    ),
                    end = Offset(
                        lineHeight,
                        topMargin + line * (verticalOffset + noteLinesHeight) + noteLinesHeight +
                            lineHeight / 2
                    )
                )
            }

            // draw scriptKeys
            for (i in 0 until handler.getLinesCount()) {
                translate(
                    0.dp.toPx(),
                    topMargin / 2 +
                        (lineHeight * 1.dp.toPx()) +
                        i * (noteLinesHeight + verticalOffset)
                ) {

                    with(scriptKey) {
                        draw(
                            this.intrinsicSize
                        )
                    }
                }
            }

            // set deltas for notes
            val horizontalNoteDelta = lineWidth / (state.lineNotesCount + 1)
            val topNoteDelta = topMargin + staveLinesDelta * 2.5f

            // draw notes
            handler.visibleNotes.forEachIndexed { index, note ->
                val lineIndex = index.div(state.lineNotesCount)
                val noteIndex = index.mod(state.lineNotesCount)
                translate(
                    horizontalNoteDelta * (noteIndex + 1.2f),
                    topNoteDelta + (verticalOffset + noteLinesHeight) * lineIndex -
                        handler.visibleNotes[lineIndex * state.lineNotesCount + noteIndex].pitch
                        * staveLinesDelta / 2f
                ) {
                    // check durations and types of notes
                    if (note.duration >= note4Duration) {
                        if (note.note in whiteNotes) {
                            with(note4) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note in blackNotes) {
                            translate(
                                -16.dp.toPx(),
                                topMargin / 2 + lineHeight * 1.5f
                            ) {
                                with(sharp) {
                                    draw(
                                        this.intrinsicSize
                                    )
                                }
                            }

                            with(note4) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note == 0) {
                            with(note4C) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }
                    } else if (note.duration >= note8Duration) {
                        if (note.note in whiteNotes) {
                            with(note8) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note in blackNotes) {
                            translate(
                                -16.dp.toPx(),
                                topMargin / 2 + lineHeight * 1.5f
                            ) {
                                with(sharp) {
                                    draw(
                                        this.intrinsicSize
                                    )
                                }
                            }

                            with(note8) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note == 0) {
                            with(note8C) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }
                    } else if (note.duration >= note16Duration) {
                        if (note.note in whiteNotes) {
                            with(note16) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note in blackNotes) {
                            translate(
                                -16.dp.toPx(),
                                topMargin / 2 + lineHeight * 1.5f
                            ) {
                                with(sharp) {
                                    draw(
                                        this.intrinsicSize
                                    )
                                }
                            }

                            with(note16) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        } else if (note.note == 0) {
                            with(note16C) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StaveViewPrev() {
    MaterialTheme {
        StaveView(
            StaveConfig(),
            StaveHandler(StaveConfig(
                listOf(
                    Note(0, 0, 0.25f, 0.0f),
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
            )),
            modifier = Modifier.fillMaxSize()
        )
    }
}

class StaveConfig(
    val initialNotes: List<Note> = emptyList(),
    val lineNotesCount: Int = 7,
)
