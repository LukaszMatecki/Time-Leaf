package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.TimeSource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_play_circle_outline_24
import timeleaf.shared.generated.resources.baseline_pause_circle_24
import timeleaf.shared.generated.resources.baseline_stop_circle_24

@Composable
fun TimerScreen() {
    val manager = sharedTimerManager
    var lastTickMark by remember { mutableStateOf(TimeSource.Monotonic.markNow()) }

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
                delay(16)
            }
        }
    }

    val progress = if (manager.isCountdownMode && manager.targetDuration > ZERO) {
        (manager.remainingDuration.inWholeMilliseconds.toFloat() / manager.targetDuration.inWholeMilliseconds.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(targetValue = progress)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val surfaceVariantColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
            val greenStart = Color(0xFF10B981)
            val greenEnd = Color(0xFF34D399)

            val gradientBrush = remember {
                Brush.linearGradient(
                    colors = listOf(greenStart, greenEnd)
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()

                drawCircle(
                    color = surfaceVariantColor,
                    radius = (size.minDimension - strokeWidth) / 2f,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Text(
                text = formatDuration(manager.remainingDuration),
                fontSize = 56.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_play_circle_outline_24),
                            contentDescription = "Start",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Rozpocznij Skupienie", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { manager.reset() },
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_stop_circle_24),
                                contentDescription = "Stop",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { manager.isRunning = !manager.isRunning },
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                contentColor = if (isRunning) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                painter = painterResource(if (isRunning) Res.drawable.baseline_pause_circle_24 else Res.drawable.baseline_play_circle_outline_24),
                                contentDescription = if (isRunning) "Pauza" else "Wznów",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isRunning) "Pauza" else "Wznów", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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