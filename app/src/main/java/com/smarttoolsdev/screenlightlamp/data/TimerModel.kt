package com.smarttoolsdev.screenlightlamp.data

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.TimeUnit

class TimerModel {
    private val _timerState = mutableStateOf(TimerState())
    val timerState: State<TimerState> = _timerState

    private var countDownTimer: CountDownTimer? = null

    fun startTimer(minutes: Int) {
        stopTimer()

        val totalMillis = TimeUnit.MINUTES.toMillis(minutes.toLong())
        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val progress = 1f - (millisUntilFinished.toFloat() / totalMillis.toFloat())
                _timerState.value = TimerState(
                    isActive = true,
                    totalMinutes = minutes,
                    remainingSeconds = remainingSeconds,
                    progress = progress
                )
            }

            override fun onFinish() {
                _timerState.value = TimerState()
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        _timerState.value = TimerState()
    }
}