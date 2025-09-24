package com.kmcounty.ridepricing.data.repository

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing app permissions
 */
@Singleton
class PermissionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _hasAccessibilityPermission = MutableStateFlow(false)
    val hasAccessibilityPermission: Flow<Boolean> = _hasAccessibilityPermission.asStateFlow()

    private val _hasOverlayPermission = MutableStateFlow(false)
    val hasOverlayPermission: Flow<Boolean> = _hasOverlayPermission.asStateFlow()

    init {
        refreshPermissions()
    }

    /**
     * Check if accessibility service permission is granted
     */
    fun hasAccessibilityPermission(): Flow<Boolean> = hasAccessibilityPermission

    /**
     * Check if overlay permission is granted
     */
    fun hasOverlayPermission(): Flow<Boolean> = hasOverlayPermission

    /**
     * Refresh permission states
     */
    fun refreshPermissions() {
        _hasAccessibilityPermission.value = checkAccessibilityPermission()
        _hasOverlayPermission.value = checkOverlayPermission()
    }

    private fun checkAccessibilityPermission(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val serviceName = "${context.packageName}/.accessibility.RideDetectionService"
            enabledServices?.contains(serviceName) == true
        } catch (e: Exception) {
            false
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // No permission needed on older versions
        }
    }

    /**
     * Check if all required permissions are granted
     */
    fun hasAllPermissions(): Boolean {
        return checkAccessibilityPermission() && checkOverlayPermission()
    }
}
