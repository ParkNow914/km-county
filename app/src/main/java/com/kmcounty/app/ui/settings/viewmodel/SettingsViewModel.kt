package com.kmcounty.app.ui.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kmcounty.app.data.preferences.UserPreferences
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar as configurações do aplicativo
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    // Thresholds
    val greenThreshold = userPreferences.getGreenThreshold()
    val orangeThreshold = userPreferences.getOrangeThreshold()

    // Modos de detecção
    val conservativeMode = userPreferences.isOcrEnabled() // TODO: Adicionar preference específica
    val ocrEnabled = userPreferences.isOcrEnabled()
    val ocrConfidenceThreshold = userPreferences.getOcrConfidenceThreshold()

    // Privacidade
    val analyticsEnabled = userPreferences.isAnalyticsEnabled()
    val crashReportingEnabled = userPreferences.isCrashReportingEnabled()
    val loggingEnabled = userPreferences.isLoggingEnabled()

    // Métodos para atualizar configurações
    fun setGreenThreshold(threshold: Double) {
        viewModelScope.launch {
            userPreferences.setGreenThreshold(threshold)
        }
    }

    fun setOrangeThreshold(threshold: Double) {
        viewModelScope.launch {
            userPreferences.setOrangeThreshold(threshold)
        }
    }

    fun setConservativeMode(enabled: Boolean) {
        // TODO: Implementar no UserPreferences
        viewModelScope.launch {
            // userPreferences.setConservativeMode(enabled)
        }
    }

    fun setOcrEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setOcrEnabled(enabled)
        }
    }

    fun setOcrConfidenceThreshold(threshold: Float) {
        viewModelScope.launch {
            userPreferences.setOcrConfidenceThreshold(threshold)
        }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setAnalyticsEnabled(enabled)
        }
    }

    fun setCrashReportingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setCrashReportingEnabled(enabled)
        }
    }

    fun setLoggingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setLoggingEnabled(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            userPreferences.clearAllData()
        }
    }
}
