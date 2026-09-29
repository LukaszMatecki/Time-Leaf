package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.TimeSource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_timer_24
import timeleaf.shared.generated.resources.Dongle_Bold
import timeleaf.shared.generated.resources.Dongle_Light
import timeleaf.shared.generated.resources.Dongle_Regular

private val ModernLightColorScheme = lightColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F4EA),
    onPrimaryContainer = Color(0xFF047857),
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFDF5),
    onSecondaryContainer = Color(0xFF064E3B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF111827),
    outlineVariant = Color(0xFFE5E7EB)
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFECFDF5),
    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),
    surface = Color(0xFF181A20),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF22252D),
    onSurfaceVariant = Color(0xFF9CA3AF),
    background = Color(0xFF0F1115),
    onBackground = Color(0xFFF9FAFB),
    outlineVariant = Color(0xFF2B2E38)
)

var appForceDarkMode by mutableStateOf<Boolean?>(null)

@Composable
@Preview
fun App() {
    val systemDark = isSystemInDarkTheme()
    val isDark = appForceDarkMode ?: systemDark
    val colorScheme = if (isDark) ModernDarkColorScheme else ModernLightColorScheme

    val dongleFontFamily = FontFamily(
        Font(Res.font.Dongle_Light, FontWeight.Light),
        Font(Res.font.Dongle_Regular, FontWeight.Normal),
        Font(Res.font.Dongle_Regular, FontWeight.Medium),
        Font(Res.font.Dongle_Bold, FontWeight.Bold),
        Font(Res.font.Dongle_Bold, FontWeight.SemiBold),
        Font(Res.font.Dongle_Bold, FontWeight.Black)
    )

    val dongleTypography = Typography(
        displayLarge = TextStyle(fontFamily = dongleFontFamily, fontSize = 110.sp, fontWeight = FontWeight.Bold),
        displayMedium = TextStyle(fontFamily = dongleFontFamily, fontSize = 88.sp, fontWeight = FontWeight.Bold),
        displaySmall = TextStyle(fontFamily = dongleFontFamily, fontSize = 68.sp, fontWeight = FontWeight.Bold),
        headlineLarge = TextStyle(fontFamily = dongleFontFamily, fontSize = 52.sp, fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontFamily = dongleFontFamily, fontSize = 44.sp, fontWeight = FontWeight.Bold),
        headlineSmall = TextStyle(fontFamily = dongleFontFamily, fontSize = 36.sp, fontWeight = FontWeight.Bold),
        titleLarge = TextStyle(fontFamily = dongleFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold),
        titleMedium = TextStyle(fontFamily = dongleFontFamily, fontSize = 26.sp, fontWeight = FontWeight.SemiBold),
        titleSmall = TextStyle(fontFamily = dongleFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Medium),
        bodyLarge = TextStyle(fontFamily = dongleFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontFamily = dongleFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Normal),
        bodySmall = TextStyle(fontFamily = dongleFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal),
        labelLarge = TextStyle(fontFamily = dongleFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Medium),
        labelMedium = TextStyle(fontFamily = dongleFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Medium),
        labelSmall = TextStyle(fontFamily = dongleFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    )

    MaterialTheme(colorScheme = colorScheme, typography = dongleTypography) {
        var selectedTab by remember { mutableStateOf(1) }
        var showSettings by remember { mutableStateOf(false) }
        var lastBackMark by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }

        if (showSettings) {
            LocalBackHandler { showSettings = false }
        } else if (selectedTab != 1) {
            LocalBackHandler { selectedTab = 1 }
        } else {
            LocalBackHandler {
                val mark = lastBackMark
                val now = TimeSource.Monotonic.markNow()
                if (mark != null && mark.elapsedNow().inWholeMilliseconds < 2000)
                { lastBackMark = null }
                else {
                    lastBackMark = now
                    showSnackbar(LocalizedStrings.snackPressAgainToExit)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 84.dp)
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
                        1 -> TimerScreen(onOpenSettings = { showSettings = true })
                        2 -> ProfileScreen()
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSettings,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                        modifier = Modifier.height(52.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
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

            AnimatedVisibility(
                visible = showSettings,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            )
            {
                SettingsScreen(onBackClick = { showSettings = false })
            }
            GlobalFloatingSnackbarHost()
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
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val activeBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)

    val targetWidth by animateDpAsState(
        targetValue = if (isSelected) 108.dp else 46.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "tabWidth"
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) activeBg else Color.Transparent,
        modifier = Modifier
            .width(targetWidth)
            .height(42.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isSelected) 12.dp else 0.dp),
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
                enter = fadeIn(tween(200)) + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut(tween(200)) + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
            }
        }
    }
}