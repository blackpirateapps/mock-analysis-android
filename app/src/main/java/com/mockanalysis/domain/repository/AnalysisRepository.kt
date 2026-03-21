package com.mockanalysis.domain.repository

import com.mockanalysis.domain.model.MockAnalysis
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for mock test analysis data.
 */
interface AnalysisRepository {
    
    /**
     * Get the latest mock analysis for the current user.
     */
    fun getLatestAnalysis(): Flow<MockAnalysis?>
    
    /**
     * Get analysis by mock ID.
     */
    suspend fun getAnalysisById(mockId: String): MockAnalysis?
    
    /**
     * Get all analyses for the current user.
     */
    fun getAllAnalyses(): Flow<List<MockAnalysis>>
    
    /**
     * Refresh analysis data from remote source.
     */
    suspend fun refreshAnalysis()
}
