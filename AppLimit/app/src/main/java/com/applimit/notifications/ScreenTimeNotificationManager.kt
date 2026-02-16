package com.applimit.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.applimit.R
import com.applimit.LanguageManager
import com.applimit.security.SecurePrefs

/**
 * Manages screen time warning notifications
 * Sends alerts at 1 hour, 30 minutes, 5 minutes, and 1 minute before screen time expires
 */
object ScreenTimeNotificationManager {
    private const val CHANNEL_ID = "screen_time_warnings"
    private const val NOTIFICATION_ID_1_HOUR = 1001
    private const val NOTIFICATION_ID_30_MIN = 1002
    private const val NOTIFICATION_ID_5_MIN = 1003
    private const val NOTIFICATION_ID_1_MIN = 1004

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Screen Time Warnings",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when screen time is about to expire"
            enableVibration(true)
            enableLights(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Check remaining time and send appropriate notifications
     * @param remainingMs Milliseconds remaining until screen time expires
     */
    fun checkAndNotify(context: Context, remainingMs: Long) {
        val prefs = SecurePrefs.get(context)
        val language = prefs.getString("app_language", LanguageManager.HEBREW) ?: LanguageManager.HEBREW
        
        val minutes = (remainingMs / 1000 / 60).toInt()
        val lastNotified = prefs.getString("last_screen_time_notification", "") ?: ""
        
        when {
            minutes == 60 && lastNotified != "60" -> {
                sendNotification(
                    context,
                    NOTIFICATION_ID_1_HOUR,
                    getTimeWarningTitle(language, "1 hour"),
                    getTimeWarningMessage(language, "1 hour")
                )
                prefs.edit().putString("last_screen_time_notification", "60").apply()
            }
            minutes == 30 && lastNotified != "30" -> {
                sendNotification(
                    context,
                    NOTIFICATION_ID_30_MIN,
                    getTimeWarningTitle(language, "30 minutes"),
                    getTimeWarningMessage(language, "30 minutes")
                )
                prefs.edit().putString("last_screen_time_notification", "30").apply()
            }
            minutes == 5 && lastNotified != "5" -> {
                sendNotification(
                    context,
                    NOTIFICATION_ID_5_MIN,
                    getTimeWarningTitle(language, "5 minutes"),
                    getTimeWarningMessage(language, "5 minutes")
                )
                prefs.edit().putString("last_screen_time_notification", "5").apply()
            }
            minutes == 1 && lastNotified != "1" -> {
                sendNotification(
                    context,
                    NOTIFICATION_ID_1_MIN,
                    getTimeWarningTitle(language, "1 minute"),
                    getTimeWarningMessage(language, "1 minute")
                )
                prefs.edit().putString("last_screen_time_notification", "1").apply()
            }
            minutes > 60 -> {
                // Reset notification state when time is refreshed
                prefs.edit().putString("last_screen_time_notification", "").apply()
            }
        }
    }

    private fun sendNotification(context: Context, notificationId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        notificationManager.notify(notificationId, notification)
    }

    private fun getTimeWarningTitle(language: String, timeLeft: String): String {
        return when (language) {
            LanguageManager.HEBREW -> "⏰ זמן המסך אוזל"
            LanguageManager.FRENCH -> "⏰ Temps d'écran bientôt épuisé"
            LanguageManager.ARABIC -> "⏰ وقت الشاشة ينفد"
            LanguageManager.SPANISH -> "⏰ Tiempo de pantalla agotándose"
            else -> "⏰ Screen Time Running Out"
        }
    }

    private fun getTimeWarningMessage(language: String, timeLeft: String): String {
        return when (language) {
            LanguageManager.HEBREW -> when (timeLeft) {
                "1 hour" -> "נותרה שעה אחת לזמן המסך שלך"
                "30 minutes" -> "נותרו 30 דקות לזמן המסך שלך"
                "5 minutes" -> "נותרו 5 דקות בלבד! 🕐"
                "1 minute" -> "דקה אחרונה! המסך יינעל בקרוב 🔒"
                else -> timeLeft
            }
            LanguageManager.FRENCH -> when (timeLeft) {
                "1 hour" -> "Il reste 1 heure de temps d'écran"
                "30 minutes" -> "Il reste 30 minutes de temps d'écran"
                "5 minutes" -> "Seulement 5 minutes restantes! 🕐"
                "1 minute" -> "Dernière minute! L'écran se verrouillera bientôt 🔒"
                else -> timeLeft
            }
            LanguageManager.ARABIC -> when (timeLeft) {
                "1 hour" -> "تبقى ساعة واحدة من وقت الشاشة"
                "30 minutes" -> "تبقى 30 دقيقة من وقت الشاشة"
                "5 minutes" -> "5 دقائق فقط متبقية! 🕐"
                "1 minute" -> "الدقيقة الأخيرة! ستُقفل الشاشة قريباً 🔒"
                else -> timeLeft
            }
            else -> when (timeLeft) {
                "1 hour" -> "1 hour of screen time remaining"
                "30 minutes" -> "30 minutes of screen time remaining"
                "5 minutes" -> "Only 5 minutes left! 🕐"
                "1 minute" -> "Last minute! Screen will lock soon 🔒"
                else -> timeLeft
            }
        }
    }
}
