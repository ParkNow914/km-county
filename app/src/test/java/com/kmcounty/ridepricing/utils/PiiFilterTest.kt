package com.kmcounty.ridepricing.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for PiiFilter
 * 
 * Tests PII detection and filtering functionality
 */
class PiiFilterTest {

    private lateinit var piiFilter: PiiFilter

    @Before
    fun setup() {
        piiFilter = PiiFilter()
    }

    @Test
    fun `should detect phone numbers in various formats`() {
        val phoneTexts = listOf(
            "(11) 99999-9999",
            "11 99999-9999",
            "11999999999",
            "(11)99999-9999",
            "11 9 9999-9999"
        )

        phoneTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("Phone detection for: $text")
                .isTrue()
        }
    }

    @Test
    fun `should detect CPF numbers`() {
        val cpfTexts = listOf(
            "123.456.789-01",
            "12345678901",
            "123.456.789-01",
            "CPF: 123.456.789-01"
        )

        cpfTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("CPF detection for: $text")
                .isTrue()
        }
    }

    @Test
    fun `should detect email addresses`() {
        val emailTexts = listOf(
            "usuario@email.com",
            "test.email@gmail.com",
            "contato@empresa.com.br",
            "Email: usuario@provedor.net"
        )

        emailTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("Email detection for: $text")
                .isTrue()
        }
    }

    @Test
    fun `should detect addresses`() {
        val addressTexts = listOf(
            "Rua das Flores, 123",
            "Avenida Paulista, 1000",
            "Travessa Santos, 45 - Apartamento 12",
            "Estrada Municipal, km 15",
            "Vila Madalena, São Paulo"
        )

        addressTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("Address detection for: $text")
                .isTrue()
        }
    }

    @Test
    fun `should detect potential names`() {
        val nameTexts = listOf(
            "João Silva",
            "Maria Santos Oliveira",
            "Dr. Carlos Eduardo",
            "Sra. Ana Paula"
        )

        nameTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("Name detection for: $text")
                .isTrue()
        }
    }

    @Test
    fun `should NOT detect PII in safe ride-related texts`() {
        val safeTexts = listOf(
            "R$ 12,50",
            "3,5 km",
            "15 minutos",
            "Corrida para Centro",
            "Uber",
            "99",
            "Aceitar",
            "Recusar",
            "Tempo estimado",
            "Distância",
            "Preço",
            "Motorista próximo",
            "Viagem disponível"
        )

        safeTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("Safe text should not be PII: $text")
                .isFalse()
        }
    }

    @Test
    fun `should filter PII from text lists`() {
        val mixedTexts = listOf(
            "R$ 15,00",           // Safe
            "João Silva",         // PII - name
            "3,2 km",            // Safe
            "(11) 99999-9999",   // PII - phone
            "Corrida disponível", // Safe
            "usuario@email.com",  // PII - email
            "Aceitar"            // Safe
        )

        val filtered = piiFilter.filterPiiFromTexts(mixedTexts)

        assertThat(filtered).containsExactly(
            "R$ 15,00",
            "3,2 km",
            "Corrida disponível",
            "Aceitar"
        )
    }

    @Test
    fun `should provide accurate PII statistics`() {
        val texts = listOf(
            "R$ 12,50",           // Safe
            "João Silva",         // Name
            "3 km",              // Safe
            "(11) 99999-9999",   // Phone
            "usuario@email.com",  // Email
            "Rua das Flores, 123", // Address
            "123.456.789-01",    // CPF
            "Aceitar corrida"    // Safe
        )

        val stats = piiFilter.getPiiStats(texts)

        assertThat(stats.totalTexts).isEqualTo(8)
        assertThat(stats.filteredTexts).isEqualTo(5)
        assertThat(stats.phoneCount).isEqualTo(1)
        assertThat(stats.emailCount).isEqualTo(1)
        assertThat(stats.addressCount).isEqualTo(1)
        assertThat(stats.cpfCount).isEqualTo(1)
        assertThat(stats.nameCount).isEqualTo(1)
        assertThat(stats.filterRate).isWithin(0.01f).of(0.625f)
    }

    @Test
    fun `should handle empty and null texts safely`() {
        assertThat(piiFilter.containsPii("")).isFalse()
        assertThat(piiFilter.containsPii("   ")).isFalse()
        
        val emptyFiltered = piiFilter.filterPiiFromTexts(emptyList())
        assertThat(emptyFiltered).isEmpty()

        val mixedWithEmpty = piiFilter.filterPiiFromTexts(listOf("", "R$ 10,00", "   ", "2 km"))
        assertThat(mixedWithEmpty).containsExactly("R$ 10,00", "2 km")
    }

    @Test
    fun `should sanitize texts correctly`() {
        assertThat(piiFilter.sanitizeText("R$ 12,50")).isEqualTo("R$ 12,50")
        assertThat(piiFilter.sanitizeText("João Silva")).isNull()
        assertThat(piiFilter.sanitizeText("(11) 99999-9999")).isNull()
        assertThat(piiFilter.sanitizeText("3,5 km")).isEqualTo("3,5 km")
    }

    @Test
    fun `should not flag common app words as names`() {
        val appTexts = listOf(
            "Uber Driver",
            "99 Taxi",
            "Cabify Premium",
            "Easy Taxi",
            "Loggi Express"
        )

        appTexts.forEach { text ->
            assertThat(piiFilter.containsPii(text))
                .named("App text should not be PII: $text")
                .isFalse()
        }
    }

    @Test
    fun `should handle edge cases correctly`() {
        // Very short potential names
        assertThat(piiFilter.containsPii("A B")).isFalse()
        
        // Numbers that look like phone but aren't
        assertThat(piiFilter.containsPii("12345")).isFalse() // Too short
        assertThat(piiFilter.containsPii("123456789012")).isFalse() // Too long
        
        // Price with dots that might look like CPF
        assertThat(piiFilter.containsPii("R$ 1.234.567-89")).isFalse() // Has R$ prefix
        
        // Street names without numbers (less likely to be addresses)
        assertThat(piiFilter.containsPii("Rua das Flores")).isFalse() // No number
    }
}
