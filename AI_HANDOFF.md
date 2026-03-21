# AI Handoff - Mock Analysis Android

## Overview

This repository has been rebuilt into a Flutter Android app focused on Cupertino UI and local-only storage.

The app allows users to:

- Create custom categories (for example: English mock, Reasoning mock, Maths mock)
- Add mock entries with:
  - mock name
  - total questions
  - right answers
  - wrong answers
  - multiple category assignment per entry
- View statistics:
  - overall stats
  - category-wise stats

All data is persisted locally in SQLite on-device. No network sync is implemented.

## Tech Stack

- Flutter
- Cupertino widgets (`CupertinoApp`, `CupertinoTabScaffold`, `CupertinoPageScaffold`)
- SQLite via `sqflite`

## Architecture

- `lib/main.dart`
  - App bootstrap, wiring database + repository + app state
- `lib/app/app.dart`
  - Root `CupertinoApp` and tab navigation
- `lib/app/app_state.dart`
  - App-level state service and update stream

### Data Layer

- `lib/data/db/app_database.dart`
  - SQLite open/create lifecycle and schema setup
- `lib/data/repositories/mock_repository.dart`
  - CRUD operations and input validation

### Domain Layer

- `lib/domain/entities/models.dart`
  - `Category`, `MockEntry`, `OverallStats`, `CategoryStats`
- `lib/domain/services/stats_service.dart`
  - Statistics aggregation logic

### Feature Screens

- `lib/features/entries/entries_screen.dart`
  - Add, list, delete entries
  - Multi-select category mapping
- `lib/features/categories/categories_screen.dart`
  - Add, list, delete categories
- `lib/features/statistics/statistics_screen.dart`
  - Overall + category-wise analytics

## Database Schema

### `categories`

- `id INTEGER PRIMARY KEY AUTOINCREMENT`
- `name TEXT NOT NULL UNIQUE`
- `created_at INTEGER NOT NULL`

### `mock_entries`

- `id INTEGER PRIMARY KEY AUTOINCREMENT`
- `mock_name TEXT NOT NULL`
- `total_questions INTEGER NOT NULL`
- `right_answers INTEGER NOT NULL`
- `wrong_answers INTEGER NOT NULL`
- `created_at INTEGER NOT NULL`

### `entry_categories` (many-to-many)

- `entry_id INTEGER NOT NULL`
- `category_id INTEGER NOT NULL`
- Composite PK: `(entry_id, category_id)`
- FK cascade delete on both sides

## Validation Rules

Implemented in repository layer:

- Mock name required
- Total questions must be > 0
- Right and wrong values cannot be negative
- `right + wrong <= total questions`
- At least one category required per entry

## Statistics Behavior

- Overall metrics are computed across all entries
- Category-wise metrics include entries linked to that category
- If one entry belongs to multiple categories, that entry contributes to each linked category

## CI/CD

Workflow file:

- `.github/workflows/android-apk.yml`

Pipeline actions:

1. Set up Java 17
2. Set up Flutter stable
3. Generate Android project files if `android/` is missing
4. `flutter pub get`
5. `flutter analyze`
6. `flutter test`
7. `flutter build apk --release`
8. Upload release APK artifact

## Tests

- `test/stats_service_test.dart`
  - Validates overall and category-wise statistics
  - Covers many-to-many category mapping behavior
- `test/widget_test.dart`
  - Basic Cupertino widget smoke test

## Known Environment Note

In this execution environment, Flutter CLI was unavailable (`flutter: command not found`), so local analyze/test/build was not run here. The GitHub Actions workflow is configured to perform full validation and APK build remotely.
