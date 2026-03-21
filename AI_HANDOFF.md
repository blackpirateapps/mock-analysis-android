# Mock Analysis Android - AI Handoff Document

## Project Overview

**App Name:** Mock Analysis  
**Package:** `com.mockanalysis`  
**Platform:** Android (Native)  
**UI Framework:** Jetpack Compose  
**Architecture:** MVVM + Clean Architecture  
**Min SDK:** 26 (Android 8.0)  
**Target SDK:** 34 (Android 14)

### Purpose
An SSC CGL exam preparation app that provides detailed analytics and insights from mock test performances. The app helps aspirants track their progress, identify weak areas, and compare their performance against peers.

---

## Design System

### "The Cognitive Architect" Design Language
The design follows a high-end, minimalist aesthetic that transforms exam preparation into a focused, sanctuary-like experience.

#### Key Design Principles
1. **Editorial Scaling** - Large display typography for key stats
2. **Tonal Layering** - Depth through background color shifts (no structural borders)
3. **Glass-morphism** - Semi-transparent surfaces with blur effects
4. **Growth Radiance** - Progress bars with subtle glow effects

### Color Palette
| Color | Hex | Usage |
|-------|-----|-------|
| Primary (Exam Blue) | `#005BBF` | Trust, primary actions |
| Primary Container | `#1A73E8` | Gradients, containers |
| Secondary (Success Green) | `#1B6D24` | Growth, achievements |
| Tertiary (Alert Red) | `#B91A20` | Weak areas, alerts |
| Surface | `#F8F9FA` | Background |
| Surface Container Lowest | `#FFFFFF` | Elevated cards |
| Surface Container Low | `#F3F4F5` | Section backgrounds |

### Typography
- **Headlines:** Manrope (ExtraBold, Bold)
- **Body/Labels:** Inter (Normal, Medium, Bold)

---

## Architecture

```
app/src/main/java/com/mockanalysis/
├── di/                          # Hilt dependency injection
│   ├── AppModule.kt             # Coroutine dispatchers
│   └── RepositoryModule.kt      # Repository bindings
├── data/
│   ├── local/                   # Room database (SQLite)
│   │   ├── MockAnalysisDatabase.kt
│   │   ├── dao/
│   │   │   ├── AnalysisDao.kt
│   │   │   └── UserDao.kt
│   │   └── entity/
│   │       ├── MockAttemptEntity.kt
│   │       └── UserProfileEntity.kt
│   ├── repository/              # Repository implementations
│   │   ├── AnalysisRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   └── source/
│       └── MockDataSource.kt    # Sample data for testing
├── domain/
│   ├── model/                   # Domain entities
│   │   ├── MockAnalysis.kt      # Analysis, scores, metrics
│   │   └── UserProfile.kt       # Profile, settings, achievements
│   ├── repository/              # Repository interfaces
│   │   ├── AnalysisRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/                 # Business logic
│       ├── AnalysisUseCases.kt
│       └── UserUseCases.kt
├── presentation/
│   ├── common/components/       # Reusable UI components
│   │   ├── TopAppBar.kt
│   │   ├── BottomNavBar.kt
│   │   ├── Cards.kt
│   │   ├── ProgressBars.kt
│   │   ├── SettingsItems.kt
│   │   └── SubjectComponents.kt
│   ├── navigation/
│   │   └── NavHost.kt           # Navigation setup
│   ├── theme/
│   │   ├── Color.kt             # Color definitions
│   │   ├── Type.kt              # Typography
│   │   ├── Shape.kt             # Corner radii
│   │   └── Theme.kt             # Theme composition
│   ├── analysis/                # Analysis Hub screen
│   │   ├── AnalysisHubScreen.kt
│   │   ├── AnalysisHubViewModel.kt
│   │   └── AnalysisHubUiState.kt
│   ├── dashboard/               # Dashboard screen (v3)
│   │   ├── DashboardScreen.kt
│   │   ├── DashboardViewModel.kt
│   │   └── DashboardUiState.kt
│   ├── input/                   # Manual score entry screen
│   │   ├── ManualScoreEntryScreen.kt
│   │   ├── ManualScoreEntryViewModel.kt
│   │   └── ManualScoreEntryUiState.kt
│   └── profile/                 # User Profile screen
│       ├── ProfileScreen.kt
│       ├── ProfileViewModel.kt
│       └── ProfileUiState.kt
├── MainActivity.kt
└── MockAnalysisApp.kt           # Application class
```

---

## Implemented Screens

### 1. Analysis Hub (`/analysis`)
**File:** `presentation/analysis/AnalysisHubScreen.kt`

The main analytics dashboard showing:
- **Hero Section** - Gradient card with mock test name and premium badge
- **Score Card** - Large score display, percentile, rank, improvement tracking
- **Competitive Benchmarking** - Horizontal bar chart comparing user vs average vs topper
- **Subject Proficiency** - 4-subject grid with scores and progress bars
- **Next Milestone** - Focus area cards with practice CTA
- **Time vs Accuracy** - Scatter plot simulation

**ViewModel:** `AnalysisHubViewModel` observes `GetLatestAnalysisUseCase`

**Stitch Source (latest loaded):** `Analysis Dashboard v2` (`98de1efc44014fbebe0efcfd1e5cc8be`)

### 2. Dashboard (`/dashboard`)
**File:** `presentation/dashboard/DashboardScreen.kt`

Dashboard hub screen showing:
- **Mastery Hero** - progress narrative with CTA to input latest mock result
- **Current Average Card** - average score, target velocity, and delta vs previous mock
- **Performance Trend** - recent mock trend bars
- **Subject Proficiency** - compact per-subject progress stack
- **Focus/Strength/Time Insight Cards** - actionable guidance blocks

**ViewModel:** `DashboardViewModel` combines `GetAllAnalysesUseCase` + `GetUserProfileUseCase`

**Stitch Source (latest loaded):** `Analysis Dashboard v3` (`fe861c5442234eccaa06ea4be596cb5b`)

### 3. Manual Score Entry (`/input`)
**File:** `presentation/input/ManualScoreEntryScreen.kt`

Input screen for local mock entry showing:
- **Platform Selector** - Testbook/Oliveboard/Adda247/Other
- **Mock Type Selector** - Full Mock/Sectional/Module
- **Subject Input Blocks** - marks and time fields per subject
- **Total Score Preview** - live aggregation out of 200
- **Generate Analysis CTA** - validates and saves locally

**ViewModel:** `ManualScoreEntryViewModel` uses `SaveAnalysisUseCase`

**Stitch Source (latest loaded):** `Manual Score Entry v2` (`ca92407cc84c4c05bd343e7238b79542`)

### 4. User Profile (`/profile`)
**File:** `presentation/profile/ProfileScreen.kt`

Profile and settings screen showing:
- **Profile Header** - Large name, exam badge, tier badge, target score card
- **Mastery Milestones** - Achievement badges (unlocked/locked)
- **Platform Accounts** - Linked services (Testbook, Oliveboard)
- **Performance Settings** - Toggle switches for analysis preferences
- **Account Management** - Navigation items grid
- **Logout Button**

**ViewModel:** `ProfileViewModel` manages settings updates and logout

**Stitch Source (latest loaded):** `User Profile` (`1ea4f37fd9eb4788b0784cc9113e9d34`)

---

## Shared Components

| Component | File | Description |
|-----------|------|-------------|
| `MockAnalysisTopBar` | `TopAppBar.kt` | Glass-morphism header |
| `MockAnalysisBottomBar` | `BottomNavBar.kt` | 5-tab navigation |
| `GradientHeroCard` | `Cards.kt` | Primary gradient container |
| `ElevatedStatCard` | `Cards.kt` | White card with shadow |
| `ContainerCard` | `Cards.kt` | Low-emphasis container |
| `GrowthProgressBar` | `ProgressBars.kt` | Glowing progress bar |
| `BenchmarkBar` | `ProgressBars.kt` | Horizontal comparison bar |
| `ToggleSettingItem` | `SettingsItems.kt` | Setting with switch |
| `NavigationSettingItem` | `SettingsItems.kt` | Setting with chevron |
| `AchievementBadge` | `SettingsItems.kt` | Circular badge |
| `SubjectProficiencyCard` | `SubjectComponents.kt` | Score display |

---

## Data Models

### MockAnalysis
```kotlin
data class MockAnalysis(
    val id: String,
    val mockName: String,
    val score: Float,
    val percentile: Float,
    val rank: Int,
    val subjectScores: List<SubjectScore>,
    val benchmarks: Benchmarks,
    val focusAreas: List<FocusArea>,
    val questionMetrics: List<QuestionMetric>
)
```

### UserProfile
```kotlin
data class UserProfile(
    val id: String,
    val name: String,
    val targetExam: TargetExam,
    val tier: UserTier,
    val targetScore: Int,
    val achievements: List<Achievement>,
    val linkedPlatforms: List<LinkedPlatform>,
    val settings: ProfileSettings
)
```

---

## Dependencies

| Category | Library | Version |
|----------|---------|---------|
| UI | Jetpack Compose BOM | 2024.01.00 |
| UI | Material 3 | via BOM |
| Navigation | Compose Navigation | 2.7.6 |
| DI | Hilt | 2.50 |
| Async | Coroutines | 1.7.3 |
| Images | Coil | 2.5.0 |
| Fonts | Google Fonts Compose | 1.6.0 |
| Storage | Room (SQLite) | 2.6.1 |

---

## CI/CD

**GitHub Actions:** `.github/workflows/android-ci.yml`

### Jobs
1. **build** - Assembles debug APK, runs unit tests
2. **lint** - Runs Android Lint checks
3. **release-build** - Builds release APK (main branch only)

### Artifacts
- `debug-apk` - Debug build (14-day retention)
- `release-apk` - Release build (30-day retention)
- `test-results` - Test reports
- `lint-report` - Lint HTML report

---

## Next Steps / TODO

### High Priority
1. [x] Implement Dashboard screen with overview stats
2. [x] Implement Manual Score Entry screen (input form)
3. [ ] Implement Mock Analysis History screen (list view)
4. [x] Add Room database for local persistence
5. [ ] Implement actual API integration (replace MockDataSource)

### Medium Priority
6. [ ] Add dark theme support
7. [ ] Implement actual charts (MPAndroidChart or Compose charts)
8. [ ] Add pull-to-refresh functionality
9. [ ] Implement real authentication flow
10. [ ] Add error handling with snackbars/dialogs

### Low Priority
11. [ ] Add animations and transitions
12. [ ] Implement deep linking
13. [ ] Add widget for home screen
14. [ ] Implement notification system
15. [ ] Add offline mode support

---

## Stitch Design Reference

The designs were generated using the Stitch MCP server:

**Project ID:** `9753487915946571180`

| Screen | Screen ID |
|--------|-----------|
| Analysis Dashboard v3 | `fe861c5442234eccaa06ea4be596cb5b` |
| Analysis Dashboard v2 | `98de1efc44014fbebe0efcfd1e5cc8be` |
| User Profile | `1ea4f37fd9eb4788b0784cc9113e9d34` |
| Analysis Hub | `ebe6ffa997e54ce7b6ec6ec3ff31ed2e` |
| Manual Score Entry v2 | `ca92407cc84c4c05bd343e7238b79542` |
| Weak Area Tracker | `a7d588b0243e4129911a3f500b3f61b4` |
| Mock Analysis History | `31e47b7b948745fbb7cf652dc4dd8893` |
| Legacy Dashboard (hidden) | `dc0a3cba723f4940b7f6906caf4fa667` |
| Legacy Mock Test List (hidden) | `c9b600c3ed2d4f6bb229e863143448b6` |
| Legacy Manual Score Entry (hidden) | `ddcf74cc9ef647b9b1fbac230fd3996e` |
| Legacy Performance Analysis (hidden) | `e11fb80835d644a48a318ec3f525340e` |
| Legacy Analysis Dashboard (hidden) | `064bda00995845879a0389a6e89528a0` |

To fetch updated designs:
```kotlin
// Use Stitch MCP tools
stitch_get_screen(
    name = "projects/9753487915946571180/screens/{screenId}",
    projectId = "9753487915946571180",
    screenId = "{screenId}"
)
```

---

## Build Instructions

### Local Development
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lintDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

Note: Release builds require signing configuration in `app/build.gradle.kts`.

---

## Known Issues

1. **Gradle Wrapper JAR** - The gradle-wrapper.jar needs to be downloaded on first build
2. **Google Fonts** - Requires network on first run to download fonts
3. **Images** - Sample avatar URLs point to external Google storage

---

## Contact

For questions about this implementation, refer to the Stitch design project or the original HTML designs downloaded from the MCP server.
