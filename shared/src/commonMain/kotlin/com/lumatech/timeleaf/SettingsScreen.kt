package com.lumatech.timeleaf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var autoStartBreaks by remember { mutableStateOf(false) }
    var keepScreenAwake by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Ustawienia",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SettingsSectionHeader("Powiadomienia i Dźwięk")
                SettingsCard {
                    SettingSwitchItem(
                        title = "Powiadomienia push",
                        description = "Alerty po zakończeniu sesji",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = "Dźwięk alarmu",
                        description = "Odtwarzaj dźwięk po odliczeniu czasu",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = "Wibracje",
                        description = "Wibruj przy zakończeniu",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("Zachowanie Timera")
                SettingsCard {
                    SettingSwitchItem(
                        title = "Automatyczna przerwa",
                        description = "Włącz przerwę po zakończeniu skupienia",
                        checked = autoStartBreaks,
                        onCheckedChange = { autoStartBreaks = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = "Nie wygaszaj ekranu",
                        description = "Trzymaj ekran włączony podczas timera",
                        checked = keepScreenAwake,
                        onCheckedChange = { keepScreenAwake = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("Wygląd")
                SettingsCard {
                    SettingSwitchItem(
                        title = "Ciemny motyw",
                        description = "Używaj ciemnej palety kolorów",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                }
            }

            item {
                SettingsSectionHeader("Informacje")
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "TimeLeaf",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Wersja 1.0.0",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nowoczesna aplikacja do zarządzania czasem i skupieniem. Stworzona w Kotlin Multiplatform.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            content = content
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
    )
}

@Composable
fun MinimalDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    )
}

@Composable
fun SettingSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}