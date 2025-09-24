package com.kmcounty.ridepricing.utils

import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for detecting and filtering Personally Identifiable Information (PII)
 * 
 * This is a critical privacy component that ensures no personal data is processed or stored
 */
@Singleton
class PiiFilter @Inject constructor() {

    // Regex patterns for detecting PII
    private val phonePattern = Pattern.compile(
        """(\(?\d{2}\)?\s?)?(\d{4,5})-?(\d{4})""" // Brazilian phone numbers
    )
    
    private val cpfPattern = Pattern.compile(
        """\d{3}\.?\d{3}\.?\d{3}-?\d{2}""" // CPF format
    )
    
    private val emailPattern = Pattern.compile(
        """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    )
    
    // Address indicators (common Brazilian address terms)
    private val addressKeywords = setOf(
        "rua", "avenida", "av", "travessa", "alameda", "praça", "largo",
        "estrada", "rodovia", "vila", "jardim", "bairro", "cidade",
        "cep", "número", "nº", "casa", "apartamento", "apt", "bloco",
        "quadra", "lote", "condomínio", "residencial"
    )
    
    // Name patterns (common Brazilian name patterns)
    private val namePatterns = listOf(
        Pattern.compile("""(?:sr\.?|sra\.?|dr\.?|dra\.?)\s+\w+""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""[A-ZÁÉÍÓÚÇÂÊÔÃÕ][a-záéíóúçâêôãõ]+\s+[A-ZÁÉÍÓÚÇÂÊÔÃÕ][a-záéíóúçâêôãõ]+""") // Name patterns
    )

    /**
     * Check if the given text contains PII
     */
    fun containsPii(text: String): Boolean {
        if (text.isBlank()) return false
        
        val cleanText = text.trim()
        
        return containsPhone(cleanText) ||
                containsCpf(cleanText) ||
                containsEmail(cleanText) ||
                containsAddress(cleanText) ||
                containsName(cleanText)
    }

    /**
     * Filter out PII from a list of texts
     */
    fun filterPiiFromTexts(texts: List<String>): List<String> {
        return texts.filter { !containsPii(it) }
    }

    /**
     * Sanitize text by removing PII while keeping useful information
     */
    fun sanitizeText(text: String): String? {
        if (containsPii(text)) {
            return null // Complete removal for now - could be enhanced to partial sanitization
        }
        return text
    }

    private fun containsPhone(text: String): Boolean {
        // Remove common separators and check
        val cleanText = text.replace(Regex("""[\s\-\(\)]"""), "")
        
        // Check for phone number patterns
        if (phonePattern.matcher(text).find()) return true
        
        // Check for sequences of 8-11 digits (likely phone numbers)
        if (cleanText.matches(Regex("""\d{8,11}"""))) return true
        
        return false
    }

    private fun containsCpf(text: String): Boolean {
        return cpfPattern.matcher(text).find()
    }

    private fun containsEmail(text: String): Boolean {
        return emailPattern.matcher(text).find()
    }

    private fun containsAddress(text: String): Boolean {
        val lowerText = text.lowercase()
        
        // Check for address keywords
        val hasAddressKeyword = addressKeywords.any { keyword ->
            lowerText.contains(keyword)
        }
        
        if (!hasAddressKeyword) return false
        
        // If it has address keywords, check if it looks like a full address
        // (has numbers and street indicators)
        val hasNumbers = text.contains(Regex("""\d+"""))
        
        return hasNumbers && hasAddressKeyword
    }

    private fun containsName(text: String): Boolean {
        // Check for name patterns
        for (pattern in namePatterns) {
            if (pattern.matcher(text).find()) return true
        }
        
        // Additional heuristics for names
        val words = text.split(Regex("""\s+"""))
        
        // Check for sequences of capitalized words (potential names)
        if (words.size >= 2) {
            val capitalizedWords = words.count { word ->
                word.isNotBlank() && word[0].isUpperCase() && word.length > 1
            }
            
            // If most words are capitalized and it's not too long, might be a name
            if (capitalizedWords >= 2 && words.size <= 4 && 
                !containsCommonAppWords(text)) {
                return true
            }
        }
        
        return false
    }

    private fun containsCommonAppWords(text: String): Boolean {
        val lowerText = text.lowercase()
        val commonAppWords = setOf(
            "uber", "99", "cabify", "easytaxi", "loggi", "ifood",
            "corrida", "viagem", "destino", "origem", "motorista",
            "preço", "valor", "distância", "tempo", "minutos", "km",
            "aceitar", "recusar", "confirmar", "solicitar", "cartão",
            "dinheiro", "pix", "pagamento", "taxa", "desconto"
        )
        
        return commonAppWords.any { word -> lowerText.contains(word) }
    }

    /**
     * Get statistics about PII filtering (for debugging/monitoring)
     */
    fun getPiiStats(texts: List<String>): PiiStats {
        var totalTexts = texts.size
        var filteredTexts = 0
        var phoneCount = 0
        var cpfCount = 0
        var emailCount = 0
        var addressCount = 0
        var nameCount = 0

        texts.forEach { text ->
            if (containsPii(text)) {
                filteredTexts++
                if (containsPhone(text)) phoneCount++
                if (containsCpf(text)) cpfCount++
                if (containsEmail(text)) emailCount++
                if (containsAddress(text)) addressCount++
                if (containsName(text)) nameCount++
            }
        }

        return PiiStats(
            totalTexts = totalTexts,
            filteredTexts = filteredTexts,
            phoneCount = phoneCount,
            cpfCount = cpfCount,
            emailCount = emailCount,
            addressCount = addressCount,
            nameCount = nameCount
        )
    }
}

/**
 * Statistics about PII filtering
 */
data class PiiStats(
    val totalTexts: Int,
    val filteredTexts: Int,
    val phoneCount: Int,
    val cpfCount: Int,
    val emailCount: Int,
    val addressCount: Int,
    val nameCount: Int
) {
    val filterRate: Float get() = if (totalTexts > 0) filteredTexts.toFloat() / totalTexts else 0f
}
