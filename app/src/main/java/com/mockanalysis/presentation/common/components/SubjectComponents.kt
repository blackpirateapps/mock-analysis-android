package com.mockanalysis.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mockanalysis.domain.model.ProficiencyLevel
import com.mockanalysis.presentation.theme.MockAnalysisColors

/**
 * Subject proficiency card showing score and progress.
 */
@Composable
fun SubjectProficiencyCard(
    score: Float,
    maxScore: Float,
    proficiencyLevel: ProficiencyLevel,
    shortName: String,
    modifier: Modifier = Modifier
) {
    val (labelColor, progressColor) = when (proficiencyLevel) {
        ProficiencyLevel.ELITE -> MockAnalysisColors.Secondary to MockAnalysisColors.Primary
        ProficiencyLevel.STRONG -> MockAnalysisColors.Secondary to MockAnalysisColors.Primary
        ProficiencyLevel.SOLID -> MockAnalysisColors.OnSurfaceVariant to MockAnalysisColors.Primary
        ProficiencyLevel.DEVELOPING -> MockAnalysisColors.OnSurfaceVariant to MockAnalysisColors.Primary
        ProficiencyLevel.ALERT -> MockAnalysisColors.Tertiary to MockAnalysisColors.Tertiary
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Score
        Row {
            Text(
                text = score.toString().removeSuffix(".0"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (proficiencyLevel == ProficiencyLevel.ALERT) 
                    MockAnalysisColors.Tertiary else MockAnalysisColors.Primary
            )
            Text(
                text = "/${maxScore.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MockAnalysisColors.OnSurfaceVariant,
                modifier = Modifier.alignByBaseline()
            )
        }
        
        // Proficiency label
        Text(
            text = "${proficiencyLevel.displayName}: $shortName",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = labelColor,
            letterSpacing = 1.sp
        )
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(progressColor.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((score / maxScore).coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(50))
                    .background(progressColor)
            )
        }
    }
}

/**
 * Tag/badge component for metadata labels.
 */
@Composable
fun MetadataTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MockAnalysisColors.SurfaceContainer
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MockAnalysisColors.OnSurfaceVariant,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/**
 * Pill badge for status indicators.
 */
@Composable
fun StatusPill(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
