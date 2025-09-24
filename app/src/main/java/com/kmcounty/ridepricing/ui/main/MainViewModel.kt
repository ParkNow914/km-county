package com.kmcounty.ridepricing.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmcounty.ridepricing.data.repository.PermissionRepository
import com.kmcounty.ridepricing.data.repository.SettingsRepository
import com.kmcounty.ridepricing.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for MainActivity
 * 
 * Manages the main application state including:
 * - Onboarding status
 * - Service permissions and status
 * - App configuration
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val permissionRepository: PermissionRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        initializeState()
    }

    private fun initializeState() {
        viewModelScope.launch {
            combine(
                settingsRepository.isOnboardingCompleted(),
                permissionRepository.hasAccessibilityPermission(),
                permissionRepository.hasOverlayPermission(),
                serviceRepository.isAccessibilityServiceEnabled(),
                serviceRepository.isOverlayServiceEnabled()
            ) { onboardingCompleted, hasAccessibility, hasOverlay, serviceEnabled, overlayEnabled ->
                MainUiState(
                    isLoading = false,
                    needsOnboarding = !onboardingCompleted,
                    hasAccessibilityPermission = hasAccessibility,
                    hasOverlayPermission = hasOverlay,
                    isServiceEnabled = serviceEnabled,
                    isOverlayEnabled = overlayEnabled,
                    isFullyConfigured = onboardingCompleted && hasAccessibility && hasOverlay
                )
            }.collect { newState ->
                _uiState.value = newState
                Timber.d("UI State updated: $newState")
            }
        }
    }

    fun onOnboardingCompleted() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
            Timber.i("Onboarding completed")
        }
    }

    fun checkPermissions() {
        viewModelScope.launch {
            permissionRepository.refreshPermissions()
            serviceRepository.refreshServiceStatus()
        }
    }

    fun toggleService() {
        viewModelScope.launch {
            if (_uiState.value.isServiceEnabled) {
                serviceRepository.stopAccessibilityService()
            } else {
                serviceRepository.startAccessibilityService()
            }
        }
    }

    fun toggleOverlay() {
        viewModelScope.launch {
            if (_uiState.value.isOverlayEnabled) {
                serviceRepository.stopOverlayService()
            } else {
                serviceRepository.startOverlayService()
            }
        }
    }
}

/**
 * UI State for MainActivity
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val needsOnboarding: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isServiceEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val isFullyConfigured: Boolean = false,
    val currentRideInfo: RideInfo? = null,
    val errorMessage: String? = null
)

/**
 * Current ride information to display
 */
data class RideInfo(
    val pricePerKm: Float,
    val pricePerMinute: Float?,
    val totalPrice: Float,
    val distance: Float,
    val estimatedTime: Int?,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)
