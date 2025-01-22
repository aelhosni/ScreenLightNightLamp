// RelaxingSoundsScreen.kt
package com.smarttoolsdev.screenlightlamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.R
import com.smarttoolsdev.screenlightlamp.ui.components.SoundItem
import com.smarttoolsdev.screenlightlamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelaxingSoundsScreen(
    onBackClick: () -> Unit
) {
    var currentlyPlayingSound by remember { mutableStateOf<Int?>(null) }
    var volume by remember { mutableStateOf(0.7f) }

    val sounds = listOf(
        Triple(1, "Night in Forest", R.drawable.ic_forest),
        Triple(2, "Wave", R.drawable.ic_wave),
        Triple(3, "Rain & Thunder", R.drawable.ic_rain_thunder),
        Triple(4, "Rain", R.drawable.ic_rain),
        Triple(5, "Lullaby 1", R.drawable.ic_lullaby),
        Triple(6, "Lullaby 2", R.drawable.ic_lullaby),
        Triple(7, "Lullaby 3", R.drawable.ic_lullaby)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relaxing Sounds", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            sounds.forEach { (id, title, icon) ->
                SoundItem(
                    title = title,
                    icon = icon,
                    isPremium = id > 5,
                    isPlaying = currentlyPlayingSound == id,
                    onPlayPause = {
                        currentlyPlayingSound = if (currentlyPlayingSound == id) null else id
                    }
                )
            }

            if (currentlyPlayingSound != null) {
                Spacer(modifier = Modifier.height(16.dp))
                VolumeSlider(
                    value = volume,
                    onValueChange = { volume = it }
                )
            }
        }
    }
}

@Composable
private fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Volume",
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = GoldYellow,
                activeTrackColor = GoldYellow,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}