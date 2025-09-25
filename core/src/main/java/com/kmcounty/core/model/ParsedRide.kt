package com.kmcounty.core.model

/**
 * Representa um pedido de corrida detectado e analisado
 */
data class ParsedRide(
    val price: Double,           // Valor total em R$
    val distance: Double,        // Distância em km
    val time: Int,              // Tempo estimado em minutos
    val pricePerKm: Double,     // R$/km calculado
    val pricePerMinute: Double, // R$/min calculado
    val confidence: Float,      // Confiança da detecção (0.0 a 1.0)
    val source: String          // Fonte da detecção ("accessibility" ou "ocr")
) {

    /**
     * Retorna a cor recomendada baseada nos thresholds configurados
     */
    fun getRecommendedColor(
        greenThreshold: Double = 1.50,
        orangeThreshold: Double = 0.80
    ): RideColor {
        return when {
            pricePerKm >= greenThreshold -> RideColor.GREEN
            pricePerKm >= orangeThreshold -> RideColor.ORANGE
            else -> RideColor.RED
        }
    }

    /**
     * Verifica se os dados são válidos para exibição
     */
    fun isValid(): Boolean {
        return price > 0 && confidence >= 0.5f &&
               (distance > 0 || time > 0) // Pelo menos um dos dois deve ser válido
    }

    /**
     * Retorna uma representação legível para debug/logs
     */
    fun toLogString(): String {
        return "ParsedRide(price=R$ %.2f, distance=%.1fkm, time=%dmin, " +
               "R$/km=R$ %.2f, R$/min=R$ %.2f, confidence=%.1f%%, source=%s)"
            .format(price, distance, time, pricePerKm, pricePerMinute, confidence * 100, source)
    }

    /**
     * Cores possíveis para indicação visual da rentabilidade
     */
    enum class RideColor {
        GREEN,   // Rentável
        ORANGE,  // Médio
        RED      // Pouco rentável
    }
}
