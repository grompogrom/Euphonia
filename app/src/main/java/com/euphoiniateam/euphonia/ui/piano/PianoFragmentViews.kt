package com.euphoiniateam.euphonia.ui.piano

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OverviewButton(
    text: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.heightIn(28.dp, 30.dp),
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 20.sp
        )
    }
}

@Composable
fun OverviewButtonSection(
    recordState: PianoState,
    onRecordClick: () -> Unit,
    onStopRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (recordState) {
        PianoState.NO_RECORD -> {
            OverviewButton(
                text = "Record",
                onClick = onRecordClick
            )
        }

        PianoState.RECORDING -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
            ) {

                BlinkingCircle(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                OverviewButton(
                    text = "Stop",
                    onClick = onStopRecordClick
                )
            }
        }

        PianoState.AFTER_RECORD -> {}
    }
}

@Composable
fun PianoOverview(
    recordState: PianoState,
    onExitClick: () -> Unit,
    onRecordClick: () -> Unit,
    onStopRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.background(Color.Black)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OverviewButton(
            text = "Exit",
            onClick = onExitClick
        )
        OverviewButtonSection(
            recordState = recordState,
            onRecordClick = onRecordClick,
            onStopRecordClick = onStopRecordClick
        )
    }
}

@Composable
fun ButtonSection(
    onPlayClick: () -> Unit,
    onApplyClick: () -> Unit,
    onRemakeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ExtendedFloatingActionButton(
            onClick = onRemakeClick,
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            icon = { Icon(Icons.Default.Refresh, null) },
            text = { Text(text = "Remake") },
        )

        ExtendedFloatingActionButton(
            onClick = onPlayClick,
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            icon = { Icon(Icons.Outlined.PlayArrow, null) },
            text = { Text(text = "Play") },
        )

        ExtendedFloatingActionButton(
            onClick = onApplyClick,
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            icon = { Icon(Icons.Default.Done, null) },
            text = { Text(text = "Apply") },
        )
    }
}

@Composable
fun BlinkingCircle(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha: Float by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .background(Color.Red.copy(alpha = alpha))
    )
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun OverviewButtonPrev() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            OverviewButton(modifier = Modifier)
        }
    }
}

@Preview
@Composable
private fun OverviewButtonSectionPrev() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OverviewButtonSection(
                recordState = PianoState.RECORDING,
                {},
                {}
            )
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun PianoOverviewPrev() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PianoOverview(
                recordState = PianoState.RECORDING,
                {},
                {},
                {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun ButtonSectionPrev() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        ButtonSection(
            onPlayClick = { /*TODO*/ },
            onApplyClick = { /*TODO*/ },
            onRemakeClick = { /*TODO*/ }
        )
    }
}
