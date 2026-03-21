package com.mockanalysis.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mockanalysis.data.local.entity.AchievementEntity
import com.mockanalysis.data.local.entity.LinkedPlatformEntity
import com.mockanalysis.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    @Query("SELECT * FROM achievement")
    suspend fun getAchievements(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("DELETE FROM achievement")
    suspend fun clearAchievements()

    @Query("SELECT * FROM linked_platform")
    suspend fun getLinkedPlatforms(): List<LinkedPlatformEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinkedPlatforms(platforms: List<LinkedPlatformEntity>)

    @Query("DELETE FROM linked_platform")
    suspend fun clearLinkedPlatforms()

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun getProfileCount(): Int
}
