package com.lumatech.timeleaf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.minutes
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_self_improvement_24
import timeleaf.shared.generated.resources.baseline_timer_24
import timeleaf.shared.generated.resources.baseline_schedule_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_emoji_events_24

data class TimeTileInfo(val title: String, val durationMinutes: Int, val description: String, val iconContent: @Composable (Color) -> Unit)

@Composable
@Preview
fun TimeTilesScreen(onTileSelected: (TimeTileInfo) -> Unit = {}) {
    val tiles = remember {
        listOf(
            TimeTileInfo("Szybkie zadanie", 5, "5 minut skupienia") { tint -> BoltVectorIcon(tint = tint) },
            TimeTileInfo("Przerwa", 10, "10 minut relaksu") { tint -> CoffeeVectorIcon(tint = tint) },
            TimeTileInfo("Power Nap", 15, "15 minut regeneracji") { tint -> TimerTileVectorIcon(tint = tint) },
            TimeTileInfo("Skupienie", 20, "20 minut pracy") { tint -> TimerTileVectorIcon(tint = tint) },
            TimeTileInfo("Pomodoro", 25, "25 minut deep work") { tint -> WorkVectorIcon(tint = tint) },
            TimeTileInfo("Sesja Głęboka", 45, "45 minut flow") { tint -> StarVectorIcon(tint = tint) },
            TimeTileInfo("Godzina Pracy", 60, "60 minut zadania") { tint -> TimerTileVectorIcon(tint = tint) },
            TimeTileInfo("Maraton", 90, "90 minut masterclass") { tint -> CheckVectorIcon(tint = tint) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Kafelki Czasowe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tiles) { tile ->
                val isSelected = sharedTimerManager.targetDuration.inWholeMinutes == tile.durationMinutes.toLong() && sharedTimerManager.isCountdownMode

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .clickable {
                            sharedTimerManager.setCountdown(tile.durationMinutes.minutes)
                            onTileSelected(tile)
                        },
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    tile.iconContent(
                                        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = tile.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tile.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = "${tile.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoltVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_bolt_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun CoffeeVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_self_improvement_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun TimerTileVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_timer_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun WorkVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_schedule_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun StarVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_star_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun CheckVectorIcon(tint: Color) {
    Icon(
        painter = painterResource(Res.drawable.baseline_emoji_events_24),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(24.dp)
    )
}