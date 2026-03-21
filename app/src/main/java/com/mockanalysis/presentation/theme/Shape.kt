package com.mockanalysis.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape system for Mock Analysis app.
 * 
 * Design System specifies:
 * - DEFAULT: 0.25rem (4dp) - Avoid for large containers
 * - lg: 0.25rem (4dp)
 * - xl: 0.5rem (8dp)
 * - full: 0.75rem (12dp) - Modern, encouraging vibe for cards
 * 
 * Note: Use xl (12dp) for cards to keep the vibe modern.
 */
val MockAnalysisShapes = Shapes(
    // Extra small for subtle rounding
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small for inputs and small elements
    small = RoundedCornerShape(6.dp),
    
    // Medium for buttons and medium elements
    medium = RoundedCornerShape(8.dp),
    
    // Large for cards and containers (xl from design system)
    large = RoundedCornerShape(12.dp),
    
    // Extra large for hero sections and prominent cards
    extraLarge = RoundedCornerShape(16.dp)
)

/**
 * Additional shape constants for specific use cases.
 */
object MockAnalysisCorners {
    val None = RoundedCornerShape(0.dp)
    val Small = RoundedCornerShape(4.dp)
    val Medium = RoundedCornerShape(8.dp)
    val Large = RoundedCornerShape(12.dp)
    val ExtraLarge = RoundedCornerShape(16.dp)
    val Full = RoundedCornerShape(24.dp)
    val Pill = RoundedCornerShape(50)
    
    // Top only corners for bottom sheets
    val TopLarge = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    val TopExtraLarge = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}
