package com.mockanalysis.presentation.profile

import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.model.UserProfile

/**
 * UI State for the User Profile screen.
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val error: String? = null,
    val isLoggingOut: Boolean = false
)
