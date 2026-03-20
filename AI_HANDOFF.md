# AI Handoff - Mock Analysis Android (Flutter + Drift)

## Product Intent
Build a local-first Android app for competitive exam mock analysis with zero cloud dependency, strict dark-only UI, and fast analytics. The app is not a generic dashboard; it is a focused utility for logging mock tests and diagnosing weak areas over time.

## Non-Negotiables from PRD
- Offline-only operation, no backend calls.
- Dark mode only (`#000000` background, `#1C1C1E` elevated, iOS accent colors).
- Bottom tabs: `Log`, `Insights`, `Settings`.
- Dynamic subjects for each mock test.
- Calculations must be automatic:
  - `Correct = Attempted - Wrong`
  - Subject score uses global marking scheme
  - Full mock rollups (totals, score, accuracy)
- Insights must include 3 tiers:
  - Tier 1 full mock overview
  - Tier 2 subject-wise cards
  - Tier 3 trend and weak-subject analysis
- Import/Export JSON with PRD-compatible schema.

## Locked Decisions
- Framework: Flutter (Cupertino-first)
- State: Riverpod
- Persistence: Drift + SQLite
- Marking scheme: global default only (v1)
- Import merge conflict policy: imported record wins on same ID
- APK distribution: universal APK via GitHub Actions

## Current Code Map
- `pubspec.yaml`: dependencies for Flutter/Riverpod/Drift/charts/import-export
- `lib/main.dart`: app entrypoint with `ProviderScope`
- `lib/app/app.dart`: `CupertinoApp` + tab scaffold
- `lib/app/providers.dart`: Riverpod providers for DB/repos/services
- `lib/data/db/app_database.dart`: Drift tables and DB setup
- `lib/data/repositories/test_repository.dart`: test CRUD + backup import/export
- `lib/data/repositories/settings_repository.dart`: marking scheme persistence
- `lib/domain/entities/models.dart`: core entities and JSON mapping
- `lib/domain/services/analytics_service.dart`: score/accuracy/weak-subject calculations
- `lib/features/log/log_screen.dart`: data entry flow
- `lib/features/insights/insights_screen.dart`: 3-tier insights UI
- `lib/features/settings/settings_screen.dart`: marking + backup controls
- `.github/workflows/android-apk.yml`: CI build and APK artifact upload
- `test/analytics_service_test.dart`: baseline calculation tests

## Data Model (Drift)
- `tests`
  - `id TEXT PK`
  - `timestamp TEXT` (ISO UTC)
  - `testName TEXT`
  - `percentile REAL`
  - `rank INTEGER`
  - `totalCandidates INTEGER`
- `subjects`
  - `subjectId TEXT PK`
  - `testId TEXT FK -> tests.id` (cascade delete)
  - `name TEXT`
  - `attempted INTEGER`
  - `wrong INTEGER`
  - `skipped INTEGER`
- `app_settings`
  - `key TEXT PK`
  - `value TEXT`

## JSON Contract
Export/import must match PRD root shape:

```json
{
  "tests": [
    {
      "id": "uuid-string",
      "timestamp": "2026-03-20T18:01:09.000Z",
      "testName": "SSC CGL Tier 1 Mock 4",
      "percentile": 94.5,
      "rank": 1200,
      "totalCandidates": 45000,
      "subjects": [
        {
          "subjectId": "subj-uuid",
          "name": "General Intelligence",
          "attempted": 22,
          "wrong": 3,
          "skipped": 3
        }
      ]
    }
  ]
}
```

## Business Rules
- Subject score = `(correct * correctMark) - (wrong * wrongPenalty)`
- Accuracy = `correct / attempted` (0 if attempted is 0)
- Full test score and accuracy are aggregate from all subjects
- Merge import: upsert by test ID, imported value replaces existing record
- Overwrite import: clear local tests+subjects, then insert imported

## CI Build Pipeline
Workflow file: `.github/workflows/android-apk.yml`

Pipeline steps:
1. Checkout
2. Setup Java 17
3. Setup Flutter stable
4. `flutter create --platforms=android --project-name mock_analysis_android .`
5. `flutter pub get`
6. `dart run build_runner build --delete-conflicting-outputs`
7. `flutter analyze`
8. `flutter test`
9. `flutter build apk --release`
10. Upload artifact: `build/app/outputs/flutter-apk/app-release.apk`

## Known Gaps / Next Agent Tasks
1. Ensure generated Drift file exists (`app_database.g.dart`) via build_runner.
2. Validate Flutter/Drift compile on CI (local machine lacks Flutter binary).
3. Tighten form validation UX and input formatting for numeric fields.
4. Add DB indexes and migration tests for version bumps.
5. Improve chart styling for Cupertino polish and larger data sets.
6. Add repository tests for import merge/overwrite edge cases.
7. Add README instructions for downloading APK artifact from GitHub Actions.

## Latest Fixes (Post-CI Analyzer Run)
- Resolved `const_eval_property_access` in tab scaffold by removing `const` from `CupertinoTabBar` construction.
- Fixed undefined `CircularProgressIndicator` in insights by importing required Material symbols.
- Addressed lint warnings in log/settings modules (unused import and unnecessary constructor params/imports).
- Replaced default widget test usage of `MyApp` with `MockAnalysisApp`.
- Removed now-unused `cross_file` dependency from `pubspec.yaml`.
- Applied additional const-optimizations in `lib/app/app.dart` for `CupertinoTabBar` colors and tab item list to satisfy strict analyze settings.

## Verification Commands (CI/local when Flutter is available)
- `flutter pub get`
- `dart run build_runner build --delete-conflicting-outputs`
- `flutter analyze`
- `flutter test`
- `flutter build apk --release`

## Definition of Done
- All PRD feature areas implemented and visible in app.
- Import/export schema compatible with PRD.
- App operates without network dependency.
- Workflow produces universal APK artifact successfully.
- Core scoring and trend logic covered by tests.

## Working Agreement (User Preference)
- Before creating a commit, update this `AI_HANDOFF.md` with any important architectural, workflow, or status changes.
- When requested, commit and push changes after the handoff update so future agents have current context.
