package com.kmcounty.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for on-device text recognition using ML Kit
 * 
 * This serves as a fallback when AccessibilityService cannot extract text
 */
@Singleton
class TextRecognitionService @Inject constructor() {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Extract text from bitmap using ML Kit
     */
    suspend fun extractTextFromBitmap(bitmap: Bitmap): TextRecognitionResult {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val visionText = textRecognizer.process(inputImage).await()
            
            val extractedTexts = mutableListOf<RecognizedText>()
            val confidence = mutableListOf<Float>()

            // Process text blocks
            for (block in visionText.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        if (element.text.isNotBlank()) {
                            extractedTexts.add(
                                RecognizedText(
                                    text = element.text.trim(),
                                    confidence = element.confidence ?: 0.0f,
                                    boundingBox = element.boundingBox
                                )
                            )
                            confidence.add(element.confidence ?: 0.0f)
                        }
                    }
                }
            }

            val avgConfidence = if (confidence.isNotEmpty()) {
                confidence.average().toFloat()
            } else {
                0.0f
            }

            Timber.d("Extracted ${extractedTexts.size} text elements with avg confidence: $avgConfidence")

            TextRecognitionResult(
                texts = extractedTexts,
                averageConfidence = avgConfidence,
                success = true
            )

        } catch (e: Exception) {
            Timber.e(e, "Failed to extract text from bitmap")
            TextRecognitionResult(
                texts = emptyList(),
                averageConfidence = 0.0f,
                success = false,
                error = e.message
            )
        }
    }

    /**
     * Extract text from specific region of bitmap
     */
    suspend fun extractTextFromRegion(
        bitmap: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): TextRecognitionResult {
        return try {
            // Crop bitmap to specific region
            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                x.coerceAtLeast(0),
                y.coerceAtLeast(0),
                width.coerceAtMost(bitmap.width - x),
                height.coerceAtMost(bitmap.height - y)
            )

            extractTextFromBitmap(croppedBitmap)
        } catch (e: Exception) {
            Timber.e(e, "Failed to extract text from region")
            TextRecognitionResult(
                texts = emptyList(),
                averageConfidence = 0.0f,
                success = false,
                error = e.message
            )
        }
    }

    /**
     * Filter texts by confidence threshold
     */
    fun filterByConfidence(
        result: TextRecognitionResult,
        minConfidence: Float = 0.5f
    ): List<String> {
        return result.texts
            .filter { it.confidence >= minConfidence }
            .map { it.text }
    }

    /**
     * Get texts as simple string list (most common use case)
     */
    fun getTextsAsStringList(result: TextRecognitionResult): List<String> {
        return result.texts.map { it.text }
    }

    /**
     * Close the text recognizer when done
     */
    fun close() {
        try {
            textRecognizer.close()
        } catch (e: Exception) {
            Timber.w(e, "Error closing text recognizer")
        }
    }
}

/**
 * Data class for recognized text with metadata
 */
data class RecognizedText(
    val text: String,
    val confidence: Float,
    val boundingBox: android.graphics.Rect?
)

/**
 * Result of text recognition operation
 */
data class TextRecognitionResult(
    val texts: List<RecognizedText>,
    val averageConfidence: Float,
    val success: Boolean,
    val error: String? = null
) {
    /**
     * Check if recognition was successful and meets confidence threshold
     */
    fun isReliable(minConfidence: Float = 0.7f): Boolean {
        return success && averageConfidence >= minConfidence
    }

    /**
     * Get only high-confidence texts
     */
    fun getHighConfidenceTexts(minConfidence: Float = 0.8f): List<String> {
        return texts
            .filter { it.confidence >= minConfidence }
            .map { it.text }
    }
}
