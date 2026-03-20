# Mock Analysis Android

Local-first competitive exam mock analysis app built with Flutter + Drift.

## Features
- Offline-only mock test logging
- Dynamic subject entry with automatic calculations
- 3-tier insights (overall, subject breakdown, trend/weakness)
- JSON backup export/import
- Global marking scheme controls

## Build APK via GitHub Actions
This machine may not be able to build APK locally. Use workflow:

1. Push branch to GitHub.
2. Open **Actions** tab.
3. Run **Android APK (Universal)** workflow.
4. Download artifact: `mock-analysis-universal-apk-<sha>`.

The workflow generates `app-release.apk` as a universal APK.
