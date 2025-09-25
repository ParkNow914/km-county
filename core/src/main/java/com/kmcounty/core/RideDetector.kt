package com.kmcounty.core

import android.util.Log
import com.kmcounty.core.model.ParsedRide
import com.kmcounty.core.parser.RideTextParser
import kotlin.math.max

/**
 * Detector principal de pedidos de corrida.
 *
 * Responsável por analisar textos da tela e determinar se há um pedido de corrida ativo,
 * extraindo preço, distância e tempo para cálculos de R$/km e R$/min.
 */
class RideDetector(
    private val textParser: RideTextParser = RideTextParser(),
    private val minConfidence: Float = 0.7f
) {

    companion object {
        private const val TAG = "RideDetector"
    }

    /**
     * Analisa uma lista de textos da tela para detectar pedido de corrida
     *
     * @param screenTexts Lista de textos extraídos da tela
     * @return ParsedRide se pedido detectado com confiança suficiente, null caso contrário
     */
    fun detectRide(screenTexts: List<String>): ParsedRide? {
        try {
            // 1. Combinar todos os textos em um único bloco para análise
            val combinedText = screenTexts.joinToString(" ")

            // 2. Verificar se parece conter um pedido de corrida
            if (!isRideRequestText(combinedText)) {
                return null
            }

            // 3. Extrair dados usando o parser
            val parsedData = textParser.parseRideData(screenTexts)

            // 4. Validar dados extraídos
            if (!isValidRideData(parsedData)) {
                Log.d(TAG, "Dados extraídos inválidos: $parsedData")
                return null
            }

            // 5. Calcular confiança geral
            val confidence = calculateConfidence(parsedData, screenTexts)

            if (confidence < minConfidence) {
                Log.d(TAG, "Confiança baixa (${confidence}) para dados: $parsedData")
                return null
            }

            // 6. Calcular métricas de rentabilidade
            val pricePerKm = if (parsedData.distance > 0) {
                parsedData.price / parsedData.distance
            } else {
                0.0
            }

            val pricePerMinute = if (parsedData.time > 0) {
                parsedData.price / parsedData.time
            } else {
                0.0
            }

            return ParsedRide(
                price = parsedData.price,
                distance = parsedData.distance,
                time = parsedData.time,
                pricePerKm = pricePerKm,
                pricePerMinute = pricePerMinute,
                confidence = confidence,
                source = "accessibility"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao detectar pedido de corrida", e)
            return null
        }
    }

    /**
     * Verificação rápida se ainda há pedido ativo na tela
     * Usada para eventos de scroll onde análise completa seria custosa
     */
    fun hasActiveRide(screenTexts: List<String>): Boolean {
        val combinedText = screenTexts.joinToString(" ")
        return isRideRequestText(combinedText)
    }

    /**
     * Verifica se o texto parece conter um pedido de corrida
     */
    private fun isRideRequestText(text: String): Boolean {
        val lowerText = text.lowercase()

        // Palavras-chave que indicam pedido de corrida
        val rideKeywords = listOf(
            "aceitar", "accept", "recusar", "decline", "cancelar",
            "passageiro", "passenger", "destino", "destination",
            "corrida", "ride", "viagem", "trip", "trajeto",
            "valor", "preço", "price", "fare", "total",
            "km", "quilômetros", "kilometers", "distância", "distance",
            "min", "minutos", "minutes", "tempo", "time"
        )

        // Contar quantas palavras-chave foram encontradas
        val keywordCount = rideKeywords.count { keyword ->
            lowerText.contains(keyword)
        }

        // Deve ter pelo menos 3 palavras-chave para considerar como pedido
        return keywordCount >= 3
    }

    /**
     * Valida se os dados extraídos formam um pedido válido
     */
    private fun isValidRideData(data: RideTextParser.ParsedRideData): Boolean {
        return when {
            data.price <= 0 -> false
            data.distance < 0 -> false  // Permite 0 para casos onde só tempo é conhecido
            data.time < 0 -> false      // Permite 0 para casos onde só distância é conhecida
            data.price > 10000 -> false // Valor muito alto (provavelmente erro)
            data.distance > 1000 -> false // Distância muito longa (provavelmente erro)
            data.time > 1440 -> false   // Tempo muito longo (>24h, provavelmente erro)
            else -> true
        }
    }

    /**
     * Calcula a confiança geral da detecção
     */
    private fun calculateConfidence(
        data: RideTextParser.ParsedRideData,
        originalTexts: List<String>
    ): Float {
        var confidence = 0.5f // Base confidence

        // Aumentar confiança baseado na qualidade dos dados extraídos
        if (data.price > 0) confidence += 0.2f
        if (data.distance > 0) confidence += 0.15f
        if (data.time > 0) confidence += 0.15f

        // Penalizar se dados parecem inconsistentes
        if (data.distance > 0 && data.time > 0) {
            // Estimativa de velocidade (km/h)
            val speedKmh = (data.distance / data.time) * 60
            if (speedKmh < 5 || speedKmh > 150) { // Velocidade irreal
                confidence -= 0.3f
            }
        }

        // Verificar se os valores aparecem múltiplas vezes (consistência)
        val priceMentions = countValueMentions(data.price, originalTexts)
        val distanceMentions = countValueMentions(data.distance, originalTexts)
        val timeMentions = countValueMentions(data.time.toDouble(), originalTexts)

        confidence += (priceMentions + distanceMentions + timeMentions) * 0.05f

        // Garantir limites
        return confidence.coerceIn(0f, 1f)
    }

    /**
     * Conta quantas vezes um valor numérico aparece nos textos
     */
    private fun countValueMentions(value: Double, texts: List<String>): Int {
        if (value <= 0) return 0

        val valueStr = String.format("%.2f", value)
        val valueInt = value.toInt()

        return texts.count { text ->
            text.contains(valueStr) || (valueInt > 0 && text.contains(valueInt.toString()))
        }
    }
}
