package com.kmcounty.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kmcounty.app.R

/**
 * Serviço em primeiro plano que mantém o overlay ativo.
 * 
 * Este serviço é usado para manter o processo do aplicativo ativo e gerenciar
 * o ciclo de vida do overlay, garantindo que ele permaneça visível mesmo quando
 * o aplicativo estiver em segundo plano.
 */
class OverlayService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "kmcounty_overlay_channel"
        private const val NOTIFICATION_ID = 1
        
        /**
         * Inicia o serviço de overlay.
         */
        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        /**
         * Para o serviço de overlay.
         */
        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /**
     * Cria a notificação para o serviço em primeiro plano.
     */
    private fun createNotification(): Notification {
        createNotificationChannel()
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_overlay_title))
            .setContentText(getString(R.string.notification_overlay_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    /**
     * Cria o canal de notificação para Android O e superior.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpar notificação ao encerrar
        stopForeground(true)
    }
}
