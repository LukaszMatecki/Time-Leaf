package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_emoji_events_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_timer_24

@Composable
@Preview
fun ProfileScreen() {
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
            ProfileMainView(onOpenAchievements = { showAchievementsScreen = true })
        }
    }
}

@Composable
private fun ProfileMainView(onOpenAchievements: () -> Unit) {
    var dialogTitle by remember { mutableStateOf<String?>(null) }
    var dialogDesc by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = LocalizedStrings.profileGreeting,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Greeting & Info Card (Clickable)
                item {
                    Surface(
                        onClick = {
                            dialogTitle = LocalizedStrings.profileLevelInfoTitle
                            dialogDesc = LocalizedStrings.profileLevelInfoDesc
                        },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = LocalizedStrings.profileGreeting,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_person_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = LocalizedStrings.profileUserName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = LocalizedStrings.profileUserLevel,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Stat Cards (Real Data)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Wynik Skupienia",
                                value = UserStats.focusScore.toString(),
                                suffix = "/100",
                                iconRes = Res.drawable.baseline_star_24,
                                modifier = Modifier.weight(1f),
                                highlight = true,
                                onClick = {
                                    dialogTitle = LocalizedStrings.profileScoreTitle
                                    dialogDesc = "${LocalizedStrings.profileScoreDesc}${UserStats.focusScore}/100"
                                }
                            )
                            StatCard(
                                title = "Dni z rzędu",
                                value = UserStats.streakDays.toString(),
                                suffix = " dni",
                                iconRes = Res.drawable.baseline_bolt_24,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    dialogTitle = LocalizedStrings.profileStreakTitle
                                    dialogDesc = "${LocalizedStrings.profileStreakDesc}${UserStats.streakDays} dni."
                                }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = LocalizedStrings.profileCompletedSessions,
                                value = UserStats.completedSessions.toString(),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    dialogTitle = LocalizedStrings.profileSessionsTitle
                                    dialogDesc = "${LocalizedStrings.profileSessionsDesc}${UserStats.completedSessions}."
                                }
                            )
                            val totalMins = UserStats.totalFocusMinutes
                            val timeStr = if (totalMins >= 60) "${totalMins / 60}h ${totalMins % 60}m" else "${totalMins}m"
                            StatCard(
                                title = LocalizedStrings.profileFocusTime,
                                value = timeStr,
                                iconRes = Res.drawable.baseline_timer_24,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    dialogTitle = LocalizedStrings.profileTimeTitle
                                    dialogDesc = "${LocalizedStrings.profileTimeDesc}$timeStr."
                                }
                            )
                        }
                    }
                }

                // Activity Chart Card (Real Data)
                item {
                    ActivityChartCard(
                        onClick = {
                            dialogTitle = LocalizedStrings.profileChartTitle
                            dialogDesc = LocalizedStrings.profileChartDesc
                        }
                    )
                }

                // Achievements Navigation Card (Dynamic count)
                item {
                    Surface(
                        onClick = onOpenAchievements,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_emoji_events_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = LocalizedStrings.profileAchievementsTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${UserStats.unlockedAchievements.size} z 7 ${LocalizedStrings.profileAchievementsSubtitle}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Icon(
                                painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(180f)
                            )
                        }
                    }
                }
            }
        }

        // Info Dialog Popup
        if (dialogTitle != null && dialogDesc != null) {
            Dialog(onDismissRequest = { dialogTitle = null; dialogDesc = null }) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.widthIn(max = 340.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dialogTitle ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = dialogDesc ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { dialogTitle = null; dialogDesc = null },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = LocalizedStrings.btnClose,
                                fontSize = 15.sp,
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
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    suffix: String = "",
    iconRes: DrawableResource? = null,
    highlight: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (highlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (suffix.isNotEmpty()) {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityChartCard(onClick: (() -> Unit)? = null) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Aktywność w tym tygodniu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            val lineColor = MaterialTheme.colorScheme.primary
            val gradientColors = listOf(
                lineColor.copy(alpha = 0.3f),
                Color.Transparent
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = LocalizedStrings.btnBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp).offset(x = (-1).dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = LocalizedStrings.achievementsHeaderTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = LocalizedStrings.achievementsHeaderSub,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach1Title,
                    description = LocalizedStrings.ach1Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("first_step")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach2Title,
                    description = LocalizedStrings.ach2Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("session_creator")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach3Title,
                    description = LocalizedStrings.ach3Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("explorer")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach4Title,
                    description = LocalizedStrings.ach4Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("marathoner")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach5Title,
                    description = LocalizedStrings.ach5Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("focus_master")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach6Title,
                    description = LocalizedStrings.ach6Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("night_owl")
                )
            }
            item {
                AchievementCardItem(
                    title = LocalizedStrings.ach7Title,
                    description = LocalizedStrings.ach7Desc,
                    isUnlocked = UserStats.unlockedAchievements.contains("flow_master")
                )
            }
        }
    }
}

@Composable
fun AchievementCardItem(title: String, description: String, isUnlocked: Boolean) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_star_24),
                        contentDescription = null,
                        tint = if (isUnlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = CircleShape,
                color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = if (isUnlocked) LocalizedStrings.achievementUnlocked else LocalizedStrings.achievementLocked,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
