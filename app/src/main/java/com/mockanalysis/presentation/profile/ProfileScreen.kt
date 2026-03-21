package com.mockanalysis.presentation.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mockanalysis.domain.model.Achievement
import com.mockanalysis.domain.model.LinkedPlatform
import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.UserProfile
import com.mockanalysis.presentation.common.components.AchievementBadge
import com.mockanalysis.presentation.common.components.ContainerCard
import com.mockanalysis.presentation.common.components.ElevatedStatCard
import com.mockanalysis.presentation.common.components.GrowthProgressBar
import com.mockanalysis.presentation.common.components.NavigationSettingItem
import com.mockanalysis.presentation.common.components.PlatformAccountItem
import com.mockanalysis.presentation.common.components.StatusPill
import com.mockanalysis.presentation.common.components.ToggleSettingItem
import com.mockanalysis.presentation.theme.ManropeFontFamily
import com.mockanalysis.presentation.theme.MockAnalysisColors
import com.mockanalysis.presentation.theme.MockAnalysisCorners

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSecurity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MockAnalysisColors.Primary)
                }
            }

            uiState.profile != null -> {
                ProfileContent(
                    profile = uiState.profile!!,
                    onTimeWeightedToggle = viewModel::updateTimeWeightedAnalysis,
                    onAccuracyFocusToggle = viewModel::updateAccuracyFocusMode,
                    onPredictiveToggle = viewModel::updatePredictiveGoalTracking,
                    onNavigateToSecurity = onNavigateToSecurity,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToHelp = onNavigateToHelp,
                    onNavigateToPrivacy = onNavigateToPrivacy,
                    onLogout = viewModel::logout,
                    isLoggingOut = uiState.isLoggingOut
                )
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error ?: "Profile not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MockAnalysisColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    onTimeWeightedToggle: (Boolean) -> Unit,
    onAccuracyFocusToggle: (Boolean) -> Unit,
    onPredictiveToggle: (Boolean) -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit,
    isLoggingOut: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        ProfileHeaderSection(profile)
        AchievementSection(achievements = profile.achievements, modifier = Modifier.fillMaxWidth())
        PlatformAccountsSection(platforms = profile.linkedPlatforms, modifier = Modifier.fillMaxWidth())
        PerformanceSettingsSection(
            settings = profile.settings,
            onTimeWeightedToggle = onTimeWeightedToggle,
            onAccuracyFocusToggle = onAccuracyFocusToggle,
            onPredictiveToggle = onPredictiveToggle,
            modifier = Modifier.fillMaxWidth()
        )
        AccountManagementSection(
            onNavigateToSecurity = onNavigateToSecurity,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToHelp = onNavigateToHelp,
            onNavigateToPrivacy = onNavigateToPrivacy,
            modifier = Modifier.fillMaxWidth()
        )
        LogoutSection(onLogout = onLogout, isLoggingOut = isLoggingOut)
    }
}

@Composable
private fun ProfileHeaderSection(profile: UserProfile) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "CANDIDATE PROFILE",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MockAnalysisColors.Primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = profile.name,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 38.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 42.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusPill(
                text = profile.targetExam.name,
                backgroundColor = MockAnalysisColors.PrimaryFixed,
                textColor = MockAnalysisColors.OnPrimaryFixed
            )
            StatusPill(
                text = profile.tier.displayName,
                backgroundColor = MockAnalysisColors.SecondaryFixed,
                textColor = MockAnalysisColors.OnSecondaryFixed
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedStatCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TARGET SCORE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MockAnalysisColors.OnSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${profile.targetScore}+",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp,
                    color = MockAnalysisColors.Primary
                )
                Text(
                    text = " / 200",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MockAnalysisColors.OnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            GrowthProgressBar(progress = profile.currentProgressPercentage / 100f)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${profile.currentProgressPercentage.toInt()}% OF TARGET PACED",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MockAnalysisColors.Secondary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AchievementSection(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    ContainerCard(modifier = modifier) {
        Text(
            text = "Mastery Milestones",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            achievements.forEach { achievement ->
                val icon = when (achievement.iconName) {
                    "search_insights" -> Icons.Outlined.QueryStats
                    "avg_pace" -> Icons.Outlined.Speed
                    "military_tech" -> Icons.Outlined.MilitaryTech
                    else -> Icons.Outlined.MilitaryTech
                }

                val gradientColors = when {
                    achievement.iconName == "avg_pace" -> listOf(
                        MockAnalysisColors.Secondary,
                        MockAnalysisColors.SecondaryContainer
                    )

                    else -> listOf(
                        MockAnalysisColors.Primary,
                        MockAnalysisColors.PrimaryContainer
                    )
                }

                AchievementBadge(
                    name = achievement.name,
                    icon = icon,
                    isUnlocked = achievement.isUnlocked,
                    gradientColors = gradientColors
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.5f),
            shape = MockAnalysisCorners.Large
        ) {
            Text(
                text = "\"Success is the sum of small efforts, repeated day-in and day-out.\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MockAnalysisColors.OnSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun PlatformAccountsSection(
    platforms: List<LinkedPlatform>,
    modifier: Modifier = Modifier
) {
    ElevatedStatCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = MockAnalysisColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Platform Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            platforms.forEach { platform ->
                val (bgColor, textColor) = when (platform.id) {
                    "testbook" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                    "oliveboard" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                    else -> MockAnalysisColors.SurfaceContainerHigh to MockAnalysisColors.OnSurface
                }

                PlatformAccountItem(
                    name = platform.name,
                    shortCode = platform.shortCode,
                    isConnected = platform.isConnected,
                    backgroundColor = bgColor,
                    textColor = textColor,
                    onAction = {}
                )
            }
        }
    }
}

@Composable
private fun PerformanceSettingsSection(
    settings: ProfileSettings,
    onTimeWeightedToggle: (Boolean) -> Unit,
    onAccuracyFocusToggle: (Boolean) -> Unit,
    onPredictiveToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null,
                tint = MockAnalysisColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Performance Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ToggleSettingItem(
                title = "Time-weighted Analysis",
                description = "Prioritize recent mock results in insights",
                isEnabled = settings.timeWeightedAnalysis,
                onToggle = onTimeWeightedToggle
            )
            ToggleSettingItem(
                title = "Accuracy Focus Mode",
                description = "Highlight error patterns during review",
                isEnabled = settings.accuracyFocusMode,
                onToggle = onAccuracyFocusToggle
            )
            ToggleSettingItem(
                title = "Predictive Goal Tracking",
                description = "Forecast final exam scores based on trends",
                isEnabled = settings.predictiveGoalTracking,
                onToggle = onPredictiveToggle
            )
        }
    }
}

@Composable
private fun AccountManagementSection(
    onNavigateToSecurity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedStatCard(modifier = modifier) {
        Text(
            text = "Account Management",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        NavigationSettingItem(
            title = "Account Security",
            icon = Icons.Outlined.Shield,
            onClick = onNavigateToSecurity
        )
        NavigationSettingItem(
            title = "Notification Preferences",
            icon = Icons.Outlined.NotificationsActive,
            onClick = onNavigateToNotifications
        )
        NavigationSettingItem(
            title = "Help & Support",
            icon = Icons.Outlined.HelpOutline,
            onClick = onNavigateToHelp
        )
        NavigationSettingItem(
            title = "Privacy Policy",
            icon = Icons.Outlined.Description,
            onClick = onNavigateToPrivacy
        )
    }
}

@Composable
private fun LogoutSection(
    onLogout: () -> Unit,
    isLoggingOut: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onLogout,
            enabled = !isLoggingOut,
            colors = ButtonDefaults.buttonColors(
                containerColor = MockAnalysisColors.TertiaryFixed,
                contentColor = MockAnalysisColors.OnTertiaryFixed
            ),
            shape = MockAnalysisCorners.Large
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MockAnalysisColors.OnTertiaryFixed,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout from Device",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
