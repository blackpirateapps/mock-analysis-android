package com.mockanalysis.domain.model

import java.time.LocalDate

/**
 * Domain model representing a complete mock test analysis.
 */
data class MockAnalysis(
    val id: String,
    val mockName: String,
    val examType: String,
    val date: LocalDate,
    val score: Float,
    val maxScore: Float,
    val percentile: Float,
    val rank: Int,
    val totalParticipants: Int,
    val previousScore: Float?,
    val subjectScores: List<SubjectScore>,
    val benchmarks: Benchmarks,
    val focusAreas: List<FocusArea>,
    val questionMetrics: List<QuestionMetric>,
    val isPremium: Boolean = false
)

/**
 * Score breakdown for a specific subject.
 */
data class SubjectScore(
    val subjectId: String,
    val subjectName: String,
    val shortName: String,
    val score: Float,
    val maxScore: Float,
    val proficiencyLevel: ProficiencyLevel,
    val averageTimePerQuestion: Int? = null // in seconds
)

/**
 * Proficiency levels for subject mastery.
 */
enum class ProficiencyLevel(val displayName: String) {
    ELITE("Elite"),
    STRONG("Strong"),
    SOLID("Solid"),
    DEVELOPING("Developing"),
    ALERT("Alert")
}

/**
 * Comparative benchmarks against other test takers.
 */
data class Benchmarks(
    val userScore: Float,
    val averageScore: Float,
    val topperScore: Float,
    val maxPossibleScore: Float
)

/**
 * Areas requiring focused practice.
 */
data class FocusArea(
    val id: String,
    val title: String,
    val description: String,
    val subjectId: String,
    val priority: Int, // 1 = highest priority
    val questionsWrong: Int? = null,
    val currentTimePerQuestion: Int? = null, // in seconds
    val targetTimePerQuestion: Int? = null // in seconds
)

/**
 * Individual question performance metrics for time vs accuracy analysis.
 */
data class QuestionMetric(
    val questionId: String,
    val questionNumber: Int,
    val subjectId: String,
    val timeTaken: Int, // in seconds
    val status: QuestionStatus,
    val difficulty: QuestionDifficulty? = null
)

/**
 * Status of a question attempt.
 */
enum class QuestionStatus {
    CORRECT,
    INCORRECT,
    SKIPPED
}

/**
 * Difficulty level of a question.
 */
enum class QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD
}
