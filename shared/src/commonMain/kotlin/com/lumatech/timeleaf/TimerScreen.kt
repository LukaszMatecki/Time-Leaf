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
import timeleaf.shared.generated.resources.baseline_settings_24
import timeleaf.shared.generated.resources.baseline_stop_circle_24
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.TimeSource

@Composable
@Preview
fun TimerScreen(onOpenSettings: () -> Unit = {}) {
    val manager = sharedTimerManager

    var customToastMessage by remember { mutableStateOf<String?>(null) }
    var showStopDialog by remember { mutableStateOf(false) }

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
                            manager.remainingDuration = ZERO
                            manager.isRunning = false
                            customToastMessage = "Sesja zakończona"
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
            delay(2500)
            customToastMessage = null
        }
    }

    val isRunning = manager.isRunning
    val hasProgress = manager.remainingDuration < manager.targetDuration && manager.remainingDuration > ZERO
    val isInitial = !isRunning && !hasProgress

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

            Spacer(modifier = Modifier.weight(1f))

            TimerClockView(manager = manager) {
                if (isInitial) {
                    manager.isRunning = true
                    customToastMessage = "Sesja rozpoczęta"
                } else {
                    manager.isRunning = !manager.isRunning
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.alpha(0.8f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_notifications_off_24),
                        contentDescription = "Tryb skupienia",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nie przeszkadzać",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .alpha(if (isInitial) 0f else 1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        if (!isInitial) {
                            manager.isRunning = !manager.isRunning
                            customToastMessage = if (manager.isRunning) "Sesja wznowiona" else "Sesja wstrzymana"
                        }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(56.dp)
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Surface(
                    onClick = {
                        if (!isInitial) {
                            manager.isRunning = false
                            showStopDialog = true
                        }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_stop_circle_24),
                            contentDescription = "Zakończ",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }

        AnimatedVisibility(
            visible = customToastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 16.dp)
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
                            text = "Zakończyć sesję?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Obecny postęp czasu zostanie zresetowany. Czy na pewno chcesz przerwać?",
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
                                    text = "Anuluj",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Button(
                                onClick = {
                                    manager.reset()
                                    showStopDialog = false
                                    customToastMessage = "Sesja zresetowana"
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text(
                                    text = "Zakończ",
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
            .fillMaxWidth(0.85f)
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary

        val gradientBrush = remember(primaryColor, secondaryColor) {
            Brush.linearGradient(colors = listOf(primaryColor, secondaryColor))
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val diameter = radius * 2f

            drawCircle(
                color = trackColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatDuration(manager.remainingDuration),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            AnimatedContent(
                targetState = when {
                    isInitial -> "Dotknij, aby rozpocząć"
                    isRunning -> "Dotknij, aby wstrzymać"
                    else -> "Dotknij, aby wznowić"
                },
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "TimerActionText",
                modifier = Modifier.offset(y = 45.dp)
            ) { text ->
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
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
