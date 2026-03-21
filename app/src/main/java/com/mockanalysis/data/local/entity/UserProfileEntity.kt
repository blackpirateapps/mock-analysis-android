package com.mockanalysis.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val targetExamId: String,
    val targetExamName: String,
    val targetExamYear: Int,
    val targetExamShortName: String,
    val tier: String,
    val targetScore: Int,
    val currentProgressPercentage: Float,
    val timeWeightedAnalysis: Boolean,
    val accuracyFocusMode: Boolean,
    val predictiveGoalTracking: Boolean,
    val notificationsEnabled: Boolean,
    val darkModeEnabled: Boolean
)

@Entity(tableName = "achievement")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long?,
    val progress: Float?
)

@Entity(tableName = "linked_platform")
data class LinkedPlatformEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortCode: String,
    val isConnected: Boolean,
    val username: String?,
    val connectedAt: Long?
)
