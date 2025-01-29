package com.smarttoolsdev.screenlightlamp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.data.TimerViewModel
import com.smarttoolsdev.screenlightlamp.ui.components.TimePickerWheel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onSelectPreset: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.timerStarted.collectLatest { started ->
            if (started) {
                onNavigateBack()
            }
        }
    }

    val presets = listOf(5L, 10L, 30L, 60L)
    var customHours by remember { mutableStateOf(0) }
    var customMinutes by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Select Timer Duration",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(160.dp)
        ) {
            items(presets) { minutes ->
                ElevatedButton(
                    onClick = { onSelectPreset(minutes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$minutes min")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Or set a custom time",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        TimePickerWheel(
            hours = customHours,
            minutes = customMinutes,
            onHoursChanged = { customHours = it },
            onMinutesChanged = { customMinutes = it }
        )

        Spacer(Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                val totalMinutes = customHours * 60 + customMinutes
                onSelectPreset(totalMinutes.toLong())
            },
        ) {
            Text("Start Timer")
        }
    }
}
