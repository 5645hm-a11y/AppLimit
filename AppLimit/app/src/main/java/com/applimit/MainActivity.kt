package com.applimit

import android.app.admin.DevicePolicyManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.applimit.security.SecurePinManager
import com.applimit.services.AppBlockerAccessibilityService
import com.applimit.tracking.TrackingForegroundService
import com.applimit.ui.screens.DashboardScreen
import com.applimit.ui.screens.OnboardingScreen
import com.applimit.ui.screens.PinVerificationScreen
import com.applimit.ui.screens.SplashScreen
import com.applimit.ui.theme.SafeTimeGuardTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    permissions ->
                android.util.Log.d("MainActivity", "Permissions requested: $permissions")
            }
    
    private var isResumeAllowed = false
    private var requirePinOnResume = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request device admin activation
        requestDeviceAdminActivation()

        // Start foreground tracking service
        TrackingForegroundService.start(this)

        // Ensure required tracking permissions and settings
        requestTrackingRequirements()

        // Check if onboarding is complete
        val prefs = getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val onboardingComplete = prefs.getBoolean("onboarding_complete", false)
        val isPinSetup = SecurePinManager.isPinSet(this)
        val splashShown = prefs.getBoolean("splash_shown", false)

        setContent {
            SafeTimeGuardTheme {
                var currentScreen by remember {
                    mutableStateOf(
                            when {
                                !onboardingComplete || !isPinSetup -> "onboarding"
                                !splashShown -> "splash"
                                else -> "dashboard"
                            }
                    )
                }
                
                // Make language reactive - update when screen changes
                var currentLanguage by remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }
                
                // React to PIN requirement on resume
                LaunchedEffect(requirePinOnResume.value) {
                    if (requirePinOnResume.value && currentScreen == "dashboard") {
                        currentScreen = "pin_verification"
                        requirePinOnResume.value = false
                    }
                }

                when (currentScreen) {
                    "onboarding" -> {
                        OnboardingScreen(
                                onOnboardingComplete = {
                                    // Onboarding screen already saves onboarding_complete
                                    // Read the freshly saved language from SharedPreferences
                                    val savedLanguage = prefs.getString("language", "en") ?: "en"
                                    android.util.Log.d("MainActivity", "Language saved: $savedLanguage")
                                    currentLanguage = savedLanguage
                                    currentScreen = "splash"
                                }
                        )
                    }
                    "splash" -> {
                        SplashScreen(
                                onComplete = {
                                    // Mark splash as shown
                                    prefs.edit().putBoolean("splash_shown", true).apply()
                                    // Show PIN verification screen
                                    currentScreen = "pin_verification"
                                }
                        )
                    }
                    "pin_verification" -> {
                        PinVerificationScreen(
                                language = currentLanguage,
                                onPinVerified = {
                                    isResumeAllowed = true
                                    currentScreen = "dashboard"
                                },
                                onCancel = {
                                    // Exit to home screen - prevent app access without PIN
                                    val intent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intent)
                                    finishAffinity() // Close all activities
                                },
                                context = this@MainActivity
                        )
                    }
                    "dashboard" -> {
                        DashboardScreen(currentLanguage) { newLang ->
                            // Update application-level language when settings change
                            currentLanguage = newLang
                        }
                    }
                }
            }
        }
    }

    private fun requestDeviceAdminActivation() {
        try {
            val devicePolicyManager =
                    getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(this, DeviceAdminReceiver::class.java)

            // Check if already an admin
            if (!devicePolicyManager.isAdminActive(adminComponent)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                intent.putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "AppLimit needs Device Admin to control app blocking"
                )

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.w(
                            "MainActivity",
                            "Could not start device admin activity: ${e.message}"
                    )
                    // This is not critical - app can still function
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "Device admin error: ${e.message}")
        }
    }

    private fun requestTrackingRequirements() {
        try {
            if (!hasUsageAccess()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                return
            }

            if (!isIgnoringBatteryOptimizations()) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                return
            }

            if (!isAccessibilityEnabled()) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "Tracking requirement check failed: ${e.message}")
        }
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(),
                packageName
            )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expected =
            ComponentName(this, AppBlockerAccessibilityService::class.java).flattenToString()
        val enabled =
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
        val accessibilityEnabled =
            Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                0
            ) == 1
        return accessibilityEnabled && enabled.split(":").any { it.equals(expected, true) }
    }

    override fun onResume() {
        super.onResume()
        // Check if PIN is required (not first time or was paused)
        val prefs = getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val onboardingComplete = prefs.getBoolean("onboarding_complete", false)
        val isPinSetup = SecurePinManager.isPinSet(this)
        
        if (onboardingComplete && isPinSetup && !isResumeAllowed) {
            // Trigger PIN verification via Compose state
            requirePinOnResume.value = true
            android.util.Log.d("MainActivity", "onResume: Requiring PIN verification")
        } else {
            isResumeAllowed = true
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Reset resume flag when app goes to background
        isResumeAllowed = false
    }
}
