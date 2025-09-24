package com.kmcounty.ridepricing.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmcounty.ridepricing.ui.main.MainScreen
import com.kmcounty.ridepricing.ui.main.MainViewModel
import com.kmcounty.ridepricing.ui.onboarding.OnboardingActivity
import com.kmcounty.ridepricing.ui.theme.RidePricingTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Main Activity for the Ride Pricing Assistant
 * 
 * Handles the main application flow including:
 * - Onboarding check and navigation
 * - Permission management
 * - Main UI display
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val onboardingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.onOnboardingCompleted()
        } else {
            // User cancelled onboarding, close app
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created")

        setContent {
            RidePricingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.needsOnboarding) {
            if (uiState.needsOnboarding) {
                startOnboarding()
            }
        }

        when {
            uiState.isLoading -> {
                // Show splash screen or loading indicator
                LoadingScreen()
            }
            
            !uiState.needsOnboarding -> {
                MainScreen(
                    uiState = uiState,
                    onNavigateToSettings = { startActivity(Intent(this@MainActivity, com.kmcounty.ridepricing.ui.settings.SettingsActivity::class.java)) },
                    onNavigateToPrivacyPolicy = { startActivity(Intent(this@MainActivity, com.kmcounty.ridepricing.ui.privacy.PrivacyPolicyActivity::class.java)) },
                    onToggleService = viewModel::toggleService,
                    onToggleOverlay = viewModel::toggleOverlay
                )
            }
        }
    }

    @Composable
    private fun LoadingScreen() {
        // Simple loading screen - you can enhance this
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            // Add loading indicator here
        }
    }

    private fun startOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        onboardingLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions()
    }
}
