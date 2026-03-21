package com.mockanalysis.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Mock Analysis Theme
 * 
 * Implements "The Cognitive Architect" design system:
 * - Clean, high-end aesthetic for a study sanctuary
 * - Editorial scaling with Manrope/Inter typography
 * - Tonal layering for depth (no structural lines)
 * - Primary blue for trust, green for success, red for alerts
 */
@Composable
fun MockAnalysisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Currently only light theme is fully designed
    // Dark theme would need separate color mappings
    val colorScheme = LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MockAnalysisTypography,
        shapes = MockAnalysisShapes,
        content = content
    )
}
