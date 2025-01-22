package com.smarttoolsdev.screenlightlamp.data

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    // Brightness state
    private val _brightness = mutableStateOf(1f)
    val brightness: State<Float> = _brightness

    // Color state
    private val _selectedColor = mutableStateOf(Color.White)
    val selectedColor: State<Color> = _selectedColor

    // Timer states
    private val _isTimerActive = mutableStateOf(false)
    val isTimerActive: State<Boolean> = _isTimerActive
    private var timerJob: Job? = null

    // Tutorial state
    private val _showTutorial = MutableStateFlow(false)
    val showTutorial = _showTutorial.asStateFlow()

    init {
        _showTutorial.value = getTutorialState()
    }

    fun updateBrightness(value: Float) {
        _brightness.value = value.coerceIn(0.01f, 1f)
    }

    fun updateColor(color: Color) {
        _selectedColor.value = color
    }

    fun updateTimerActive(active: Boolean, minutes: Int? = null) {
        _isTimerActive.value = active
        timerJob?.cancel()

        if (active && minutes != null) {
            timerJob = viewModelScope.launch {
                delay(minutes * 60 * 1000L)
                _isTimerActive.value = false
                // Reset brightness when timer completes
                _brightness.value = 0f
            }
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            saveTutorialState(false)
            _showTutorial.value = false
        }
    }

    fun resetTutorial() {
        viewModelScope.launch {
            saveTutorialState(true)
            _showTutorial.value = true
        }
    }

    private fun getTutorialState(): Boolean {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_TUTORIAL, true)
    }

    private fun saveTutorialState(show: Boolean) {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_TUTORIAL, show).apply()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_SHOW_TUTORIAL = "show_tutorial"
    }
}