package com.kmcounty.core.parser

import android.util.Log
import java.util.regex.Pattern

/**
 * Parser de texto para extrair dados de pedidos de corrida.
 *
 * Implementa regex patterns robustos para extrair preço, distância e tempo
 * de textos de tela de aplicativos de transporte.
 */
class RideTextParser {

    companion object {
        private const val TAG = "RideTextParser"

        // Regex patterns conforme especificado no prompt
        private val PRICE_PATTERN = Pattern.compile(
            "(?i)(?:R\\$\\s*|\\$\\s*)?([\\d]{1,3}(?:[.,]\\d{2})?)|" +
            "([\\d]+(?:[.,]\\d{2})?)\\s*(?:R\\$|\\$|reais|real)"
        )

        private val DISTANCE_PATTERN = Pattern.compile(
            "(?i)([\\d]+(?:[.,]\\d+)?)\\s*(?:km|quilômetros|kilometers|quilometro)"
        )

        private val TIME_PATTERN = Pattern.compile(
            "(?i)([\\d]+)\\s*(?:min|minutos|minutes|hora|hora|hs|h)"
        )
    }

    /**
     * Dados extraídos de um pedido de corrida
     */
    data class ParsedRideData(
        val price: Double = 0.0,
        val distance: Double = 0.0,
        val time: Int = 0, // em minutos
        val priceConfidence: Float = 0f,
        val distanceConfidence: Float = 0f,
        val timeConfidence: Float = 0f
    )

    /**
     * Analisa uma lista de textos para extrair dados de corrida
     */
    fun parseRideData(texts: List<String>): ParsedRideData {
        try {
            // 1. Extrair preços de todos os textos
            val prices = extractPrices(texts)

            // 2. Extrair distâncias
            val distances = extractDistances(texts)

            // 3. Extrair tempos
            val times = extractTimes(texts)

            // 4. Selecionar melhores candidatos
            val bestPrice = selectBestPrice(prices)
            val bestDistance = selectBestDistance(distances)
            val bestTime = selectBestTime(times)

            Log.d(TAG, "Extraído - Preço: R$ ${bestPrice.first}, Distância: ${bestDistance.first}km, Tempo: ${bestTime.first}min")

            return ParsedRideData(
                price = bestPrice.first,
                distance = bestDistance.first,
                time = bestTime.first,
                priceConfidence = bestPrice.second,
                distanceConfidence = bestDistance.second,
                timeConfidence = bestTime.second
            )

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao fazer parse dos dados", e)
            return ParsedRideData()
        }
    }

    /**
     * Extrai todos os preços encontrados nos textos
     */
    private fun extractPrices(texts: List<String>): List<Pair<Double, Float>> {
        val prices = mutableListOf<Pair<Double, Float>>()

        texts.forEach { text ->
            val matcher = PRICE_PATTERN.matcher(text)
            while (matcher.find()) {
                try {
                    val priceStr = matcher.group(1) ?: matcher.group(2) ?: continue

                    // Normalizar formato (vírgula para ponto)
                    val normalizedPrice = priceStr.replace(',', '.')
                    val price = normalizedPrice.toDouble()

                    // Calcular confiança baseado no contexto
                    val confidence = calculatePriceConfidence(text, price)

                    if (price > 0 && price < 10000) { // Filtros básicos
                        prices.add(Pair(price, confidence))
                    }
                } catch (e: NumberFormatException) {
                    // Ignorar preços mal formatados
                }
            }
        }

        return prices
    }

    /**
     * Extrai todas as distâncias encontradas nos textos
     */
    private fun extractDistances(texts: List<String>): List<Pair<Double, Float>> {
        val distances = mutableListOf<Pair<Double, Float>>()

        texts.forEach { text ->
            val matcher = DISTANCE_PATTERN.matcher(text)
            while (matcher.find()) {
                try {
                    val distanceStr = matcher.group(1) ?: continue
                    val normalizedDistance = distanceStr.replace(',', '.')
                    val distance = normalizedDistance.toDouble()

                    val confidence = calculateDistanceConfidence(text, distance)

                    if (distance >= 0.1 && distance < 1000) { // Filtros básicos
                        distances.add(Pair(distance, confidence))
                    }
                } catch (e: NumberFormatException) {
                    // Ignorar distâncias mal formatadas
                }
            }
        }

        return distances
    }

    /**
     * Extrai todos os tempos encontrados nos textos
     */
    private fun extractTimes(texts: List<String>): List<Pair<Int, Float>> {
        val times = mutableListOf<Pair<Int, Float>>()

        texts.forEach { text ->
            val matcher = TIME_PATTERN.matcher(text)
            while (matcher.find()) {
                try {
                    val timeStr = matcher.group(1) ?: continue
                    var time = timeStr.toInt()

                    // Converter horas para minutos se necessário
                    if (text.contains(Regex("(?i)hora|hs|h"))) {
                        time *= 60
                    }

                    val confidence = calculateTimeConfidence(text, time)

                    if (time >= 1 && time < 1440) { // 1 min até 24 horas
                        times.add(Pair(time, confidence))
                    }
                } catch (e: NumberFormatException) {
                    // Ignorar tempos mal formatados
                }
            }
        }

        return times
    }

    /**
     * Seleciona o melhor preço baseado na confiança e contexto
     */
    private fun selectBestPrice(prices: List<Pair<Double, Float>>): Pair<Double, Float> {
        if (prices.isEmpty()) return Pair(0.0, 0f)

        // Priorizar preços com maior confiança
        val sortedByConfidence = prices.sortedByDescending { it.second }

        // Se há preços muito próximos, escolher o maior (mais provável de ser o total)
        val topCandidates = sortedByConfidence.take(3)
        val bestPrice = topCandidates.maxByOrNull { it.first } ?: topCandidates.first()

        return bestPrice
    }

    /**
     * Seleciona a melhor distância baseada na confiança
     */
    private fun selectBestDistance(distances: List<Pair<Double, Float>>): Pair<Double, Float> {
        if (distances.isEmpty()) return Pair(0.0, 0f)

        // Selecionar a mais confiante, mas preferir valores razoáveis (não muito pequenos)
        return distances
            .filter { it.first >= 0.5 } // Pelo menos 500m
            .maxByOrNull { it.second } ?: distances.maxByOrNull { it.second } ?: distances.first()
    }

    /**
     * Seleciona o melhor tempo baseado na confiança
     */
    private fun selectBestTime(times: List<Pair<Int, Float>>): Pair<Int, Float> {
        if (times.isEmpty()) return Pair(0, 0f)

        // Selecionar a mais confiante, preferindo valores razoáveis
        return times
            .filter { it.first >= 5 } // Pelo menos 5 minutos
            .maxByOrNull { it.second } ?: times.maxByOrNull { it.second } ?: times.first()
    }

    /**
     * Calcula confiança para um preço baseado no contexto
     */
    private fun calculatePriceConfidence(text: String, price: Double): Float {
        var confidence = 0.5f

        val lowerText = text.lowercase()

        // Aumentar confiança se está próximo de palavras relacionadas a preço
        if (lowerText.contains("total") || lowerText.contains("valor") ||
            lowerText.contains("preço") || lowerText.contains("fare")) {
            confidence += 0.3f
        }

        // Aumentar se tem símbolo de moeda
        if (lowerText.contains("r$") || lowerText.contains("$")) {
            confidence += 0.2f
        }

        // Penalizar se parece ser um valor muito baixo para corrida
        if (price < 5.0) {
            confidence -= 0.2f
        }

        return confidence.coerceIn(0f, 1f)
    }

    /**
     * Calcula confiança para uma distância baseada no contexto
     */
    private fun calculateDistanceConfidence(text: String, distance: Double): Float {
        var confidence = 0.6f

        val lowerText = text.lowercase()

        // Aumentar confiança se está próximo de palavras relacionadas a distância
        if (lowerText.contains("distância") || lowerText.contains("distance") ||
            lowerText.contains("trajeto") || lowerText.contains("rota")) {
            confidence += 0.3f
        }

        // Penalizar distâncias muito pequenas ou muito grandes
        if (distance < 0.5 || distance > 500) {
            confidence -= 0.3f
        }

        return confidence.coerceIn(0f, 1f)
    }

    /**
     * Calcula confiança para um tempo baseada no contexto
     */
    private fun calculateTimeConfidence(text: String, time: Int): Float {
        var confidence = 0.6f

        val lowerText = text.lowercase()

        // Aumentar confiança se está próximo de palavras relacionadas a tempo
        if (lowerText.contains("tempo") || lowerText.contains("time") ||
            lowerText.contains("duração") || lowerText.contains("duration")) {
            confidence += 0.3f
        }

        // Penalizar tempos muito curtos ou muito longos
        if (time < 3 || time > 720) { // 3 min até 12 horas
            confidence -= 0.3f
        }

        return confidence.coerceIn(0f, 1f)
    }
}
