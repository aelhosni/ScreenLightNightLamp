package com.smarttoolsdev.screenlightlamp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ColorWheel(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val distance = (offset - center).getDistance()
                    if (distance <= radius) {
                        val angle = atan2(
                            offset.y - center.y,
                            offset.x - center.x
                        )
                        val hue = (Math.toDegrees(angle.toDouble()) + 360) % 360
                        val saturation = (distance / radius).coerceIn(0f, 1f)
                        val color = android.graphics.Color.HSVToColor(
                            floatArrayOf(
                                hue.toFloat(),
                                saturation,
                                1f
                            )
                        )
                        onColorSelected(Color(color))
                    }
                }
            }
    ) {
        center = Offset(size.width / 2, size.height / 2)
        radius = minOf(size.width, size.height) / 2

        for (angle in 0..360 step 1) {
            for (saturation in 0..100 step 1) {
                val color = android.graphics.Color.HSVToColor(
                    floatArrayOf(
                        angle.toFloat(),
                        saturation / 100f,
                        1f
                    )
                )
                val angleRad = Math.toRadians(angle.toDouble())
                val satRad = saturation / 100f * radius
                val x = center.x + cos(angleRad) * satRad
                val y = center.y + sin(angleRad) * satRad

                drawCircle(
                    color = Color(color),
                    radius = 2f,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }

        // Draw outer circle
        drawCircle(
            color = Color.White,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun ColorPicker(
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorWheel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onColorSelected = onColorSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset colors
        val presetColors = remember {
            listOf(
                Color.White,
                Color.Red,
                Color(0xFFFF8000), // Orange
                Color.Yellow,
                Color.Green,
                Color(0xFF008000), // Dark Green
                Color.Cyan,
                Color.Blue,
                Color(0xFF800080), // Purple
                Color(0xFFFF69B4)  // Pink
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(100.dp)
        ) {
            items(presetColors) { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onColorSelected(color) }
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.small
                        )
                        .background(color, MaterialTheme.shapes.small)
                )
            }
        }
    }
}