package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import timeleaf.shared.generated.resources.baseline_timer_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_settings_24

private val ModernLightColorScheme = lightColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF34D399),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFDF5),
    onSecondaryContainer = Color(0xFF064E3B),
    surface = Color(0xFFF9FAFB),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFF3F4F6),
    onBackground = Color(0xFF111827),
    outlineVariant = Color(0xFFE5E7EB)
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFECFDF5),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF),
    background = Color(0xFF030712),
    onBackground = Color(0xFFF9FAFB),
    outlineVariant = Color(0xFF374151)
)

@Composable
@Preview
fun App() {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

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
                            .padding(bottom = 24.dp, top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                            tonalElevation = 0.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .widthIn(max = 260.dp)
                                .height(64.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val activeColor = MaterialTheme.colorScheme.primary
                                val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

                                NavigationIcon(
                                    iconRes = Res.drawable.baseline_bolt_24,
                                    description = "Kafelki",
                                    isSelected = selectedTab == 0,
                                    activeColor = activeColor,
                                    inactiveColor = inactiveColor,
                                    onClick = { selectedTab = 0 }
                                )

                                NavigationIcon(
                                    iconRes = Res.drawable.baseline_timer_24,
                                    description = "Timer",
                                    isSelected = selectedTab == 1,
                                    activeColor = activeColor,
                                    inactiveColor = inactiveColor,
                                    onClick = { selectedTab = 1 }
                                )

                                NavigationIcon(
                                    iconRes = Res.drawable.baseline_person_24,
                                    description = "Profil",
                                    isSelected = selectedTab == 2,
                                    activeColor = activeColor,
                                    inactiveColor = inactiveColor,
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
                        fadeIn() + slideInHorizontally { if (targetState > initialState) it else -it } togetherWith
                                fadeOut() + slideOutHorizontally { if (targetState > initialState) -it else it }
                    },
                    label = "TabTransition"
                ) { tab ->
                    when (tab) {
                        0 -> TimeTilesScreen(onTileSelected = { _ -> selectedTab = 1 })
                        1 -> TimerScreen()
                        2 -> ProfileScreen()
                    }
                }

                if (selectedTab == 1 && !showSettings) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(24.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = { showSettings = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_settings_24),
                                contentDescription = "Ustawienia",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
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
private fun NavigationIcon(
    iconRes: DrawableResource,
    description: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = activeColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {}
            }
            Icon(
                painter = painterResource(iconRes),
                contentDescription = description,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
