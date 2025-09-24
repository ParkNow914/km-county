package com.kmcounty.ridepricing.overlay

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.kmcounty.core.model.PriceCategory
import com.kmcounty.ridepricing.R
import com.kmcounty.ridepricing.data.model.DetectedRide
import com.kmcounty.ridepricing.ui.theme.RidePricingTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Foreground service that shows a floating overlay with ride pricing information
 * 
 * The overlay is read-only and never interferes with the underlying app
 */
@AndroidEntryPoint
class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var currentRide: DetectedRide? = null
    private var isExpanded by mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        Timber.i("OverlayService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_OVERLAY -> {
                startForeground(NOTIFICATION_ID, createNotification())
                showOverlay()
            }
            ACTION_STOP_OVERLAY -> {
                hideOverlay()
                stopSelf()
            }
            ACTION_UPDATE_RIDE -> {
                val rideData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_RIDE_DATA, DetectedRide::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<DetectedRide>(EXTRA_RIDE_DATA)
                }
                
                if (rideData != null) {
                    updateRideInfo(rideData)
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showOverlay() {
        if (overlayView != null) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 200
        }

        overlayView = FrameLayout(this).apply {
            addView(ComposeView(context).apply {
                setContent {
                    RidePricingTheme {
                        OverlayContent(
                            ride = currentRide,
                            isExpanded = isExpanded,
                            onToggleExpanded = { isExpanded = !isExpanded },
                            onClose = { hideOverlay(); stopSelf() }
                        )
                    }
                }
            })
        }

        try {
            windowManager?.addView(overlayView, layoutParams)
            Timber.i("Overlay shown")
        } catch (e: Exception) {
            Timber.e(e, "Failed to show overlay")
        }
    }

    private fun hideOverlay() {
        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
                overlayView = null
                Timber.i("Overlay hidden")
            } catch (e: Exception) {
                Timber.e(e, "Failed to hide overlay")
            }
        }
    }

    private fun updateRideInfo(ride: DetectedRide) {
        currentRide = ride
        // The compose content will automatically recompose when currentRide changes
        Timber.d("Ride info updated: R$%.2f/km", ride.pricePerKm)
    }

    @Composable
    private fun OverlayContent(
        ride: DetectedRide?,
        isExpanded: Boolean,
        onToggleExpanded: () -> Unit,
        onClose: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = ride?.let { getRideBackgroundColor(it) } ?: MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (ride == null) {
                // No ride detected state
                NoRideOverlay(onClose = onClose)
            } else {
                // Active ride state
                RideOverlay(
                    ride = ride,
                    isExpanded = isExpanded,
                    onToggleExpanded = onToggleExpanded,
                    onClose = onClose
                )
            }
        }
    }

    @Composable
    private fun NoRideOverlay(onClose: () -> Unit) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🔍 Buscando...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    @Composable
    private fun RideOverlay(
        ride: DetectedRide,
        isExpanded: Boolean,
        onToggleExpanded: () -> Unit,
        onClose: () -> Unit
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header with main info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "R$ %.2f/km".format(ride.pricePerKm),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (ride.pricePerMinute != null) {
                        Text(
                            text = "R$ %.2f/min".format(ride.pricePerMinute),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleExpanded,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Recolher" else "Expandir",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expanded content
            if (isExpanded) {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DetailRow("Total", "R$ %.2f".format(ride.totalPrice))
                    DetailRow("Distância", "%.1f km".format(ride.distance))
                    
                    if (ride.estimatedTime != null) {
                        DetailRow("Tempo", "${ride.estimatedTime} min")
                    }
                    
                    // Confidence indicator
                    if (ride.confidence < 0.8f) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Confiança: ${(ride.confidence * 100).toInt()}%",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    @Composable
    private fun getRideBackgroundColor(ride: DetectedRide): Color {
        return when {
            ride.pricePerKm >= 1.50f -> Color(0xFF4CAF50).copy(alpha = 0.9f) // Green
            ride.pricePerKm >= 0.80f -> Color(0xFFFF9800).copy(alpha = 0.9f) // Orange
            else -> Color(0xFFF44336).copy(alpha = 0.9f) // Red
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay de Preços",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificação para o overlay de preços de corridas"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, OverlayService::class.java).apply {
            action = ACTION_STOP_OVERLAY
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Assistente R\$/km")
            .setContentText("Monitorando preços de corridas")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setSilent(true)
            .addAction(
                R.drawable.ic_close,
                "Parar",
                stopPendingIntent
            )
            .build()
    }

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
        Timber.i("OverlayService destroyed")
    }

    companion object {
        const val ACTION_START_OVERLAY = "com.kmcounty.ridepricing.START_OVERLAY"
        const val ACTION_STOP_OVERLAY = "com.kmcounty.ridepricing.STOP_OVERLAY"
        const val ACTION_UPDATE_RIDE = "com.kmcounty.ridepricing.UPDATE_RIDE"
        const val EXTRA_RIDE_DATA = "ride_data"
        
        private const val CHANNEL_ID = "overlay_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
