package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24
import timeleaf.shared.generated.resources.baseline_arrow_forward_ios_24
import timeleaf.shared.generated.resources.baseline_bedtime_24
import timeleaf.shared.generated.resources.baseline_delete_24
import timeleaf.shared.generated.resources.baseline_group_24
import timeleaf.shared.generated.resources.baseline_help_24
import timeleaf.shared.generated.resources.baseline_notifications_off_24
import timeleaf.shared.generated.resources.baseline_open_in_new_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_translate_24
import timeleaf.shared.generated.resources.baseline_volume_up_24

enum class SettingsSubScreen {
    AUTHORS, HELP
}

@Composable
@Preview
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf<SettingsSubScreen?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val uriHandler = LocalUriHandler.current

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2500)
            toastMessage = null
        }
    }

    if (currentSubScreen != null) {
        LocalBackHandler { currentSubScreen = null }
    } else {
        LocalBackHandler { onBackClick() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentSubScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "SettingsNav"
        ) { subScreen ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (subScreen) {
                    SettingsSubScreen.AUTHORS -> AuthorsScreen(onBackClick = { currentSubScreen = null })
                    SettingsSubScreen.HELP -> HelpScreen(onBackClick = { currentSubScreen = null })
                    null -> {
                        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .windowInsetsPadding(WindowInsets.statusBars)
                                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = onBackClick,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                                            contentDescription = LocalizedStrings.btnBack,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(16.dp).offset(x = (-1).dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = LocalizedStrings.settingsTitle,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Spacer(modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.size(36.dp))
                                }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    item {
                                        SettingsSectionHeader(LocalizedStrings.settingsSectionAppearance)
                                    }

                                    // Theme Mode Dropdown Item
                                    item {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            val currentThemeText = when (currentThemeMode) {
                                                AppThemeMode.SYSTEM -> LocalizedStrings.themeSystem
                                                AppThemeMode.LIGHT -> LocalizedStrings.themeLight
                                                AppThemeMode.DARK -> LocalizedStrings.themeDark
                                            }

                                            SettingsDropdownItem(
                                                iconRes = Res.drawable.baseline_bedtime_24,
                                                title = LocalizedStrings.settingsThemeModeLabel,
                                                selectedText = currentThemeText,
                                                onClick = { themeExpanded = !themeExpanded },
                                                isExpanded = themeExpanded
                                            )

                                            AnimatedVisibility(visible = themeExpanded) {
                                                Surface(
                                                    shape = RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 0.dp,
                                                        bottomStart = 20.dp,
                                                        bottomEnd = 20.dp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column {
                                                        AppThemeMode.entries.forEach { mode ->
                                                            val modeName = when (mode) {
                                                                AppThemeMode.SYSTEM -> LocalizedStrings.themeSystem
                                                                AppThemeMode.LIGHT -> LocalizedStrings.themeLight
                                                                AppThemeMode.DARK -> LocalizedStrings.themeDark
                                                            }
                                                            val isSelected = mode == currentThemeMode

                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(
                                                                        if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                                                        else Color.Transparent
                                                                    )
                                                                    .clickable {
                                                                        currentThemeMode = mode
                                                                        themeExpanded = false
                                                                    }
                                                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = modeName,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                if (isSelected) {
                                                                    Text(
                                                                        text = "✓",
                                                                        fontSize = 16.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = MaterialTheme.colorScheme.primary
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Language Dropdown Item
                                    item {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            val currentShortLang = if (currentAppLanguage.name.contains("POL", ignoreCase = true) ||
                                                currentAppLanguage.name.contains("PL", ignoreCase = true)) "PL" else "ENG"

                                            SettingsDropdownItem(
                                                iconRes = Res.drawable.baseline_translate_24,
                                                title = LocalizedStrings.settingsLanguageLabel,
                                                selectedText = currentShortLang,
                                                onClick = { languageExpanded = !languageExpanded },
                                                isExpanded = languageExpanded
                                            )

                                            AnimatedVisibility(visible = languageExpanded) {
                                                Surface(
                                                    shape = RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 0.dp,
                                                        bottomStart = 20.dp,
                                                        bottomEnd = 20.dp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column {
                                                        AppLanguage.entries.forEach { lang ->
                                                            val langFullName = when {
                                                                lang.name.contains("POL", ignoreCase = true) || lang.name.contains("PL", ignoreCase = true) -> "POLSKI"
                                                                lang.name.contains("ENG", ignoreCase = true) -> "ENGLISH"
                                                                else -> lang.displayName.uppercase()
                                                            }

                                                            val isSelected = lang == currentAppLanguage

                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(
                                                                        if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                                                        else Color.Transparent
                                                                    )
                                                                    .clickable {
                                                                        currentAppLanguage = lang
                                                                        languageExpanded = false
                                                                    }
                                                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = langFullName,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                if (isSelected) {
                                                                    Text(
                                                                        text = "✓",
                                                                        fontSize = 16.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = MaterialTheme.colorScheme.primary
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(12.dp))
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
                                        Spacer(modifier = Modifier.height(12.dp))
                                        SettingsSectionHeader(LocalizedStrings.settingsSectionAbout)
                                    }

                                    item {
                                        SettingsActionItem(
                                            iconRes = Res.drawable.baseline_star_24,
                                            title = LocalizedStrings.settingsRateApp,
                                            trailingIconRes = Res.drawable.baseline_open_in_new_24,
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
                                            iconRes = Res.drawable.baseline_group_24,
                                            title = LocalizedStrings.settingsAuthorsTitle,
                                            onClick = { currentSubScreen = SettingsSubScreen.AUTHORS }
                                        )
                                    }

                                    item {
                                        SettingsActionItem(
                                            iconRes = Res.drawable.baseline_help_24,
                                            title = LocalizedStrings.settingsHelpTitle,
                                            onClick = { currentSubScreen = SettingsSubScreen.HELP }
                                        )
                                    }

                                    item {
                                        SettingsActionItem(
                                            iconRes = Res.drawable.baseline_person_24,
                                            title = LocalizedStrings.profileAboutTitle,
                                            onClick = { showAboutDialog = true }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        SettingsSectionHeader("Dane aplikacyjne")
                                    }

                                    item {
                                        SettingsActionItem(
                                            iconRes = Res.drawable.baseline_delete_24,
                                            title = LocalizedStrings.settingsDeleteDataTitle,
                                            onClick = { showDeleteDialog = true }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- TOAST / SNACKBAR MESSAGE ---
    AnimatedVisibility(
        visible = toastMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .zIndex(10f)
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 16.dp),
        content = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shadowElevation = 6.dp
                ) {
                    Text(
                        text = toastMessage ?: "",
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.padding(horizontal = 26.dp, vertical = 14.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )

    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.widthIn(max = 360.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_person_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "TimeLeaf",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = LocalizedStrings.aboutDialogVersion,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    Text(
                        text = LocalizedStrings.aboutDialogDesc,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { showAboutDialog = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = LocalizedStrings.btnClose,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalizedStrings.settingsDeleteDialogTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = LocalizedStrings.settingsDeleteDialogDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = LocalizedStrings.btnNo,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Button(
                            onClick = {
                                sharedCustomTiles.clear()
                                showDeleteDialog = false
                                toastMessage = LocalizedStrings.snackDataCleared
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(
                                text = LocalizedStrings.btnYes,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
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
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
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
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = if (isExpanded) 0.dp else 20.dp,
            bottomEnd = if (isExpanded) 0.dp else 20.dp
        ),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    fontSize = 12.sp,
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
    trailingIconRes: DrawableResource = Res.drawable.baseline_arrow_forward_ios_24,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                painter = painterResource(trailingIconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
