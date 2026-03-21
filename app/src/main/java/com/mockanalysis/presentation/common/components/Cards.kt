package com.mockanalysis.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mockanalysis.presentation.theme.MockAnalysisColors
import com.mockanalysis.presentation.theme.MockAnalysisCorners

/**
 * Hero card with gradient background matching the design system.
 * Used for prominent headers like the Analysis Hub hero section.
 */
@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MockAnalysisCorners.ExtraLarge)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MockAnalysisColors.Primary,
                        MockAnalysisColors.PrimaryContainer
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Decorative blur circle (simulated)
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.1f))
        )
        
        Column(content = content)
    }
}

/**
 * Elevated card using the design system's tonal layering.
 * Uses surface-container-lowest with ambient shadow.
 */
@Composable
fun ElevatedStatCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = MockAnalysisCorners.Large,
                ambientColor = MockAnalysisColors.OnSurface.copy(alpha = 0.06f),
                spotColor = MockAnalysisColors.OnSurface.copy(alpha = 0.06f)
            ),
        color = MockAnalysisColors.SurfaceContainerLowest,
        shape = MockAnalysisCorners.Large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}

/**
 * Low-emphasis container card using surface-container-low.
 * Used for sections that need subtle containment.
 */
@Composable
fun ContainerCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = MockAnalysisColors.SurfaceContainerLow,
        shape = MockAnalysisCorners.Large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}

/**
 * Focus area card with subtle border accent.
 */
@Composable
fun FocusAreaCard(
    title: String,
    subtitle: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MockAnalysisColors.SurfaceContainerLowest,
        shape = MockAnalysisCorners.Large,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MockAnalysisColors.Primary.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MockAnalysisColors.Primary,
                letterSpacing = 1.sp
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MockAnalysisColors.OnSurfaceVariant
            )
        }
    }
}
