package com.mockanalysis.data.source

import com.mockanalysis.domain.model.*
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock data source providing sample data for development and testing.
 * This simulates what would come from a real API or database.
 */
@Singleton
class MockDataSource @Inject constructor() {

    fun getMockAnalysis(): MockAnalysis {
        return MockAnalysis(
            id = "mock_42",
            mockName = "SSC CGL Tier-1 Mock #42",
            examType = "SSC CGL",
            date = LocalDate.of(2024, 2, 14),
            score = 168.5f,
            maxScore = 200f,
            percentile = 98.2f,
            rank = 412,
            totalParticipants = 24102,
            previousScore = 156.1f,
            subjectScores = listOf(
                SubjectScore(
                    subjectId = "quant",
                    subjectName = "Quantitative Aptitude",
                    shortName = "QUANT",
                    score = 48.5f,
                    maxScore = 50f,
                    proficiencyLevel = ProficiencyLevel.ELITE
                ),
                SubjectScore(
                    subjectId = "reasoning",
                    subjectName = "General Intelligence & Reasoning",
                    shortName = "REASONING",
                    score = 46.0f,
                    maxScore = 50f,
                    proficiencyLevel = ProficiencyLevel.STRONG
                ),
                SubjectScore(
                    subjectId = "english",
                    subjectName = "English Comprehension",
                    shortName = "ENGLISH",
                    score = 42.5f,
                    maxScore = 50f,
                    proficiencyLevel = ProficiencyLevel.SOLID
                ),
                SubjectScore(
                    subjectId = "ga",
                    subjectName = "General Awareness",
                    shortName = "GA",
                    score = 31.5f,
                    maxScore = 50f,
                    proficiencyLevel = ProficiencyLevel.ALERT,
                    averageTimePerQuestion = 45
                )
            ),
            benchmarks = Benchmarks(
                userScore = 168.5f,
                averageScore = 116.0f,
                topperScore = 192.0f,
                maxPossibleScore = 200f
            ),
            focusAreas = listOf(
                FocusArea(
                    id = "fa_1",
                    title = "Modern History (1857-1947)",
                    description = "You missed 4 questions here. Review the chronological sequence of events.",
                    subjectId = "ga",
                    priority = 1,
                    questionsWrong = 4
                ),
                FocusArea(
                    id = "fa_2",
                    title = "Advanced Geometry",
                    description = "Accuracy is 100%, but average time per question is 92s. Goal: < 60s.",
                    subjectId = "quant",
                    priority = 2,
                    currentTimePerQuestion = 92,
                    targetTimePerQuestion = 60
                )
            ),
            questionMetrics = generateQuestionMetrics(),
            isPremium = true
        )
    }

    fun getUserProfile(): UserProfile {
        return UserProfile(
            id = "user_001",
            name = "Aditya Sharma",
            email = "aditya.sharma@email.com",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDoR95ZIhK435esYJt7VnhTu9fOKoYA5cjsiguq6VrY6fGAbOvmHa4yizzNV1K9_MXAK78nUSWCVMASpjV-T9NrvsZnLEprSCow8TMUkNLfBTvfHJHwnXiG-22uNlpJ-94CIhwjjRhOtvdrKV78OhPb3_3gPTavzacJ6ZoO0YeB6g46zZyGcocs-sVsZI73atTVDxvSdwaeX25RzdRx0Ii2gtYf4CRsBd0mz2MtkE4VGcSXmHqbdQIQ6Ya4VQaAc79Vvl52WwB-kpk",
            targetExam = TargetExam(
                id = "ssc_cgl_2024",
                name = "SSC CGL 2024",
                year = 2024,
                shortName = "CGL 24"
            ),
            tier = UserTier.ELITE,
            targetScore = 175,
            currentProgressPercentage = 82f,
            achievements = listOf(
                Achievement(
                    id = "ach_1",
                    name = "Data Detective",
                    description = "Analyzed 50+ mock tests with detailed review",
                    iconName = "search_insights",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000 * 30
                ),
                Achievement(
                    id = "ach_2",
                    name = "Consistency King",
                    description = "Maintained a 30-day practice streak",
                    iconName = "avg_pace",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000 * 7
                ),
                Achievement(
                    id = "ach_3",
                    name = "Logic Legend",
                    description = "Score 95%+ in Reasoning for 10 consecutive mocks",
                    iconName = "military_tech",
                    isUnlocked = false,
                    progress = 0.7f
                )
            ),
            linkedPlatforms = listOf(
                LinkedPlatform(
                    id = "testbook",
                    name = "Testbook",
                    shortCode = "T",
                    isConnected = true,
                    username = "aditya_ssc",
                    connectedAt = System.currentTimeMillis() - 86400000 * 60
                ),
                LinkedPlatform(
                    id = "oliveboard",
                    name = "Oliveboard",
                    shortCode = "O",
                    isConnected = false
                )
            ),
            settings = ProfileSettings(
                timeWeightedAnalysis = true,
                accuracyFocusMode = false,
                predictiveGoalTracking = true,
                notificationsEnabled = true,
                darkModeEnabled = false
            )
        )
    }

    private fun generateQuestionMetrics(): List<QuestionMetric> {
        val metrics = mutableListOf<QuestionMetric>()
        val subjects = listOf("quant", "reasoning", "english", "ga")
        var questionNumber = 1

        // Generate 100 questions (25 per subject)
        subjects.forEach { subjectId ->
            repeat(25) { index ->
                val status = when {
                    index < 20 -> QuestionStatus.CORRECT
                    index < 23 -> QuestionStatus.INCORRECT
                    else -> QuestionStatus.SKIPPED
                }
                val timeTaken = when (status) {
                    QuestionStatus.CORRECT -> (30..60).random()
                    QuestionStatus.INCORRECT -> (60..120).random()
                    QuestionStatus.SKIPPED -> (5..15).random()
                }
                metrics.add(
                    QuestionMetric(
                        questionId = "q_${questionNumber}",
                        questionNumber = questionNumber,
                        subjectId = subjectId,
                        timeTaken = timeTaken,
                        status = status,
                        difficulty = QuestionDifficulty.values().random()
                    )
                )
                questionNumber++
            }
        }
        return metrics
    }

    fun getAccountMenuItems(): List<AccountMenuItem> {
        return listOf(
            AccountMenuItem(
                id = "security",
                title = "Account Security",
                iconName = "shield",
                route = "security"
            ),
            AccountMenuItem(
                id = "notifications",
                title = "Notification Preferences",
                iconName = "notifications_active",
                route = "notifications"
            ),
            AccountMenuItem(
                id = "help",
                title = "Help & Support",
                iconName = "quiz",
                route = "help"
            ),
            AccountMenuItem(
                id = "privacy",
                title = "Privacy Policy",
                iconName = "description",
                route = "privacy"
            )
        )
    }
}
