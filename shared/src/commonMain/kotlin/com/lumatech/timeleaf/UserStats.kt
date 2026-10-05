package com.lumatech.timeleaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class LevelInfo(
    val levelNumber: Int,
    val titlePl: String,
    val titleEn: String,
    val minExp: Int,
    val maxExp: Int
) {
    fun title(lang: AppLanguage): String = if (lang == AppLanguage.PL) titlePl else titleEn
}

data class UserLevelState(
    val currentLevel: LevelInfo,
    val nextLevel: LevelInfo?,
    val totalExp: Int,
    val expInCurrentLevel: Int,
    val expNeededForNextLevel: Int,
    val progressInLevel: Float
)

val allLevels = listOf(
    LevelInfo(1, "Świeżak", "Rookie", 0, 200),
    LevelInfo(2, "Początkujący", "Novice", 200, 500),
    LevelInfo(3, "Amator", "Amateur", 500, 1000),
    LevelInfo(4, "Praktyk", "Practitioner", 1000, 2000),
    LevelInfo(5, "Specjalista", "Specialist", 2000, 3500),
    LevelInfo(6, "Ekspert", "Expert", 3500, 5500),
    LevelInfo(7, "Mistrz", "Master", 5500, 8500),
    LevelInfo(8, "Wirtuoz", "Virtuoso", 8500, 12000),
    LevelInfo(9, "Guru Skupienia", "Focus Guru", 12000, Int.MAX_VALUE)
)

object UserStats {
    var completedSessions by mutableStateOf(PersistentStorage.getInt("completed_sessions", 0))
    var totalFocusMinutes by mutableStateOf(PersistentStorage.getInt("total_focus_minutes", 0))
    var todayFocusMinutes by mutableStateOf(PersistentStorage.getInt("today_focus_minutes", 0))
    var todayCompletedSessions by mutableStateOf(PersistentStorage.getInt("today_completed_sessions", 0))
    var yesterdayFocusMinutes by mutableStateOf(PersistentStorage.getInt("yesterday_focus_minutes", 0))
    var dailyGoal by mutableStateOf(PersistentStorage.getInt("daily_goal", 5))

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
        val saved = PersistentStorage.getString("weekly_activity", "")
        if (saved.isNotBlank()) {
            val parts = saved.split(",")
            if (parts.size == 7) {
                for (i in 0..6) {
                    set(i, parts[i].toFloatOrNull() ?: 0f)
                }
            }
        }
    }

    val unlockedAchievements = mutableStateListOf<String>().apply {
        val saved = PersistentStorage.getString("unlocked_achievements", "")
        if (saved.isNotEmpty()) {
            addAll(saved.split(",").filter { it.isNotBlank() })
        }
    }

    val totalExp: Int
        get() = (totalFocusMinutes * 10) + (unlockedAchievements.size * 100)

    val currentLevelState: UserLevelState
        get() {
            val exp = totalExp
            val current = allLevels.lastOrNull { exp >= it.minExp } ?: allLevels.first()
            val next = allLevels.firstOrNull { it.levelNumber == current.levelNumber + 1 }

            val expInCurrent = exp - current.minExp
            val expSpan = if (next != null) (next.minExp - current.minExp) else 1
            val expNeeded = if (next != null) (next.minExp - exp).coerceAtLeast(0) else 0
            val progress = if (next != null) (expInCurrent.toFloat() / expSpan.toFloat()).coerceIn(0f, 1f) else 1f

            return UserLevelState(
                currentLevel = current,
                nextLevel = next,
                totalExp = exp,
                expInCurrentLevel = expInCurrent,
                expNeededForNextLevel = expNeeded,
                progressInLevel = progress
            )
        }

    val comparisonText: String?
        get() {
            if (completedSessions == 0 || todayFocusMinutes == 0) return null
            return if (yesterdayFocusMinutes == 0) {
                if (currentAppLanguage == AppLanguage.PL) "+100% od wczoraj" else "+100% vs yesterday"
            } else {
                val diff = ((todayFocusMinutes - yesterdayFocusMinutes) * 100) / yesterdayFocusMinutes
                val sign = if (diff >= 0) "+" else ""
                if (currentAppLanguage == AppLanguage.PL) "$sign$diff% od wczoraj" else "$sign$diff% vs yesterday"
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
        checkDayRollover()
        checkAchievements(notify = false)
    }

    fun checkDayRollover() {
        val currentDay = getCurrentDayOfWeekIndex()
        val savedDay = PersistentStorage.getInt("last_active_day", -1)
        if (savedDay != -1 && savedDay != currentDay) {
            yesterdayFocusMinutes = todayFocusMinutes
            todayFocusMinutes = 0
            todayCompletedSessions = 0
        }
        PersistentStorage.setInt("last_active_day", currentDay)
    }

    private fun save() {
        PersistentStorage.setInt("completed_sessions", completedSessions)
        PersistentStorage.setInt("total_focus_minutes", totalFocusMinutes)
        PersistentStorage.setInt("today_focus_minutes", todayFocusMinutes)
        PersistentStorage.setInt("today_completed_sessions", todayCompletedSessions)
        PersistentStorage.setInt("yesterday_focus_minutes", yesterdayFocusMinutes)
        PersistentStorage.setInt("daily_goal", dailyGoal)
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
        PersistentStorage.setString("weekly_activity", weeklyActivity.joinToString(","))
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
        checkDayRollover()
        val currentDay = getCurrentDayOfWeekIndex()

        completedSessions += 1
        todayCompletedSessions += 1
        totalFocusMinutes += minutes
        todayFocusMinutes += minutes

        focusScore = (focusScore + 5).coerceIn(0, 100)
        streakDays = (1 + (completedSessions / 3)).coerceAtLeast(1)

        val boost = (minutes / 25f).coerceAtLeast(0.2f)
        weeklyActivity[currentDay] = (weeklyActivity[currentDay] + boost).coerceAtMost(1f)

        checkAchievements()
        save()
    }

    fun resetAll() {
        completedSessions = 0
        totalFocusMinutes = 0
        todayFocusMinutes = 0
        todayCompletedSessions = 0
        yesterdayFocusMinutes = 0
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
