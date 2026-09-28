package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_settings_24
import timeleaf.shared.generated.resources.baseline_timer_24

// Warm & Modern Off-White/Cream Light Palette
private val ModernLightColorScheme = lightColorScheme(
    primary = Color(0xFF059669), // Rich Emerald
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF047857),
    secondary = Color(0xFF10B981),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6F4EA),
    onSecondaryContainer = Color(0xFF064E3B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF0ECE3), // Warm soft cream tint
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFF7F5F0), // Warm off-white / soft beige
    onBackground = Color(0xFF111827),
    outlineVariant = Color(0xFFE5E0D8)
)

// Deep Warm Charcoal Dark Palette
private val ModernDarkColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFECFDF5),
    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),
    surface = Color(0xFF1C1D22),
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = Color(0xFF282A30),
    onSurfaceVariant = Color(0xFF9CA3AF),
    background = Color(0xFF121316),
    onBackground = Color(0xFFF9FAFB),
    outlineVariant = Color(0xFF2E3038)
)

var appForceDarkMode by mutableStateOf<Boolean?>(null)

@Composable
@Preview
fun App() {
    val systemDark = isSystemInDarkTheme()
    val isDark = appForceDarkMode ?: systemDark
    val colorScheme = if (isDark) ModernDarkColorScheme else ModernLightColorScheme

    MaterialTheme(colorScheme = colorScheme) {
        var selectedTab by remember { mutableStateOf(1) }
        var showSettings by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = !showSettings,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 20.dp, top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            tonalElevation = 6.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .height(60.dp)
                                .wrapContentWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ExpandingNavItem(
                                    iconRes = Res.drawable.baseline_bolt_24,
                                    label = LocalizedStrings.navTiles,
                                    isSelected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )

                                ExpandingNavItem(
                                    iconRes = Res.drawable.baseline_timer_24,
                                    label = LocalizedStrings.navTimer,
                                    isSelected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )

                                ExpandingNavItem(
                                    iconRes = Res.drawable.baseline_person_24,
                                    label = LocalizedStrings.navProfile,
                                    isSelected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                slideInHorizontally { if (targetState > initialState) it / 2 else -it / 2 } togetherWith
                                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                slideOutHorizontally { if (targetState > initialState) -it / 2 else it / 2 }
                    },
                    label = "TabTransition"
                ) { tab ->
                    when (tab) {
                        0 -> TimeTilesScreen(onTileSelected = { selectedTab = 1 })
                        1 -> TimerScreen()
                        2 -> ProfileScreen()
                    }
                }

                // Settings gear button on top right when on Timer screen
                if (selectedTab == 1 && !showSettings) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(top = 8.dp, end = 16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_settings_24),
                                    contentDescription = "Ustawienia",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSettings,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                SettingsScreen(onBackClick = { showSettings = false })
            }
        }
    }
}

@Composable
private fun ExpandingNavItem(
    iconRes: DrawableResource,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    val activeBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) activeBg else Color.Transparent,
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
