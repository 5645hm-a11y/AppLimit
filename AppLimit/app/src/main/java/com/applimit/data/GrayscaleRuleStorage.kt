package com.applimit.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// A separate storage class for grayscale rules to avoid conflicts with app blocking rules.
object GrayscaleRuleStorage {

    private const val PREF = "grayscale_rules_storage"
    private const val KEY = "grayscale_rules_json"

    fun save(context: Context, rules: List<BlockRule>) {
        val array = JSONArray()
        rules.forEach { rule ->
            val obj = JSONObject()
            obj.put("id", rule.id)
            // We don't need a package name for a global grayscale rule
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
    }

    fun delete(context: Context, ruleId: String) {
        val rules = load(context).toMutableList()
        rules.removeAll { it.id == ruleId }
        save(context, rules)
    }

    fun load(context: Context): List<BlockRule> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, "[]") ?: "[]"
        val list = mutableListOf<BlockRule>()

        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
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

                // Package name is irrelevant for grayscale, so we use a placeholder.
                list.add(
                    BlockRule(
                        id = id,
                        packageName = "grayscale", // Placeholder
                        days = days,
                        startHour = startHour,
                        startMinute = startMinute,
                        endHour = endHour,
                        endMinute = endMinute,
                        type = BlockType.GRAYSCALE
                    )
                )
            }
        } catch (e: Exception) {
            // In case of error, clear the storage to prevent crash loops.
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
            return emptyList()
        }

        return list
    }
}
