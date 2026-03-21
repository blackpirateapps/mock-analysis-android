package com.mockanalysis.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Mock Analysis Design System Colors
 * Based on "The Cognitive Architect" design language
 * 
 * Primary: Exam Blue (#005BBF) - Trust and authority
 * Secondary: Success Green (#1B6D24) - Growth and achievement  
 * Tertiary: Alert Red (#B91A20) - Attention and weak areas
 */
object MockAnalysisColors {
    
    // Primary Colors
    val Primary = Color(0xFF005BBF)
    val PrimaryContainer = Color(0xFF1A73E8)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFFFFFFFF)
    val PrimaryFixed = Color(0xFFD8E2FF)
    val PrimaryFixedDim = Color(0xFFADC7FF)
    val OnPrimaryFixed = Color(0xFF001A41)
    val OnPrimaryFixedVariant = Color(0xFF004493)
    val InversePrimary = Color(0xFFADC7FF)
    
    // Secondary Colors (Success Green)
    val Secondary = Color(0xFF1B6D24)
    val SecondaryContainer = Color(0xFFA0F399)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF217128)
    val SecondaryFixed = Color(0xFFA3F69C)
    val SecondaryFixedDim = Color(0xFF88D982)
    val OnSecondaryFixed = Color(0xFF002204)
    val OnSecondaryFixedVariant = Color(0xFF005312)
    
    // Tertiary Colors (Alert Red)
    val Tertiary = Color(0xFFB91A20)
    val TertiaryContainer = Color(0xFFDD3635)
    val OnTertiary = Color(0xFFFFFFFF)
    val OnTertiaryContainer = Color(0xFFFFFFFF)
    val TertiaryFixed = Color(0xFFFFDAD6)
    val TertiaryFixedDim = Color(0xFFFFB3AC)
    val OnTertiaryFixed = Color(0xFF410003)
    val OnTertiaryFixedVariant = Color(0xFF930010)
    
    // Error Colors
    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnError = Color(0xFFFFFFFF)
    val OnErrorContainer = Color(0xFF93000A)
    
    // Surface Colors (Editorial layering system)
    val Background = Color(0xFFF8F9FA)
    val OnBackground = Color(0xFF191C1D)
    val Surface = Color(0xFFF8F9FA)
    val OnSurface = Color(0xFF191C1D)
    val SurfaceVariant = Color(0xFFE1E3E4)
    val OnSurfaceVariant = Color(0xFF414754)
    val SurfaceTint = Color(0xFF005BC0)
    
    // Surface containers (for "No-Line" rule - depth through background shifts)
    val SurfaceBright = Color(0xFFF8F9FA)
    val SurfaceDim = Color(0xFFD9DADB)
    val SurfaceContainer = Color(0xFFEDEEEF)
    val SurfaceContainerHigh = Color(0xFFE7E8E9)
    val SurfaceContainerHighest = Color(0xFFE1E3E4)
    val SurfaceContainerLow = Color(0xFFF3F4F5)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    
    // Inverse Colors
    val InverseSurface = Color(0xFF2E3132)
    val InverseOnSurface = Color(0xFFF0F1F2)
    
    // Outline Colors
    val Outline = Color(0xFF727785)
    val OutlineVariant = Color(0xFFC1C6D6)
    
    // Scrim
    val Scrim = Color(0xFF000000)
}

// Light Theme Colors
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = MockAnalysisColors.Primary,
    onPrimary = MockAnalysisColors.OnPrimary,
    primaryContainer = MockAnalysisColors.PrimaryContainer,
    onPrimaryContainer = MockAnalysisColors.OnPrimaryContainer,
    secondary = MockAnalysisColors.Secondary,
    onSecondary = MockAnalysisColors.OnSecondary,
    secondaryContainer = MockAnalysisColors.SecondaryContainer,
    onSecondaryContainer = MockAnalysisColors.OnSecondaryContainer,
    tertiary = MockAnalysisColors.Tertiary,
    onTertiary = MockAnalysisColors.OnTertiary,
    tertiaryContainer = MockAnalysisColors.TertiaryContainer,
    onTertiaryContainer = MockAnalysisColors.OnTertiaryContainer,
    error = MockAnalysisColors.Error,
    onError = MockAnalysisColors.OnError,
    errorContainer = MockAnalysisColors.ErrorContainer,
    onErrorContainer = MockAnalysisColors.OnErrorContainer,
    background = MockAnalysisColors.Background,
    onBackground = MockAnalysisColors.OnBackground,
    surface = MockAnalysisColors.Surface,
    onSurface = MockAnalysisColors.OnSurface,
    surfaceVariant = MockAnalysisColors.SurfaceVariant,
    onSurfaceVariant = MockAnalysisColors.OnSurfaceVariant,
    surfaceTint = MockAnalysisColors.SurfaceTint,
    inverseSurface = MockAnalysisColors.InverseSurface,
    inverseOnSurface = MockAnalysisColors.InverseOnSurface,
    inversePrimary = MockAnalysisColors.InversePrimary,
    outline = MockAnalysisColors.Outline,
    outlineVariant = MockAnalysisColors.OutlineVariant,
    scrim = MockAnalysisColors.Scrim,
    surfaceBright = MockAnalysisColors.SurfaceBright,
    surfaceDim = MockAnalysisColors.SurfaceDim,
    surfaceContainer = MockAnalysisColors.SurfaceContainer,
    surfaceContainerHigh = MockAnalysisColors.SurfaceContainerHigh,
    surfaceContainerHighest = MockAnalysisColors.SurfaceContainerHighest,
    surfaceContainerLow = MockAnalysisColors.SurfaceContainerLow,
    surfaceContainerLowest = MockAnalysisColors.SurfaceContainerLowest
)

// Extended color properties for design system specific colors
object ExtendedColors {
    val PrimaryFixed = MockAnalysisColors.PrimaryFixed
    val PrimaryFixedDim = MockAnalysisColors.PrimaryFixedDim
    val OnPrimaryFixed = MockAnalysisColors.OnPrimaryFixed
    val SecondaryFixed = MockAnalysisColors.SecondaryFixed
    val SecondaryFixedDim = MockAnalysisColors.SecondaryFixedDim
    val TertiaryFixed = MockAnalysisColors.TertiaryFixed
    val TertiaryFixedDim = MockAnalysisColors.TertiaryFixedDim
    val OnTertiaryFixed = MockAnalysisColors.OnTertiaryFixed
}
