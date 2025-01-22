// SoundItem.kt
package com.smarttoolsdev.screenlightlamp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.R
import com.smarttoolsdev.screenlightlamp.ui.theme.GoldYellow

@Composable
fun SoundItem(
    title: String,
    icon: Int,
    isPremium: Boolean = false,
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF44394E).copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = title,
                color = Color.White
            )
        }

        IconButton(
            onClick = if (!isPremium) onPlayPause else { {} }
        ) {
            Icon(
                painter = painterResource(
                    id = when {
                        isPremium -> R.drawable.ic_download
                        isPlaying -> R.drawable.ic_pause
                        else -> R.drawable.ic_play
                    }
                ),
                contentDescription = when {
                    isPremium -> "Premium"
                    isPlaying -> "Pause"
                    else -> "Play"
                },
                tint = if (isPremium) GoldYellow else Color.White
            )
        }
    }
}