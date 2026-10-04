package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_check_24
import timeleaf.shared.generated.resources.baseline_edit_24
import timeleaf.shared.generated.resources.baseline_emoji_events_24
import timeleaf.shared.generated.resources.baseline_fire_24
import timeleaf.shared.generated.resources.baseline_history_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_settings_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_timer_24
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

data class TaskItemModel(
    val id: Int,
    val title: String,
    var isCompleted: Boolean = false
)

@Composable
@Preview
fun ProfileScreen(onOpenSettings: () -> Unit = {}) {
    var showAchievementsScreen by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = showAchievementsScreen,
        transitionSpec = {
            if (targetState) {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "ProfileNav"
    ) { inAchievements ->
        if (inAchievements) {
            AchievementsScreen(onBackClick = { showAchievementsScreen = false })
        } else {
            ProfileMainView(
                onOpenAchievements = { showAchievementsScreen = true },
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun ProfileMainView(
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var dialogTitle by remember { mutableStateOf<String?>(null) }
    var dialogDesc by remember { mutableStateOf<String?>(null) }

    val focusMinutes = UserStats.totalFocusMinutes

    val tasks = remember {
        mutableStateListOf(
            TaskItemModel(1, "Analiza Raportu Kwartalnego"),
            TaskItemModel(2, "Planowanie Projektu"),
            TaskItemModel(3, "Spotkanie Zespołu")
        )
    }

    var isAddingTask by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var nextTaskId by remember { mutableStateOf(4) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Text(
                            text = "Witaj w profilu!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Surface(
                            onClick = {
                                dialogTitle = LocalizedStrings.profileLevelInfoTitle
                                dialogDesc = LocalizedStrings.profileLevelInfoDesc
                            },
                            shape = RoundedCornerShape(26.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            shadowElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_person_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Poziom Ekspert",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Lvl 4",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(8.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(0.65f)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    DesignerChartCard(
                        score = UserStats.focusScore.toString(),
                        onClick = {
                            dialogTitle = LocalizedStrings.profileChartTitle
                            dialogDesc = LocalizedStrings.profileChartDesc
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val cardModifier = Modifier.width(115.dp).aspectRatio(1f)

                        FocusScoreStatCard(
                            score = UserStats.focusScore,
                            trend = "+5%",
                            modifier = cardModifier,
                            onClick = {
                                dialogTitle = LocalizedStrings.profileScoreTitle
                                dialogDesc = "${LocalizedStrings.profileScoreDesc}${UserStats.focusScore}/100"
                            }
                        )

                        StreakStatCard(
                            streak = UserStats.streakDays,
                            modifier = cardModifier,
                            onClick = {
                                dialogTitle = LocalizedStrings.profileStreakTitle
                                dialogDesc = "${LocalizedStrings.profileStreakDesc}${UserStats.streakDays} dni."
                            }
                        )

                        TimeStatCard(
                            minutes = focusMinutes,
                            modifier = cardModifier,
                            onClick = {
                                dialogTitle = LocalizedStrings.profileTimeTitle
                                dialogDesc = "Twój łączny czas skupienia to $focusMinutes minut."
                            }
                        )
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        shadowElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Zadania",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            tasks.forEach { task ->
                                key(task.id) {
                                    AnimatedTaskItem(
                                        task = task,
                                        onComplete = {
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks[index] = task.copy(isCompleted = true)
                                            }
                                        },
                                        onRemove = {
                                            tasks.remove(task)
                                        }
                                    )
                                }
                            }

                            if (tasks.isEmpty() && !isAddingTask) {
                                Text(
                                    text = "Brak zadań na dziś. Odpoczywaj!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            AnimatedVisibility(visible = isAddingTask) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = newTaskTitle,
                                        onValueChange = { newTaskTitle = it },
                                        placeholder = { Text("Wpisz zadanie...", style = MaterialTheme.typography.bodyMedium) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (newTaskTitle.isNotBlank()) {
                                                tasks.add(TaskItemModel(nextTaskId++, newTaskTitle.trim()))
                                                newTaskTitle = ""
                                                isAddingTask = false
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_check_24),
                                            contentDescription = "Zapisz",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = !isAddingTask) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isAddingTask = true }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_edit_24),
                                        contentDescription = "Dodaj zadanie",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Dodaj nowe zadanie...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Skróty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 2.dp, top = 4.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SmallIconTile(
                            iconRes = Res.drawable.baseline_emoji_events_24,
                            iconTint = Color(0xFFF59E0B),
                            containerColor = MaterialTheme.colorScheme.surface,
                            onClick = onOpenAchievements,
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        SmallIconTile(
                            iconRes = Res.drawable.baseline_history_24,
                            iconTint = Color(0xFF3B82F6),
                            containerColor = MaterialTheme.colorScheme.surface,
                            onClick = {
                                dialogTitle = LocalizedStrings.profileSessionsTitle
                                dialogDesc = "${LocalizedStrings.profileSessionsDesc}${UserStats.completedSessions}."
                            },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        SmallIconTile(
                            iconRes = Res.drawable.baseline_settings_24,
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = MaterialTheme.colorScheme.surface,
                            onClick = onOpenSettings,
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        SmallIconTile(
                            iconRes = Res.drawable.baseline_settings_24, // Placeholder
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = MaterialTheme.colorScheme.surface,
                            onClick = { /* Placeholder action */ },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                    }
                }
            }
        }

        if (dialogTitle != null && dialogDesc != null) {
            Dialog(onDismissRequest = { dialogTitle = null; dialogDesc = null }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.widthIn(max = 320.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dialogTitle ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = dialogDesc ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { dialogTitle = null; dialogDesc = null },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = LocalizedStrings.btnClose,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlipContainer(
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(500),
        label = "flipAnimation"
    )

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { flipped = !flipped }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                back()
            }
        }
    }
}

@Composable
fun FocusScoreStatCard(
    score: Int,
    trend: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF3E8FF),
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val dotRadius = 1.5.dp.toPx()
                val spacing = 12.dp.toPx()
                for (x in 0..(size.width.toInt()) step spacing.toInt()) {
                    val waveY = sin(x.toFloat() * 0.05f) * 15f
                    for (y in 0..(size.height.toInt()) step spacing.toInt()) {
                        drawCircle(
                            color = Color(0xFFC4B5FD).copy(alpha = 0.5f),
                            radius = dotRadius,
                            center = Offset(x.toFloat(), y.toFloat() + waveY)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier.size(42.dp),
                            color = Color(0xFF9333EA),
                            trackColor = Color(0xFFD8B4FE),
                            strokeWidth = 3.dp,
                            strokeCap = StrokeCap.Round
                        )

                        FlipContainer(
                            front = {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_bolt_24),
                                    contentDescription = null,
                                    tint = Color(0xFF9333EA),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            back = {
                                Text(
                                    text = trend,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9333EA),
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }

                Column {
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "Skupienie",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9333EA)
                    )
                }
            }
        }
    }
}

@Composable
fun StreakStatCard(
    streak: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFF7ED),
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path1 = Path().apply {
                    moveTo(size.width * 0.4f, 0f)
                    lineTo(size.width, size.height * 0.6f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path1, color = Color(0xFFFDBA74).copy(alpha = 0.2f))

                val path2 = Path().apply {
                    moveTo(0f, size.height * 0.4f)
                    lineTo(size.width * 0.8f, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path2, color = Color(0xFFFDBA74).copy(alpha = 0.25f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(42.dp)) {
                            drawArc(
                                color = Color(0xFFFDBA74).copy(alpha = 0.4f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        FlipContainer(
                            front = {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_fire_24),
                                    contentDescription = "Fire Streak",
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            back = {
                                Text(
                                    text = "+1",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEA580C),
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                Column {
                    Text(
                        text = streak.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "Dni",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEA580C)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeStatCard(
    minutes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFECFDF5),
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for(i in 1..4) {
                    drawCircle(
                        color = Color(0xFF34D399).copy(alpha = 0.1f * (5 - i)),
                        radius = size.width * 0.25f * i,
                        center = Offset(size.width, 0f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(42.dp)) {
                            drawArc(
                                color = Color(0xFFA7F3D0),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx())
                            )
                            drawArc(
                                color = Color(0xFF10B981),
                                startAngle = -90f,
                                sweepAngle = 210f, // Przykładowy postęp
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        FlipContainer(
                            front = {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_timer_24),
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            back = {
                                Text(
                                    text = "+15m",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669),
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = minutes.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = " m",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    Text(
                        text = "Czas",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskItem(
    task: TaskItemModel,
    onComplete: () -> Unit,
    onRemove: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(task.isCompleted) {
        if (task.isCompleted) {
            delay(500.milliseconds)
            isVisible = false
            delay(300.milliseconds)
            onRemove()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(animationSpec = tween(300))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !task.isCompleted) { onComplete() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) Color(0xFF9333EA) else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (task.isCompleted) Color(0xFF9333EA) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_check_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            val textProgress by animateFloatAsState(targetValue = if (task.isCompleted) 1f else 0f)

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (textProgress > 0.5f) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
}

@Composable
fun SmallIconTile(
    iconRes: DrawableResource,
    iconTint: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(36.dp) // Powiększone ikony skrótów
            )
        }
    }
}

@Composable
fun DesignerChartCard(score: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Statystyki",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = score,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 28.sp
                        )
                        Text(
                            text = " /100 pkt.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "+5% od wczoraj",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val lineColor = MaterialTheme.colorScheme.primary
            val gradientColors = listOf(
                lineColor.copy(alpha = 0.2f),
                Color.Transparent
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                val values = UserStats.weeklyActivity
                val width = size.width
                val height = size.height
                val stepX = width / (values.size - 1)

                val path = Path()
                path.moveTo(0f, height - (values[0] * height))

                for (i in 0 until values.size - 1) {
                    val x1 = i * stepX
                    val y1 = height - (values[i] * height)
                    val x2 = (i + 1) * stepX
                    val y2 = height - (values[i + 1] * height)

                    val cx = (x1 + x2) / 2f
                    path.cubicTo(cx, y1, cx, y2, x2, y2)
                }

                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = 0f,
                        endY = height
                    )
                )

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                values.forEachIndexed { i, value ->
                    val x = i * stepX
                    val y = height - (value * height)
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 2.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd")
                days.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsScreen(onBackClick: () -> Unit) {
    val achievements = listOf(
        Triple(LocalizedStrings.ach1Title, LocalizedStrings.ach1Desc, UserStats.unlockedAchievements.contains("first_step")),
        Triple(LocalizedStrings.ach2Title, LocalizedStrings.ach2Desc, UserStats.unlockedAchievements.contains("session_creator")),
        Triple(LocalizedStrings.ach3Title, LocalizedStrings.ach3Desc, UserStats.unlockedAchievements.contains("explorer")),
        Triple(LocalizedStrings.ach4Title, LocalizedStrings.ach4Desc, UserStats.unlockedAchievements.contains("marathoner")),
        Triple(LocalizedStrings.ach5Title, LocalizedStrings.ach5Desc, UserStats.unlockedAchievements.contains("focus_master")),
        Triple(LocalizedStrings.ach6Title, LocalizedStrings.ach6Desc, UserStats.unlockedAchievements.contains("night_owl")),
        Triple(LocalizedStrings.ach7Title, LocalizedStrings.ach7Desc, UserStats.unlockedAchievements.contains("flow_master"))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Cofnij",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp).offset(x = (-2).dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Osiągnięcia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Zdobywaj nagrody za swoją produktywność",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(achievements) { (title, desc, isUnlocked) ->
                AchievementGridCard(title, desc, isUnlocked)
            }
        }
    }
}

@Composable
fun AchievementGridCard(title: String, description: String, isUnlocked: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isUnlocked) 0f else 0.4f)),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(if (isUnlocked) Res.drawable.baseline_emoji_events_24 else Res.drawable.baseline_star_24),
                        contentDescription = null,
                        tint = if (isUnlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}