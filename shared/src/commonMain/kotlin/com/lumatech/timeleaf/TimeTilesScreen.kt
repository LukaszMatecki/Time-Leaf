package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.minutes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_add_24
import timeleaf.shared.generated.resources.baseline_bedtime_24
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_delete_24
import timeleaf.shared.generated.resources.baseline_emoji_events_24
import timeleaf.shared.generated.resources.baseline_group_24
import timeleaf.shared.generated.resources.baseline_palette_24
import timeleaf.shared.generated.resources.baseline_self_improvement_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_timer_24

@Composable
@Preview
fun TimeTilesScreen(onTileSelected: (TimeTileInfo) -> Unit = {}) {
    var showCustomModal by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf(30) }
    var customTitle by remember { mutableStateOf("") }
    var selectedIconRes by remember { mutableStateOf<DrawableResource>(Res.drawable.baseline_timer_24) }
    var tileToDelete by remember { mutableStateOf<TimeTileInfo?>(null) }

    val availableIcons = listOf(
        Res.drawable.baseline_timer_24,
        Res.drawable.baseline_bolt_24,
        Res.drawable.baseline_star_24,
        Res.drawable.baseline_palette_24,
        Res.drawable.baseline_bedtime_24,
        Res.drawable.baseline_emoji_events_24,
        Res.drawable.baseline_group_24,
        Res.drawable.baseline_self_improvement_24
    )

    val defaultTiles = remember {
        mutableStateListOf(
            TimeTileInfo(
                title = LocalizedStrings.presetQuickTaskTitle,
                durationMinutes = 5,
                description = "",
                iconContent = { tint -> BoltVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetPomodoroTitle,
                durationMinutes = 25,
                description = "",
                iconContent = { tint -> WorkVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetDeepSessionTitle,
                durationMinutes = 45,
                description = "",
                iconContent = { tint -> StarVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetHourTitle,
                durationMinutes = 60,
                description = "",
                iconContent = { tint -> TimerTileVectorIcon(tint = tint) }
            )
        )
    }

    val allTiles = remember(defaultTiles.size, sharedCustomTiles.size) {
        defaultTiles + sharedCustomTiles
    }

    val accentColors = listOf(
        Color(0xFF9EA8DB),
        Color(0xFF80CBC4),
        Color(0xFF90CAF9),
        Color(0xFFFFCC80),
        Color(0xFFF48FB1),
        Color(0xFFBCAAA4)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = LocalizedStrings.tilesHeaderTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = LocalizedStrings.tilesHeaderSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        DailyOverviewSummary()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    onClick = { showCustomModal = true },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    shadowElevation = 0.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF909CFF).copy(alpha = 0.9f),
                                        Color(0xFFA76DF0).copy(alpha = 0.8f)
                                    )
                                )
                            )
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val wavePath = Path().apply {
                                moveTo(0f, h * 0.5f)
                                cubicTo(w * 0.3f, h * 0.2f, w * 0.7f, h * 0.9f, w, h * 0.4f)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(wavePath, Color.White.copy(alpha = 0.08f))

                            drawCircle(
                                color = Color.White.copy(alpha = 0.12f),
                                radius = h * 0.7f,
                                center = Offset(w * 0.9f, h * 0.1f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_add_24),
                                        contentDescription = LocalizedStrings.tilesCustomCardTitle,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LocalizedStrings.tilesCustomCardTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = LocalizedStrings.tilesCustomCardSub,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            itemsIndexed(allTiles) { index, tile ->
                val isSelected = sharedTimerManager.targetDuration.inWholeMinutes == tile.durationMinutes.toLong() && sharedTimerManager.isCountdownMode
                val accentColor = accentColors[index % accentColors.size]

                BentoTileCard(
                    tile = tile,
                    isSelected = isSelected,
                    accentColor = accentColor,
                    patternIndex = index % 3,
                    onClick = {
                        sharedTimerManager.setCountdown(tile.durationMinutes.minutes)
                        onTileSelected(tile)
                    },
                    onDelete = if (tile.isCustom) {
                        { tileToDelete = tile }
                    } else null
                )
            }
        }
    }

    if (tileToDelete != null) {
        Dialog(onDismissRequest = { tileToDelete = null }) {
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
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_delete_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = if (currentAppLanguage == AppLanguage.PL) "Usunąć sesję?" else "Delete session?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (currentAppLanguage == AppLanguage.PL) "Czy na pewno chcesz usunąć tę sesję? Tej operacji nie można cofnąć." else "Are you sure you want to delete this session? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { tileToDelete = null },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = LocalizedStrings.btnNo, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {
                                tileToDelete?.let { sharedCustomTiles.remove(it) }
                                tileToDelete = null
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(text = LocalizedStrings.btnYes, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    if (showCustomModal) {
        Dialog(onDismissRequest = { showCustomModal = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = LocalizedStrings.dialogCustomTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = LocalizedStrings.hintSessionName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        placeholder = { Text(LocalizedStrings.dialogSessionNameLabel, style = MaterialTheme.typography.bodyMedium) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = LocalizedStrings.hintSelectIcon,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableIconPicker(
                        icons = availableIcons,
                        selectedIcon = selectedIconRes,
                        onIconSelected = { selectedIconRes = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = LocalizedStrings.hintSelectDuration,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableMinutePicker(
                        selectedMinutes = customMinutes,
                        onMinutesChanged = { customMinutes = it }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showCustomModal = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(LocalizedStrings.btnCancel, style = MaterialTheme.typography.titleMedium)
                        }

                        Button(
                            onClick = {
                                val name = customTitle.trim()
                                val finalTitle = if (name.isBlank()) "Własna sesja" else name
                                val iconResToUse = selectedIconRes
                                if (name.isNotBlank()) {
                                    val newTile = TimeTileInfo(
                                        title = finalTitle,
                                        durationMinutes = customMinutes,
                                        isCustom = true,
                                        iconContent = { tint ->
                                            Icon(
                                                painter = painterResource(iconResToUse),
                                                contentDescription = null,
                                                tint = tint,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    )
                                    sharedCustomTiles.add(newTile)
                                    UserStats.recordCreateCustomSession()
                                }
                                sharedTimerManager.setCountdown(customMinutes.minutes)
                                onTileSelected(
                                    TimeTileInfo(
                                        title = finalTitle,
                                        durationMinutes = customMinutes,
                                        iconContent = { tint ->
                                            Icon(
                                                painter = painterResource(iconResToUse),
                                                contentDescription = null,
                                                tint = tint,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    )
                                )

                                customTitle = ""
                                customMinutes = 30
                                selectedIconRes = Res.drawable.baseline_timer_24
                                showCustomModal = false
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(LocalizedStrings.btnAdd, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyOverviewSummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_bolt_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dzisiejszy cel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "3/5",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_self_improvement_24),
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Czas skupienia",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "2h 15m",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoTileCard(
    tile: TimeTileInfo,
    isSelected: Boolean,
    accentColor: Color,
    patternIndex: Int,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)) else null,
        shadowElevation = if (isSelected) 8.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val patternAlpha = if (isSelected) 0.25f else 0.1f
                val basePatternColor = if (isSelected) Color.White else accentColor
                val w = size.width
                val h = size.height

                when (patternIndex) {
                    0 -> {
                        val path1 = Path().apply {
                            moveTo(0f, h * 0.5f)
                            cubicTo(w * 0.3f, h * 0.3f, w * 0.7f, h * 0.7f, w, h * 0.4f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(path1, basePatternColor.copy(alpha = patternAlpha * 0.7f))

                        val path2 = Path().apply {
                            moveTo(0f, h * 0.7f)
                            cubicTo(w * 0.4f, h * 1.0f, w * 0.6f, h * 0.4f, w, h * 0.6f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(path2, basePatternColor.copy(alpha = patternAlpha))
                    }
                    1 -> {
                        val spacing = 20f
                        for (x in 0..(w.toInt()) step spacing.toInt()) {
                            for (y in 0..(h.toInt()) step spacing.toInt()) {
                                val wave = sin(x * 0.03f + y * 0.03f) * cos(x * 0.02f)
                                val dotRadius = 1.5f + wave * 2.5f
                                if (dotRadius > 0) {
                                    drawCircle(
                                        color = basePatternColor.copy(alpha = patternAlpha * 0.8f),
                                        radius = dotRadius,
                                        center = Offset(x.toFloat(), y.toFloat())
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        drawCircle(
                            color = basePatternColor.copy(alpha = patternAlpha * 0.3f),
                            radius = w * 0.25f,
                            center = Offset(w, 0f)
                        )
                        drawCircle(
                            color = basePatternColor.copy(alpha = patternAlpha * 0.6f),
                            radius = w * 0.45f,
                            center = Offset(0f, h)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            tile.iconContent(
                                if (isSelected) Color.White else accentColor
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    ) {
                        Text(
                            text = "${tile.durationMinutes} min",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = tile.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    if (tile.isCustom && onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(start = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_delete_24),
                                contentDescription = "Usuń",
                                tint = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoltVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_bolt_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun WorkVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_star_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun StarVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_star_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun TimerTileVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_timer_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
fun ScrollableIconPicker(
    icons: List<DrawableResource>,
    selectedIcon: DrawableResource,
    onIconSelected: (DrawableResource) -> Unit
) {
    val actualSize = icons.size
    val initialIndex = remember {
        val middle = Int.MAX_VALUE / 2
        middle - (middle % actualSize) + icons.indexOf(selectedIcon).coerceAtLeast(0)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            var closestIndex: Int? = null
            var minDistance = Int.MAX_VALUE
            for (item in layoutInfo.visibleItemsInfo) {
                val itemCenter = item.offset + item.size / 2
                val distance = abs(itemCenter - center)
                if (distance < minDistance) {
                    minDistance = distance
                    closestIndex = item.index
                }
            }
            closestIndex
        }.collect { index ->
            if (index != null) {
                onIconSelected(icons[index % actualSize])
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val halfWidth = maxWidth / 2
        val itemWidth = 44.dp
        val horizontalPadding = halfWidth - (itemWidth / 2)

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(56.dp)
        ) {}

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth().height(76.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(Int.MAX_VALUE) { index ->
                val iconRes = icons[index % actualSize]
                val isSelected = iconRes == selectedIcon
                val alpha = if (isSelected) 1f else 0.4f

                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                            modifier = Modifier.size(if (isSelected) 22.dp else 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableMinutePicker(selectedMinutes: Int, onMinutesChanged: (Int) -> Unit) {
    val actualSize = 120
    val initialIndex = remember {
        val middle = Int.MAX_VALUE / 2
        middle - (middle % actualSize) + (selectedMinutes - 1).coerceAtLeast(0)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            var closestIndex: Int? = null
            var minDistance = Int.MAX_VALUE
            for (item in layoutInfo.visibleItemsInfo) {
                val itemCenter = item.offset + item.size / 2
                val distance = abs(itemCenter - center)
                if (distance < minDistance) {
                    minDistance = distance
                    closestIndex = item.index
                }
            }
            closestIndex
        }.collect { index ->
            if (index != null) {
                onMinutesChanged((index % actualSize) + 1)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val halfHeight = maxHeight / 2
        val itemHeight = 48.dp
        val verticalPadding = halfHeight - (itemHeight / 2)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().height(130.dp),
            contentPadding = PaddingValues(vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(Int.MAX_VALUE) { index ->
                val minute = (index % actualSize) + 1
                val isSelected = minute == selectedMinutes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$minute min",
                        style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}