package com.lumatech.timeleaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
