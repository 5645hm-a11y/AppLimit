package com.applimit

import android.app.Application
import android.util.Log

class SafeTimeGuardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("SafeTimeGuard", "Application initialized")

        // Initialize any app-wide resources here
        initializeAppResources()
    }

    private fun initializeAppResources() {
        // Initialize encryption, preferences, services, etc.
        Log.d("SafeTimeGuard", "App resources initialized")
    }
}

