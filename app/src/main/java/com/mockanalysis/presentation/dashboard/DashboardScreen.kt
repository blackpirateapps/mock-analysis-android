package com.mockanalysis.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mockanalysis.domain.model.MockAnalysis
import com.mockanalysis.presentation.common.components.ContainerCard
import com.mockanalysis.presentation.common.components.ElevatedStatCard
import com.mockanalysis.presentation.common.components.GradientHeroCard
import com.mockanalysis.presentation.common.components.GrowthProgressBar
import com.mockanalysis.presentation.theme.ManropeFontFamily
import com.mockanalysis.presentation.theme.MockAnalysisColors

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToInput: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MockAnalysisColors.Primary)
                }
            }

            uiState.latestAnalysis == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error ?: "No dashboard data available",
                        color = MockAnalysisColors.OnSurfaceVariant
                    )
                }
            }

            else -> {
                DashboardContent(
                    state = uiState,
                    onNavigateToInput = onNavigateToInput
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onNavigateToInput: () -> Unit
) {
    val latest = state.latestAnalysis ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        GradientHeroCard {
            Text(
                text = "MASTERY HUB",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome back, ${state.profile?.name?.substringBefore(" ") ?: "Aspirant"}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your path to SSC CGL Tier-1 is ${(state.progressToTarget * 100).toInt()}% architected.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNavigateToInput,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MockAnalysisColors.Primary
                )
            ) {
                Text(text = "Input New Mock Result", fontWeight = FontWeight.Bold)
            }
        }

        ElevatedStatCard {
            Text(
                text = "CURRENT AVG",
                style = MaterialTheme.typography.labelSmall,
                color = MockAnalysisColors.OnSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatScore(state.currentAverage),
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                color = MockAnalysisColors.Primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Target: ${state.profile?.targetScore ?: 175}/200",
                style = MaterialTheme.typography.bodySmall,
                color = MockAnalysisColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.scoreDeltaVsPrevious?.let { "${if (it >= 0) "+" else ""}${formatScore(it)} vs previous" }
                    ?: "No previous delta yet",
                style = MaterialTheme.typography.labelMedium,
                color = if ((state.scoreDeltaVsPrevious ?: 0f) >= 0f) {
                    MockAnalysisColors.Secondary
                } else {
                    MockAnalysisColors.Tertiary
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            GrowthProgressBar(progress = state.progressToTarget)
        }

        ContainerCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performance Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Outlined.ShowChart,
                    contentDescription = null,
                    tint = MockAnalysisColors.Primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TrendRow(state.analyses.take(6))
        }

        ElevatedStatCard {
            Text(
                text = "Subject Proficiency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            latest.subjectScores.forEach { subject ->
                val pct = (subject.score / subject.maxScore).coerceIn(0f, 1f)
                Text(
                    text = subject.subjectName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                GrowthProgressBar(
                    progress = pct,
                    color = if (pct >= 0.7f) MockAnalysisColors.Primary else MockAnalysisColors.Tertiary,
                    backgroundColor = MockAnalysisColors.SurfaceContainerHigh
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        InsightCard(
            title = "Focus Area",
            message = "Modern History dates are your biggest bottleneck this week.",
            iconTint = MockAnalysisColors.Tertiary,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MockAnalysisColors.Tertiary
                )
            }
        )

        InsightCard(
            title = "Strengths",
            message = "Geometry accuracy reached 100% in your last 3 mocks.",
            iconTint = MockAnalysisColors.Primary,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MockAnalysisColors.Primary
                )
            }
        )

        InsightCard(
            title = "Time Management",
            message = "Avg. speed is 38s per question. Target is 35s.",
            iconTint = MockAnalysisColors.Secondary,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    tint = MockAnalysisColors.Secondary
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TrendRow(analyses: List<MockAnalysis>) {
    if (analyses.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        analyses.reversed().forEachIndexed { index, analysis ->
            val barHeight = (analysis.score / analysis.maxScore).coerceIn(0.2f, 1f)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height((barHeight * 80).dp)
                            .clip(CircleShape)
                            .background(if (index == analyses.lastIndex) MockAnalysisColors.Primary else MockAnalysisColors.Primary.copy(alpha = 0.35f))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (index == analyses.lastIndex) "Latest" else "M${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MockAnalysisColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    message: String,
    iconTint: Color,
    icon: @Composable () -> Unit
) {
    ContainerCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MockAnalysisColors.OnSurfaceVariant)
    }
}

private fun formatScore(value: Float): String {
    return if (value % 1f == 0f) value.toInt().toString() else String.format("%.1f", value)
}
