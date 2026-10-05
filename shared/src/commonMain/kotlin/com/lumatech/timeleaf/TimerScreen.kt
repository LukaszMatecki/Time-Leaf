package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_notifications_off_24
import timeleaf.shared.generated.resources.baseline_pause_circle_24
import timeleaf.shared.generated.resources.baseline_play_circle_outline_24
import timeleaf.shared.generated.resources.baseline_refresh_24
import timeleaf.shared.generated.resources.baseline_settings_24
import timeleaf.shared.generated.resources.baseline_stop_circle_24
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Composable
@Preview
fun TimerScreen(onOpenSettings: () -> Unit = {}) {
    val manager = sharedTimerManager

    var customToastMessage by remember { mutableStateOf<String?>(null) }
    var showStopDialog by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(manager.isRunning) {
        if (manager.isRunning) {
            showControls = true
            delay(10000.milliseconds)
            if (manager.isRunning) {
                showControls = false
            }
        } else {
            showControls = true
        }
    }

    LaunchedEffect(manager.isRunning) {
        if (manager.isRunning) {
            var lastTickMark = TimeSource.Monotonic.markNow()
            while (isActive) {
                withFrameNanos {
                    val elapsed = lastTickMark.elapsedNow()
                    lastTickMark = TimeSource.Monotonic.markNow()

                    if (manager.isCountdownMode) {
                        val newRemaining = manager.remainingDuration - elapsed
                        if (newRemaining <= ZERO) {
                            val minutesSpent = manager.targetDuration.inWholeMinutes.toInt()
                            manager.reset()
                            UserStats.recordCompletedSession(minutesSpent)
                            customToastMessage = LocalizedStrings.timerCompletedToast
                        } else {
                            manager.remainingDuration = newRemaining
                        }
                    } else {
                        manager.remainingDuration += elapsed
                    }
                }
            }
        }
    }

    LaunchedEffect(customToastMessage) {
        if (customToastMessage != null) {
            delay(2500.milliseconds)
            customToastMessage = null
        }
    }

    val isRunning = manager.isRunning
    val hasProgress = manager.remainingDuration < manager.targetDuration && manager.remainingDuration > ZERO
    val isInitial = !isRunning && !hasProgress

    val controlsAlpha by animateFloatAsState(
        targetValue = if (!isInitial && showControls) 1f else 0f,
        animationSpec = tween(300),
        label = "ControlsAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 20.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_settings_24),
                        contentDescription = "Ustawienia",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            TimerClockView(manager = manager) {
                if (isInitial) {
                    manager.isRunning = true
                    UserStats.recordStartTimer()
                    customToastMessage = LocalizedStrings.timerStartedToast
                } else {
                    if (!showControls) {
                        showControls = true
                        manager.isRunning = false
                        customToastMessage = LocalizedStrings.timerPausedToast
                    } else {
                        manager.isRunning = !manager.isRunning
                        customToastMessage = if (manager.isRunning) LocalizedStrings.timerResumedToast else LocalizedStrings.timerPausedToast
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shadowElevation = 0.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_notifications_off_24),
                        contentDescription = LocalizedStrings.timerDndMode,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = LocalizedStrings.timerDndMode,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .alpha(if (isInitial) 0f else controlsAlpha)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        manager.isRunning = false
                        manager.remainingDuration = manager.targetDuration
                        manager.reset()
                        customToastMessage = LocalizedStrings.timerResetToast
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_refresh_24),
                            contentDescription = "Od nowa",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Surface(
                    onClick = {
                        if (!isInitial && showControls) {
                            manager.isRunning = !manager.isRunning
                            customToastMessage = if (manager.isRunning) LocalizedStrings.timerResumedToast else LocalizedStrings.timerPausedToast
                        }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isRunning) Res.drawable.baseline_pause_circle_24
                                else Res.drawable.baseline_play_circle_outline_24
                            ),
                            contentDescription = if (isRunning) "Wstrzymaj" else "Wznów",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Surface(
                    onClick = {
                        if (!isInitial && showControls) {
                            manager.isRunning = false
                            showStopDialog = true
                        }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_stop_circle_24),
                            contentDescription = "Zakończ",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        AnimatedVisibility(
            visible = customToastMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 175.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = customToastMessage ?: "",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 14.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (showStopDialog) {
            Dialog(onDismissRequest = { showStopDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.widthIn(max = 320.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LocalizedStrings.timerStopDialogTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = LocalizedStrings.timerStopDialogDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { showStopDialog = false },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text(
                                    text = LocalizedStrings.timerBtnCancel,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Button(
                                onClick = {
                                    manager.reset()
                                    showStopDialog = false
                                    customToastMessage = LocalizedStrings.timerResetToast
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text(
                                    text = LocalizedStrings.timerBtnStop,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerClockView(manager: TimerManager, onClick: () -> Unit) {
    val exactProgress = if (manager.isCountdownMode && manager.targetDuration > ZERO) {
        (manager.remainingDuration.inWholeMilliseconds.toFloat() / manager.targetDuration.inWholeMilliseconds.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = exactProgress,
        animationSpec = if (manager.isRunning) tween(0) else tween(400, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )

    val isRunning = manager.isRunning
    val hasProgress = manager.remainingDuration < manager.targetDuration && manager.remainingDuration > ZERO
    val isInitial = !isRunning && !hasProgress

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .padding(16.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

        val gradientBrush = remember(primaryColor, secondaryColor) {
            Brush.linearGradient(colors = listOf(primaryColor, secondaryColor))
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val diameter = radius * 2f
            val center = Offset(size.width / 2, size.height / 2)
            val arcTopLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(diameter, diameter)

            // Szara tło-ścieżka (trail) widoczna pod obrysem timera
            drawCircle(
                color = trackColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Aktywny, wypełniający się pasek postępu z gradientem
            if (animatedProgress > 0f) {
                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDuration(manager.remainingDuration),
                fontSize = 68.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = when {
                    isInitial -> LocalizedStrings.timerTapToStart
                    isRunning -> LocalizedStrings.timerFocusing
                    else -> LocalizedStrings.timerPausedLabel
                },
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "TimerActionText"
            ) { text ->
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        val hStr = hours.toString().padStart(2, '0')
        val mStr = minutes.toString().padStart(2, '0')
        val sStr = seconds.toString().padStart(2, '0')
        "$hStr:$mStr:$sStr"
    } else {
        val mStr = minutes.toString().padStart(2, '0')
        val sStr = seconds.toString().padStart(2, '0')
        "$mStr:$sStr"
    }
}