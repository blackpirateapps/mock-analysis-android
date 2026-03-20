
# System Context & Product Requirements Document (PRD)
**Project:** Local-First Competitive Exam Mock Analysis Application (Android)
**Target Architecture:** React Native / Expo (or comparable component-based framework)

## 1. Product Objective
A hyper-focused, privacy-first Android utility designed to log, track, and analyze mock test performances for competitive exams (e.g., SSC CGL, WBPSC Clerkship). The application must operate entirely offline using local storage, featuring strict zero-cloud dependency, with manual JSON import/export capabilities.

## 2. Design Language & UI/UX Constraints
**AI Agent Directive:** Adhere strictly to these design principles. Do not introduce brutalist elements, light mode toggles, or unnecessary "page overviews." The UI must remain highly readable, data-focused, and barebones.

*   **Aesthetic:** "Things 3" inspired minimalism combined with modern Cupertino (Apple iOS/macOS) design principles.
*   **Theme:** **Strictly Dark Mode Only.**
    *   Main Background: True Black (`#000000`)
    *   Elevated Cards/Modals: Dark Gray (`#1C1C1E`)
    *   Primary Text: Crisp White (`#FFFFFF`)
    *   Secondary Text: System Gray (`#8E8E93`)
    *   Accent Colors (for data visualization): iOS Green (`#34C759`), iOS Red (`#FF3B30`), iOS Blue (`#0A84FF`).
*   **Typography:** System-native sans-serif (San Francisco / Inter). Use heavy, large font weights for primary metrics and headers.
*   **Shapes:** Generous border radii (`16px` to `20px`) for all cards and buttons. Pill-shaped buttons for primary actions.
*   **Navigation:** Minimalist bottom tab bar (`Log`, `Insights`, `Settings`).

## 3. Core Features & User Flows

### A. Data Entry ("Log" Flow)
*   **Smart Timestamp:** Upon initiating a new entry, automatically capture and display the current date/time. This field must be editable via a native-style wheel picker or modal, saving the final output as a standardized ISO string or Unix timestamp.
*   **Test Metadata:** Inputs for Test Name (text), Percentile (number), Rank (number), and Total Candidates (number).
*   **Modular Subject Inputs:** 
    *   Users add subjects dynamically (e.g., "Quantitative Aptitude", "General Intelligence").
    *   For each subject, user inputs: `Attempted`, `Wrong`, and `Skipped`.
    *   *Calculation Logic:* `Correct = Attempted - Wrong`. App must automatically calculate subject-level scores based on predefined or user-entered marking schemes (e.g., +2 for correct, -0.5 for wrong).
    *   *Rollup Logic:* The app automatically aggregates subject data to calculate the "Full Mock" total marks, total score, and overall accuracy.

### B. Three-Tiered Analysis Engine ("Insights" Flow)
*   **Tier 1: Full Mock Overview**
    *   Hero section displaying overall Score, Rank, and Percentile in large typography.
    *   A master circular progress ring showing overall Accuracy %.
    *   A horizontal, iOS-storage-style segmented bar representing the ratio of Correct (Green), Wrong (Red), and Skipped (Gray) questions.
*   **Tier 2: Subject-Wise Breakdown**
    *   Rendered as an iOS-style grouped list below the overview.
    *   Each row/card displays the subject name, subject score, accuracy %, and a miniature horizontal ratio bar.
*   **Tier 3: Overall Macro Analysis (Trends)**
    *   A dedicated view rendering a chronological line chart plotting Percentile over time (using the standardized timestamps).
    *   A radar chart or ranked list identifying historically weak subjects based on average accuracy over rolling timeframes.

### C. Data Portability & Settings
*   **Local Storage:** All entries must be saved to the device's local async storage or local SQLite database.
*   **Export:** Generate a `mock_analysis_backup.json` file containing all historical data and allow the user to save it to device storage or share it via the native OS share sheet.
*   **Import:** Parse an uploaded JSON file, validate the schema, and restore the local database, overwriting or merging based on entry IDs.

## 4. Expected Data Schema (JSON)
**AI Agent Directive:** Use this structure for state management and local database modeling.

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
