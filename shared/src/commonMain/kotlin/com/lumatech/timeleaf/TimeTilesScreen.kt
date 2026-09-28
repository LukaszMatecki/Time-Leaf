package com.lumatech.timeleaf

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        // --- HEADER ---
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp)
        ) {
            Text(
                text = LocalizedStrings.tilesHeaderTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = LocalizedStrings.tilesHeaderSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        // --- BENTO GRID ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    onClick = { showCustomModal = true },
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_add_24),
                                    contentDescription = LocalizedStrings.tilesCustomCardTitle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LocalizedStrings.tilesCustomCardTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = LocalizedStrings.tilesCustomCardSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 11.sp
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

    // --- OVERHAULED CUSTOM SESSION DIALOG (INFINITE SCROLL ONLY) ---
    if (showCustomModal) {
        Dialog(onDismissRequest = { showCustomModal = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalizedStrings.dialogCustomTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        placeholder = { Text(LocalizedStrings.dialogSessionNameLabel, fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pure Infinite Scroll Wheel Picker
                    ScrollableMinutePicker(
                        selectedMinutes = customMinutes,
                        onMinutesChanged = { customMinutes = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = { showCustomModal = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(LocalizedStrings.btnCancel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
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
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(LocalizedStrings.btnAdd, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
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
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        tile.iconContent(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "${tile.durationMinutes} m",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column {
                Text(
                    text = tile.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = tile.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
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
            .height(110.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ) {}

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(120) { index ->
                val minutes = index + 1
                val isSelected = minutes == selectedMinutes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clickable { onMinutesChanged(minutes) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$minutes min",
                        style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun BoltVectorIcon(tint: Color) {
    Icon(painterResource(Res.drawable.baseline_bolt_24), contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
}

@Composable
fun TimerTileVectorIcon(tint: Color) {
    Icon(painterResource(Res.drawable.baseline_timer_24), contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
}

@Composable
fun WorkVectorIcon(tint: Color) {
    Icon(painterResource(Res.drawable.baseline_schedule_24), contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
}

@Composable
fun StarVectorIcon(tint: Color) {
    Icon(painterResource(Res.drawable.baseline_star_24), contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
}
