package com.kmcounty.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Gerenciador de preferências do usuário usando DataStore
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

        // Keys das preferências
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val ACCESSIBILITY_ENABLED = booleanPreferencesKey("accessibility_enabled")
        private val OVERLAY_ENABLED = booleanPreferencesKey("overlay_enabled")
        private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        private val CRASH_REPORTING_ENABLED = booleanPreferencesKey("crash_reporting_enabled")
        private val LOGGING_ENABLED = booleanPreferencesKey("logging_enabled")

        // Thresholds de cor
        private val GREEN_THRESHOLD = doublePreferencesKey("green_threshold")
        private val ORANGE_THRESHOLD = doublePreferencesKey("orange_threshold")

        // Configurações de OCR
        private val OCR_ENABLED = booleanPreferencesKey("ocr_enabled")
        private val OCR_CONFIDENCE_THRESHOLD = floatPreferencesKey("ocr_confidence_threshold")

        // Configurações de UI
        private val OVERLAY_POSITION_X = intPreferencesKey("overlay_position_x")
        private val OVERLAY_POSITION_Y = intPreferencesKey("overlay_position_y")
        private val OVERLAY_SIZE = stringPreferencesKey("overlay_size")
    }

    // Onboarding
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return context.dataStore.data.first()[ONBOARDING_COMPLETED] ?: false
    }

    // Permissões e serviços
    suspend fun setAccessibilityEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ACCESSIBILITY_ENABLED] = enabled
        }
    }

    fun isAccessibilityEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESSIBILITY_ENABLED] ?: false
        }
    }

    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OVERLAY_ENABLED] = enabled
        }
    }

    fun isOverlayEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[OVERLAY_ENABLED] ?: true
        }
    }

    // Analytics e relatórios
    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }

    fun isAnalyticsEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ANALYTICS_ENABLED] ?: true
        }
    }

    suspend fun setCrashReportingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CRASH_REPORTING_ENABLED] = enabled
        }
    }

    fun isCrashReportingEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[CRASH_REPORTING_ENABLED] ?: true
        }
    }

    suspend fun setLoggingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGGING_ENABLED] = enabled
        }
    }

    fun isLoggingEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGGING_ENABLED] ?: false
        }
    }

    // Thresholds de rentabilidade
    suspend fun setGreenThreshold(threshold: Double) {
        context.dataStore.edit { preferences ->
            preferences[GREEN_THRESHOLD] = threshold
        }
    }

    fun getGreenThreshold(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[GREEN_THRESHOLD] ?: 1.50
        }
    }

    suspend fun setOrangeThreshold(threshold: Double) {
        context.dataStore.edit { preferences ->
            preferences[ORANGE_THRESHOLD] = threshold
        }
    }

    fun getOrangeThreshold(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[ORANGE_THRESHOLD] ?: 0.80
        }
    }

    // OCR
    suspend fun setOcrEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OCR_ENABLED] = enabled
        }
    }

    fun isOcrEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[OCR_ENABLED] ?: false
        }
    }

    suspend fun setOcrConfidenceThreshold(threshold: Float) {
        context.dataStore.edit { preferences ->
            preferences[OCR_CONFIDENCE_THRESHOLD] = threshold
        }
    }

    fun getOcrConfidenceThreshold(): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[OCR_CONFIDENCE_THRESHOLD] ?: 0.7f
        }
    }

    // UI - Posição do overlay
    suspend fun setOverlayPosition(x: Int, y: Int) {
        context.dataStore.edit { preferences ->
            preferences[OVERLAY_POSITION_X] = x
            preferences[OVERLAY_POSITION_Y] = y
        }
    }

    fun getOverlayPosition(): Flow<Pair<Int, Int>> {
        return context.dataStore.data.map { preferences ->
            val x = preferences[OVERLAY_POSITION_X] ?: 16
            val y = preferences[OVERLAY_POSITION_Y] ?: 100
            Pair(x, y)
        }
    }

    suspend fun setOverlaySize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[OVERLAY_SIZE] = size
        }
    }

    fun getOverlaySize(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[OVERLAY_SIZE] ?: "medium"
        }
    }

    /**
     * Limpa todas as preferências do usuário (conforme LGPD)
     */
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
