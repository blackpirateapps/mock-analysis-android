package com.mockanalysis.domain.repository

import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile data.
 */
interface UserRepository {
    
    /**
     * Get the current user's profile.
     */
    fun getUserProfile(): Flow<UserProfile?>
    
    /**
     * Update user's profile settings.
     */
    suspend fun updateSettings(settings: ProfileSettings)
    
    /**
     * Link an external platform account.
     */
    suspend fun linkPlatform(platformId: String, username: String)
    
    /**
     * Unlink an external platform account.
     */
    suspend fun unlinkPlatform(platformId: String)
    
    /**
     * Log out the current user.
     */
    suspend fun logout()
    
    /**
     * Refresh user profile from remote source.
     */
    suspend fun refreshProfile()

    /**
     * Persist full profile details locally.
     */
    suspend fun saveUserProfile(profile: UserProfile)
}
