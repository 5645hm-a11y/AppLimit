package com.applimit.data

import android.content.Context
import android.content.Intent
import com.applimit.BuildConfig
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

object RuleStorage {

    private const val PREF = "rules_storage_v2"
    private const val KEY = "rules_json"
    val ACTION_RULES_UPDATED = "${BuildConfig.APPLICATION_ID}.RULES_UPDATED"

    fun save(context: Context, rules: List<BlockRule>) {
        val array = JSONArray()
        rules.forEach { rule ->
            val obj = JSONObject()
            obj.put("id", rule.id)
            obj.put("packageName", rule.packageName)
            obj.put("days", JSONArray(rule.days.toList()))
            obj.put("startHour", rule.startHour)
            obj.put("startMinute", rule.startMinute)
            obj.put("endHour", rule.endHour)
            obj.put("endMinute", rule.endMinute)
            array.put(obj)
        }

        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, array.toString())
                .apply()

        // Notify the accessibility service that the rules have changed.
        val intent = Intent(ACTION_RULES_UPDATED)
        context.sendBroadcast(intent)
    }

    fun load(context: Context): List<BlockRule> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, "[]") ?: "[]"
        val list = mutableListOf<BlockRule>()

        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val packageName = obj.optString("packageName")
                if (packageName.isNullOrEmpty()) continue

                val daysArray = obj.optJSONArray("days")
                val days = mutableSetOf<Int>()
                if (daysArray != null) {
                    for (d in 0 until daysArray.length()) {
                        days.add(daysArray.getInt(d))
                    }
                }

                val id = obj.optString("id", UUID.randomUUID().toString())
                val startHour = obj.optInt("startHour", -1)
                val startMinute = obj.optInt("startMinute", 0)
                val endHour = obj.optInt("endHour", -1)
                val endMinute = obj.optInt("endMinute", 0)

                if (startHour == -1 || endHour == -1) continue

                list.add(
                        BlockRule(
                                id = id,
                                packageName = packageName,
                                days = days,
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute
                        )
                )
            }
        } catch (e: Exception) {
            clear(context)
            return emptyList()
        }

        return list
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(ACTION_RULES_UPDATED)
        context.sendBroadcast(intent)
    }

    /** Get rule for a specific package, or null if not found */
    fun getRuleForPackage(context: Context, packageName: String): BlockRule? {
        return load(context).firstOrNull { it.packageName == packageName }
    }
}

