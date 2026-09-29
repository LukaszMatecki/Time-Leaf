package com.lumatech.timeleaf

import androidx.compose.runtime.Composable

@Composable
actual fun LocalBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a system back button, no-op
}
