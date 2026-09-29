package com.lumatech.timeleaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    PL("pl", "Polski", "🇵🇱"),
    EN("en", "English", "🇬🇧")
}

var currentAppLanguage by mutableStateOf(AppLanguage.PL)

object LocalizedStrings {
    val navTiles: String get() = if (currentAppLanguage == AppLanguage.PL) "Sesje" else "Sessions"
    val navTimer: String get() = if (currentAppLanguage == AppLanguage.PL) "Timer" else "Timer"
    val navProfile: String get() = if (currentAppLanguage == AppLanguage.PL) "Profil" else "Profile"

    // TimeTilesScreen
    val tilesHeaderTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Szybkie Sesje" else "Quick Sessions"
    val tilesHeaderSubtitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Wybierz preset lub stwórz własny czas" else "Pick a preset or create custom focus time"
    val tilesCustomCardTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Utwórz własną sesję" else "Create custom session"
    val tilesCustomCardSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Skonfiguruj własną nazwę i czas" else "Configure custom title & duration"
    val dialogCustomTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Nowa sesja" else "New custom session"
    val dialogSessionNameLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Nazwa nowej sesji..." else "Session name..."
    val dialogDurationLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Czas trwania" else "Duration"
    val btnCancel: String get() = if (currentAppLanguage == AppLanguage.PL) "Anuluj" else "Cancel"
    val btnAdd: String get() = if (currentAppLanguage == AppLanguage.PL) "Dodaj" else "Add"
    val btnStart: String get() = if (currentAppLanguage == AppLanguage.PL) "Rozpocznij" else "Start"

    // Presets
    val presetQuickTaskTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Szybkie zadanie" else "Quick task"
    val presetQuickTaskDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "5 min skupienia" else "5 min focus"
    val presetPomodoroTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Pomodoro" else "Pomodoro"
    val presetPomodoroDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "25 min deep work" else "25 min deep work"
    val presetDeepSessionTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Głęboka sesja" else "Deep session"
    val presetDeepSessionDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "45 min flow" else "45 min flow"
    val presetHourTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Godzina" else "One Hour"
    val presetHourDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "60 min zadania" else "60 min task"

    // TimerScreen
    val timerAddTask: String get() = if (currentAppLanguage == AppLanguage.PL) "Dodaj nowego taska" else "Add a new task"
    val timerCurrentTask: String get() = if (currentAppLanguage == AppLanguage.PL) "Aktualne zadanie" else "Current task"
    val timerFocusTimeLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Czas skupienia" else "Focus time"
    val timerDndActive: String get() = if (currentAppLanguage == AppLanguage.PL) "DND aktywne" else "DND active"
    val timerBtnPause: String get() = if (currentAppLanguage == AppLanguage.PL) "Pauza" else "Pause"
    val timerBtnResume: String get() = if (currentAppLanguage == AppLanguage.PL) "Wznów" else "Resume"
    val timerBtnStop: String get() = if (currentAppLanguage == AppLanguage.PL) "Stop" else "Stop"
    val taskDialogTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Co dzisiaj robisz?" else "What are you working on?"
    val taskDialogInputLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Nazwa zadania..." else "Task name..."

    // ProfileScreen
    val profileTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Profil" else "Profile"
    val profileUserName: String get() = if (currentAppLanguage == AppLanguage.PL) "Mistrz Skupienia" else "Focus Master"
    val profileUserLevel: String get() = if (currentAppLanguage == AppLanguage.PL) "Poziom 3 • 1200 XP" else "Level 3 • 1200 XP"
    val profileCompletedSessions: String get() = if (currentAppLanguage == AppLanguage.PL) "Ukończone sesje" else "Completed sessions"
    val profileFocusTime: String get() = if (currentAppLanguage == AppLanguage.PL) "Czas skupienia" else "Focus time"
    val profileAchievementsTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Osiągnięcia" else "Achievements"
    val profileAchievementsSubtitle: String get() = if (currentAppLanguage == AppLanguage.PL) "2 z 4 odblokowane" else "2 of 4 unlocked"
    val profileAboutTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "O aplikacji TimeLeaf" else "About TimeLeaf"
    val profileAboutText: String get() = if (currentAppLanguage == AppLanguage.PL) "Nowoczesna, minimalistyczna aplikacja do zarządzania czasem skupienia." else "Modern, minimalist focus timer application."

    // Achievements Screen
    val achievementsHeaderTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Osiągnięcia" else "Achievements"
    val achievementsHeaderSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Zdobądź odznaki za konsekwencję w nauce i pracy" else "Earn badges for consistent focus sessions"
    val achievement1Title: String get() = if (currentAppLanguage == AppLanguage.PL) "Pierwszy krok" else "First Step"
    val achievement1Desc: String get() = if (currentAppLanguage == AppLanguage.PL) "Ukończ swoją pierwszą sesję skupienia" else "Complete your first focus session"
    val achievement2Title: String get() = if (currentAppLanguage == AppLanguage.PL) "Maratończyk" else "Marathoner"
    val achievement2Desc: String get() = if (currentAppLanguage == AppLanguage.PL) "Przepracuj łącznie 5 godzin" else "Work for 5 hours in total"
    val achievement3Title: String get() = if (currentAppLanguage == AppLanguage.PL) "Nocny Marek" else "Night Owl"
    val achievement3Desc: String get() = if (currentAppLanguage == AppLanguage.PL) "Ukończ sesję po godzinie 22:00" else "Complete a session after 10 PM"
    val achievement4Title: String get() = if (currentAppLanguage == AppLanguage.PL) "Mistrz Flow" else "Flow Master"
    val achievement4Desc: String get() = if (currentAppLanguage == AppLanguage.PL) "Zrealizuj 3 sesje w jeden dzień" else "Complete 3 sessions in one day"
    val achievementUnlocked: String get() = if (currentAppLanguage == AppLanguage.PL) "Odblokowane" else "Unlocked"
    val achievementLocked: String get() = if (currentAppLanguage == AppLanguage.PL) "Zablokowane" else "Locked"

    // SettingsScreen
    val settingsTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Ustawienia" else "Settings"
    val settingsSectionApp: String get() = if (currentAppLanguage == AppLanguage.PL) "Aplikacja i Język" else "App & Language"
    val settingsLanguageLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Język aplikacji" else "App language"
    val settingsLanguageSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Wybierz język interfejsu" else "Choose interface language"
    val settingsSectionNotif: String get() = if (currentAppLanguage == AppLanguage.PL) "Powiadomienia i Dźwięk" else "Notifications & Sound"
    val settingsPushTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Powiadomienia push" else "Push notifications"
    val settingsPushSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Alerty po zakończeniu sesji" else "Alerts on session completion"
    val settingsSoundTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Dźwięk alarmu" else "Alarm sound"
    val settingsSoundSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Sygnał dźwiękowy po odliczeniu" else "Audio signal on timer finish"
    val settingsVibTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Wibracje" else "Vibration"
    val settingsVibSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Sygnał haptyczny przy końcu" else "Haptic feedback on finish"
    val settingsSectionBehavior: String get() = if (currentAppLanguage == AppLanguage.PL) "Zachowanie Timera" else "Timer Behavior"
    val settingsAutoBreakTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Automatyczna przerwa" else "Auto break"
    val settingsAutoBreakSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Uruchamiaj przerwę automatycznie" else "Automatically start break"
    val settingsKeepScreenTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Nie wygaszaj ekranu" else "Keep screen awake"
    val settingsKeepScreenSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Ekran włączony podczas odliczania" else "Keep screen active during countdown"
    val settingsSectionAppearance: String get() = if (currentAppLanguage == AppLanguage.PL) "Wygląd" else "Appearance"
    val settingsDarkModeTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Ciemny motyw" else "Dark theme"
    val settingsDarkModeSub: String get() = if (currentAppLanguage == AppLanguage.PL) "Ręczne przełączanie motywu" else "Manual theme override"
    val btnBack: String get() = if (currentAppLanguage == AppLanguage.PL) "Wróć" else "Back"

    // Snackbars
    val snackTimerStarted: String get() = if (currentAppLanguage == AppLanguage.PL) "Tryb skupienia włączony • Powiadomienia wyciszone 🔕" else "Focus mode active • Notifications muted 🔕"
    val snackTimerReset: String get() = if (currentAppLanguage == AppLanguage.PL) "Timer zresetowany ⏱️" else "Timer reset ⏱️"
    val snackPressAgainToExit: String get() = if (currentAppLanguage == AppLanguage.PL) "Naciśnij ponownie 'Wróć', aby wyjść z aplikacji" else "Press back again to exit"
}
