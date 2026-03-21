package com.mockanalysis.presentation.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mockanalysis.presentation.common.components.ContainerCard
import com.mockanalysis.presentation.common.components.ElevatedStatCard
import com.mockanalysis.presentation.theme.MockAnalysisColors

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ManualScoreEntryScreen(
    viewModel: ManualScoreEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Manual Score Entry",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Input your performance data to architect your growth path.",
            style = MaterialTheme.typography.bodyMedium,
            color = MockAnalysisColors.OnSurfaceVariant
        )

        ElevatedStatCard {
            Text(text = "Select Platform", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Testbook", "Oliveboard", "Adda247", "Other").forEach { platform ->
                    FilterChip(
                        selected = uiState.platform == platform,
                        onClick = { viewModel.updatePlatform(platform) },
                        label = { Text(platform) }
                    )
                }
            }
        }

        ContainerCard {
            Text(text = "Mock Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Full Mock", "Sectional", "Module").forEach { type ->
                    FilterChip(
                        selected = uiState.mockType == type,
                        onClick = { viewModel.updateMockType(type) },
                        label = { Text(type) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.mockName,
                onValueChange = viewModel::updateMockName,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mock Name (optional)") },
                singleLine = true
            )
        }

        SubjectEntryCard(
            title = "Quantitative Aptitude",
            marks = uiState.quantScore,
            time = uiState.quantTime,
            onMarksChange = viewModel::updateQuantScore,
            onTimeChange = viewModel::updateQuantTime
        )
        SubjectEntryCard(
            title = "General Intelligence",
            marks = uiState.reasoningScore,
            time = uiState.reasoningTime,
            onMarksChange = viewModel::updateReasoningScore,
            onTimeChange = viewModel::updateReasoningTime
        )
        SubjectEntryCard(
            title = "English Comprehension",
            marks = uiState.englishScore,
            time = uiState.englishTime,
            onMarksChange = viewModel::updateEnglishScore,
            onTimeChange = viewModel::updateEnglishTime
        )
        SubjectEntryCard(
            title = "General Awareness",
            marks = uiState.gaScore,
            time = uiState.gaTime,
            onMarksChange = viewModel::updateGaScore,
            onTimeChange = viewModel::updateGaTime
        )

        ElevatedStatCard {
            Text(
                text = "Total Score: ${formatScore(uiState.totalScore)} / 200",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MockAnalysisColors.Primary
            )
            uiState.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MockAnalysisColors.Tertiary, style = MaterialTheme.typography.bodySmall)
            }
            uiState.successMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MockAnalysisColors.Secondary, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = viewModel::saveEntry,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MockAnalysisColors.Primary)
            ) {
                Text(
                    text = if (uiState.isSaving) "Saving..." else "Generate Analysis",
                    modifier = Modifier.padding(vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SubjectEntryCard(
    title: String,
    marks: String,
    time: String,
    onMarksChange: (String) -> Unit,
    onTimeChange: (String) -> Unit
) {
    ContainerCard {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = marks,
                onValueChange = onMarksChange,
                label = { Text("Marks (0-50)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = time,
                onValueChange = onTimeChange,
                label = { Text("Time (min)") },
                modifier = Modifier.width(140.dp),
                singleLine = true
            )
        }
    }
}

private fun formatScore(value: Float): String {
    return if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)
}
