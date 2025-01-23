package com.smarttoolsdev.screenlightlamp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smarttoolsdev.screenlightlamp.data.TimerState
import com.smarttoolsdev.screenlightlamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPicker(
    timerState: TimerState,
    onTimerStart: (Int) -> Unit,
    onTimerStop: () -> Unit,
    onDismiss: () -> Unit
) {
    val presetTimes = listOf(5, 10, 15, 30, 45, 60, 90, 120)
    var showCustomDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        dragHandle = { Box(modifier = Modifier.height(4.dp)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (timerState.isActive) "Timer Active" else "Set Timer",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                if (timerState.isActive) {
                    TimerDisplay(timerState)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (timerState.isActive) {
                TimerProgressIndicator(timerState)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onTimerStop,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Stop Timer")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetTimes) { minutes ->
                        TimerPresetButton(
                            minutes = minutes,
                            isSelected = selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes }
                        )
                    }

                    item {
                        TimerPresetButton(
                            text = "Custom",
                            isSelected = false,
                            onClick = { showCustomDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            selectedMinutes?.let {
                                onTimerStart(it)
                                onDismiss()
                            }
                        },
                        enabled = selectedMinutes != null,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start", color = Color.Black)
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomTimerDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { minutes ->
                selectedMinutes = minutes
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun TimerDisplay(timerState: TimerState) {
    val minutes = timerState.remainingSeconds / 60
    val seconds = timerState.remainingSeconds % 60

    Text(
        text = String.format("%02d:%02d", minutes, seconds),
        style = MaterialTheme.typography.headlineMedium,
        color = GoldYellow
    )
}

@Composable
private fun TimerProgressIndicator(timerState: TimerState) {
    LinearProgressIndicator(
        progress = timerState.progress,
        color = GoldYellow,
        trackColor = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TimerPresetButton(
    minutes: Int? = null,
    text: String = "${minutes}m",
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) GoldYellow.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) GoldYellow else Color.White.copy(alpha = 0.3f)
        ),
        modifier = Modifier.height(80.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = text,
                color = if (isSelected) GoldYellow else Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomTimerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var minutes by remember { mutableStateOf(15) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .width(280.dp)
            ) {
                Text(
                    text = "Custom Timer",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberStepper(
                        value = minutes,
                        onValueChange = { minutes = it.coerceIn(1, 180) },
                        modifier = Modifier.width(120.dp)
                    )

                    Text(
                        text = "minutes",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(minutes) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow)
                    ) {
                        Text("Set", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = value > 1
        ) {
            Text("-", color = Color.White)
        }

        Text(
            text = "$value",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = value < 180
        ) {
            Text("+", color = Color.White)
        }
    }
}