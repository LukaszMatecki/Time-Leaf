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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.abs
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

    // Paleta akcentów dla kafelków
    val accentColors = listOf(
        Color(0xFF8B5CF6), // Violet
        Color(0xFF10B981), // Emerald
        Color(0xFF3B82F6), // Blue
        Color(0xFFF59E0B), // Amber
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4)  // Cyan
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)
        ) {
            Text(
                text = LocalizedStrings.tilesHeaderTitle,
                style = MaterialTheme.typography.headlineSmall, // Zmniejszono z headlineMedium
                fontWeight = FontWeight.Bold, // Zmieniono z Black na Bold
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = LocalizedStrings.tilesHeaderSubtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kafelek dodawania nowej sesji - Pełna szerokość, designerski gradient
            item(span = { GridItemSpan(2) }) {
                Surface(
                    onClick = { showCustomModal = true },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                                )
                            )
                    ) {
                        // Abstrakcyjny wzór kół
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = size.height * 0.8f,
                                center = Offset(size.width * 0.9f, size.height * 0.2f)
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = size.height * 0.5f,
                                center = Offset(size.width * 0.1f, size.height * 0.9f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_add_24),
                                        contentDescription = LocalizedStrings.tilesCustomCardTitle,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LocalizedStrings.tilesCustomCardTitle,
                                    style = MaterialTheme.typography.titleMedium, // Zmniejszono z titleLarge
                                    fontWeight = FontWeight.Bold, // Zmieniono z Black na Bold
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = LocalizedStrings.tilesCustomCardSub,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal,
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
                        style = MaterialTheme.typography.titleLarge, // Zmniejszono
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentAppLanguage == AppLanguage.PL) "Czy na pewno chcesz usunąć tę sesję? Tej operacji nie można cofnąć." else "Are you sure you want to delete this session? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { tileToDelete = null },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = LocalizedStrings.btnNo, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                tileToDelete?.let { sharedCustomTiles.remove(it) }
                                tileToDelete = null
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(text = LocalizedStrings.btnYes, fontWeight = FontWeight.SemiBold)
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
                        style = MaterialTheme.typography.titleLarge, // Zmniejszono z headlineSmall
                        fontWeight = FontWeight.Bold, // Zmieniono z Black na Bold
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
private fun BentoTileCard(
    tile: TimeTileInfo,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp, // Delikatniejszy cień
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Lekko obniżone z 140dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Abstrakcyjne tło dla urozmaicenia
            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleAlpha = if (isSelected) 0.15f else 0.03f
                drawCircle(
                    color = if (isSelected) Color.White.copy(alpha = circleAlpha) else accentColor.copy(alpha = circleAlpha),
                    radius = size.width * 0.5f,
                    center = Offset(size.width, 0f)
                )
                drawCircle(
                    color = if (isSelected) Color.White.copy(alpha = circleAlpha * 0.5f) else accentColor.copy(alpha = circleAlpha * 0.5f),
                    radius = size.width * 0.3f,
                    center = Offset(0f, size.height)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp), // Zmniejszono padding
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            tile.iconContent(
                                if (isSelected) Color.White else accentColor
                            )
                        }
                    }

                    if (tile.isCustom && onDelete != null) {
                        Surface(
                            onClick = onDelete,
                            shape = CircleShape,
                            color = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_delete_24),
                                    contentDescription = "Usuń",
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "${tile.durationMinutes} min",
                        style = MaterialTheme.typography.titleLarge, // Zmniejszono z headlineMedium
                        fontWeight = FontWeight.Bold, // Zmieniono z Black na Bold
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = tile.title,
                        style = MaterialTheme.typography.titleSmall, // Zmieniono z labelMedium na titleSmall
                        fontWeight = FontWeight.SemiBold, // Zmieniono z Bold
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
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
            .height(76.dp) // Lekko zmniejszono
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
            modifier = Modifier.size(56.dp) // Zmniejszono z 64dp
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
                    modifier = Modifier.size(44.dp) // Zmniejszono z 48dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                            modifier = Modifier.size(if (isSelected) 22.dp else 20.dp) // Mniejsze ikonki
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
            .height(130.dp) // Zmniejszono ze 140dp
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
                .height(48.dp) // Zmniejszono z 52dp
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
                        style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium, // Zmniejszono z headlineSmall
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, // Zmieniono z Black
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}