package com.lumatech.timeleaf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24
import timeleaf.shared.generated.resources.baseline_bedtime_24
import timeleaf.shared.generated.resources.baseline_notifications_off_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_translate_24
import timeleaf.shared.generated.resources.baseline_volume_up_24

@Composable
@Preview
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var languageExpanded by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = LocalizedStrings.btnBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp).offset(x = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = LocalizedStrings.settingsTitle,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(52.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSectionHeader(LocalizedStrings.settingsSectionAppearance)
            }

            item {
                SettingsSwitchItem(
                    iconRes = Res.drawable.baseline_bedtime_24,
                    title = LocalizedStrings.settingsDarkModeTitle,
                    checked = appForceDarkMode == true,
                    onCheckedChange = { isDark -> appForceDarkMode = isDark }
                )
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val currentShortLang = if (currentAppLanguage.name.contains("POL", ignoreCase = true)) "PL" else "ENG"

                    SettingsDropdownItem(
                        iconRes = Res.drawable.baseline_translate_24,
                        title = LocalizedStrings.settingsLanguageLabel,
                        selectedText = currentShortLang,
                        onClick = { languageExpanded = !languageExpanded },
                        isExpanded = languageExpanded
                    )

                    AnimatedVisibility(visible = languageExpanded) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                AppLanguage.entries.forEach { lang ->
                                    // Rozwiązanie problemu z podwójnym "ENGLISH"
                                    val langFullName = when {
                                        lang.name.contains("POL", ignoreCase = true) -> "POLSKI"
                                        lang.name.contains("ENG", ignoreCase = true) -> "ENGLISH"
                                        else -> lang.displayName.uppercase()
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                currentAppLanguage = lang
                                                languageExpanded = false
                                            }
                                            .padding(horizontal = 28.dp, vertical = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = langFullName,
                                            fontSize = 22.sp,
                                            fontWeight = if (lang == currentAppLanguage) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(LocalizedStrings.settingsSectionNotif)
            }

            item {
                SettingsSwitchItem(
                    iconRes = Res.drawable.baseline_volume_up_24,
                    title = LocalizedStrings.settingsSoundTitle,
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
            }

            item {
                SettingsSwitchItem(
                    iconRes = Res.drawable.baseline_notifications_off_24,
                    title = LocalizedStrings.settingsPushTitle,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("O aplikacji i Pomoc")
            }

            item {
                SettingsActionItem(
                    iconRes = Res.drawable.baseline_star_24,
                    title = "Oceń naszą aplikację",
                    trailingText = "↗",
                    onClick = {
                        try {
                            uriHandler.openUri("market://details?id=com.lumatech.timeleaf")
                        } catch (e: Exception) {
                            uriHandler.openUri("https://play.google.com/store/apps/details?id=com.lumatech.timeleaf")
                        }
                    }
                )
            }

            item {
                SettingsActionItem(
                    iconRes = Res.drawable.baseline_person_24,
                    title = LocalizedStrings.profileAboutTitle,
                    trailingText = "v1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }
        }
    }

    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White, // Sztywne białe tło zamiast surface
                tonalElevation = 8.dp,
                modifier = Modifier.widthIn(max = 360.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(76.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_person_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "TimeLeaf",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Text(
                        text = "Wersja 1.0.0",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                    )

                    Text(
                        text = "Aplikacja stworzona przez Lumatech. Pomagamy Ci zarządzać czasem i skupieniem podczas codziennych zadań.",
                        fontSize = 18.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = { showAboutDialog = false },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Zamknij",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    iconRes: DrawableResource,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // Szary tint ikon
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
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
}

@Composable
private fun SettingsDropdownItem(
    iconRes: DrawableResource,
    title: String,
    selectedText: String,
    onClick: () -> Unit,
    isExpanded: Boolean
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // Szary tint ikon
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SettingsActionItem(
    iconRes: DrawableResource,
    title: String,
    trailingText: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // Szary tint ikon
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = trailingText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}