package com.smarttoolsdev.screenlightlamp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private val _timerStarted = MutableSharedFlow<Boolean>()
    val timerStarted = _timerStarted.asSharedFlow()

    private var timerJob: Job? = null

    fun startTimer(minutes: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val duration = minutes * 60 * 1000
            _timerState.value = TimerState(true, duration, duration)
            _timerStarted.emit(true)

            while (_timerState.value.remainingTime > 0) {
                delay(1000)
                _timerState.update { it.copy(
                    remainingTime = it.remainingTime - 1000
                )}
            }
            _timerState.value = TimerState()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}