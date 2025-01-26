package com.smarttoolsdev.screenlightlamp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val hasTimer: Boolean = false
) {
    object Home : NavItem("home", "Light", Icons.Default.LightMode, true)
    object Timer : NavItem("timer", "Timer", Icons.Default.Timer)
    object Presets : NavItem("presets", "Scenes", Icons.Default.Palette)
    object Settings : NavItem("settings", "Settings", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Timer, Presets, Settings)
    }
}