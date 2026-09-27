# TimeLeaf

TimeLeaf to minimalistyczna aplikacja mobilna na Androida i iOS, której głównym zadaniem jest wspieranie pracy w skupieniu poprzez sesje czasowe. Aplikacja łączy prosty timer z systemem wizualnej progresji, w którym ukończone sesje przekładają się na rozwój wirtualnego ogrodu.

Projekt został zbudowany z wykorzystaniem **Kotlin Multiplatform** oraz **Compose Multiplatform**, dzięki czemu większość logiki aplikacji i warstwy interfejsu jest współdzielona pomiędzy platformami.

## O projekcie

TimeLeaf został zaprojektowany jako aplikacja **offline-first**. Dane dotyczące sesji są przechowywane lokalnie na urządzeniu, bez konieczności tworzenia konta lub korzystania z zewnętrznego backendu.

Jednym z ważniejszych elementów projektu jest obsługa timera. Zamiast wykonywać operację odliczania w sposób ciągły, aplikacja zapisuje punkt odniesienia w postaci znacznika czasu (`timestamp`). Dzięki temu po przejściu aplikacji do tła lub jej ponownym otwarciu możliwe jest wyliczenie rzeczywistego czasu, który upłynął. Rozwiązanie ogranicza zużycie zasobów i pozwala zachować poprawność timera niezależnie od cyklu życia aplikacji.

Projekt jest również przykładem zastosowania współdzielonej architektury w aplikacji wieloplatformowej, z wyraźnym podziałem odpowiedzialności pomiędzy warstwę domenową, dane oraz prezentację.

## Funkcjonalności

* **Focus Timer**
  Timer przeznaczony do realizacji sesji pracy w skupieniu. Stan sesji jest oparty na znacznikach czasu, dzięki czemu aplikacja poprawnie obsługuje przejście do tła.

* **Forest Grid**
  Ukończone sesje wpływają na rozwój wirtualnego ogrodu. Widok siatki prezentuje rośliny i drzewa odpowiadające postępom użytkownika.

* **Historia sesji**
  Dane ukończonych sesji są zapisywane lokalnie i mogą być wykorzystywane do prezentowania statystyk oraz postępów.

* **Offline-first**
  Aplikacja nie wymaga logowania ani połączenia z zewnętrznym serwerem. Dane pozostają na urządzeniu użytkownika.

* **Współdzielony kod Android/iOS**
  Logika biznesowa, dostęp do danych oraz większość interfejsu jest współdzielona pomiędzy platformami za pomocą Kotlin Multiplatform i Compose Multiplatform.

* **Custom UI**
  Interfejs został przygotowany bez korzystania z gotowego szablonu aplikacji. Wykorzystuje m.in. własne komponenty Compose, rysowanie za pomocą `Canvas`, niestandardowe modyfikatory oraz obsługę interfejsu edge-to-edge.

## Technologie

| Obszar               | Technologie                       |
| -------------------- | --------------------------------- |
| Język                | Kotlin                            |
| Multiplatform        | Kotlin Multiplatform              |
| UI                   | Compose Multiplatform             |
| Architektura         | Clean Architecture, MVVM/MVI, UDF |
| Stan aplikacji       | Coroutines, StateFlow             |
| Data i czas          | kotlinx-datetime                  |
| Baza danych          | Room KMP                          |
| Code generation      | KSP                               |
| Dependency Injection | Koin                              |
| Platformy            | Android, iOS                      |

## Architektura

Projekt wykorzystuje podział na warstwy inspirowany **Clean Architecture**. Celem jest oddzielenie logiki biznesowej od szczegółów implementacyjnych platformy oraz infrastruktury.

### `domain/`

Warstwa domenowa zawierająca logikę biznesową aplikacji.

Znajdują się tutaj m.in.:

* modele domenowe, np. `FocusSession`,
* interfejsy repozytoriów,
* przypadki użycia (Use Cases),
* reguły związane z obsługą sesji i progresji.

Warstwa domenowa nie zależy bezpośrednio od Room, Compose ani innych elementów infrastruktury.

### `data/`

Warstwa odpowiedzialna za dostęp do danych.

Obejmuje m.in.:

* encje i DAO dla Room,
* implementacje repozytoriów,
* mapowanie modeli bazodanowych na modele domenowe,
* lokalne źródło danych aplikacji.

Repozytoria pełnią rolę pośrednika pomiędzy warstwą domenową a konkretną implementacją przechowywania danych.

### `presentation/`

Warstwa odpowiedzialna za interfejs użytkownika oraz zarządzanie jego stanem.

Zawiera m.in.:

* ViewModele,
* `StateFlow` reprezentujące stan UI,
* ekrany Compose,
* komponenty UI,
* motyw aplikacji,
* logikę prezentacyjną.

Stan przepływa jednokierunkowo od logiki aplikacji do interfejsu, zgodnie z założeniami **Unidirectional Data Flow**.

## Struktura projektu

Najważniejsze katalogi projektu:

```text
TimeLeaf/
├── androidApp/
│   └── src/
│
├── iosApp/
│   └── iosApp/
│
├── shared/
│   └── src/
│       ├── commonMain/
│       ├── androidMain/
│       └── iosMain/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

Kod współdzielony znajduje się przede wszystkim w module `shared`. Kod specyficzny dla danej platformy trafia do odpowiednich source setów, np. `androidMain` lub `iosMain`.

## Uruchomienie projektu

### Wymagania

* [Android Studio](https://developer.android.com/studio) lub IntelliJ IDEA
* JDK 17 lub nowsze
* Xcode, jeśli projekt ma być uruchamiany na iOS
* skonfigurowane środowisko Kotlin Multiplatform

### Klonowanie repozytorium

```bash
git clone https://github.com/twoj-profil/TimeLeaf.git
cd TimeLeaf
```

### Android

Projekt można uruchomić bezpośrednio z Android Studio przy użyciu konfiguracji dostępnej w IDE.

Możliwe jest również zbudowanie wersji debug:

```bash
./gradlew :androidApp:assembleDebug
```

### iOS

Otwórz projekt:

```text
iosApp/iosApp
```

w Xcode, wybierz odpowiedni symulator lub urządzenie i uruchom aplikację.

## Testy

Testy można uruchamiać bezpośrednio z poziomu IDE lub za pomocą Gradle.

### Android

```bash
./gradlew :shared:testAndroidHostTest
```

### iOS

```bash
./gradlew :shared:iosSimulatorArm64Test
```

## Status projektu

TimeLeaf jest projektem rozwijanym jako praktyczne wykorzystanie Kotlin Multiplatform i Compose Multiplatform. Głównym celem jest rozwój aplikacji oraz eksperymentowanie z architekturą współdzieloną pomiędzy Androidem i iOS.

---

# Kotlin Multiplatform

This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform, you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications. It contains several subfolders:

  * [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  * Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name. For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app, the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls. Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin) folder is the appropriate location.

## Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

* Android app: `./gradlew :androidApp:assembleDebug`
* iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

## Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

* Android tests: `./gradlew :shared:testAndroidHostTest`
* iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
