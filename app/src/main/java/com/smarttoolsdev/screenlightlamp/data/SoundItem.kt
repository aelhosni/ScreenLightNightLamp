// SoundItem.kt
package com.smarttoolsdev.screenlightlamp.data

data class SoundItem(
    val id: Int,
    val title: String,
    val icon: String,
    val isPremium: Boolean = false,
    val soundResource: Int? = null
)
