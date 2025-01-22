// TimerPicker.kt
package com.smarttoolsdev.screenlightlamp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.data.TimerOption
import com.smarttoolsdev.screenlightlamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPicker(
    onTimerSelected: (TimerOption) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        TimerOption(0, "Infinite", isInfinite = true),
        TimerOption(1, "30 Minutes", minutes = 30),
        TimerOption(2, "1 Hour", minutes = 60),
        TimerOption(3, "Custom", isCustom = true)
    )

    var selectedOption by remember { mutableStateOf<TimerOption?>(null) }

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
            Text(
                text = "Timer",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            options.forEach { option ->
                TimerOptionItem(
                    option = option,
                    isSelected = option == selectedOption,
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F2F2F)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL", color = Color.White)
                }

                Button(
                    onClick = {
                        selectedOption?.let { onTimerSelected(it) }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldYellow
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("SET", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TimerOptionItem(
    option: TimerOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) BackgroundDark else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) GoldYellow else Color.White.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = GoldYellow,
                    unselectedColor = Color.White.copy(alpha = 0.3f)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = option.title,
                color = if (isSelected) GoldYellow else Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}