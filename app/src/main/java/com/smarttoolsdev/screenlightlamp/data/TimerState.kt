package com.smarttoolsdev.screenlightlamp.data

data class TimerState(
    val isActive: Boolean = false,
    val totalMinutes: Int = 0,
    val remainingSeconds: Long = 0,
    val progress: Float = 0f
)