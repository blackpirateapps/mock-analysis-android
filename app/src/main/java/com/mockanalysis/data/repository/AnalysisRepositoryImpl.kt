package com.mockanalysis.data.repository

import com.mockanalysis.data.local.dao.AnalysisDao
import com.mockanalysis.data.local.entity.MockAttemptEntity
import com.mockanalysis.data.local.entity.SubjectScoreEntity
import com.mockanalysis.data.source.MockDataSource
import com.mockanalysis.domain.model.Benchmarks
import com.mockanalysis.domain.model.FocusArea
import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.domain.model.ProficiencyLevel
import com.mockanalysis.domain.model.QuestionMetric
import com.mockanalysis.domain.model.SubjectScore
import com.mockanalysis.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDate

/**
 * Room-backed implementation of AnalysisRepository.
 */
@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val analysisDao: AnalysisDao,
    private val mockDataSource: MockDataSource
) : AnalysisRepository {

    init {
        // Seed starter data on first app launch.
        kotlinx.coroutines.runBlocking {
            if (analysisDao.getAttemptCount() == 0) {
                saveAnalysis(mockDataSource.getMockAnalysis())
            }
        }
    }

    override fun getLatestAnalysis(): Flow<MockAnalysis?> {
        return analysisDao.observeLatestAttempt().map { attempt ->
            attempt?.let { runBlocking { mapAttemptToDomain(it) } }
        }
    }

    override suspend fun getAnalysisById(mockId: String): MockAnalysis? {
        val attempt = analysisDao.getAttemptById(mockId) ?: return null
        return mapAttemptToDomain(attempt)
    }

    override fun getAllAnalyses(): Flow<List<MockAnalysis>> {
        return analysisDao.observeAllAttempts().map { attempts ->
            attempts.map { runBlocking { mapAttemptToDomain(it) } }
        }
    }

    override suspend fun saveAnalysis(analysis: MockAnalysis) {
        val attempt = MockAttemptEntity(
            id = analysis.id,
            mockName = analysis.mockName,
            examType = analysis.examType,
            dateEpochDay = analysis.date.toEpochDay(),
            score = analysis.score,
            maxScore = analysis.maxScore,
            percentile = analysis.percentile,
            averageScore = analysis.benchmarks.averageScore,
            topperScore = analysis.benchmarks.topperScore,
            rank = analysis.rank,
            totalParticipants = analysis.totalParticipants,
            previousScore = analysis.previousScore,
            isPremium = analysis.isPremium,
            createdAt = System.currentTimeMillis()
        )
        val subjectScores = analysis.subjectScores.map { subject ->
            SubjectScoreEntity(
                id = "${analysis.id}_${subject.subjectId}",
                attemptId = analysis.id,
                subjectId = subject.subjectId,
                subjectName = subject.subjectName,
                shortName = subject.shortName,
                score = subject.score,
                maxScore = subject.maxScore,
                proficiencyLevel = subject.proficiencyLevel.name,
                averageTimePerQuestion = subject.averageTimePerQuestion
            )
        }

        analysisDao.insertAttempt(attempt)
        analysisDao.deleteSubjectScoresForAttempt(analysis.id)
        analysisDao.insertSubjectScores(subjectScores)
    }

    override suspend fun refreshAnalysis() {
        // Local-first app, no remote refresh.
    }

    private suspend fun mapAttemptToDomain(attempt: MockAttemptEntity): MockAnalysis {
        val subjectScores = analysisDao.getSubjectScoresForAttempt(attempt.id).map { subject ->
            SubjectScore(
                subjectId = subject.subjectId,
                subjectName = subject.subjectName,
                shortName = subject.shortName,
                score = subject.score,
                maxScore = subject.maxScore,
                proficiencyLevel = runCatching {
                    ProficiencyLevel.valueOf(subject.proficiencyLevel)
                }.getOrDefault(ProficiencyLevel.SOLID),
                averageTimePerQuestion = subject.averageTimePerQuestion
            )
        }

        return MockAnalysis(
            id = attempt.id,
            mockName = attempt.mockName,
            examType = attempt.examType,
            date = LocalDate.ofEpochDay(attempt.dateEpochDay),
            score = attempt.score,
            maxScore = attempt.maxScore,
            percentile = attempt.percentile,
            rank = attempt.rank,
            totalParticipants = attempt.totalParticipants,
            previousScore = attempt.previousScore,
            subjectScores = subjectScores,
            benchmarks = Benchmarks(
                userScore = attempt.score,
                averageScore = attempt.averageScore,
                topperScore = attempt.topperScore,
                maxPossibleScore = attempt.maxScore
            ),
            focusAreas = defaultFocusAreas(),
            questionMetrics = defaultQuestionMetrics(),
            isPremium = attempt.isPremium
        )
    }

    private fun defaultFocusAreas(): List<FocusArea> {
        return listOf(
            FocusArea(
                id = "fa_local_1",
                title = "General Awareness Revision",
                description = "Focus on static GK and current affairs mix for consistency.",
                subjectId = "ga",
                priority = 1
            ),
            FocusArea(
                id = "fa_local_2",
                title = "Speed Drills: Quant",
                description = "Target 60s average per question for algebra and geometry sets.",
                subjectId = "quant",
                priority = 2
            )
        )
    }

    private fun defaultQuestionMetrics(): List<QuestionMetric> = emptyList()
}
