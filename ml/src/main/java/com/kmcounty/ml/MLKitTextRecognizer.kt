package com.kmcounty.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kmcounty.core.parser.RideTextParser
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Wrapper para ML Kit Text Recognition
 * Fornece OCR fallback quando AccessibilityService não consegue extrair dados
 */
class MLKitTextRecognizer(private val context: Context) {

    companion object {
        private const val TAG = "MLKitTextRecognizer"
    }

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val rideParser = RideTextParser()

    /**
     * Processa um bitmap e extrai texto usando ML Kit
     */
    suspend fun recognizeText(bitmap: Bitmap): OCRResult {
        return try {
            Log.d(TAG, "Iniciando reconhecimento OCR no bitmap ${bitmap.width}x${bitmap.height}")

            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val visionText = textRecognizer.process(inputImage).await()

            // Extrair linhas de texto
            val textLines = visionText.textBlocks.flatMap { block ->
                block.lines.map { line ->
                    TextLine(
                        text = line.text,
                        confidence = line.confidence,
                        boundingBox = line.boundingBox
                    )
                }
            }

            val extractedTexts = textLines.map { it.text }
            Log.d(TAG, "OCR reconheceu ${extractedTexts.size} linhas de texto")

            // Tentar analisar como pedido de corrida
            val parsedData = rideParser.parseRideData(extractedTexts)

            OCRResult.Success(
                textLines = textLines,
                parsedRideData = if (parsedData.price > 0) parsedData else null
            )

        } catch (e: Exception) {
            Log.e(TAG, "Erro no reconhecimento OCR", e)
            OCRResult.Error(e)
        }
    }

    /**
     * Libera recursos do recognizer
     */
    fun close() {
        textRecognizer.close()
    }

    /**
     * Resultado do processamento OCR
     */
    sealed class OCRResult {
        data class Success(
            val textLines: List<TextLine>,
            val parsedRideData: RideTextParser.ParsedRideData?
        ) : OCRResult()

        data class Error(val exception: Exception) : OCRResult()
    }

    /**
     * Representa uma linha de texto reconhecida
     */
    data class TextLine(
        val text: String,
        val confidence: Float,
        val boundingBox: android.graphics.Rect?
    )
}

/**
 * Extensão para converter Task em suspendCoroutine
 */
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return suspendCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
