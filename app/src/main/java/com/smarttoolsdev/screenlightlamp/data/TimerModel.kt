package com.smarttoolsdev.screenlightlamp.data

import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class TimerModel {
    private var countDownTimer: CountDownTimer? = null
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Inactive)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _timerFinished = MutableStateFlow(false)
    val timerFinished: StateFlow<Boolean> = _timerFinished.asStateFlow()

    fun startTimer(minutes: Int) {
        stopTimer()
        val totalMillis = TimeUnit.MINUTES.toMillis(minutes.toLong())
        startCountdown(totalMillis)
        _timerFinished.value = false
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        _timerState.value = TimerState.Inactive
        _timerFinished.value = false
    }

    private fun startCountdown(totalMillis: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(totalMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = 1f - (millisUntilFinished.toFloat() / totalMillis.toFloat())
                _timerState.value = TimerState.Active(
                    totalMillis = totalMillis,
                    remainingMillis = millisUntilFinished,
                    progress = progress
                )
            }

            override fun onFinish() {
                _timerState.value = TimerState.Inactive
                _timerFinished.value = true
            }
        }.start()
    }
}