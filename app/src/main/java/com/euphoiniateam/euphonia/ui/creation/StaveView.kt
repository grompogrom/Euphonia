package com.euphoiniateam.euphonia.ui.creation

import android.util.Log
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
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
    val noteVector = ImageVector.vectorResource(id = R.drawable.note)
    val notePainter = rememberVectorPainter(image = noteVector)
    val scripKeyVector = ImageVector.vectorResource(id = R.drawable.notekey)
    val scripKeyPainter = rememberVectorPainter(image = scripKeyVector)


    Canvas(modifier = modifier){
        val lineHeight = 4.dp.value
        val lineWidth = size.width - lineHeight
        val topMargin = 110.dp.value
        val staveLinesDelta = 40.dp.value
        val verticalOffset = 230.dp.value

        val noteLinesHeight = staveLinesDelta * 4


        state.setViewSize(size.width, size.height)
        repeat(state.linesCount){line ->
            val lineDelta = if (line !=0 && line != state.lineNotesCount-1) (verticalOffset + noteLinesHeight) * line else 0f
            repeat(5) {
                drawLine(
                    color = Color.White,
                    strokeWidth = lineHeight,
                    start = Offset(lineHeight,
                        topMargin + lineDelta + staveLinesDelta * it),
                    end = Offset(lineWidth,
                        topMargin +lineDelta + staveLinesDelta * it)
                )

            }
            drawLine(
                color = Color.White,
                strokeWidth = lineHeight,
                start = Offset(lineWidth,
                    topMargin  + line * (verticalOffset + noteLinesHeight) - lineHeight / 2   ),
                end = Offset(lineWidth,
                    topMargin + line * (verticalOffset + noteLinesHeight) + noteLinesHeight +  lineHeight / 2 )
            )
            drawLine(
                color = Color.White,
                strokeWidth = lineHeight,
                start = Offset(lineHeight,
                    topMargin  + line * (verticalOffset + noteLinesHeight) - lineHeight / 2   ),
                end = Offset(lineHeight,
                    topMargin + line * (verticalOffset + noteLinesHeight) + noteLinesHeight +  lineHeight / 2 )
            )
        }


        translate(
            10f,
            topMargin /2
        ) {

            with(scripKeyPainter) {
                draw(
                    this.intrinsicSize
                )
            }
        }

        val horizontalNoteDelta = lineWidth / (state.lineNotesCount + 1)
        val topNoteDelta = topMargin + staveLinesDelta * 2.5f

        state.visibleNotes.forEachIndexed { index, note ->
            val lineIndex = index.div(state.lineNotesCount)
            val noteIndex = index.mod(state.lineNotesCount)
            translate(
                horizontalNoteDelta * (noteIndex + 1),
                topNoteDelta + (verticalOffset + noteLinesHeight) * lineIndex
                    - state.visibleNotes[lineIndex * state.lineNotesCount + noteIndex].pitch * staveLinesDelta / 2f
            ) {
            with(notePainter) {
                draw(
                    this.intrinsicSize
                )
            }
        }
        }
    }
}

@Preview
@Composable
fun StaveViewPrev() {
    MaterialTheme{
        StaveView(
            StaveConfig(),
            modifier=Modifier.fillMaxSize()
        )
    }
}

class StaveConfig(
    initial_notes: List<Note> = emptyList(),
    val linesCount: Int = 3,
    var lineNotesCount: Int = 7,
){

    private var viewWidth = 0f
    private var viewHeight = 0f

    private val notes = mutableStateListOf<Note>()

    val visibleNotes by derivedStateOf {
        if (notes.isNotEmpty()) {
            notes.subList(
                0,
                min(lineNotesCount * linesCount, notes.size-1)
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

    fun updateNotes(newNotes: List<Note>){
        notes.clear()
        notes.addAll(newNotes)
    }
}
