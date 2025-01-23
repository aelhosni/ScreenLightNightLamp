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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smarttoolsdev.screenlightlamp.data.TimerState
import com.smarttoolsdev.screenlightlamp.ui.theme.*
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPicker(
    timerState: TimerState,
    onTimerStart: (Int) -> Unit,
    onTimerStop: () -> Unit,
    onDismiss: () -> Unit
) {
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
            when (timerState) {
                is TimerState.Active -> ActiveTimerView(
                    timerState = timerState,
                    onStop = onTimerStop
                )
                is TimerState.Paused -> {}  // Paused state not used in this implementation
                is TimerState.Inactive -> TimerSetup(
                    onTimeSelected = onTimerStart,
                    onCancel = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ActiveTimerView(
    timerState: TimerState.Active,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTime(timerState.remainingMillis),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { 1f - timerState.progress },
            modifier = Modifier.fillMaxWidth(),
            color = GoldYellow,
            trackColor = Color.White.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel Timer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerSetup(
    onTimeSelected: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Timer",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TimeInput(
            hours = selectedHour,
            minutes = selectedMinute,
            onHoursChange = { selectedHour = it.coerceIn(0, 4) },
            onMinutesChange = { selectedMinute = it.coerceIn(0, 59) },
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        QuickSelect(
            onTimeSelected = { minutes ->
                selectedHour = minutes / 60
                selectedMinute = minutes % 60
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val totalMinutes = (selectedHour * 60) + selectedMinute
                    if (totalMinutes > 0) {
                        onTimeSelected(totalMinutes)
                    }
                },
                enabled = selectedHour > 0 || selectedMinute > 0,
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                modifier = Modifier.weight(1f)
            ) {
                Text("Start", color = Color.Black)
            }
        }
    }
}

@Composable
private fun QuickSelect(onTimeSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(15, 30, 45, 60).forEach { minutes ->
            OutlinedButton(
                onClick = { onTimeSelected(minutes) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Text("${minutes}m")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeInput(
    hours: Int,
    minutes: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimePicker(
            state = TimePickerState(hours, minutes, is24Hour = true),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}