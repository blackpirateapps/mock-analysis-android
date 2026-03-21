package com.mockanalysis.data.repository

import com.mockanalysis.data.local.dao.UserDao
import com.mockanalysis.data.local.entity.AchievementEntity
import com.mockanalysis.data.local.entity.LinkedPlatformEntity
import com.mockanalysis.data.local.entity.UserProfileEntity
import com.mockanalysis.data.source.MockDataSource
import com.mockanalysis.domain.model.Achievement
import com.mockanalysis.domain.model.LinkedPlatform
import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.TargetExam
import com.mockanalysis.domain.model.UserProfile
import com.mockanalysis.domain.model.UserTier
import com.mockanalysis.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation of UserRepository.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val mockDataSource: MockDataSource
) : UserRepository {

    init {
        // Seed starter profile on first launch.
        kotlinx.coroutines.runBlocking {
            if (userDao.getProfileCount() == 0) {
                saveUserProfile(mockDataSource.getUserProfile())
            }
        }
    }

    override fun getUserProfile(): Flow<UserProfile?> {
        return userDao.observeUserProfile().map { profileEntity ->
            profileEntity?.let { runBlocking { mapEntityToDomain(it) } }
        }
    }

    override suspend fun updateSettings(settings: ProfileSettings) {
        val current = userDao.getUserProfile() ?: return
        userDao.insertUserProfile(
            current.copy(
                timeWeightedAnalysis = settings.timeWeightedAnalysis,
                accuracyFocusMode = settings.accuracyFocusMode,
                predictiveGoalTracking = settings.predictiveGoalTracking,
                notificationsEnabled = settings.notificationsEnabled,
                darkModeEnabled = settings.darkModeEnabled
            )
        )
    }

    override suspend fun linkPlatform(platformId: String, username: String) {
        val platforms = userDao.getLinkedPlatforms().map { platform ->
            if (platform.id == platformId) {
                platform.copy(
                    isConnected = true,
                    username = username,
                    connectedAt = System.currentTimeMillis()
                )
            } else {
                platform
            }
        }
        userDao.insertLinkedPlatforms(platforms)
    }

    override suspend fun unlinkPlatform(platformId: String) {
        val platforms = userDao.getLinkedPlatforms().map { platform ->
            if (platform.id == platformId) {
                platform.copy(
                    isConnected = false,
                    username = null,
                    connectedAt = null
                )
            } else {
                platform
            }
        }
        userDao.insertLinkedPlatforms(platforms)
    }

    override suspend fun logout() {
        userDao.clearUserProfile()
        userDao.clearAchievements()
        userDao.clearLinkedPlatforms()
    }

    override suspend fun refreshProfile() {
        // Local-first app, no remote refresh.
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertUserProfile(
            UserProfileEntity(
                id = profile.id,
                name = profile.name,
                email = profile.email,
                avatarUrl = profile.avatarUrl,
                targetExamId = profile.targetExam.id,
                targetExamName = profile.targetExam.name,
                targetExamYear = profile.targetExam.year,
                targetExamShortName = profile.targetExam.shortName,
                tier = profile.tier.name,
                targetScore = profile.targetScore,
                currentProgressPercentage = profile.currentProgressPercentage,
                timeWeightedAnalysis = profile.settings.timeWeightedAnalysis,
                accuracyFocusMode = profile.settings.accuracyFocusMode,
                predictiveGoalTracking = profile.settings.predictiveGoalTracking,
                notificationsEnabled = profile.settings.notificationsEnabled,
                darkModeEnabled = profile.settings.darkModeEnabled
            )
        )

        userDao.clearAchievements()
        userDao.insertAchievements(
            profile.achievements.map { achievement ->
                AchievementEntity(
                    id = achievement.id,
                    name = achievement.name,
                    description = achievement.description,
                    iconName = achievement.iconName,
                    isUnlocked = achievement.isUnlocked,
                    unlockedAt = achievement.unlockedAt,
                    progress = achievement.progress
                )
            }
        )

        userDao.clearLinkedPlatforms()
        userDao.insertLinkedPlatforms(
            profile.linkedPlatforms.map { platform ->
                LinkedPlatformEntity(
                    id = platform.id,
                    name = platform.name,
                    shortCode = platform.shortCode,
                    isConnected = platform.isConnected,
                    username = platform.username,
                    connectedAt = platform.connectedAt
                )
            }
        )
    }

    private suspend fun mapEntityToDomain(entity: UserProfileEntity): UserProfile {
        val achievements = userDao.getAchievements().map { achievement ->
            Achievement(
                id = achievement.id,
                name = achievement.name,
                description = achievement.description,
                iconName = achievement.iconName,
                isUnlocked = achievement.isUnlocked,
                unlockedAt = achievement.unlockedAt,
                progress = achievement.progress
            )
        }
        val linkedPlatforms = userDao.getLinkedPlatforms().map { platform ->
            LinkedPlatform(
                id = platform.id,
                name = platform.name,
                shortCode = platform.shortCode,
                isConnected = platform.isConnected,
                username = platform.username,
                connectedAt = platform.connectedAt
            )
        }

        return UserProfile(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            targetExam = TargetExam(
                id = entity.targetExamId,
                name = entity.targetExamName,
                year = entity.targetExamYear,
                shortName = entity.targetExamShortName
            ),
            tier = runCatching { UserTier.valueOf(entity.tier) }.getOrDefault(UserTier.FREE),
            targetScore = entity.targetScore,
            currentProgressPercentage = entity.currentProgressPercentage,
            achievements = achievements,
            linkedPlatforms = linkedPlatforms,
            settings = ProfileSettings(
                timeWeightedAnalysis = entity.timeWeightedAnalysis,
                accuracyFocusMode = entity.accuracyFocusMode,
                predictiveGoalTracking = entity.predictiveGoalTracking,
                notificationsEnabled = entity.notificationsEnabled,
                darkModeEnabled = entity.darkModeEnabled
            )
        )
    }
}
