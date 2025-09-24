package com.kmcounty.core.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RideParser
 * 
 * Tests various text parsing scenarios for ride information extraction
 */
class RideParserTest {

    private lateinit var parser: RideParser

    @Before
    fun setup() {
        parser = RideParser()
    }

    @Test
    fun `parseRideInfo should extract price and distance from Uber-like text`() {
        val texts = listOf(
            "Corrida para Centro",
            "R$ 12,50",
            "3,2 km",
            "15 min",
            "Motorista: João",
            "Aceitar",
            "Recusar"
        )

        val result = parser.parseRideInfo(texts, "com.ubercab")

        assertThat(result).isNotNull()
        assertThat(result!!.totalPrice).isEqualTo(12.50f)
        assertThat(result.distance).isEqualTo(3.2f)
        assertThat(result.estimatedTime).isEqualTo(15)
        assertThat(result.pricePerKm).isWithin(0.01f).of(3.91f)
        assertThat(result.pricePerMinute).isWithin(0.01f).of(0.83f)
    }

    @Test
    fun `parseRideInfo should handle different price formats`() {
        val testCases = listOf(
            listOf("R$15,00", "2 km", "corrida") to 15.0f,
            listOf("R$ 8,75", "1,5 km", "viagem") to 8.75f,
            listOf("12,30 R$", "4 km", "destino") to 12.30f,
            listOf("R$25.50", "3 km", "uber") to 25.50f
        )

        testCases.forEach { (texts, expectedPrice) ->
            val result = parser.parseRideInfo(texts, "com.ubercab")
            assertThat(result).isNotNull()
            assertThat(result!!.totalPrice).isEqualTo(expectedPrice)
        }
    }

    @Test
    fun `parseRideInfo should handle different distance formats`() {
        val testCases = listOf(
            listOf("R$ 10,00", "2,5 km", "corrida") to 2.5f,
            listOf("R$ 10,00", "3.2 km", "viagem") to 3.2f,
            listOf("R$ 10,00", "1 quilômetro", "destino") to 1.0f,
            listOf("R$ 10,00", "5,8 quilômetros", "uber") to 5.8f
        )

        testCases.forEach { (texts, expectedDistance) ->
            val result = parser.parseRideInfo(texts, "com.ubercab")
            assertThat(result).isNotNull()
            assertThat(result!!.distance).isEqualTo(expectedDistance)
        }
    }

    @Test
    fun `parseRideInfo should handle different time formats`() {
        val testCases = listOf(
            listOf("R$ 10,00", "2 km", "15 min", "corrida") to 15,
            listOf("R$ 10,00", "2 km", "20 minutos", "viagem") to 20,
            listOf("R$ 10,00", "2 km", "1h 30min", "destino") to 90,
            listOf("R$ 10,00", "2 km", "2 horas", "uber") to 120
        )

        testCases.forEach { (texts, expectedTime) ->
            val result = parser.parseRideInfo(texts, "com.ubercab")
            assertThat(result).isNotNull()
            assertThat(result!!.estimatedTime).isEqualTo(expectedTime)
        }
    }

    @Test
    fun `parseRideInfo should return null for non-ride content`() {
        val texts = listOf(
            "Weather forecast",
            "Temperature: 25°C",
            "Humidity: 60%"
        )

        val result = parser.parseRideInfo(texts, "com.weather.app")

        assertThat(result).isNull()
    }

    @Test
    fun `parseRideInfo should return null for invalid price ranges`() {
        val texts = listOf(
            "R$ 99999,00", // Too expensive
            "1 km",
            "corrida"
        )

        val result = parser.parseRideInfo(texts, "com.ubercab")

        assertThat(result).isNull()
    }

    @Test
    fun `parseRideInfo should return null for invalid distance ranges`() {
        val texts = listOf(
            "R$ 10,00",
            "9999 km", // Too far
            "corrida"
        )

        val result = parser.parseRideInfo(texts, "com.ubercab")

        assertThat(result).isNull()
    }

    @Test
    fun `parseRideInfo should handle missing time information`() {
        val texts = listOf(
            "R$ 12,50",
            "3,2 km",
            "corrida para centro"
        )

        val result = parser.parseRideInfo(texts, "com.ubercab")

        assertThat(result).isNotNull()
        assertThat(result!!.estimatedTime).isNull()
        assertThat(result.pricePerMinute).isNull()
        assertThat(result.pricePerKm).isWithin(0.01f).of(3.91f)
    }

    @Test
    fun `parseRideInfo should have higher confidence for known apps`() {
        val texts = listOf(
            "R$ 12,50",
            "3,2 km",
            "15 min",
            "corrida uber"
        )

        val uberResult = parser.parseRideInfo(texts, "com.ubercab")
        val unknownResult = parser.parseRideInfo(texts, "com.unknown.app")

        assertThat(uberResult).isNotNull()
        assertThat(unknownResult).isNotNull()
        assertThat(uberResult!!.confidence).isGreaterThan(unknownResult!!.confidence)
    }

    @Test
    fun `parseRideInfo should handle 99 app specific content`() {
        val texts = listOf(
            "Viagem 99",
            "Valor: R$ 18,90",
            "Distância: 4,5 km",
            "Tempo estimado: 22 min",
            "Aceitar corrida"
        )

        val result = parser.parseRideInfo(texts, "br.com.app99")

        assertThat(result).isNotNull()
        assertThat(result!!.totalPrice).isEqualTo(18.90f)
        assertThat(result.distance).isEqualTo(4.5f)
        assertThat(result.estimatedTime).isEqualTo(22)
        assertThat(result.sourceApp).isEqualTo("br.com.app99")
    }

    @Test
    fun `parseRideInfo should calculate correct price categories`() {
        // Good price (>= 1.50)
        val goodPriceTexts = listOf("R$ 15,00", "10 km", "corrida")
        val goodResult = parser.parseRideInfo(goodPriceTexts, "com.ubercab")
        assertThat(goodResult!!.getPriceCategory()).isEqualTo(com.kmcounty.core.model.PriceCategory.GOOD)

        // Neutral price (0.80 - 1.49)
        val neutralPriceTexts = listOf("R$ 10,00", "10 km", "corrida")
        val neutralResult = parser.parseRideInfo(neutralPriceTexts, "com.ubercab")
        assertThat(neutralResult!!.getPriceCategory()).isEqualTo(com.kmcounty.core.model.PriceCategory.NEUTRAL)

        // Poor price (< 0.80)
        val poorPriceTexts = listOf("R$ 5,00", "10 km", "corrida")
        val poorResult = parser.parseRideInfo(poorPriceTexts, "com.ubercab")
        assertThat(poorResult!!.getPriceCategory()).isEqualTo(com.kmcounty.core.model.PriceCategory.POOR)
    }

    @Test
    fun `parseRideInfo should handle mixed text content with noise`() {
        val texts = listOf(
            "Menu",
            "Configurações",
            "R$ 14,75",
            "Distância: 2,8 km",
            "Tempo: 18 min",
            "Corrida disponível",
            "Motorista próximo",
            "Aceitar",
            "Ver detalhes"
        )

        val result = parser.parseRideInfo(texts, "com.ubercab")

        assertThat(result).isNotNull()
        assertThat(result!!.totalPrice).isEqualTo(14.75f)
        assertThat(result.distance).isEqualTo(2.8f)
        assertThat(result.estimatedTime).isEqualTo(18)
    }
}
