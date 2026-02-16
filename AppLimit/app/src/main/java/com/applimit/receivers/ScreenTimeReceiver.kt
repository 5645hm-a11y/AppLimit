package com.applimit.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log

/**
 * Tracks screen on/off events to calculate actual screen time
 */
class ScreenTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ScreenTime", "onReceive called with action: ${intent.action}")
        val action = intent.action ?: return
        val prefs = context.getSharedPreferences("screen_time_prefs", Context.MODE_PRIVATE)
        
        when (action) {
            Intent.ACTION_SCREEN_ON -> {
                val screenOnTime = System.currentTimeMillis()
                prefs.edit().putLong("last_screen_on_time", screenOnTime).apply()
                Log.d("ScreenTime", "Screen ON at: $screenOnTime")
            }
            Intent.ACTION_SCREEN_OFF -> {
                val screenOffTime = System.currentTimeMillis()
                val screenOnTime = prefs.getLong("last_screen_on_time", screenOffTime)
                val sessionDuration = screenOffTime - screenOnTime
                
                if (sessionDuration > 0) {
                    // Add to today's total
                    val today = getTodayKey()
                    val todayTotal = prefs.getLong("screen_time_$today", 0)
                    prefs.edit().putLong("screen_time_$today", todayTotal + sessionDuration).apply()
                    
                    // Add to week total
                    val weekStart = getWeekStartKey(context)
                    val weekTotal = prefs.getLong("screen_time_week_$weekStart", 0)
                    prefs.edit().putLong("screen_time_week_$weekStart", weekTotal + sessionDuration).apply()
                    
                    Log.d("ScreenTime", "Screen OFF at: $screenOffTime, session: $sessionDuration ms, today total: ${todayTotal + sessionDuration} ms")
                }
                
                prefs.edit().putLong("last_screen_on_time", 0).apply()
            }
        }
    }
    
    private fun getTodayKey(): String {
        val calendar = java.util.Calendar.getInstance()
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH)}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }
    
    private fun getWeekStartKey(context: Context): String {
        val calendar = java.util.Calendar.getInstance()
        val settings = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val weekStartPref = settings.getString("week_start", "monday") ?: "monday"
        calendar.firstDayOfWeek =
            if (weekStartPref == "sunday") java.util.Calendar.SUNDAY else java.util.Calendar.MONDAY
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH)}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
    }
}
