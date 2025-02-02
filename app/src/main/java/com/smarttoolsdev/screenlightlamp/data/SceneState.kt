// SceneState.kt
package com.smarttoolsdev.screenlightlamp.data

import androidx.compose.ui.graphics.Color

data class Scene(
    val id: String,
    val name: String,
    val colors: List<Color>,
    val transitionDuration: Long = 2000,
    val brightness: Float = 1f,
    val isAnimated: Boolean = false
)