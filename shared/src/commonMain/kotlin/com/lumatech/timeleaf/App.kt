package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.time.TimeSource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_bolt_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_timer_24
import timeleaf.shared.generated.resources.Nunito_Black
import timeleaf.shared.generated.resources.Nunito_Bold
import timeleaf.shared.generated.resources.Nunito_ExtraBold
import timeleaf.shared.generated.resources.Nunito_ExtraLight
import timeleaf.shared.generated.resources.Nunito_Light
import timeleaf.shared.generated.resources.Nunito_Medium
import timeleaf.shared.generated.resources.Nunito_Regular
import timeleaf.shared.generated.resources.Nunito_SemiBold

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
    background = Color(0xFFF5F6F7),
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
    val isDark = when (currentThemeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    val colorScheme = if (isDark) ModernDarkColorScheme else ModernLightColorScheme

    val nunitoFontFamily = FontFamily(
        Font(Res.font.Nunito_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.Nunito_Light, FontWeight.Light),
        Font(Res.font.Nunito_Regular, FontWeight.Normal),
        Font(Res.font.Nunito_Medium, FontWeight.Medium),
        Font(Res.font.Nunito_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Nunito_Bold, FontWeight.Bold),
        Font(Res.font.Nunito_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.Nunito_Black, FontWeight.Black)
    )

    val nunitoTypography = Typography(
        displayLarge = TextStyle(fontFamily = nunitoFontFamily, fontSize = 57.sp, fontWeight = FontWeight.Bold),
        displayMedium = TextStyle(fontFamily = nunitoFontFamily, fontSize = 45.sp, fontWeight = FontWeight.Bold),
        displaySmall = TextStyle(fontFamily = nunitoFontFamily, fontSize = 36.sp, fontWeight = FontWeight.Bold),
        headlineLarge = TextStyle(fontFamily = nunitoFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontFamily = nunitoFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Bold),
        headlineSmall = TextStyle(fontFamily = nunitoFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold),
        titleLarge = TextStyle(fontFamily = nunitoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold),
        titleMedium = TextStyle(fontFamily = nunitoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
        titleSmall = TextStyle(fontFamily = nunitoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        bodyLarge = TextStyle(fontFamily = nunitoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontFamily = nunitoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Normal),
        bodySmall = TextStyle(fontFamily = nunitoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Normal),
        labelLarge = TextStyle(fontFamily = nunitoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        labelMedium = TextStyle(fontFamily = nunitoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium),
        labelSmall = TextStyle(fontFamily = nunitoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    )

    MaterialTheme(colorScheme = colorScheme, typography = nunitoTypography) {
        var selectedTab by remember { mutableStateOf(1) }
        var showSettings by remember { mutableStateOf(false) }
        var lastBackMark by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }

        val pagerState = rememberPagerState(initialPage = selectedTab) { 3 }

        val unlockedAch = UserStats.latestUnlockedPopup
        LaunchedEffect(unlockedAch) {
            if (unlockedAch != null) {
                delay(3500L)
                UserStats.latestUnlockedPopup = null
            }
        }

        LaunchedEffect(selectedTab) {
            if (pagerState.currentPage != selectedTab) {
                pagerState.animateScrollToPage(selectedTab)
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            if (selectedTab != pagerState.currentPage) {
                selectedTab = pagerState.currentPage
            }
        }

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
            // Achievement Unlocked Popup Banner
            AnimatedVisibility(
                visible = unlockedAch != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .zIndex(20f)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🏆", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentAppLanguage == AppLanguage.PL) "Odblokowano osiągnięcie!" else "Achievement Unlocked!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = unlockedAch ?: "",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> TimeTilesScreen(onTileSelected = { selectedTab = 1 })
                        1 -> TimerScreen(onOpenSettings = { showSettings = true })
                        2 -> ProfileScreen(onOpenSettings = { showSettings = true })
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSettings,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BottomNavItem(
                                iconRes = Res.drawable.baseline_bolt_24,
                                label = LocalizedStrings.navTiles,
                                isSelected = selectedTab == 0,
                                onClick = { selectedTab = 0 }
                            )

                            BottomNavItem(
                                iconRes = Res.drawable.baseline_timer_24,
                                label = LocalizedStrings.navTimer,
                                isSelected = selectedTab == 1,
                                onClick = { selectedTab = 1 }
                            )

                            BottomNavItem(
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
private fun BottomNavItem(
    iconRes: DrawableResource,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val activeBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) activeBg else Color.Transparent,
        modifier = Modifier.height(42.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
