package com.mockanalysis.presentation.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
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
import com.mockanalysis.domain.model.*
import com.mockanalysis.presentation.common.components.*
import com.mockanalysis.presentation.theme.ManropeFontFamily
import com.mockanalysis.presentation.theme.MockAnalysisColors
import com.mockanalysis.presentation.theme.MockAnalysisCorners
import java.time.format.DateTimeFormatter

/**
 * Analysis Hub Screen - Main analytics dashboard showing mock test performance.
 */
@Composable
fun AnalysisHubScreen(
    viewModel: AnalysisHubViewModel = hiltViewModel(),
    onStartPractice: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MockAnalysisColors.Primary)
            }
        } else if (uiState.analysis != null) {
            AnalysisHubContent(
                analysis = uiState.analysis!!,
                scoreImprovement = uiState.scoreImprovement,
                onStartPractice = onStartPractice
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "No data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MockAnalysisColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnalysisHubContent(
    analysis: MockAnalysis,
    scoreImprovement: Float?,
    onStartPractice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Hero Section
        HeroSection(analysis)
        
        // Score and Benchmarking Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreCard(
                analysis = analysis,
                scoreImprovement = scoreImprovement,
                modifier = Modifier.weight(5f)
            )
            BenchmarkingCard(
                benchmarks = analysis.benchmarks,
                modifier = Modifier.weight(7f)
            )
        }
        
        // Proficiency and Milestone Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProficiencySection(
                subjectScores = analysis.subjectScores,
                modifier = Modifier.weight(8f)
            )
            MilestoneCard(
                focusAreas = analysis.focusAreas,
                onStartPractice = onStartPractice,
                modifier = Modifier.weight(4f)
            )
        }
        
        // Time vs Accuracy Section
        TimeAccuracySection(questionMetrics = analysis.questionMetrics)
    }
}

@Composable
private fun HeroSection(analysis: MockAnalysis) {
    GradientHeroCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ANALYSIS HUB",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
            
            Text(
                text = analysis.mockName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            
            Text(
                text = "Deep-dive diagnostics of your performance from ${
                    analysis.date.format(DateTimeFormatter.ofPattern("MMM d"))
                }. Your trajectory shows mastery in Quantitative Aptitude but requires focus on GA precision.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (analysis.isPremium) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "PREMIUM ANALYSIS ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(
    analysis: MockAnalysis,
    scoreImprovement: Float?,
    modifier: Modifier = Modifier
) {
    ElevatedStatCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Latest Score",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MockAnalysisColors.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = analysis.score.toString(),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        color = MockAnalysisColors.Primary
                    )
                    Text(
                        text = " / ${analysis.maxScore.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MockAnalysisColors.OnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            
            // Percentile badge
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MockAnalysisColors.SecondaryContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PERCENTILE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MockAnalysisColors.OnSecondaryContainer,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${analysis.percentile}%",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = MockAnalysisColors.OnSecondaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = MockAnalysisColors.SurfaceContainerLow)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = null,
                    tint = MockAnalysisColors.Secondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "+${scoreImprovement?.toString() ?: "0"} from last mock",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MockAnalysisColors.Secondary
                )
            }
            
            Text(
                text = "RANK ${analysis.rank} / ${String.format("%,d", analysis.totalParticipants)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MockAnalysisColors.OnSurfaceVariant,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        GrowthProgressBar(progress = analysis.score / analysis.maxScore)
    }
}

@Composable
private fun BenchmarkingCard(
    benchmarks: Benchmarks,
    modifier: Modifier = Modifier
) {
    ContainerCard(modifier = modifier) {
        Text(
            text = "Competitive Benchmarking",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BenchmarkBar(
                label = "You",
                value = benchmarks.userScore,
                maxValue = benchmarks.maxPossibleScore,
                displayValue = benchmarks.userScore.toString(),
                color = MockAnalysisColors.Primary
            )
            
            BenchmarkBar(
                label = "Avg. Aspirant",
                value = benchmarks.averageScore,
                maxValue = benchmarks.maxPossibleScore,
                displayValue = benchmarks.averageScore.toString(),
                color = MockAnalysisColors.OutlineVariant.copy(alpha = 0.4f)
            )
            
            BenchmarkBar(
                label = "Topper (AIR 1)",
                value = benchmarks.topperScore,
                maxValue = benchmarks.maxPossibleScore,
                displayValue = benchmarks.topperScore.toString(),
                color = MockAnalysisColors.SecondaryFixedDim
            )
        }
    }
}

@Composable
private fun ProficiencySection(
    subjectScores: List<SubjectScore>,
    modifier: Modifier = Modifier
) {
    ElevatedStatCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Subject-wise Proficiency",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                subjectScores.forEach { score ->
                    MetadataTag(text = score.shortName)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Subject scores grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            subjectScores.forEach { score ->
                SubjectProficiencyCard(
                    score = score.score,
                    maxScore = score.maxScore,
                    proficiencyLevel = score.proficiencyLevel,
                    shortName = score.shortName,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Visual bar chart simulation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(MockAnalysisCorners.Large)
                .background(MockAnalysisColors.SurfaceContainerLow)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(0.8f, 0.6f, 0.95f, 0.4f, 0.7f, 0.85f, 0.55f, 0.9f).forEach { height ->
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .fillMaxHeight(height)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(MockAnalysisColors.Primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    focusAreas: List<FocusArea>,
    onStartPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MockAnalysisColors.TertiaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MockAnalysisColors.OnTertiaryFixed,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = "Next Milestone",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            focusAreas.take(2).forEachIndexed { index, area ->
                FocusAreaCard(
                    title = area.title,
                    subtitle = "Focus Area ${index + 1}",
                    description = area.description
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onStartPractice,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MockAnalysisColors.Primary
            ),
            shape = MockAnalysisCorners.Large
        ) {
            Text(
                text = "Start Targeted Practice",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun TimeAccuracySection(questionMetrics: List<QuestionMetric>) {
    ElevatedStatCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Time vs. Accuracy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Mapping cognitive load per question across the 60-minute session.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MockAnalysisColors.OnSurfaceVariant
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(color = MockAnalysisColors.Primary, label = "CORRECT")
                LegendItem(color = MockAnalysisColors.Tertiary, label = "INCORRECT")
                LegendItem(color = MockAnalysisColors.OutlineVariant, label = "SKIPPED")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Scatter plot simulation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MockAnalysisCorners.Large)
                .background(MockAnalysisColors.SurfaceContainerLow)
                .padding(24.dp)
        ) {
            // Grid lines (simplified)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(5) {
                    Divider(
                        color = MockAnalysisColors.OnSurfaceVariant.copy(alpha = 0.1f)
                    )
                }
            }
            
            // Data points simulation
            Box(modifier = Modifier.fillMaxSize()) {
                // High accuracy, low time (good)
                DataPoint(
                    color = MockAnalysisColors.Primary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 20.dp, y = 10.dp)
                )
                DataPoint(
                    color = MockAnalysisColors.Primary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 40.dp, y = 5.dp)
                )
                DataPoint(
                    color = MockAnalysisColors.Primary,
                    size = 14,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 70.dp, y = 2.dp)
                )
                
                // Low accuracy, high time (pain points)
                DataPoint(
                    color = MockAnalysisColors.Tertiary,
                    size = 14,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-40).dp, y = (-20).dp)
                )
                DataPoint(
                    color = MockAnalysisColors.Tertiary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-20).dp, y = (-30).dp)
                )
                
                // Mixed area
                DataPoint(
                    color = MockAnalysisColors.Primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-20).dp, y = 10.dp)
                )
                DataPoint(
                    color = MockAnalysisColors.OutlineVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 10.dp, y = (-5).dp)
                )
            }
            
            // Axis labels
            Text(
                text = "TIME SPENT PER QUESTION (SECONDS)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MockAnalysisColors.OnSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun DataPoint(
    color: Color,
    modifier: Modifier = Modifier,
    size: Int = 10
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}
