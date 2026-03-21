package com.mockanalysis.domain.usecase

import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.UserProfile
import com.mockanalysis.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the current user's profile.
 */
class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<UserProfile?> = repository.getUserProfile()
}

/**
 * Use case for updating user settings.
 */
class UpdateSettingsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(settings: ProfileSettings) = repository.updateSettings(settings)
}

/**
 * Use case for logging out the user.
 */
class LogoutUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.logout()
}

/**
 * Use case for linking external platform.
 */
class LinkPlatformUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(platformId: String, username: String) = 
        repository.linkPlatform(platformId, username)
}

/**
 * Use case for unlinking external platform.
 */
class UnlinkPlatformUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(platformId: String) = repository.unlinkPlatform(platformId)
}
