package com.kmcounty.ridepricing.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmcounty.ridepricing.data.repository.PermissionRepository
import com.kmcounty.ridepricing.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for OnboardingActivity
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionRepository: PermissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        observePermissions()
    }

    private fun observePermissions() {
        viewModelScope.launch {
            combine(
                permissionRepository.hasAccessibilityPermission(),
                permissionRepository.hasOverlayPermission()
            ) { accessibility, overlay ->
                OnboardingUiState(
                    hasAccessibilityPermission = accessibility,
                    hasOverlayPermission = overlay,
                    canComplete = accessibility && overlay
                )
            }.collect { newState ->
                _uiState.value = newState
                Timber.d("Onboarding state updated: $newState")
            }
        }
    }

    fun checkPermissions() {
        permissionRepository.refreshPermissions()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                settingsRepository.setOnboardingCompleted(true)
                Timber.i("Onboarding marked as completed")
            } catch (e: Exception) {
                Timber.e(e, "Failed to complete onboarding")
            }
        }
    }
}

/**
 * UI State for onboarding
 */
data class OnboardingUiState(
    val hasAccessibilityPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val canComplete: Boolean = false
)
