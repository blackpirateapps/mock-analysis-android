package com.mockanalysis.presentation.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockanalysis.domain.model.Benchmarks
import com.mockanalysis.domain.model.FocusArea
import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.domain.model.ProficiencyLevel
import com.mockanalysis.domain.model.QuestionMetric
import com.mockanalysis.domain.model.SubjectScore
import com.mockanalysis.domain.usecase.GetLatestAnalysisUseCase
import com.mockanalysis.domain.usecase.SaveAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ManualScoreEntryViewModel @Inject constructor(
    private val saveAnalysisUseCase: SaveAnalysisUseCase,
    private val getLatestAnalysisUseCase: GetLatestAnalysisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualScoreEntryUiState())
    val uiState: StateFlow<ManualScoreEntryUiState> = _uiState.asStateFlow()

    fun updatePlatform(value: String) = _uiState.update { it.copy(platform = value) }
    fun updateMockType(value: String) = _uiState.update { it.copy(mockType = value) }
    fun updateMockName(value: String) = _uiState.update { it.copy(mockName = value) }
    fun updateQuantScore(value: String) = _uiState.update { it.copy(quantScore = sanitize(value)) }
    fun updateQuantTime(value: String) = _uiState.update { it.copy(quantTime = sanitize(value, false)) }
    fun updateReasoningScore(value: String) = _uiState.update { it.copy(reasoningScore = sanitize(value)) }
    fun updateReasoningTime(value: String) = _uiState.update { it.copy(reasoningTime = sanitize(value, false)) }
    fun updateEnglishScore(value: String) = _uiState.update { it.copy(englishScore = sanitize(value)) }
    fun updateEnglishTime(value: String) = _uiState.update { it.copy(englishTime = sanitize(value, false)) }
    fun updateGaScore(value: String) = _uiState.update { it.copy(gaScore = sanitize(value)) }
    fun updateGaTime(value: String) = _uiState.update { it.copy(gaTime = sanitize(value, false)) }

    fun saveEntry() {
        val state = _uiState.value
        if (!isValid(state)) {
            _uiState.update { it.copy(error = "Please enter all subject marks (0-50) before generating analysis.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, successMessage = null) }

            val latest = getLatestAnalysisUseCase().first()
            val total = state.totalScore
            val maxScore = 200f

            val analysis = MockAnalysis(
                id = "mock_${System.currentTimeMillis()}",
                mockName = state.mockName.ifBlank { "${state.platform} ${state.mockType}" },
                examType = "SSC CGL",
                date = LocalDate.now(),
                score = total,
                maxScore = maxScore,
                percentile = ((total / maxScore) * 100f).coerceIn(1f, 99.9f),
                rank = ((1f - (total / maxScore)) * 20000).toInt().coerceAtLeast(1),
                totalParticipants = 24102,
                previousScore = latest?.score,
                subjectScores = buildSubjects(state),
                benchmarks = Benchmarks(
                    userScore = total,
                    averageScore = 116f,
                    topperScore = 192f,
                    maxPossibleScore = maxScore
                ),
                focusAreas = listOf(
                    FocusArea(
                        id = "focus_${System.currentTimeMillis()}",
                        title = "Improve General Awareness recall",
                        description = "Revise date-based and factual question clusters.",
                        subjectId = "ga",
                        priority = 1
                    )
                ),
                questionMetrics = emptyList<QuestionMetric>(),
                isPremium = true
            )

            saveAnalysisUseCase(analysis)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    successMessage = "Analysis generated and saved locally."
                )
            }
        }
    }

    private fun sanitize(value: String, allowDecimal: Boolean = true): String {
        return if (allowDecimal) value.filter { it.isDigit() || it == '.' } else value.filter { it.isDigit() }
    }

    private fun isValid(state: ManualScoreEntryUiState): Boolean {
        return listOf(state.quantScore, state.reasoningScore, state.englishScore, state.gaScore)
            .all { value ->
                val parsed = value.toFloatOrNull() ?: return@all false
                parsed in 0f..50f
            }
    }

    private fun buildSubjects(state: ManualScoreEntryUiState): List<SubjectScore> {
        fun level(score: Float): ProficiencyLevel {
            return when {
                score >= 45f -> ProficiencyLevel.ELITE
                score >= 38f -> ProficiencyLevel.STRONG
                score >= 30f -> ProficiencyLevel.SOLID
                score >= 22f -> ProficiencyLevel.DEVELOPING
                else -> ProficiencyLevel.ALERT
            }
        }

        val quant = state.quantScore.toFloatOrNull() ?: 0f
        val reasoning = state.reasoningScore.toFloatOrNull() ?: 0f
        val english = state.englishScore.toFloatOrNull() ?: 0f
        val ga = state.gaScore.toFloatOrNull() ?: 0f

        return listOf(
            SubjectScore("quant", "Quantitative Aptitude", "QUANT", quant, 50f, level(quant), state.quantTime.toIntOrNull()),
            SubjectScore("reasoning", "General Intelligence", "REASON", reasoning, 50f, level(reasoning), state.reasoningTime.toIntOrNull()),
            SubjectScore("english", "English Comprehension", "ENGLISH", english, 50f, level(english), state.englishTime.toIntOrNull()),
            SubjectScore("ga", "General Awareness", "GA", ga, 50f, level(ga), state.gaTime.toIntOrNull())
        )
    }
}
