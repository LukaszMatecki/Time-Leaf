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
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24

@Composable
@Preview
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var autoStartBreaks by remember { mutableStateOf(false) }
    var keepScreenAwake by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = LocalizedStrings.btnBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.dp).offset(x = (-1).dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = LocalizedStrings.settingsTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language Selection Section
            item {
                SettingsSectionHeader(LocalizedStrings.settingsSectionApp)
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = LocalizedStrings.settingsLanguageLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = LocalizedStrings.settingsLanguageSub,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                val isSelected = currentAppLanguage == lang
                                Surface(
                                    onClick = { currentAppLanguage = lang },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(lang.flag, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = lang.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notifications Section
            item {
                SettingsSectionHeader(LocalizedStrings.settingsSectionNotif)
                SettingsCard {
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsPushTitle,
                        description = LocalizedStrings.settingsPushSub,
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsSoundTitle,
                        description = LocalizedStrings.settingsSoundSub,
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsVibTitle,
                        description = LocalizedStrings.settingsVibSub,
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }

            // Behavior Section
            item {
                SettingsSectionHeader(LocalizedStrings.settingsSectionBehavior)
                SettingsCard {
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsAutoBreakTitle,
                        description = LocalizedStrings.settingsAutoBreakSub,
                        checked = autoStartBreaks,
                        onCheckedChange = { autoStartBreaks = it }
                    )
                    MinimalDivider()
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsKeepScreenTitle,
                        description = LocalizedStrings.settingsKeepScreenSub,
                        checked = keepScreenAwake,
                        onCheckedChange = { keepScreenAwake = it }
                    )
                }
            }

            // Appearance Section
            item {
                SettingsSectionHeader(LocalizedStrings.settingsSectionAppearance)
                SettingsCard {
                    SettingSwitchItem(
                        title = LocalizedStrings.settingsDarkModeTitle,
                        description = LocalizedStrings.settingsDarkModeSub,
                        checked = appForceDarkMode == true,
                        onCheckedChange = { isDark ->
                            appForceDarkMode = isDark
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            content = content
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun MinimalDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
