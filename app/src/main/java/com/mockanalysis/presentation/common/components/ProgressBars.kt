package com.mockanalysis.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mockanalysis.presentation.theme.MockAnalysisColors

/**
 * "Growth Radiance" Progress Bar from the design system.
 * Features a glow effect on the leading edge to represent momentum.
 */
@Composable
fun GrowthProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MockAnalysisColors.Secondary,
    backgroundColor: Color = MockAnalysisColors.SecondaryContainer,
    height: Int = 8
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = color.copy(alpha = 0.4f),
                    spotColor = color.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

/**
 * Primary-colored progress bar (for general progress).
 */
@Composable
fun PrimaryProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 4
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50))
            .background(MockAnalysisColors.Primary.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(MockAnalysisColors.Primary)
        )
    }
}

/**
 * Alert-colored progress bar (for weak areas).
 */
@Composable
fun AlertProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 4
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50))
            .background(MockAnalysisColors.Tertiary.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(MockAnalysisColors.Tertiary)
        )
    }
}

/**
 * Horizontal bar chart item for competitive benchmarking.
 */
@Composable
fun BenchmarkBar(
    label: String,
    value: Float,
    maxValue: Float,
    displayValue: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Label
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MockAnalysisColors.OnSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.width(100.dp)
        )
        
        // Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MockAnalysisColors.SurfaceContainerLowest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((value / maxValue).coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
            )
            
            // Value label
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MockAnalysisColors.OnSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            )
        }
    }
}
