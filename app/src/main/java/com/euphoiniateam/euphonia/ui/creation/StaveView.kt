package com.euphoiniateam.euphonia.ui.creation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.domain.models.Note
import kotlin.math.min

@Composable
fun StaveView(
    state: StaveConfig,
    modifier: Modifier = Modifier
) {
    val whiteNotes = intArrayOf(2, 4, 5, 7, 9, 11)
    val blackNotes = intArrayOf(1, 3, 6, 8, 10)
    val note_1_4_duration = 0.55
    val note_1_8_duration = 0.30
    val note_1_16_duration = 0.10

//    val note_1_4_vector = ImageVector.vectorResource(id = R.drawable.note_1_4)
//    val note_1_4_painter = rememberVectorPainter(image = note_1_4_vector)
    val note_1_4 = painterResource(R.drawable.note_1_4)
    val note_1_4_c = painterResource(R.drawable.note_1_4_c)

    val note_1_8 = painterResource(R.drawable.note_1_8)
    val note_1_8_c = painterResource(R.drawable.note_1_8_c)

    val note_1_16 = painterResource(R.drawable.note_1_16)
    val note_1_16_c = painterResource(R.drawable.note_1_16_c)

    val diez = painterResource(R.drawable.diez)
    val scriptKey = painterResource(R.drawable.notekey)
//
//    val note_1_4_c_vector = ImageVector.vectorResource(id = R.drawable.note_1_4_c)
//    val note_1_4_c_painter = rememberVectorPainter(image = note_1_4_c_vector)
//
//    val note_1_8_vector = ImageVector.vectorResource(id = R.drawable.note_1_8)
//    val note_1_8_painter = rememberVectorPainter(image = note_1_8_vector)
//
//    val note_1_8_c_vector = ImageVector.vectorResource(id = R.drawable.note_1_8_c)
//    val note_1_8_c_painter = rememberVectorPainter(image = note_1_8_c_vector)
//
//    val note_1_16_vector = ImageVector.vectorResource(id = R.drawable.note_1_16)
//    val note_1_16_painter = rememberVectorPainter(image = note_1_16_vector)
//
//    val note_1_16_c_vector = ImageVector.vectorResource(id = R.drawable.note_1_16_c)
//    val note_1_16_c_painter = rememberVectorPainter(image = note_1_16_c_vector)
//
//    val diezVector = ImageVector.vectorResource(id = R.drawable.diez)
//    val diezPainter = rememberVectorPainter(image = diezVector)
//
//    val scripKeyVector = ImageVector.vectorResource(id = R.drawable.notekey)
//    val scripKeyPainter = rememberVectorPainter(image = scripKeyVector)

    Canvas(modifier = modifier) {
        val lineHeight = 4.dp.value
        val lineWidth = size.width - lineHeight
        val topMargin = 110.dp.value
        val staveLinesDelta = 40.dp.value
        val verticalOffset = 230.dp.value 

        val noteLinesHeight = staveLinesDelta * 4

        // draw lines for stave
        state.setViewSize(size.width, size.height)
        repeat(state.linesCount) { line ->
            val lineDelta = if (line != 0 && line != state.lineNotesCount - 1)
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
        for (i in 0..state.lineNotesCount) {
            translate(
                0f,
                topMargin / 2 - (2 * lineHeight) + i * (noteLinesHeight + verticalOffset)
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
        state.visibleNotes.forEachIndexed { index, note ->
            val lineIndex = index.div(state.lineNotesCount)
            val noteIndex = index.mod(state.lineNotesCount)
            translate(
                horizontalNoteDelta * (noteIndex + 1),
                topNoteDelta + (verticalOffset + noteLinesHeight) * lineIndex -
                    state.visibleNotes[lineIndex * state.lineNotesCount + noteIndex].pitch
                    * staveLinesDelta / 2f
            ) {
//              // check durations and types of notes
                if (note.duration >= note_1_4_duration) {
                    if (note.note in whiteNotes) {
                        with(note_1_4) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    } else if (note.note in blackNotes) {
                        translate(
                            -45f,
                            topMargin / 2 + lineHeight * 3
                        ) {
                            with(diez) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }

                        with(note_1_4) {
                            draw(
                                this.intrinsicSize
                            )
                        }

                    } else if (note.note == 0) {
                        with(note_1_4_c) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    }
                } else if (note.duration >= note_1_8_duration) {
                    if (note.note in whiteNotes) {
                        with(note_1_8) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    } else if (note.note in blackNotes) {
                        translate(
                            -45f,
                            topMargin / 2 + lineHeight * 3
                        ) {
                            with(diez) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }

                        with(note_1_8) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    } else if (note.note == 0) {
                        with(note_1_8_c) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    }
                } else if (note.duration >= note_1_16_duration) {
                    if (note.note in whiteNotes) {
                        with(note_1_16) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    } else if (note.note in blackNotes) {
                        translate(
                            -45f,
                            topMargin / 2 + lineHeight * 3
                        ) {
                            with(diez) {
                                draw(
                                    this.intrinsicSize
                                )
                            }
                        }

                        with(note_1_16) {
                            draw(
                                this.intrinsicSize
                            )
                        }
                    } else if (note.note == 0) {
                        with(note_1_16_c) {
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

@Preview
@Composable
fun StaveViewPrev() {
    MaterialTheme {
        StaveView(
            StaveConfig(
                listOf(Note(5, 9, 0.25f, 0.0f), Note(5, 8, 0.6f, 0.0f), Note(6, 2, 0.0f, 0.0f))
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

class StaveConfig(
    initial_notes: List<Note> = emptyList(),
    val linesCount: Int = 10,
    var lineNotesCount: Int = 7,
) {

    private var viewWidth = 0f
    private var viewHeight = 0f

    private val notes = mutableStateListOf<Note>()

    val visibleNotes by derivedStateOf {
        if (notes.isNotEmpty()) {
            notes.subList(
                0,
                min(lineNotesCount * linesCount, notes.size - 1)
            )
        } else {
            emptyList()
        }
    }
    init {
        updateNotes(initial_notes)
    }

    fun setViewSize(width: Float, height: Float) {
        viewWidth = width
        viewHeight = height
    }

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
    }
}
