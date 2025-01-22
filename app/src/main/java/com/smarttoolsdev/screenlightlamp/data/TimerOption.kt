// TimerOption.kt
package com.smarttoolsdev.screenlightlamp.data

data class TimerOption(
    val id: Int,
    val title: String,
    val minutes: Int? = null,
    val isInfinite: Boolean = false,
    val isCustom: Boolean = false
)