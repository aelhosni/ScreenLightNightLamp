package com.smarttoolsdev.screenlightlamp.data

data class TimerState(
    val isRunning: Boolean = false,
    val duration: Long = 0,
    val remainingTime: Long = 0
)