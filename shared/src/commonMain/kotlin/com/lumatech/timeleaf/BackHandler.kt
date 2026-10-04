package com.lumatech.timeleaf

import androidx.compose.runtime.Composable

@Composable
expect fun LocalBackHandler(enabled: Boolean = true, onBack: () -> Unit)