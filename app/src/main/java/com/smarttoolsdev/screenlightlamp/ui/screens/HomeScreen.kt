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
import androidx.compose.ui.unit.IntSize
import com.smarttoolsdev.screenlightlamp.data.AppViewModel
import com.smarttoolsdev.screenlightlamp.ui.components.*
import com.smarttoolsdev.screenlightlamp.ui.theme.*
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
) {

    val brightnessController = rememberBrightnessController()
    val colors = remember {
        listOf(
            Color.White, Red, Orange, Yellow, Green, DarkGreen,
            Cyan, Blue, Purple, Pink
        )
    }
    var colorPosition by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(viewModel.brightness.value) {
        brightnessController.setBrightness(viewModel.brightness.value)
    }

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
        if (viewModel.brightness.value < 1f) {
            Text(
                text = "${(viewModel.brightness.value * 100).toInt()}%",
                color = if (interpolatedColor == Color.White) Color.Black else Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}