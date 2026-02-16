package com.applimit.grayscale

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.applimit.data.GrayscaleRuleStorage
import java.util.Calendar

object GrayscaleController {
    private const val PREF = "grayscale_settings"

    fun update(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("enabled", false)
            if (!enabled) {
                setGrayscaleEnabled(context, false)
                Log.d("GrayscaleController", "Grayscale disabled by user")
                return
            }

            val rules = GrayscaleRuleStorage.load(context)
            Log.d("GrayscaleController", "Loaded ${rules.size} rules")
            if (rules.isEmpty()) {
                setGrayscaleEnabled(context, false)
                Log.d("GrayscaleController", "No rules found")
                return
            }

            val shouldEnable = rules.any { rule ->
                val withinSchedule = isWithinSchedule(rule)
                Log.d("GrayscaleController", "Rule: ${rule.startHour}:${rule.startMinute} - ${rule.endHour}:${rule.endMinute}, Days: ${rule.days}, Within: $withinSchedule")
                withinSchedule
            }
            Log.d("GrayscaleController", "Should enable grayscale: $shouldEnable")
            setGrayscaleEnabled(context, shouldEnable)
        } catch (e: Exception) {
            Log.w("GrayscaleController", "Update failed: ${e.message}")
        }
    }

    private fun setGrayscaleEnabled(context: Context, enabled: Boolean) {
        try {
            if (enabled) {
                Settings.Secure.putInt(
                    context.contentResolver,
                    "accessibility_display_daltonizer_enabled",
                    1
                )
                Settings.Secure.putInt(
                    context.contentResolver,
                    "accessibility_display_daltonizer",
                    0
                )
            } else {
                Settings.Secure.putInt(
                    context.contentResolver,
                    "accessibility_display_daltonizer_enabled",
                    0
                )
            }
        } catch (e: SecurityException) {
            Log.w("GrayscaleController", "Missing secure settings permission")
        } catch (e: Exception) {
            Log.w("GrayscaleController", "Failed to set grayscale: ${e.message}")
        }
    }

    private fun isWithinSchedule(rule: com.applimit.data.BlockRule): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayIndex = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

        val nowMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val startMinutes = rule.startHour * 60 + rule.startMinute
        val endMinutes = rule.endHour * 60 + rule.endMinute

        // Check if start and end are the same (all day)
        if (startMinutes == endMinutes) {
            return dayIndex in rule.days
        }

        // Normal case: start < end (same day)
        if (startMinutes < endMinutes) {
            return dayIndex in rule.days && nowMinutes in startMinutes until endMinutes
        } else {
            // Overnight case: start > end (e.g., 22:00 - 08:00)
            // Check if current time is after start time TODAY
            if (nowMinutes >= startMinutes) {
                return dayIndex in rule.days
            } else {
                // Check if current time is before end time YESTERDAY
                val prevDay = if (dayIndex == 1) 7 else dayIndex - 1
                return prevDay in rule.days && nowMinutes < endMinutes
            }
        }
    }
}
