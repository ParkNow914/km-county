package com.kmcounty.core.parser

import com.kmcounty.core.model.ParsedRide
import java.util.regex.Pattern
import kotlin.math.max

/**
 * Core parser for extracting ride information from text content
 * 
 * Supports multiple formats and languages (primarily pt-BR)
 * Uses confidence scoring to validate extracted data
 */
class RideParser {

    // Regex patterns for different data extraction
    private val pricePatterns = listOf(
        // R$ 12,50 or R$12,50
        Pattern.compile("""R\$\s*(\d{1,3}(?:[.,]\d{2})?)""", Pattern.CASE_INSENSITIVE),
        // 12,50 R$ or 12.50 R$
        Pattern.compile("""(\d{1,3}(?:[.,]\d{2}))\s*R\$""", Pattern.CASE_INSENSITIVE),
        // More flexible price matching
        Pattern.compile("""(\d{1,3}(?:[.,]\d{2})?).*(?:reais?|real)""", Pattern.CASE_INSENSITIVE)
    )

    private val distancePatterns = listOf(
        // 5.2 km or 5,2 km
        Pattern.compile("""(\d+(?:[.,]\d+)?)\s*km""", Pattern.CASE_INSENSITIVE),
        // 5.2 quilômetros
        Pattern.compile("""(\d+(?:[.,]\d+)?)\s*(?:quilômetros?|quilometros?)""", Pattern.CASE_INSENSITIVE)
    )

    private val timePatterns = listOf(
        // 15 min or 15 minutos
        Pattern.compile("""(\d+)\s*(?:min|minutos?)""", Pattern.CASE_INSENSITIVE),
        // 1h 15min or 1h15min
        Pattern.compile("""(\d+)h\s*(\d+)?(?:min|minutos?)?""", Pattern.CASE_INSENSITIVE),
        // 1 hora 15 minutos
        Pattern.compile("""(\d+)\s*horas?\s*(?:(?:e\s*)?(\d+)\s*minutos?)?""", Pattern.CASE_INSENSITIVE)
    )

    // Keywords that indicate a ride request screen
    private val rideIndicatorKeywords = setOf(
        "corrida", "viagem", "destino", "origem", "motorista",
        "aceitar", "recusar", "confirmar", "solicitar",
        "tempo estimado", "preço", "valor", "distância",
        "uber", "99", "cabify", "easytaxi"
    )

    /**
     * Parse ride information from extracted texts
     */
    fun parseRideInfo(texts: List<String>, sourceApp: String): ParsedRide? {
        if (texts.isEmpty()) return null

        // Check if this looks like a ride request screen
        val hasRideIndicators = texts.any { text ->
            rideIndicatorKeywords.any { keyword ->
                text.lowercase().contains(keyword)
            }
        }

        if (!hasRideIndicators) {
            return null // Not a ride screen
        }

        // Extract price, distance, and time
        val extractedPrice = extractPrice(texts)
        val extractedDistance = extractDistance(texts)
        val extractedTime = extractTime(texts)

        // Validate extracted data
        if (extractedPrice == null || extractedDistance == null) {
            return null
        }

        if (!isValidPrice(extractedPrice) || !isValidDistance(extractedDistance)) {
            return null
        }

        // Calculate metrics
        val pricePerKm = extractedPrice / extractedDistance
        val pricePerMinute = if (extractedTime != null && extractedTime > 0) {
            extractedPrice / extractedTime
        } else null

        // Calculate confidence score
        val confidence = calculateConfidence(
            texts = texts,
            hasPrice = extractedPrice > 0,
            hasDistance = extractedDistance > 0,
            hasTime = extractedTime != null,
            sourceApp = sourceApp
        )

        return ParsedRide(
            totalPrice = extractedPrice,
            distance = extractedDistance,
            estimatedTime = extractedTime,
            pricePerKm = pricePerKm,
            pricePerMinute = pricePerMinute,
            confidence = confidence,
            sourceApp = sourceApp
        )
    }

    private fun extractPrice(texts: List<String>): Float? {
        for (text in texts) {
            for (pattern in pricePatterns) {
                val matcher = pattern.matcher(text)
                if (matcher.find()) {
                    val priceString = matcher.group(1)?.replace(",", ".") ?: continue
                    return priceString.toFloatOrNull()
                }
            }
        }
        return null
    }

    private fun extractDistance(texts: List<String>): Float? {
        for (text in texts) {
            for (pattern in distancePatterns) {
                val matcher = pattern.matcher(text)
                if (matcher.find()) {
                    val distanceString = matcher.group(1)?.replace(",", ".") ?: continue
                    return distanceString.toFloatOrNull()
                }
            }
        }
        return null
    }

    private fun extractTime(texts: List<String>): Int? {
        for (text in texts) {
            for (pattern in timePatterns) {
                val matcher = pattern.matcher(text)
                if (matcher.find()) {
                    return when (pattern) {
                        timePatterns[0] -> { // minutes only
                            matcher.group(1)?.toIntOrNull()
                        }
                        timePatterns[1] -> { // hours and minutes (1h 15min)
                            val hours = matcher.group(1)?.toIntOrNull() ?: 0
                            val minutes = matcher.group(2)?.toIntOrNull() ?: 0
                            hours * 60 + minutes
                        }
                        timePatterns[2] -> { // hours and minutes (1 hora 15 minutos)
                            val hours = matcher.group(1)?.toIntOrNull() ?: 0
                            val minutes = matcher.group(2)?.toIntOrNull() ?: 0
                            hours * 60 + minutes
                        }
                        else -> null
                    }
                }
            }
        }
        return null
    }

    private fun isValidPrice(price: Float): Boolean {
        return price in 0.1f..10000.0f // R$0.10 to R$10,000
    }

    private fun isValidDistance(distance: Float): Boolean {
        return distance in 0.1f..1000.0f // 0.1km to 1000km
    }

    private fun calculateConfidence(
        texts: List<String>,
        hasPrice: Boolean,
        hasDistance: Boolean,
        hasTime: Boolean,
        sourceApp: String
    ): Float {
        var confidence = 0.0f

        // Base confidence for having required data
        if (hasPrice) confidence += 0.4f
        if (hasDistance) confidence += 0.4f
        if (hasTime) confidence += 0.1f

        // Bonus for known apps
        when {
            sourceApp.contains("uber") -> confidence += 0.05f
            sourceApp.contains("99") -> confidence += 0.05f
            sourceApp.contains("cabify") -> confidence += 0.05f
        }

        // Bonus for ride-specific keywords
        val keywordCount = texts.sumOf { text ->
            rideIndicatorKeywords.count { keyword ->
                text.lowercase().contains(keyword)
            }
        }
        confidence += (keywordCount * 0.02f).coerceAtMost(0.1f)

        return confidence.coerceIn(0.0f, 1.0f)
    }
}
