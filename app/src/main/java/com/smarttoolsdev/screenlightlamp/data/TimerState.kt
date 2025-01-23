package com.smarttoolsdev.screenlightlamp.data

sealed class TimerState {
    data object Inactive : TimerState()
    data class Active(
        val totalMillis: Long,
        val remainingMillis: Long,
        val progress: Float
    ) : TimerState()
    data class Paused(
        val totalMillis: Long,
        val remainingMillis: Long,
        val progress: Float
    ) : TimerState()
}