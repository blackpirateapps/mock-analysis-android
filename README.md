# Mock Analysis Android

Cupertino-style Flutter app to log and analyze mock test performance locally.

## Features

- Add mock entries with:
  - mock name
  - total questions
  - right answers
  - wrong answers
  - multiple categories per entry
- Create and manage custom categories (English, Reasoning, Maths, etc.)
- View statistics:
  - overall stats
  - category-wise stats
- Fully local data storage (SQLite)

## Local run

1. Install Flutter SDK.
2. Run:

```bash
flutter pub get
flutter run
```

## CI build (recommended for weak laptops)

GitHub Actions workflow is available at `.github/workflows/android-apk.yml` and performs:

- `flutter analyze`
- `flutter test`
- `flutter build apk --release`

The built APK is uploaded as a workflow artifact.
