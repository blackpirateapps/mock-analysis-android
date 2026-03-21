package com.mockanalysis.presentation.dashboard

import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.domain.model.UserProfile

data class DashboardUiState(
    val isLoading: Boolean = true,
    val latestAnalysis: MockAnalysis? = null,
    val analyses: List<MockAnalysis> = emptyList(),
    val profile: UserProfile? = null,
    val error: String? = null
) {
    val currentAverage: Float
        get() = if (analyses.isEmpty()) 0f else analyses.map { it.score }.average().toFloat()

    val progressToTarget: Float
        get() {
            val target = profile?.targetScore?.toFloat() ?: return 0f
            if (target <= 0f) return 0f
            return (currentAverage / target).coerceIn(0f, 1f)
        }

    val scoreDeltaVsPrevious: Float?
        get() {
            if (analyses.size < 2) return null
            return analyses[0].score - analyses[1].score
        }
}
