package com.lumatech.timeleaf

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_add_24
import timeleaf.shared.generated.resources.baseline_notifications_off_24
import timeleaf.shared.generated.resources.baseline_pause_circle_24
import timeleaf.shared.generated.resources.baseline_play_circle_outline_24
import timeleaf.shared.generated.resources.baseline_settings_24
import timeleaf.shared.generated.resources.baseline_stop_circle_24
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Composable
@Preview
fun TimerScreen(onOpenSettings: () -> Unit = {}) {
    val manager = sharedTimerManager
    var lastTickMark by remember { mutableStateOf(TimeSource.Monotonic.markNow()) }

    var currentTask by remember { mutableStateOf<String?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskInputText by remember { mutableStateOf("") }

    LaunchedEffect(manager.isRunning) {
        if (manager.isRunning) {
            lastTickMark = TimeSource.Monotonic.markNow()
            while (isActive && manager.isRunning) {
                val elapsed = lastTickMark.elapsedNow()
                lastTickMark = TimeSource.Monotonic.markNow()

                if (manager.isCountdownMode) {
                    val newRemaining = manager.remainingDuration - elapsed
                    if (newRemaining <= ZERO) {
                        manager.remainingDuration = ZERO
                        manager.isRunning = false
                    } else {
                        manager.remainingDuration = newRemaining
                    }
                } else {
                    manager.remainingDuration += elapsed
                }
                delay(16.milliseconds)
            }
        }
    }

    val progress = if (manager.isCountdownMode && manager.targetDuration > ZERO) {
        (manager.remainingDuration.inWholeMilliseconds.toFloat() / manager.targetDuration.inWholeMilliseconds.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progressAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- TOP BAR: TASK PILL CENTERED, SETTINGS ICON RIGHT (SAME ROW, NO GEAR BACKGROUND) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Invisible placeholder on left to keep pill centered
            Spacer(modifier = Modifier.size(36.dp))

            // Task Pill (Center)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        taskInputText = currentTask ?: ""
                        showTaskDialog = true
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (currentTask.isNullOrBlank()) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_add_24),
                            contentDescription = LocalizedStrings.timerAddTask,
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocalizedStrings.timerAddTask,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentTask!!,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Settings gear button (Right - NO BACKGROUND)
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_settings_24),
                    contentDescription = "Ustawienia",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.15f))

        // --- TIMER RING (COMPACT, SLEEK) ---
        Box(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .aspectRatio(1f)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary

            val gradientBrush = remember(primaryColor, secondaryColor) {
                Brush.linearGradient(colors = listOf(primaryColor, secondaryColor))
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 12.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f

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
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                if (animatedProgress > 0f) {
                    val angleInDegrees = -90f + (360f * animatedProgress)
                    val angleInRad = angleInDegrees * (PI / 180f)
                    val dotX = center.x + radius * cos(angleInRad).toFloat()
                    val dotY = center.y + radius * sin(angleInRad).toFloat()
                    drawCircle(
                        color = Color.White,
                        radius = strokeWidth / 2.6f,
                        center = Offset(dotX, dotY)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatDuration(manager.remainingDuration),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = LocalizedStrings.timerFocusTimeLabel,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- DND CHIP ---
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.alpha(0.7f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_notifications_off_24),
                    contentDescription = LocalizedStrings.timerDndActive,
                    modifier = Modifier.size(13.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = LocalizedStrings.timerDndActive,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.8f))

        // --- CONTROLS ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val isRunning = manager.isRunning
            val hasProgress = manager.remainingDuration < manager.targetDuration && manager.remainingDuration > ZERO

            AnimatedContent(
                targetState = !isRunning && !hasProgress,
                transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
                label = "TimerControls"
            ) { isInitial ->
                if (isInitial) {
                    Button(
                        onClick = { manager.isRunning = true },
                        modifier = Modifier.height(48.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_play_circle_outline_24),
                            contentDescription = LocalizedStrings.btnStart,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LocalizedStrings.btnStart, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(onClick = { manager.reset() }) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_stop_circle_24),
                                    contentDescription = LocalizedStrings.timerBtnStop,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { manager.isRunning = !manager.isRunning },
                            modifier = Modifier.height(48.dp).widthIn(min = 130.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                                contentColor = if (isRunning) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                painter = painterResource(if (isRunning) Res.drawable.baseline_pause_circle_24 else Res.drawable.baseline_play_circle_outline_24),
                                contentDescription = if (isRunning) LocalizedStrings.timerBtnPause else LocalizedStrings.timerBtnResume,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isRunning) LocalizedStrings.timerBtnPause else LocalizedStrings.timerBtnResume,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // --- MODERN COMPACT TASK DIALOG ---
    if (showTaskDialog) {
        Dialog(onDismissRequest = { showTaskDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalizedStrings.taskDialogTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = taskInputText,
                        onValueChange = { taskInputText = it },
                        label = { Text(LocalizedStrings.taskDialogInputLabel) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { showTaskDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(LocalizedStrings.btnCancel, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                currentTask = taskInputText.trim().ifBlank { null }
                                showTaskDialog = false
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Text(LocalizedStrings.btnAdd, fontWeight = FontWeight.Bold)
                        }
                    }
                }
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
