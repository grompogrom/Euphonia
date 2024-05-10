package com.euphoiniateam.euphonia.ui.creation.stave

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.euphoiniateam.euphonia.R

@Composable
fun Stave(
    staveConfig: StaveConfig,
    staveHandler: StaveHandler,
    modifier: Modifier = Modifier,
    isLoading: Boolean
) {
    var alpha by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            alpha = 1f
            animate(
                initialValue = 1f,
                targetValue = 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                block = { value, _ -> alpha = value }
            )
        } else {
            alpha = 1f
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(16.dp)
            .graphicsLayer(alpha = alpha)
    ) {
        StaveView(
            state = staveConfig,
            handler = remember { staveHandler }
        )
    }
}

@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onRegenerateClick: () -> Unit,
    onExitClick: () -> Unit,
    onPlayClick: () -> Unit,
    isPlaying: Boolean,
    onGenerateClick: () -> Unit
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
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                icon = { Icon(Icons.Default.ArrowBack, null) },
                text = { Text(text = stringResource(R.string.btn_exit_creation_fragment)) },
            )
            ExtendedFloatingActionButton(
                onClick = onRegenerateClick,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                icon = { Icon(Icons.Default.Refresh, null) },
                text = { Text(text = stringResource(R.string.btn_remake_creation_fragment)) },
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
                    onClick = { }
                ) {
                    Icon(Icons.Default.Done, null)
                }
                FloatingActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    onClick = { onPlayClick() }
                ) {
                    val icon = if (isPlaying) Icons.Outlined.Close else Icons.Outlined.PlayArrow
                    Icon(icon, null)
                }
            }
            ExtendedFloatingActionButton(
                onClick = onGenerateClick,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text(text = stringResource(R.string.btn_generate_creation_fragment)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
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
            {},
            {},
            {},
            false,
            {}
        )
    }
}

@Preview
@Composable
fun StavePrev() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 1f),
            color = MaterialTheme.colorScheme.background
        ) {

            Stave(StaveConfig(), StaveHandler(StaveConfig()), isLoading = true)
        }
    }
}