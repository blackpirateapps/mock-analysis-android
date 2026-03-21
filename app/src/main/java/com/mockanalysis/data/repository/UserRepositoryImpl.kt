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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
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

    override fun getUserProfile(): Flow<UserProfile?> {
        return combine(
            userDao.observeUserProfile(),
            userDao.observeAchievements(),
            userDao.observeLinkedPlatforms()
        ) { profileEntity, achievements, platforms ->
            profileEntity?.let {
                mapEntityToDomain(
                    entity = it,
                    achievementEntities = achievements,
                    platformEntities = platforms
                )
            }
        }.onStart {
            seedIfEmpty()
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
        val defaultProfile = mockDataSource.getUserProfile()
        userDao.replaceProfileBundle(
            profile = defaultProfile.toEntity(),
            achievements = defaultProfile.achievements.map { it.toEntity() },
            platforms = defaultProfile.linkedPlatforms.map { it.toEntity() }
        )
    }

    override suspend fun refreshProfile() {
        // Local-first app, no remote refresh.
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.replaceProfileBundle(
            profile = profile.toEntity(),
            achievements = profile.achievements.map { it.toEntity() },
            platforms = profile.linkedPlatforms.map { it.toEntity() }
        )
    }

    private fun mapEntityToDomain(
        entity: UserProfileEntity,
        achievementEntities: List<AchievementEntity>,
        platformEntities: List<LinkedPlatformEntity>
    ): UserProfile {
        val achievements = achievementEntities.map { achievement ->
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
        val linkedPlatforms = platformEntities.map { platform ->
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

    private suspend fun seedIfEmpty() {
        if (userDao.getProfileCount() == 0) {
            saveUserProfile(mockDataSource.getUserProfile())
        }
    }

    private fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            id = id,
            name = name,
            email = email,
            avatarUrl = avatarUrl,
            targetExamId = targetExam.id,
            targetExamName = targetExam.name,
            targetExamYear = targetExam.year,
            targetExamShortName = targetExam.shortName,
            tier = tier.name,
            targetScore = targetScore,
            currentProgressPercentage = currentProgressPercentage,
            timeWeightedAnalysis = settings.timeWeightedAnalysis,
            accuracyFocusMode = settings.accuracyFocusMode,
            predictiveGoalTracking = settings.predictiveGoalTracking,
            notificationsEnabled = settings.notificationsEnabled,
            darkModeEnabled = settings.darkModeEnabled
        )
    }

    private fun Achievement.toEntity(): AchievementEntity {
        return AchievementEntity(
            id = id,
            name = name,
            description = description,
            iconName = iconName,
            isUnlocked = isUnlocked,
            unlockedAt = unlockedAt,
            progress = progress
        )
    }

    private fun LinkedPlatform.toEntity(): LinkedPlatformEntity {
        return LinkedPlatformEntity(
            id = id,
            name = name,
            shortCode = shortCode,
            isConnected = isConnected,
            username = username,
            connectedAt = connectedAt
        )
    }
}
