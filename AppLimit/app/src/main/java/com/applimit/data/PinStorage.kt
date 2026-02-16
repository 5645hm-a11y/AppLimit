package com.applimit.data

import android.content.Context

object PinStorage {
    private const val PREF_NAME = "pin_storage"
    private const val KEY_PIN = "user_pin"

    fun save(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun check(context: Context, pin: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, null) == pin
    }

    fun isPinSet(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.contains(KEY_PIN)
    }
}
