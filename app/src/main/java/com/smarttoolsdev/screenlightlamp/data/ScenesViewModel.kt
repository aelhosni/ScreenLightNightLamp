package com.smarttoolsdev.screenlightlamp.data

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ScenesViewModel(
    private val appViewModel: AppViewModel
) : ViewModel() {
    private val _currentScene = MutableStateFlow<Scene?>(null)
    val currentScene = _currentScene.asStateFlow()

    private var animationJob: Job? = null

    val predefinedScenes = listOf(
        Scene(
            id = "reading",
            name = "Reading Light",
            colors = listOf(Color(0xFFFFF4E5)),
            brightness = 0.7f
        ),
        Scene(
            id = "night",
            name = "Night Mode",
            colors = listOf(Color(0xFFFF6B6B)),
            brightness = 0.3f
        ),
        Scene(
            id = "warm",
            name = "Warm Light",
            colors = listOf(Color(0xFFFFE4B5)),
            brightness = 0.5f
        ),
        Scene(
            id = "rainbow",
            name = "Rainbow",
            colors = listOf(
                Color.Red,
                Color.Green,
                Color.Blue,
                Color.Yellow,
                Color.Magenta,
                Color.Cyan
            ),
            isAnimated = true
        ),
        Scene(
            id = "sunset",
            name = "Sunset",
            colors = listOf(
                Color(0xFFFF8C00),
                Color(0xFFFF6B6B),
                Color(0xFF4A90E2)
            ),
            isAnimated = true,
            transitionDuration = 5000
        )
    )

    fun activateScene(scene: Scene) {
        _currentScene.value = scene
        animationJob?.cancel()

        if (scene.isAnimated) {
            animationJob = viewModelScope.launch {
                var colorIndex = 0
                while (isActive) {
                    appViewModel.updateColor(scene.colors[colorIndex])
                    appViewModel.updateBrightness(scene.brightness)
                    delay(scene.transitionDuration)
                    colorIndex = (colorIndex + 1) % scene.colors.size
                }
            }
        } else {
            appViewModel.updateColor(scene.colors.first())
            appViewModel.updateBrightness(scene.brightness)
        }
    }

    fun stopScene() {
        animationJob?.cancel()
        _currentScene.value = null
    }

    override fun onCleared() {
        super.onCleared()
        animationJob?.cancel()
    }
}

class ScenesViewModelFactory(
    private val appViewModel: AppViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScenesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScenesViewModel(appViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}