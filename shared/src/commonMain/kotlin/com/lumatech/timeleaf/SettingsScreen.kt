package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import timeleaf.shared.generated.resources.Res
import timeleaf.shared.generated.resources.baseline_arrow_back_ios_new_24
import timeleaf.shared.generated.resources.baseline_arrow_forward_ios_24
import timeleaf.shared.generated.resources.baseline_bedtime_24
import timeleaf.shared.generated.resources.baseline_check_24
import timeleaf.shared.generated.resources.baseline_delete_24
import timeleaf.shared.generated.resources.baseline_group_24
import timeleaf.shared.generated.resources.baseline_help_24
import timeleaf.shared.generated.resources.baseline_notifications_off_24
import timeleaf.shared.generated.resources.baseline_open_in_new_24
import timeleaf.shared.generated.resources.baseline_person_24
import timeleaf.shared.generated.resources.baseline_star_24
import timeleaf.shared.generated.resources.baseline_timer_24
import timeleaf.shared.generated.resources.baseline_translate_24
import timeleaf.shared.generated.resources.baseline_volume_up_24

enum class SettingsSubScreen {
    AUTHORS, HELP
}

@Composable
@Preview
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    var notificationsEnabled by remember { mutableStateOf(UserStats.pushNotifications) }
    var soundEnabled by remember { mutableStateOf(UserStats.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(UserStats.vibrationEnabled) }
    var keepScreenAwake by remember { mutableStateOf(UserStats.keepScreenAwake) }

    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf<SettingsSubScreen?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletionStage by remember { mutableStateOf(0) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val uriHandler = LocalUriHandler.current

    LaunchedEffect(deletionStage) {
        when (deletionStage) {
            1 -> {
                delay(1200)
                sharedCustomTiles.clear()
                UserStats.resetAll()
                deletionStage = 2
            }
            2 -> {
                delay(1200)
                showDeleteDialog = false
                delay(300)
                deletionStage = 0
            }
        }
    }

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
                                                                        UserStats.updateSettings(
                                                                            currentAppLanguage, mode,
                                                                            notificationsEnabled, soundEnabled,
                                                                            vibrationEnabled, false, keepScreenAwake
                                                                        )
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

                                    item {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            val currentLangDisplayName = currentAppLanguage.displayName

                                            SettingsDropdownItem(
                                                iconRes = Res.drawable.baseline_translate_24,
                                                title = LocalizedStrings.settingsLanguageLabel,
                                                selectedText = currentLangDisplayName,
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
                                                                        UserStats.updateSettings(
                                                                            lang, currentThemeMode,
                                                                            notificationsEnabled, soundEnabled,
                                                                            vibrationEnabled, false, keepScreenAwake
                                                                        )
                                                                        languageExpanded = false
                                                                    }
                                                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = lang.displayName,
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
                                            onCheckedChange = {
                                                soundEnabled = it
                                                UserStats.updateSettings(
                                                    currentAppLanguage, currentThemeMode,
                                                    notificationsEnabled, soundEnabled,
                                                    vibrationEnabled, false, keepScreenAwake
                                                )
                                            }
                                        )
                                    }

                                    item {
                                        SettingsSwitchItem(
                                            iconRes = Res.drawable.baseline_notifications_off_24,
                                            title = LocalizedStrings.settingsPushTitle,
                                            checked = notificationsEnabled,
                                            onCheckedChange = {
                                                notificationsEnabled = it
                                                UserStats.updateSettings(
                                                    currentAppLanguage, currentThemeMode,
                                                    notificationsEnabled, soundEnabled,
                                                    vibrationEnabled, false, keepScreenAwake
                                                )
                                            }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        SettingsSectionHeader(LocalizedStrings.settingsSectionBehavior)
                                    }

                                    item {
                                        SettingsSwitchItem(
                                            iconRes = Res.drawable.baseline_bedtime_24,
                                            title = LocalizedStrings.settingsKeepScreenTitle,
                                            checked = keepScreenAwake,
                                            onCheckedChange = {
                                                keepScreenAwake = it
                                                UserStats.updateSettings(
                                                    currentAppLanguage, currentThemeMode,
                                                    notificationsEnabled, soundEnabled,
                                                    vibrationEnabled, false, keepScreenAwake
                                                )
                                            }
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
                                            iconRes = Res.drawable.baseline_help_24,
                                            title = LocalizedStrings.settingsHelpTitle,
                                            onClick = { currentSubScreen = SettingsSubScreen.HELP }
                                        )
                                    }

                                    item {
                                        SettingsActionItem(
                                            iconRes = Res.drawable.baseline_group_24,
                                            title = LocalizedStrings.settingsAuthorsTitle,
                                            onClick = {
                                                UserStats.recordVisitAuthors()
                                                currentSubScreen = SettingsSubScreen.AUTHORS
                                            }
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
        Dialog(onDismissRequest = { if (deletionStage == 0) showDeleteDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .animateContentSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = deletionStage == 0,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_delete_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = LocalizedStrings.settingsDeleteDialogTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = LocalizedStrings.settingsDeleteDialogDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { showDeleteDialog = false },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
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
                                    onClick = { deletionStage = 1 },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
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

                    androidx.compose.animation.AnimatedVisibility(
                        visible = deletionStage > 0,
                        enter = fadeIn(tween(300, delayMillis = 150)),
                        exit = fadeOut(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedContent(
                                targetState = deletionStage == 2,
                                transitionSpec = {
                                    (scaleIn(tween(400)) + fadeIn(tween(300))) togetherWith
                                            (scaleOut(tween(300)) + fadeOut(tween(300)))
                                },
                                label = "iconTransition"
                            ) { isDone ->
                                if (!isDone) {
                                    Box(
                                        modifier = Modifier.size(72.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.fillMaxSize(),
                                            color = MaterialTheme.colorScheme.error,
                                            strokeWidth = 4.dp
                                        )
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_delete_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                painter = painterResource(Res.drawable.baseline_check_24),
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedContent(
                                targetState = deletionStage == 2,
                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                label = "textTransition"
                            ) { isDone ->
                                Text(
                                    text = if (isDone) {
                                        if (currentAppLanguage == AppLanguage.PL) "Usunięto!" else "Deleted!"
                                    } else {
                                        if (currentAppLanguage == AppLanguage.PL) "Usuwanie danych..." else "Deleting data..."
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                )
                            }
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