package com.applimit.tracking

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.applimit.AppBlockedActivity
import com.applimit.DeviceAdminReceiver
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class BootCompletedReceiver : BroadcastReceiver() {
    private val TAG = "BootCompletedReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d(TAG, "Boot completed, checking for screen time block...")

        val pendingResult = goAsync()
        
        try {
            // Start tracking service immediately
            TrackingForegroundService.start(context)
            Log.d(TAG, "Started TrackingForegroundService")

            // Check and enforce screen time block synchronously
            val screenTimeBlock = runBlocking {
                getScreenTimeBlock(context)
            }

            if (screenTimeBlock != null) {
                Log.d(TAG, "Screen time block detected, showing block screen")
                
                // Try Device Admin lock first
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val adminComponent = ComponentName(context, DeviceAdminReceiver::class.java)
                
                if (devicePolicyManager.isAdminActive(adminComponent)) {
                    try {
                        devicePolicyManager.lockNow()
                        Log.d(TAG, "Device locked via Device Admin")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to lock via Device Admin: ${e.message}")
                    }
                }

                // Show block screen
                val untilTime = Instant.ofEpochMilli(screenTimeBlock.blockedUntilEpoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                val untilText = String.format("%02d:%02d", untilTime.hour, untilTime.minute)

                val blockIntent = Intent(context, AppBlockedActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_NO_HISTORY
                    putExtra("package_name", "screen_time")
                    putExtra("blocked_until", untilText)
                    putExtra("blocked_until_epoch", screenTimeBlock.blockedUntilEpoch)
                    putExtra("block_reason", screenTimeBlock.reason)
                }
                context.startActivity(blockIntent)
                Log.d(TAG, "Block screen started with time: $untilText")
            } else {
                Log.d(TAG, "No screen time block detected")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in BootCompletedReceiver: ${e.message}", e)
        } finally {
            pendingResult.finish()
        }
    }

    private data class ScreenTimeBlock(
        val reason: String,
        val blockedUntilEpoch: Long
    )

    private suspend fun getScreenTimeBlock(context: Context): ScreenTimeBlock? {
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val dailyEnabled = prefs.getBoolean("screen_time_daily_enabled", false)
        val dailyLimitMinutes = prefs.getInt("screen_time_daily_limit_minutes", 0)
        val weeklyEnabled = prefs.getBoolean("screen_time_weekly_enabled", false)
        val weeklyLimitMinutes = prefs.getInt("screen_time_weekly_limit_minutes", 0)

        if (!dailyEnabled && !weeklyEnabled) return null

        val usageRepository = UsageRepository.getInstance(context)

        val todayMs =
            if (dailyEnabled && dailyLimitMinutes > 0) usageRepository.getTodayUsage() else 0L
        val weekMs =
            if (weeklyEnabled && weeklyLimitMinutes > 0) usageRepository.getWeeklyUsage() else 0L

        if (dailyEnabled && dailyLimitMinutes > 0 && todayMs >= dailyLimitMinutes * 60_000L) {
            return ScreenTimeBlock("screen_time_daily", getDailyResetEpoch())
        }

        if (weeklyEnabled && weeklyLimitMinutes > 0 && weekMs >= weeklyLimitMinutes * 60_000L) {
            return ScreenTimeBlock("screen_time_weekly", getWeeklyResetEpoch(context))
        }

        return null
    }

    private fun getDailyResetEpoch(): Long {
        val zone = ZoneId.systemDefault()
        val tomorrow = LocalDate.now(zone).plusDays(1)
        return tomorrow.atStartOfDay(zone).toInstant().toEpochMilli()
    }

    private fun getWeeklyResetEpoch(context: Context): Long {
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val weekStartPref = prefs.getString("week_start", "monday") ?: "monday"
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val weekStart = getWeekStartDate(today, weekStartPref)
        return weekStart.plusDays(7).atStartOfDay(zone).toInstant().toEpochMilli()
    }

    private fun getWeekStartDate(today: LocalDate, weekStartPref: String): LocalDate {
        val daysToSubtract = if (weekStartPref == "sunday") {
            (today.dayOfWeek.value % DayOfWeek.SUNDAY.value).toLong()
        } else {
            (today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()
        }
        return today.minusDays(daysToSubtract)
    }
}
