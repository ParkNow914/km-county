package com.kmcounty.ridepricing

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class for the Ride Pricing Assistant
 * 
 * Handles application-level initialization including:
 * - Hilt dependency injection setup
 * - Timber logging configuration
 * - Crash reporting setup (if enabled)
 */
@HiltAndroidApp
class RidePricingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        setupLogging()
        setupCrashReporting()
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, only log warnings and errors
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority >= android.util.Log.WARN) {
                        // Log to file or crash reporting service if needed
                        // For privacy, we don't send logs anywhere by default
                    }
                }
            })
        }
        
        Timber.i("RidePricingApplication initialized")
    }

    private fun setupCrashReporting() {
        // Setup crash reporting here if needed
        // For privacy reasons, we don't use external crash reporting by default
        // All crashes are handled locally only
    }
}
