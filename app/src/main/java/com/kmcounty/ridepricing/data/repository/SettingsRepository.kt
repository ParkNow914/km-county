package com.kmcounty.ridepricing.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for app settings using DataStore
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // Settings keys
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val GOOD_THRESHOLD = floatPreferencesKey("good_threshold")
    private val NEUTRAL_THRESHOLD = floatPreferencesKey("neutral_threshold") 
    private val CONFIDENCE_THRESHOLD = floatPreferencesKey("confidence_threshold")
    private val CONSERVATIVE_MODE = booleanPreferencesKey("conservative_mode")
    private val LOGGING_ENABLED = booleanPreferencesKey("logging_enabled")
    private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    private val OVERLAY_POSITION_X = intPreferencesKey("overlay_position_x")
    private val OVERLAY_POSITION_Y = intPreferencesKey("overlay_position_y")

    // Default values
    companion object {
        const val DEFAULT_GOOD_THRESHOLD = 1.50f
        const val DEFAULT_NEUTRAL_THRESHOLD = 0.80f
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.7f
        const val DEFAULT_CONSERVATIVE_MODE = true
        const val DEFAULT_LOGGING_ENABLED = false
        const val DEFAULT_VIBRATION_ENABLED = true
        const val DEFAULT_OVERLAY_X = 16
        const val DEFAULT_OVERLAY_Y = 200
    }

    // Onboarding
    fun isOnboardingCompleted(): Flow<Boolean> = 
        dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    // Price thresholds
    fun getGoodThreshold(): Flow<Float> = 
        dataStore.data.map { it[GOOD_THRESHOLD] ?: DEFAULT_GOOD_THRESHOLD }

    suspend fun setGoodThreshold(threshold: Float) {
        dataStore.edit { it[GOOD_THRESHOLD] = threshold }
    }

    fun getNeutralThreshold(): Flow<Float> = 
        dataStore.data.map { it[NEUTRAL_THRESHOLD] ?: DEFAULT_NEUTRAL_THRESHOLD }

    suspend fun setNeutralThreshold(threshold: Float) {
        dataStore.edit { it[NEUTRAL_THRESHOLD] = threshold }
    }

    // Confidence threshold
    fun getConfidenceThreshold(): Flow<Float> = 
        dataStore.data.map { it[CONFIDENCE_THRESHOLD] ?: DEFAULT_CONFIDENCE_THRESHOLD }

    suspend fun setConfidenceThreshold(threshold: Float) {
        dataStore.edit { it[CONFIDENCE_THRESHOLD] = threshold }
    }

    // Conservative mode
    fun isConservativeModeEnabled(): Flow<Boolean> = 
        dataStore.data.map { it[CONSERVATIVE_MODE] ?: DEFAULT_CONSERVATIVE_MODE }

    suspend fun setConservativeMode(enabled: Boolean) {
        dataStore.edit { it[CONSERVATIVE_MODE] = enabled }
    }

    // Logging
    fun isLoggingEnabled(): Flow<Boolean> = 
        dataStore.data.map { it[LOGGING_ENABLED] ?: DEFAULT_LOGGING_ENABLED }

    suspend fun setLoggingEnabled(enabled: Boolean) {
        dataStore.edit { it[LOGGING_ENABLED] = enabled }
    }

    // Vibration
    fun isVibrationEnabled(): Flow<Boolean> = 
        dataStore.data.map { it[VIBRATION_ENABLED] ?: DEFAULT_VIBRATION_ENABLED }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[VIBRATION_ENABLED] = enabled }
    }

    // Overlay position
    fun getOverlayPositionX(): Flow<Int> = 
        dataStore.data.map { it[OVERLAY_POSITION_X] ?: DEFAULT_OVERLAY_X }

    suspend fun setOverlayPositionX(x: Int) {
        dataStore.edit { it[OVERLAY_POSITION_X] = x }
    }

    fun getOverlayPositionY(): Flow<Int> = 
        dataStore.data.map { it[OVERLAY_POSITION_Y] ?: DEFAULT_OVERLAY_Y }

    suspend fun setOverlayPositionY(y: Int) {
        dataStore.edit { it[OVERLAY_POSITION_Y] = y }
    }

    // Clear all data
    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }

    // Get all settings as a combined data class
    fun getAllSettings(): Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
            goodThreshold = preferences[GOOD_THRESHOLD] ?: DEFAULT_GOOD_THRESHOLD,
            neutralThreshold = preferences[NEUTRAL_THRESHOLD] ?: DEFAULT_NEUTRAL_THRESHOLD,
            confidenceThreshold = preferences[CONFIDENCE_THRESHOLD] ?: DEFAULT_CONFIDENCE_THRESHOLD,
            conservativeMode = preferences[CONSERVATIVE_MODE] ?: DEFAULT_CONSERVATIVE_MODE,
            loggingEnabled = preferences[LOGGING_ENABLED] ?: DEFAULT_LOGGING_ENABLED,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: DEFAULT_VIBRATION_ENABLED,
            overlayPositionX = preferences[OVERLAY_POSITION_X] ?: DEFAULT_OVERLAY_X,
            overlayPositionY = preferences[OVERLAY_POSITION_Y] ?: DEFAULT_OVERLAY_Y
        )
    }
}

/**
 * Data class containing all app settings
 */
data class AppSettings(
    val onboardingCompleted: Boolean,
    val goodThreshold: Float,
    val neutralThreshold: Float,
    val confidenceThreshold: Float,
    val conservativeMode: Boolean,
    val loggingEnabled: Boolean,
    val vibrationEnabled: Boolean,
    val overlayPositionX: Int,
    val overlayPositionY: Int
)
