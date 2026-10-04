package com.lumatech.timeleaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserStats {
    var completedSessions by mutableStateOf(PersistentStorage.getInt("completed_sessions", 0))
    var totalFocusMinutes by mutableStateOf(PersistentStorage.getInt("total_focus_minutes", 0))
    var streakDays by mutableStateOf(PersistentStorage.getInt("streak_days", 1))
    var focusScore by mutableStateOf(PersistentStorage.getInt("focus_score", 50))

    // Settings flags
    var pushNotifications by mutableStateOf(PersistentStorage.getInt("push_notif", 1) == 1)
    var soundEnabled by mutableStateOf(PersistentStorage.getInt("sound_enabled", 1) == 1)
    var vibrationEnabled by mutableStateOf(PersistentStorage.getInt("vibration_enabled", 1) == 1)
    var autoBreak by mutableStateOf(PersistentStorage.getInt("auto_break", 0) == 1)
    var keepScreenAwake by mutableStateOf(PersistentStorage.getInt("keep_awake", 1) == 1)

    // Action tracking flags
    var hasStartedTimer by mutableStateOf(PersistentStorage.getInt("has_started_timer", 0) == 1)
    var hasCreatedCustomSession by mutableStateOf(PersistentStorage.getInt("has_created_custom", 0) == 1)
    var hasVisitedAuthors by mutableStateOf(PersistentStorage.getInt("has_visited_authors", 0) == 1)

    var latestUnlockedPopup by mutableStateOf<String?>(null)

    val weeklyActivity = mutableStateListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f).apply {
        if (completedSessions > 0) {
            set(3, 0.4f)
        }
    }

    val unlockedAchievements = mutableStateListOf<String>().apply {
        val saved = PersistentStorage.getString("unlocked_achievements", "")
        if (saved.isNotEmpty()) {
            addAll(saved.split(",").filter { it.isNotBlank() })
        }
    }

    init {
        val langCode = PersistentStorage.getString("app_language", "pl")
        currentAppLanguage = if (langCode == "en") AppLanguage.EN else AppLanguage.PL

        val themeKey = PersistentStorage.getString("theme_mode", "system")
        currentThemeMode = when (themeKey) {
            "light" -> AppThemeMode.LIGHT
            "dark" -> AppThemeMode.DARK
            else -> AppThemeMode.SYSTEM
        }
        checkAchievements(false)
    }

    private fun save() {
        PersistentStorage.setInt("completed_sessions", completedSessions)
        PersistentStorage.setInt("total_focus_minutes", totalFocusMinutes)
        PersistentStorage.setInt("streak_days", streakDays)
        PersistentStorage.setInt("focus_score", focusScore)
        PersistentStorage.setInt("push_notif", if (pushNotifications) 1 else 0)
        PersistentStorage.setInt("sound_enabled", if (soundEnabled) 1 else 0)
        PersistentStorage.setInt("vibration_enabled", if (vibrationEnabled) 1 else 0)
        PersistentStorage.setInt("auto_break", if (autoBreak) 1 else 0)
        PersistentStorage.setInt("keep_awake", if (keepScreenAwake) 1 else 0)
        PersistentStorage.setInt("has_started_timer", if (hasStartedTimer) 1 else 0)
        PersistentStorage.setInt("has_created_custom", if (hasCreatedCustomSession) 1 else 0)
        PersistentStorage.setInt("has_visited_authors", if (hasVisitedAuthors) 1 else 0)
        PersistentStorage.setString("app_language", currentAppLanguage.code)
        PersistentStorage.setString("theme_mode", currentThemeMode.key)
        PersistentStorage.setString("unlocked_achievements", unlockedAchievements.joinToString(","))
    }

    fun checkAchievements(notify: Boolean = true) {
        val list = listOf(
            Pair("first_step", hasStartedTimer),
            Pair("session_creator", hasCreatedCustomSession),
            Pair("explorer", hasVisitedAuthors),
            Pair("marathoner", totalFocusMinutes >= 300),
            Pair("focus_master", focusScore > 90),
            Pair("flow_master", completedSessions >= 3)
        )

        for ((id, condition) in list) {
            if (condition && !unlockedAchievements.contains(id)) {
                unlockedAchievements.add(id)
                if (notify) {
                    latestUnlockedPopup = getAchievementTitle(id)
                }
            }
        }
        save()
    }

    private fun getAchievementTitle(id: String): String {
        return when (id) {
            "first_step" -> if (currentAppLanguage == AppLanguage.PL) "Pierwszy krok" else "First Step"
            "session_creator" -> if (currentAppLanguage == AppLanguage.PL) "Twórca sesji" else "Session Creator"
            "explorer" -> if (currentAppLanguage == AppLanguage.PL) "Badacz" else "Explorer"
            "marathoner" -> if (currentAppLanguage == AppLanguage.PL) "Maratończyk" else "Marathoner"
            "focus_master" -> if (currentAppLanguage == AppLanguage.PL) "Mistrz Skupienia" else "Focus Master"
            "flow_master" -> if (currentAppLanguage == AppLanguage.PL) "Mistrz Flow" else "Flow Master"
            else -> "Osiągnięcie"
        }
    }

    fun recordStartTimer() {
        hasStartedTimer = true
        checkAchievements()
    }

    fun recordCreateCustomSession() {
        hasCreatedCustomSession = true
        checkAchievements()
    }

    fun recordVisitAuthors() {
        hasVisitedAuthors = true
        checkAchievements()
    }

    fun recordCompletedSession(minutes: Int) {
        completedSessions += 1
        totalFocusMinutes += minutes
        focusScore = (50 + (completedSessions * 8)).coerceAtMost(98)
        streakDays = (1 + (completedSessions / 3)).coerceAtLeast(1)

        val dayIndex = 3
        if (dayIndex in 0..6) {
            weeklyActivity[dayIndex] = (weeklyActivity[dayIndex] + 0.2f).coerceAtMost(1f)
        }

        checkAchievements()
    }

    fun resetAll() {
        completedSessions = 0
        totalFocusMinutes = 0
        streakDays = 1
        focusScore = 50
        weeklyActivity.fill(0f)
        unlockedAchievements.clear()
        hasStartedTimer = false
        hasCreatedCustomSession = false
        hasVisitedAuthors = false
        latestUnlockedPopup = null
        save()
    }

    fun updateSettings(
        lang: AppLanguage,
        theme: AppThemeMode,
        push: Boolean,
        sound: Boolean,
        vib: Boolean,
        autoB: Boolean,
        keep: Boolean
    ) {
        currentAppLanguage = lang
        currentThemeMode = theme
        pushNotifications = push
        soundEnabled = sound
        vibrationEnabled = vib
        autoBreak = autoB
        keepScreenAwake = keep
        save()
    }
}
