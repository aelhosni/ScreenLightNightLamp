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

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToRelaxingSounds: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showTimer by remember { mutableStateOf(false) }

    // Brightness controller to sync system brightness with the app
    val brightnessController = rememberBrightnessController()

    // Predefined “key” colors
    val colors = remember {
        listOf(
            Color.White, Red, Orange, Yellow, Green, DarkGreen,
            Cyan, Blue, Purple, Pink
        )
    }

    // We’ll treat colorPosition as a float from 0f..(colors.size - 1)
    // so we can smoothly interpolate between discrete palette colors.
    var colorPosition by remember { mutableStateOf(0f) }

    // Container size so we can compute drag distances as fractions
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Sync brightness from ViewModel once or whenever it changes externally
    LaunchedEffect(viewModel.brightness.value) {
        brightnessController.setBrightness(viewModel.brightness.value)
    }

    /**
     * Whenever ViewModel’s color changes externally (e.g. user picks from ColorPicker),
     * we snap [colorPosition] to whichever two colors in [colors] bracket that color.
     * If the color exactly matches an item in [colors], we set position to that index.
     */
    LaunchedEffect(viewModel.selectedColor.value) {
        val index = colors.indexOf(viewModel.selectedColor.value)
        if (index >= 0) {
            // If the color is exactly in the list, snap to that integer position.
            colorPosition = index.toFloat()
        } else {
            // Optional: If color isn't found, you could find the closest pair
            // and set colorPosition accordingly. For simplicity, we skip that.
        }
    }

    /**
     * For the background, we do a “real-time” interpolation (lerp) based on [colorPosition].
     * The integer part of [colorPosition] is the lower bound; the fraction is how far
     * we are between that color and the next color in the list.
     */
    val lastIndex = colors.lastIndex
    val lowerIndex = floor(colorPosition).toInt().coerceIn(0, lastIndex)
    val upperIndex = (lowerIndex + 1).coerceAtMost(lastIndex)
    val fraction = (colorPosition - lowerIndex).coerceIn(0f, 1f)
    val interpolatedColor = if (lowerIndex == upperIndex) {
        colors[lowerIndex]
    } else {
        // Blend between adjacent colors
        lerp(
            start = colors[lowerIndex],
            stop = colors[upperIndex],
            fraction = fraction
        )
    }

    // Keep the ViewModel color in sync with the interpolated color
    // so the rest of your UI sees the “gradual” color.
    // If you want to do it only onDragEnd, move this to onDragEnd.
    LaunchedEffect(interpolatedColor) {
        viewModel.updateColor(interpolatedColor)
    }

    // Top-level Box for background + pointer input
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .background(interpolatedColor)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        // No special logic needed if we do real-time updates.
                    },
                    onDragEnd = {
                        // Optionally snap to the nearest discrete color on drag end:
                        // val nearestIndex = (colorPosition + 0.5f).toInt().coerceIn(0, lastIndex)
                        // colorPosition = nearestIndex.toFloat()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        val (dx, dy) = dragAmount
                        // If horizontal drag is bigger, change the colorPosition
                        // If vertical drag is bigger, change brightness
                        if (abs(dx) > abs(dy)) {
                            // Adjust colorPosition gradually by dx
                            // The factor below determines how “fast” colors change as you swipe.
                            // A bigger divisor => slower transitions (user must drag more).
                            val dragFactor = 200f
                            colorPosition = (colorPosition - dx / dragFactor)
                                .coerceIn(0f, lastIndex.toFloat())
                        } else {
                            // Vertical drag => brightness
                            if (containerSize.height > 0) {
                                val currentBrightness = viewModel.brightness.value
                                val fractionDragged = dy / containerSize.height
                                val newBrightness = (currentBrightness - fractionDragged)
                                    .coerceIn(0.01f, 1f)
                                viewModel.updateBrightness(newBrightness)
                            }
                        }
                    }
                )
            }
    ) {
        // Show brightness % in center if below 100%
        if (viewModel.brightness.value < 1f) {
            Text(
                text = "${(viewModel.brightness.value * 100).toInt()}%",
                color = if (interpolatedColor == Color.White) Color.Black else Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom bar with 4 icons
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
                onColorSelected = { selected ->
                    // Snap colorPosition to that color’s exact index if it exists.
                    val idx = colors.indexOf(selected)
                    if (idx >= 0) colorPosition = idx.toFloat()

                    viewModel.updateColor(selected)
                },
                onDismiss = { showColorPicker = false }
            )
        }

        if (showTimer) {
            TimerPicker(
                onTimerSelected = { option ->
                    viewModel.updateTimerActive(true, option.minutes)
                },
                onDismiss = { showTimer = false }
            )
        }
    }
}

// Example extracted composable for bottom nav bar
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

        // Labels under icons (optional)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Timer",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Color",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Relax",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "More",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
