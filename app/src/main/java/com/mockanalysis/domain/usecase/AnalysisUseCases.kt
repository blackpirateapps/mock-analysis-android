package com.mockanalysis.domain.usecase

import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the latest mock test analysis.
 */
class GetLatestAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    operator fun invoke(): Flow<MockAnalysis?> = repository.getLatestAnalysis()
}

/**
 * Use case for getting all mock test analyses.
 */
class GetAllAnalysesUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    operator fun invoke(): Flow<List<MockAnalysis>> = repository.getAllAnalyses()
}

/**
 * Use case for refreshing analysis data.
 */
class RefreshAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    suspend operator fun invoke() = repository.refreshAnalysis()
}

/**
 * Use case for saving a mock analysis entry.
 */
class SaveAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    suspend operator fun invoke(analysis: MockAnalysis) = repository.saveAnalysis(analysis)
}
