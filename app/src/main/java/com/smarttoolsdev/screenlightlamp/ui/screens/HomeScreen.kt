package com.smarttoolsdev.screenlightlamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.R
import com.smarttoolsdev.screenlightlamp.data.AppViewModel
import com.smarttoolsdev.screenlightlamp.ui.components.*
import com.smarttoolsdev.screenlightlamp.ui.theme.*
import kotlin.math.abs
import kotlin.math.floor
import androidx.compose.foundation.layout.Box

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToRelaxingSounds: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    val brightnessController = rememberBrightnessController()
    val colors = remember {
        listOf(
            Color.White, Red, Orange, Yellow, Green, DarkGreen,
            Cyan, Blue, Purple, Pink
        )
    }
    var colorPosition by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Sync brightness from ViewModel
    LaunchedEffect(viewModel.brightness.value) {
        brightnessController.setBrightness(viewModel.brightness.value)
    }

    // Snap colorPosition if color was externally changed
    LaunchedEffect(viewModel.selectedColor.value) {
        val index = colors.indexOf(viewModel.selectedColor.value)
        if (index >= 0) {
            colorPosition = index.toFloat()
        }
    }

    val lastIndex = colors.lastIndex
    val lowerIndex = floor(colorPosition).toInt().coerceIn(0, lastIndex)
    val upperIndex = (lowerIndex + 1).coerceAtMost(lastIndex)
    val fraction = (colorPosition - lowerIndex).coerceIn(0f, 1f)
    val interpolatedColor = if (lowerIndex == upperIndex) {
        colors[lowerIndex]
    } else {
        lerp(colors[lowerIndex], colors[upperIndex], fraction)
    }

    LaunchedEffect(interpolatedColor) {
        viewModel.updateColor(interpolatedColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .onSizeChanged { containerSize = it }
            .background(interpolatedColor)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val (dx, dy) = dragAmount
                        if (abs(dx) > abs(dy)) {
                            val dragFactor = 200f
                            colorPosition = (colorPosition - dx / dragFactor)
                                .coerceIn(0f, lastIndex.toFloat())
                        } else {
                            if (containerSize.height > 0) {
                                val fractionDragged = dy / containerSize.height
                                val newBrightness = (viewModel.brightness.value - fractionDragged)
                                    .coerceIn(0.01f, 1f)
                                viewModel.updateBrightness(newBrightness)
                            }
                        }
                    }
                )
            }
    ) {
        // Show brightness % if below 100%
        if (viewModel.brightness.value < 1f) {
            Text(
                text = "${(viewModel.brightness.value * 100).toInt()}%",
                color = if (interpolatedColor == Color.White) Color.Black else Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (viewModel.timerState.value.isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Timer: ${viewModel.timerState.value.totalMinutes} min",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(16.dp))
                    TextButton(onClick = { viewModel.stopTimer() }) {
                        Text(text = "Cancel", color = Color.Red)
                    }
                }
            }
        }
        BottomNavBar(
            onTimerClick = { showTimer = true },
            onColorClick = { showColorPicker = true },
            onRelaxClick = onNavigateToRelaxingSounds,
            onMoreClick = onNavigateToSettings,
            modifier = Modifier.align(Alignment.BottomCenter)
        )




        // Dialogs
        if (showColorPicker) {
            ColorPickerSheet(
                selectedColor = viewModel.selectedColor.value,
                onColorSelected = {
                    val idx = colors.indexOf(it)
                    if (idx >= 0) colorPosition = idx.toFloat()
                    viewModel.updateColor(it)
                },
                onDismiss = { showColorPicker = false }
            )
        }

    if (showTimer) {
        TimerPicker(
            timerState = viewModel.timerState.value,
            onTimerStart = { minutes -> viewModel.startTimer(minutes) },
            onTimerStop = { viewModel.stopTimer() },
            onDismiss = { showTimer = false }
        )
    }
    }
}

@Composable
private fun BottomNavBar(
    onTimerClick: () -> Unit,
    onColorClick: () -> Unit,
    onRelaxClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onTimerClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_timer),
                    contentDescription = "Timer",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onColorClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color),
                    contentDescription = "Color",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onRelaxClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_relax),
                    contentDescription = "Relaxing Sounds",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Timer", color = Color.White, style = MaterialTheme.typography.bodySmall)
            Text("Color", color = Color.White, style = MaterialTheme.typography.bodySmall)
            Text("Relax", color = Color.White, style = MaterialTheme.typography.bodySmall)
            Text("More",  color = Color.White, style = MaterialTheme.typography.bodySmall)
        }
    }
}
