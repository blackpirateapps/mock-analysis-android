package com.mockanalysis.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockanalysis.domain.usecase.GetLatestAnalysisUseCase
import com.mockanalysis.domain.usecase.RefreshAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Analysis Hub screen.
 */
@HiltViewModel
class AnalysisHubViewModel @Inject constructor(
    private val getLatestAnalysisUseCase: GetLatestAnalysisUseCase,
    private val refreshAnalysisUseCase: RefreshAnalysisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisHubUiState())
    val uiState: StateFlow<AnalysisHubUiState> = _uiState.asStateFlow()

    init {
        loadAnalysis()
    }

    private fun loadAnalysis() {
        viewModelScope.launch {
            getLatestAnalysisUseCase().collect { analysis ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        analysis = analysis,
                        error = if (analysis == null) "No analysis data available" else null
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                refreshAnalysisUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to refresh"
                    )
                }
            }
        }
    }
}
