package com.mockanalysis.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockanalysis.domain.usecase.GetAllAnalysesUseCase
import com.mockanalysis.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllAnalysesUseCase: GetAllAnalysesUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            combine(
                getAllAnalysesUseCase(),
                getUserProfileUseCase()
            ) { analyses, profile ->
                val sorted = analyses.sortedByDescending { it.date }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        latestAnalysis = sorted.firstOrNull(),
                        analyses = sorted,
                        profile = profile,
                        error = if (sorted.isEmpty()) "No mock data available" else null
                    )
                }
            }.collect {}
        }
    }
}
