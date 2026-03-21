package com.mockanalysis.domain.model

/**
 * Domain model representing a user's profile.
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val targetExam: TargetExam,
    val tier: UserTier,
    val targetScore: Int,
    val currentProgressPercentage: Float,
    val achievements: List<Achievement>,
    val linkedPlatforms: List<LinkedPlatform>,
    val settings: ProfileSettings
)

/**
 * Target examination details.
 */
data class TargetExam(
    val id: String,
    val name: String,
    val year: Int,
    val shortName: String
)

/**
 * User membership tier.
 */
enum class UserTier(val displayName: String) {
    FREE("Free"),
    STANDARD("Standard"),
    ELITE("Elite"),
    PREMIUM("Premium")
}

/**
 * User achievement/badge.
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null, // timestamp
    val progress: Float? = null // 0.0 to 1.0 for locked achievements showing progress
)

/**
 * External platform account linkage.
 */
data class LinkedPlatform(
    val id: String,
    val name: String,
    val shortCode: String,
    val isConnected: Boolean,
    val username: String? = null,
    val connectedAt: Long? = null
)

/**
 * User's profile settings.
 */
data class ProfileSettings(
    val timeWeightedAnalysis: Boolean = true,
    val accuracyFocusMode: Boolean = false,
    val predictiveGoalTracking: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)

/**
 * Account management menu items.
 */
data class AccountMenuItem(
    val id: String,
    val title: String,
    val iconName: String,
    val route: String? = null
)
