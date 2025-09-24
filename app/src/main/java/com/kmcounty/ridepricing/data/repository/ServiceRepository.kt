package com.kmcounty.ridepricing.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.kmcounty.ridepricing.accessibility.RideDetectionService
import com.kmcounty.ridepricing.overlay.OverlayService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing app services (Accessibility and Overlay)
 */
@Singleton
class ServiceRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _isAccessibilityServiceEnabled = MutableStateFlow(false)
    val isAccessibilityServiceEnabled: Flow<Boolean> = _isAccessibilityServiceEnabled.asStateFlow()

    private val _isOverlayServiceEnabled = MutableStateFlow(false)
    val isOverlayServiceEnabled: Flow<Boolean> = _isOverlayServiceEnabled.asStateFlow()

    init {
        refreshServiceStatus()
    }

    /**
     * Check if accessibility service is running
     */
    fun isAccessibilityServiceEnabled(): Flow<Boolean> = isAccessibilityServiceEnabled

    /**
     * Check if overlay service is running
     */
    fun isOverlayServiceEnabled(): Flow<Boolean> = isOverlayServiceEnabled

    /**
     * Start accessibility service (user needs to enable it manually in settings)
     */
    fun startAccessibilityService() {
        // We can't programmatically start accessibility service
        // User must enable it in Android Settings
        Timber.i("Accessibility service start requested - user must enable in settings")
        refreshServiceStatus()
    }

    /**
     * Stop accessibility service
     */
    fun stopAccessibilityService() {
        // We can't programmatically stop accessibility service
        // User must disable it in Android Settings
        Timber.i("Accessibility service stop requested - user must disable in settings")
        refreshServiceStatus()
    }

    /**
     * Start overlay service
     */
    fun startOverlayService() {
        try {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_START_OVERLAY
            }
            context.startForegroundService(intent)
            _isOverlayServiceEnabled.value = true
            Timber.i("Overlay service started")
        } catch (e: Exception) {
            Timber.e(e, "Failed to start overlay service")
        }
    }

    /**
     * Stop overlay service
     */
    fun stopOverlayService() {
        try {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_STOP_OVERLAY
            }
            context.startService(intent)
            _isOverlayServiceEnabled.value = false
            Timber.i("Overlay service stopped")
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop overlay service")
        }
    }

    /**
     * Refresh service status
     */
    fun refreshServiceStatus() {
        _isAccessibilityServiceEnabled.value = checkAccessibilityServiceRunning()
        _isOverlayServiceEnabled.value = checkOverlayServiceRunning()
    }

    private fun checkAccessibilityServiceRunning(): Boolean {
        // Check if accessibility service is actually running
        // This is a simplified check - in practice, we might need more sophisticated detection
        return try {
            // We can check if the service class has static flags or use other methods
            // For now, we'll use a basic approach
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = activityManager.getRunningServices(Integer.MAX_VALUE)
            
            services.any { service ->
                service.service.className == RideDetectionService::class.java.name
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to check accessibility service status")
            false
        }
    }

    private fun checkOverlayServiceRunning(): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = activityManager.getRunningServices(Integer.MAX_VALUE)
            
            services.any { service ->
                service.service.className == OverlayService::class.java.name
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to check overlay service status")
            false
        }
    }
}
