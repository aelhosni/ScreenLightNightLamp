package com.smarttoolsdev.screenlightlamp.data

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val timerModel = TimerModel()
    val timerState: State<TimerState> = timerModel.timerState

    // Brightness state
    private val _brightness = mutableStateOf(1f)
    val brightness: State<Float> = _brightness

    // Color state
    private val _selectedColor = mutableStateOf(Color.White)
    val selectedColor: State<Color> = _selectedColor

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

    fun startTimer(minutes: Int) {
        timerModel.startTimer(minutes)
    }

    fun stopTimer() {
        timerModel.stopTimer()
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
        timerModel.stopTimer()
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_SHOW_TUTORIAL = "show_tutorial"
    }
}