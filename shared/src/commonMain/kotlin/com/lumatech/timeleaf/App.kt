package com.lumatech.timeleaf

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val SoftGreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF558B2F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEDC8),
    onSecondaryContainer = Color(0xFF33691E),
    surface = Color(0xFFF8F9F8),
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF384E3A),
    background = Color(0xFFFCFDFC),
    onBackground = Color(0xFF1A1C1A)
)

private val SoftGreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003910),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFAED581),
    onSecondary = Color(0xFF1F3700),
    secondaryContainer = Color(0xFF33691E),
    onSecondaryContainer = Color(0xFFDCEDC8),
    surface = Color(0xFF121412),
    onSurface = Color(0xFFE2E3E1),
    surfaceVariant = Color(0xFF1E261F),
    onSurfaceVariant = Color(0xFFC2C8C2),
    background = Color(0xFF0F110F),
    onBackground = Color(0xFFE2E3E1)
)

@Composable
@Preview
fun App() {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) SoftGreenDarkColorScheme else SoftGreenLightColorScheme

    MaterialTheme(colorScheme = colorScheme) {
        // 0: Left (Time Tiles / Kafelki), 1: Center (Timer / Stoper - default), 2: Right (Settings / Ustawienia)
        var selectedTab by remember { mutableStateOf(1) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Text("⏱️", style = MaterialTheme.typography.titleMedium) },
                        label = { Text("Kafelki") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Text("⏳", style = MaterialTheme.typography.titleMedium) },
                        label = { Text("Timer") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Text("⚙️", style = MaterialTheme.typography.titleMedium) },
                        label = { Text("Ustawienia") }
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (selectedTab) {
                    0 -> TimeTilesScreen(onTileSelected = { _ ->
                        selectedTab = 1
                    })
                    1 -> TimerScreen()
                    2 -> SettingsScreen()
                }
            }
        }
    }
}
