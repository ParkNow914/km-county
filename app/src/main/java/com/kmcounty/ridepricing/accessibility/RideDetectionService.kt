package com.kmcounty.ridepricing.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.kmcounty.core.parser.RideParser
import com.kmcounty.ridepricing.data.model.DetectedRide
import com.kmcounty.ridepricing.overlay.OverlayService
import com.kmcounty.ridepricing.utils.PiiFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Accessibility service that monitors screen content for ride information
 * 
 * This service:
 * - Monitors specific transport apps (Uber, 99, etc.)
 * - Extracts ride information from screen content
 * - Filters out PII automatically  
 * - Sends parsed data to overlay service
 * - Never performs any actions in monitored apps
 */
@AndroidEntryPoint
class RideDetectionService : AccessibilityService() {

    @Inject
    lateinit var rideParser: RideParser
    
    @Inject 
    lateinit var piiFilter: PiiFilter

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Target package names for ride apps
    private val targetPackages = setOf(
        "com.ubercab",                    // Uber
        "com.uber.android",               // Uber alternative
        "br.com.app99",                   // 99
        "com.easytaxi.android",          // Easy Taxi
        "com.ifood.delivery",            // iFood (delivery rides)
        "com.loggi.rider",               // Loggi
        "br.com.cabify.rider",           // Cabify
        "com.getninjas.app"              // GetNinjas
    )

    private var lastProcessedTime = 0L
    private val minProcessingInterval = 2000L // 2 seconds minimum between processing

    override fun onServiceConnected() {
        super.onServiceConnected()
        Timber.i("RideDetectionService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Only process events from target apps
        val packageName = event.packageName?.toString()
        if (packageName == null || !targetPackages.contains(packageName)) {
            return
        }

        // Rate limiting to avoid excessive processing
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedTime < minProcessingInterval) {
            return
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                processAccessibilityEvent(event, packageName)
                lastProcessedTime = currentTime
            }
        }
    }

    private fun processAccessibilityEvent(event: AccessibilityEvent, packageName: String) {
        serviceScope.launch {
            try {
                val rootNode = rootInActiveWindow ?: return@launch
                val extractedTexts = extractTextFromNode(rootNode)
                
                if (extractedTexts.isEmpty()) {
                    Timber.d("No texts extracted from $packageName")
                    return@launch
                }

                // Filter out PII before processing
                val filteredTexts = extractedTexts.mapNotNull { text ->
                    if (piiFilter.containsPii(text)) {
                        Timber.d("Filtered out PII content")
                        null
                    } else {
                        text
                    }
                }

                if (filteredTexts.isEmpty()) {
                    return@launch
                }

                // Try to parse ride information
                val parsedRide = rideParser.parseRideInfo(filteredTexts, packageName)
                
                if (parsedRide != null) {
                    Timber.i("Detected ride: ${parsedRide.pricePerKm} R$/km")
                    
                    // Send to overlay service
                    val detectedRide = DetectedRide(
                        totalPrice = parsedRide.totalPrice,
                        distance = parsedRide.distance,
                        estimatedTime = parsedRide.estimatedTime,
                        pricePerKm = parsedRide.pricePerKm,
                        pricePerMinute = parsedRide.pricePerMinute,
                        confidence = parsedRide.confidence,
                        sourceApp = packageName,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    sendRideToOverlay(detectedRide)
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error processing accessibility event")
            }
        }
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo): List<String> {
        val texts = mutableListOf<String>()
        
        // Extract text from current node
        node.text?.toString()?.let { text ->
            if (text.isNotBlank()) {
                texts.add(text.trim())
            }
        }
        
        // Extract content description
        node.contentDescription?.toString()?.let { description ->
            if (description.isNotBlank()) {
                texts.add(description.trim())
            }
        }

        // Recursively extract from child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                texts.addAll(extractTextFromNode(childNode))
                childNode.recycle()
            }
        }

        return texts.distinct()
    }

    private fun sendRideToOverlay(detectedRide: DetectedRide) {
        val intent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_UPDATE_RIDE
            putExtra(OverlayService.EXTRA_RIDE_DATA, detectedRide)
        }
        startService(intent)
    }

    override fun onInterrupt() {
        Timber.w("RideDetectionService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("RideDetectionService destroyed")
    }

    companion object {
        const val SERVICE_NAME = "com.kmcounty.ridepricing/.accessibility.RideDetectionService"
    }
}
