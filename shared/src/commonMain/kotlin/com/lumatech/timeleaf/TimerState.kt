package com.lumatech.timeleaf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class TimeTileInfo(
    val title: String,
    val durationMinutes: Int,
    val description: String = "",
    val isCustom: Boolean = false,
    val iconContent: @Composable (Color) -> Unit
)

class TimerManager {
    var targetDuration by mutableStateOf(20.minutes)
    var remainingDuration by mutableStateOf(20.minutes)
    var isRunning by mutableStateOf(false)
    var isCountdownMode by mutableStateOf(true)

    fun setCountdown(duration: Duration) {
        targetDuration = duration
        remainingDuration = duration
        isCountdownMode = true
        isRunning = false
    }

    fun reset() {
        remainingDuration = if (isCountdownMode) targetDuration else Duration.ZERO
        isRunning = false
    }
}

val sharedTimerManager = TimerManager()
val sharedCustomTiles = mutableStateListOf<TimeTileInfo>()
