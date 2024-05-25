package com.euphoiniateam.euphonia.ui.piano

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val whiteIndexes = listOf(0, 2, 4, 5, 7, 9, 11)
val blackIndexes = listOf(1, 3, -1, 6, 8, 10)

@Composable
fun Key(
    color: Color,
    modifier: Modifier = Modifier,
    onButtonDown: () -> Unit = { },
    onButtonUp: () -> Unit = { },
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
            .pointerInput(onButtonDown) {
                awaitPointerEventScope { }
                awaitEachGesture {
                    awaitFirstDown().also {
                        isPressed = true
                        onButtonDown()
                        it.consume()
                    }
                    waitForUpOrCancellation()?.consume()
                    isPressed = false
                    onButtonUp()
                }
            }
    ) {
    }
}

@Composable
fun PianoOctave(
    pianoConfig: PianoConfig,
    whiteWidth: Dp = pianoConfig.defaultWhiteWidth,
    blackWidth: Dp = pianoConfig.defaultBlackWidth,
    blackHeight: Float = pianoConfig.defaultBlackHeight,
    onKeyDown: ((pitch: Int) -> Unit),
    onKeyUp: ((pitch: Int) -> Unit),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier
        ) {
            repeat(7) {
                Key(
                    onButtonDown = { onKeyDown(whiteIndexes[it]) },
                    onButtonUp = { onKeyUp(whiteIndexes[it]) },
                    color = Color.White,
                    modifier = Modifier
                        .width(whiteWidth)
                        .fillMaxHeight(),
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
                        onButtonDown = { onKeyDown(blackIndexes[it]) },
                        onButtonUp = { onKeyUp(blackIndexes[it]) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PianoKeyboard(
    pianoConfig: PianoConfig,
    whiteWidth: Dp = pianoConfig.defaultWhiteWidth,
    blackWidth: Dp = pianoConfig.defaultWhiteWidth,
    blackHeight: Float = pianoConfig.defaultBlackHeight,
    onKeyDown: ((octave: Int, pitch: Int) -> Unit),
    onKeyUp: ((octave: Int, pitch: Int) -> Unit),
    modifier: Modifier = Modifier,
    octaveCount: Int = 3,
) {
    val scrollState = rememberScrollState()
    var sliderValue by remember { mutableStateOf(0f) }
    val sliderMaxValue = 100f

    LaunchedEffect(sliderValue) {
        val scrollTo: Int = (scrollState.maxValue / sliderMaxValue * sliderValue).toInt()
        scrollState.scrollTo(scrollTo)
    }

    Column(
        modifier = modifier
    ) {
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
            },
            valueRange = 0f..sliderMaxValue,
            thumb = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .fillMaxWidth(0.15f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .align(Alignment.CenterHorizontally)
                )
            },
            colors = SliderDefaults.colors(
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent

            ),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(0.15f)
                .padding(horizontal = 50.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.85f)
                .horizontalScroll(scrollState, false)

        ) {
            repeat(octaveCount) {
                PianoOctave(
                    PianoConfig(),
                    whiteWidth,
                    blackWidth,
                    blackHeight,
                    onKeyDown = { pitch: Int -> onKeyDown(it, pitch) },
                    onKeyUp = { pitch: Int -> onKeyUp(it, pitch) },
                    modifier = Modifier
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview
@Composable
private fun KeyPrev() {
    MaterialTheme {
        Key(color = Color.White, modifier = Modifier.fillMaxSize())
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun OctavePrev() {
    MaterialTheme {
        PianoKeyboard(
            pianoConfig = PianoConfig(),
            onKeyDown = { octave: Int, pitch: Int ->
                println("key down octave $octave pitch $pitch")
            },
            onKeyUp = { octave: Int, pitch: Int ->
                println("key up octave $octave pitch $pitch")
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

class PianoConfig(
    val defaultWhiteWidth: Dp = 100.dp,
    val defaultBlackWidth: Dp = 35.dp,
    val defaultBlackHeight: Float = 0.5f
)
