package com.lumatech.timeleaf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.time.Duration.Companion.minutes
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_add_24
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_schedule_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_timer_24

data class TimeTileInfo(
    val title: String,
    val durationMinutes: Int,
    val description: String,
    val iconContent: @Composable (Color) -> Unit
)

@Composable
@Preview
fun TimeTilesScreen(onTileSelected: (TimeTileInfo) -> Unit = {}) {
    var showCustomModal by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf(30) }
    var customTitle by remember { mutableStateOf("") }

    val defaultTiles = remember {
        mutableStateListOf(
            TimeTileInfo(
                title = LocalizedStrings.presetQuickTaskTitle,
                durationMinutes = 5,
                description = LocalizedStrings.presetQuickTaskDesc,
                iconContent = { tint -> BoltVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetPomodoroTitle,
                durationMinutes = 25,
                description = LocalizedStrings.presetPomodoroDesc,
                iconContent = { tint -> WorkVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetDeepSessionTitle,
                durationMinutes = 45,
                description = LocalizedStrings.presetDeepSessionDesc,
                iconContent = { tint -> StarVectorIcon(tint = tint) }
            ),
            TimeTileInfo(
                title = LocalizedStrings.presetHourTitle,
                durationMinutes = 60,
                description = LocalizedStrings.presetHourDesc,
                iconContent = { tint -> TimerTileVectorIcon(tint = tint) }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp)
        ) {
            Text(
                text = LocalizedStrings.tilesHeaderTitle,
                style = MaterialTheme.typography.headlineSmall, // Mniejszy nagłówek
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = LocalizedStrings.tilesHeaderSubtitle,
                style = MaterialTheme.typography.titleSmall, // Mniejszy podtytuł
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    onClick = { showCustomModal = true },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
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
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_add_24),
                                    contentDescription = LocalizedStrings.tilesCustomCardTitle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LocalizedStrings.tilesCustomCardTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = LocalizedStrings.tilesCustomCardSub,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            itemsIndexed(defaultTiles) { _, tile ->
                val isSelected = sharedTimerManager.targetDuration.inWholeMinutes == tile.durationMinutes.toLong() && sharedTimerManager.isCountdownMode

                BentoTileCard(
                    tile = tile,
                    isSelected = isSelected,
                    onClick = {
                        sharedTimerManager.setCountdown(tile.durationMinutes.minutes)
                        onTileSelected(tile)
                    }
                )
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
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalizedStrings.dialogCustomTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        placeholder = { Text(LocalizedStrings.dialogSessionNameLabel, style = MaterialTheme.typography.bodyMedium) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(LocalizedStrings.btnCancel, style = MaterialTheme.typography.titleMedium)
                        }

                        Button(
                            onClick = {
                                val name = if (customTitle.isBlank()) "Własna sesja" else customTitle.trim()
                                val newTile = TimeTileInfo(
                                    title = name,
                                    durationMinutes = customMinutes,
                                    description = "$customMinutes min",
                                    iconContent = { tint -> TimerTileVectorIcon(tint = tint) }
                                )

                                defaultTiles.add(newTile)
                                sharedTimerManager.setCountdown(customMinutes.minutes)
                                onTileSelected(newTile)

                                customTitle = ""
                                customMinutes = 30
                                showCustomModal = false
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = CircleShape,
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
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        tile.iconContent(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "${tile.durationMinutes} min",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column {
                Text(
                    text = tile.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = tile.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableMinutePicker(selectedMinutes: Int, onMinutesChanged: (Int) -> Unit) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedMinutes - 1).coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            onMinutesChanged(index + 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.02f),
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.02f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(120) { index ->
                val minutes = index + 1
                val isSelected = minutes == selectedMinutes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { onMinutesChanged(minutes) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$minutes min",
                        style = if (isSelected) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun BoltVectorIcon(tint: Color) { Icon(painterResource(Res.drawable.baseline_bolt_24), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) }
@Composable
fun TimerTileVectorIcon(tint: Color) { Icon(painterResource(Res.drawable.baseline_timer_24), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) }
@Composable
fun WorkVectorIcon(tint: Color) { Icon(painterResource(Res.drawable.baseline_schedule_24), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) }
@Composable
fun StarVectorIcon(tint: Color) { Icon(painterResource(Res.drawable.baseline_star_24), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) }