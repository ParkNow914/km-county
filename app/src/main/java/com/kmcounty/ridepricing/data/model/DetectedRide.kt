package com.kmcounty.ridepricing.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for ride information detected by the system
 */
@Parcelize
data class DetectedRide(
    val totalPrice: Float,
    val distance: Float,
    val estimatedTime: Int?, // in minutes
    val pricePerKm: Float,
    val pricePerMinute: Float?,
    val confidence: Float, // 0.0 to 1.0
    val sourceApp: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Get the price category based on R$/km thresholds
     */
    fun getPriceCategory(): PriceCategory = when {
        pricePerKm >= 1.50f -> PriceCategory.GOOD
        pricePerKm >= 0.80f -> PriceCategory.NEUTRAL
        else -> PriceCategory.POOR
    }

    /**
     * Check if the confidence level is acceptable
     */
    fun isConfident(threshold: Float = 0.7f): Boolean = confidence >= threshold

    /**
     * Get formatted price per km string
     */
    fun getFormattedPricePerKm(): String = "R$ %.2f/km".format(pricePerKm)

    /**
     * Get formatted price per minute string
     */
    fun getFormattedPricePerMinute(): String? = pricePerMinute?.let { "R$ %.2f/min".format(it) }
}

/**
 * Price category enumeration
 */
enum class PriceCategory {
    GOOD,    // >= R$ 1.50/km (green)
    NEUTRAL, // R$ 0.80-1.49/km (orange) 
    POOR     // < R$ 0.80/km (red)
}
