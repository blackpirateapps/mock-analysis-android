package com.mockanalysis.data.repository

import com.mockanalysis.data.source.MockDataSource
import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.UserProfile
import com.mockanalysis.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository using mock data.
 * In a real app, this would interact with Room database and/or remote API.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val mockDataSource: MockDataSource
) : UserRepository {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)

    init {
        // Initialize with mock data
        _userProfile.value = mockDataSource.getUserProfile()
    }

    override fun getUserProfile(): Flow<UserProfile?> {
        return _userProfile.asStateFlow()
    }

    override suspend fun updateSettings(settings: ProfileSettings) {
        _userProfile.update { profile ->
            profile?.copy(settings = settings)
        }
    }

    override suspend fun linkPlatform(platformId: String, username: String) {
        _userProfile.update { profile ->
            profile?.copy(
                linkedPlatforms = profile.linkedPlatforms.map { platform ->
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
            )
        }
    }

    override suspend fun unlinkPlatform(platformId: String) {
        _userProfile.update { profile ->
            profile?.copy(
                linkedPlatforms = profile.linkedPlatforms.map { platform ->
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
            )
        }
    }

    override suspend fun logout() {
        // In a real app, this would clear tokens, cache, etc.
        _userProfile.value = null
    }

    override suspend fun refreshProfile() {
        // In a real app, this would fetch from remote API
        _userProfile.value = mockDataSource.getUserProfile()
    }
}
