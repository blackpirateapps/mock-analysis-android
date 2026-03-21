package com.mockanalysis.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockanalysis.domain.model.ProfileSettings
import com.mockanalysis.domain.usecase.GetUserProfileUseCase
import com.mockanalysis.domain.usecase.LogoutUseCase
import com.mockanalysis.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the User Profile screen.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        error = if (profile == null) "Profile not found" else null
                    )
                }
            }
        }
    }

    fun updateTimeWeightedAnalysis(enabled: Boolean) {
        updateSetting { it.copy(timeWeightedAnalysis = enabled) }
    }

    fun updateAccuracyFocusMode(enabled: Boolean) {
        updateSetting { it.copy(accuracyFocusMode = enabled) }
    }

    fun updatePredictiveGoalTracking(enabled: Boolean) {
        updateSetting { it.copy(predictiveGoalTracking = enabled) }
    }

    private fun updateSetting(update: (ProfileSettings) -> ProfileSettings) {
        val currentSettings = _uiState.value.profile?.settings ?: return
        val newSettings = update(currentSettings)
        
        viewModelScope.launch {
            updateSettingsUseCase(newSettings)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                logoutUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoggingOut = false,
                        error = e.message ?: "Logout failed"
                    )
                }
            }
        }
    }
}
