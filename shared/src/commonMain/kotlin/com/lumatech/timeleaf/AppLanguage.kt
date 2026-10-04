package com.lumatech.timeleaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    PL("pl", "Polski", "🇵🇱"),
    EN("en", "English", "🇬🇧")
}

var currentAppLanguage by mutableStateOf(AppLanguage.PL)

enum class AppThemeMode(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark")
}

var currentThemeMode by mutableStateOf(AppThemeMode.SYSTEM)

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
    val settingsThemeModeLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Tryb motywu" else "Theme mode"
    val themeSystem: String get() = if (currentAppLanguage == AppLanguage.PL) "Systemowy" else "System"
    val themeLight: String get() = if (currentAppLanguage == AppLanguage.PL) "Jasny" else "Light"
    val themeDark: String get() = if (currentAppLanguage == AppLanguage.PL) "Ciemny" else "Dark"

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
    val btnBack: String get() = if (currentAppLanguage == AppLanguage.PL) "Wróć" else "Back"
    val settingsSectionAbout: String get() = if (currentAppLanguage == AppLanguage.PL) "O aplikacji i Pomoc" else "About App & Help"
    val settingsRateApp: String get() = if (currentAppLanguage == AppLanguage.PL) "Oceń naszą aplikację" else "Rate our app"
    val aboutDialogVersion: String get() = if (currentAppLanguage == AppLanguage.PL) "Wersja 1.0.0" else "Version 1.0.0"
    val aboutDialogDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "Aplikacja stworzona przez Lumatech. Pomagamy Ci zarządzać czasem i skupieniem podczas codziennych zadań." else "Created by Lumatech. Helping you manage time and focus during daily tasks."
    val btnClose: String get() = if (currentAppLanguage == AppLanguage.PL) "Zamknij" else "Close"
    val settingsAuthorsTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Autorzy aplikacji" else "App Authors"
    val settingsDeleteDataTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Usuń wszystkie dane" else "Delete all data"
    val settingsDeleteDialogTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Usuń wszystkie dane?" else "Delete all data?"
    val settingsDeleteDialogDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "Ta operacja jest nieodwracalna. Czy na pewno chcesz usunąć wszystkie dane?" else "This operation is irreversible. Are you sure you want to delete all data?"
    val authorsDialogDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "TimeLeaf został stworzony z pasją przez zespół LumaTech, aby wspierać Twoją produktywność i głębokie skupienie (Deep Work)." else "TimeLeaf was crafted with passion by the LumaTech team to empower your productivity and deep work."
    val btnYes: String get() = if (currentAppLanguage == AppLanguage.PL) "Tak" else "Yes"
    val btnNo: String get() = if (currentAppLanguage == AppLanguage.PL) "Nie" else "No"
    val snackDataCleared: String get() = if (currentAppLanguage == AppLanguage.PL) "Wszystkie dane zostały usunięte 🗑️" else "All data cleared 🗑️"
    val settingsHelpTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Pomoc i instrukcja" else "Help & Guide"

    // Authors Screen Strings
    val authorsScreenTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Autorzy aplikacji" else "App Authors"
    val authorMainName: String get() = "Łukasz (Lumatech)"
    val authorRole: String get() = if (currentAppLanguage == AppLanguage.PL) "Główny Twórca & Lead Mobile Developer" else "Lead Creator & Mobile Developer"
    val authorBio: String get() = if (currentAppLanguage == AppLanguage.PL) "Pasjonat nowoczesnych technologii mobilnych, Jetpack Compose oraz tworzenia aplikacji wspierających głębokie skupienie (Deep Work) i produktywność." else "Passionate about modern mobile tech, Jetpack Compose, and creating apps that empower deep work and productivity."
    val authorLinkedinLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Profil LinkedIn" else "LinkedIn Profile"
    val authorEmail1: String get() = "kontakt@lumatech.com"
    val authorEmail2: String get() = "support@timeleaf.app"
    val authorEmailsLabel: String get() = if (currentAppLanguage == AppLanguage.PL) "Adresy e-mail kontaktowe" else "Contact Email Addresses"

    // Help & Guide Screen Strings
    val helpScreenTitle: String get() = if (currentAppLanguage == AppLanguage.PL) "Pomoc i instrukcja" else "Help & Guide"
    val helpTabFaq: String get() = if (currentAppLanguage == AppLanguage.PL) "FAQ & Instrukcja" else "FAQ & Guide"
    val helpTabContact: String get() = if (currentAppLanguage == AppLanguage.PL) "Kontakt" else "Contact"
    val helpTabBug: String get() = if (currentAppLanguage == AppLanguage.PL) "Zgłoś błąd" else "Report Bug"

    val contactSupportHeader: String get() = if (currentAppLanguage == AppLanguage.PL) "Kontakt z działem pomocy" else "Contact Support"
    val contactSupportDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "Masz pytania, sugestie lub potrzebujesz pomocy z aplikacją TimeLeaf? Skontaktuj się bezpośrednio z naszym zespołem." else "Have questions, suggestions, or need help with TimeLeaf? Reach out directly to our team."
    val contactSupportEmailBtn: String get() = if (currentAppLanguage == AppLanguage.PL) "Wyślij e-mail do wsparcia" else "Email Support Team"

    val reportBugHeader: String get() = if (currentAppLanguage == AppLanguage.PL) "Zgłoszenie błędu" else "Report an Issue"
    val reportBugDesc: String get() = if (currentAppLanguage == AppLanguage.PL) "Znalazłeś błąd lub aplikacja działa nieprawidłowo? Wyślij nam zgłoszenie wraz z opisem." else "Found a bug or experiencing unexpected behavior? Send us a bug report with details."
    val reportBugEmailBtn: String get() = if (currentAppLanguage == AppLanguage.PL) "Zgłoś błąd przez e-mail" else "Send Bug Report"

    // FAQ Items
    val faq1Q: String get() = if (currentAppLanguage == AppLanguage.PL) "Jak działa timer skupienia?" else "How does the focus timer work?"
    val faq1A: String get() = if (currentAppLanguage == AppLanguage.PL) "Wybierz preset lub własną sesję z zakładki Sesje, a następnie przejdź do Timera i dotknij okręgu, aby uruchomić odliczanie." else "Pick a preset or custom session from the Sessions tab, then go to the Timer tab and tap the circle to start countdown."

    val faq2Q: String get() = if (currentAppLanguage == AppLanguage.PL) "Jak usuwać własne sesje?" else "How to delete custom sessions?"
    val faq2A: String get() = if (currentAppLanguage == AppLanguage.PL) "Przy każdej stworzonej przez Ciebie sesji znajduje się ikonka kosza obok nazwy. Kliknij ją, aby usunąć wybraną sesję." else "Each custom session card has a trash icon next to its title. Click it to delete that specific session."

    val faq3Q: String get() = if (currentAppLanguage == AppLanguage.PL) "Jak zmienić motyw i język?" else "How to change theme and language?"
    val faq3A: String get() = if (currentAppLanguage == AppLanguage.PL) "Przejdź do Ustawień. Tam znajdziesz rozwijane menu wyboru języka (PL/EN) oraz trybu motywu (Systemowy, Jasny, Ciemny)." else "Go to Settings. There you will find expandable dropdown menus for Language (PL/EN) and Theme mode (System, Light, Dark)."

    val faq4Q: String get() = if (currentAppLanguage == AppLanguage.PL) "Czym jest tryb DND / skupienia?" else "What is Focus / DND mode?"
    val faq4A: String get() = if (currentAppLanguage == AppLanguage.PL) "Podczas aktywnego odliczania TimeLeaf pomaga Ci zachować głębokie skupienie, wyciszając powiadomienia i utrzymując ekran włączony (jeśli opcja jest włączona)." else "During active countdown, TimeLeaf helps you stay in deep focus by muting notifications and keeping the screen awake (if enabled)."

    // Snackbars
    val snackTimerStarted: String get() = if (currentAppLanguage == AppLanguage.PL) "Tryb skupienia włączony • Powiadomienia wyciszone 🔕" else "Focus mode active • Notifications muted 🔕"
    val snackTimerReset: String get() = if (currentAppLanguage == AppLanguage.PL) "Timer zresetowany ⏱️" else "Timer reset ⏱️"
    val snackPressAgainToExit: String get() = if (currentAppLanguage == AppLanguage.PL) "Naciśnij ponownie 'Wróć', aby wyjść z aplikacji" else "Press back again to exit"
}
