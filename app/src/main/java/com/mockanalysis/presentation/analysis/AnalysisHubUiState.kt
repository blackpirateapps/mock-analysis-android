package com.mockanalysis.presentation.analysis

import com.mockanalysis.domain.model.MockAnalysis

/**
 * UI State for the Analysis Hub screen.
 */
data class AnalysisHubUiState(
    val isLoading: Boolean = true,
    val analysis: MockAnalysis? = null,
    val error: String? = null
) {
    val scoreImprovement: Float?
        get() = analysis?.let { a ->
            a.previousScore?.let { prev -> a.score - prev }
        }
    
    val progressPercentage: Float
        get() = analysis?.let { (it.score / it.maxScore) } ?: 0f
}
