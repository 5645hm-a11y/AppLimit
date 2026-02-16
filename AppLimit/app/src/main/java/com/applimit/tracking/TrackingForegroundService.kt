package com.applimit.tracking

import android.app.Service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.applimit.R
import com.applimit.grayscale.GrayscaleController

class TrackingForegroundService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Start foreground service with notification FIRST
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Delay heavy initialization to avoid ANR
        scope.launch(Dispatchers.Default) {
            delay(2000) // Delay to allow system to settle (increased from 1s to 2s for safety)
            scheduleReconcile()
            scheduleGrayscale()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotification(): android.app.Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AppLimit Service",
                NotificationManager.IMPORTANCE_MIN
            )
            channel.description = ""
            channel.setShowBadge(false)
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setSound(null, null)
            channel.lockscreenVisibility = android.app.Notification.VISIBILITY_SECRET
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun scheduleReconcile() {
        val repository = UsageRepository.getInstance(this)
        scope.launch(Dispatchers.IO) {
            while (true) {
                repository.reconcileWithUsageStats()
                repository.validateAccuracy()
                delay(RECONCILE_INTERVAL_MS)
            }
        }
    }

    companion object {
        private const val RECONCILE_INTERVAL_MS = 15 * 60 * 1000L
        private const val GRAYSCALE_CHECK_INTERVAL_MS = 10 * 1000L
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "safetimeguard_service"

        fun start(context: Context) {
            val intent = Intent(context, TrackingForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private fun scheduleGrayscale() {
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    GrayscaleController.update(this@TrackingForegroundService)
                } catch (e: Exception) {
                    android.util.Log.e("TrackingService", "Grayscale update failed", e)
                }
                delay(GRAYSCALE_CHECK_INTERVAL_MS)
            }
        }
    }
}
