package com.kmcounty.app.ui.main.viewmodel

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kmcounty.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar o estado da tela principal
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val userPreferences = UserPreferences(context)

    // Estados dos serviços
    private val _accessibilityEnabled = MutableStateFlow(false)
    val accessibilityEnabled: StateFlow<Boolean> = _accessibilityEnabled

    private val _overlayEnabled = MutableStateFlow(false)
    val overlayEnabled: StateFlow<Boolean> = _overlayEnabled

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning

    init {
        checkServiceStatus()
    }

    /**
     * Verifica o status atual dos serviços e permissões
     */
    fun checkServiceStatus() {
        viewModelScope.launch {
            _accessibilityEnabled.value = isAccessibilityServiceEnabled()
            _overlayEnabled.value = isOverlayPermissionGranted()
            _isServiceRunning.value = _accessibilityEnabled.value && _overlayEnabled.value
        }
    }

    /**
     * Verifica se o serviço de acessibilidade está ativo
     */
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Exception) {
            0
        }

        if (accessibilityEnabled != 1) return false

        val serviceString = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        return serviceString.contains(context.packageName)
    }

    /**
     * Verifica se a permissão de overlay está concedida
     */
    private fun isOverlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Verifica se o onboarding foi completado
     */
    fun isOnboardingCompleted(): Boolean {
        // TODO: Implementar verificação real
        // Por enquanto, retorna true para permitir testes
        return true
    }
}
