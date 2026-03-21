package com.mockanalysis.presentation.input

data class ManualScoreEntryUiState(
    val platform: String = "Testbook",
    val mockType: String = "Full Mock",
    val mockName: String = "",
    val quantScore: String = "",
    val quantTime: String = "",
    val reasoningScore: String = "",
    val reasoningTime: String = "",
    val englishScore: String = "",
    val englishTime: String = "",
    val gaScore: String = "",
    val gaTime: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
) {
    val totalScore: Float
        get() = listOf(quantScore, reasoningScore, englishScore, gaScore).sumOf {
            it.toFloatOrNull()?.toDouble() ?: 0.0
        }.toFloat()
}
