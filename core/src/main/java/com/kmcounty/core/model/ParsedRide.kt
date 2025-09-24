package com.kmcounty.core.model

/**
 * Data class representing a parsed ride with extracted information
 */
data class ParsedRide(
    val totalPrice: Float,
    val distance: Float,
    val estimatedTime: Int?, // in minutes
    val pricePerKm: Float,
    val pricePerMinute: Float?,
    val confidence: Float, // 0.0 to 1.0
    val sourceApp: String
) {
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
     * Get a human-readable confidence description
     */
    fun getConfidenceDescription(): String = when {
        confidence >= 0.9f -> "Muito Alta"
        confidence >= 0.8f -> "Alta"
        confidence >= 0.7f -> "Boa"
        confidence >= 0.6f -> "Média"
        confidence >= 0.5f -> "Baixa"
        else -> "Muito Baixa"
    }
}

/**
 * Price category enumeration
 */
enum class PriceCategory {
    GOOD,    // >= R$ 1.50/km (green)
    NEUTRAL, // R$ 0.80-1.49/km (orange) 
    POOR     // < R$ 0.80/km (red)
}
