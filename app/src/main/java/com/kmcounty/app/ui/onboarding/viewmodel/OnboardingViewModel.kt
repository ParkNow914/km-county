package com.kmcounty.app.ui.onboarding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kmcounty.app.data.preferences.UserPreferences
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar o estado do onboarding
 */
class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    /**
     * Marca o onboarding como completo
     */
    fun markOnboardingCompleted() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted(true)
        }
    }

    /**
     * Verifica se o onboarding já foi completado
     */
    suspend fun isOnboardingCompleted(): Boolean {
        return userPreferences.isOnboardingCompleted()
    }
}
