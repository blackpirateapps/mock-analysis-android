package com.mockanalysis.data.repository

import com.mockanalysis.data.source.MockDataSource
import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AnalysisRepository using mock data.
 * In a real app, this would interact with Room database and/or remote API.
 */
@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val mockDataSource: MockDataSource
) : AnalysisRepository {

    private val _latestAnalysis = MutableStateFlow<MockAnalysis?>(null)

    init {
        // Initialize with mock data
        _latestAnalysis.value = mockDataSource.getMockAnalysis()
    }

    override fun getLatestAnalysis(): Flow<MockAnalysis?> {
        return _latestAnalysis.asStateFlow()
    }

    override suspend fun getAnalysisById(mockId: String): MockAnalysis? {
        val analysis = _latestAnalysis.value
        return if (analysis?.id == mockId) analysis else null
    }

    override fun getAllAnalyses(): Flow<List<MockAnalysis>> {
        return MutableStateFlow(listOfNotNull(_latestAnalysis.value)).asStateFlow()
    }

    override suspend fun refreshAnalysis() {
        // In a real app, this would fetch from remote API
        _latestAnalysis.value = mockDataSource.getMockAnalysis()
    }
}
