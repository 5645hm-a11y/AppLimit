package com.applimit

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.applimit.LanguageManager
import com.applimit.data.RuleStorage
import com.applimit.security.SecurePrefs
import com.applimit.ui.screens.AppBlockedScreen
import com.applimit.ui.theme.SafeTimeGuardTheme

/**
 * Activity shown when a blocked app is attempted to be opened This is a full-screen overlay that
 * cannot be dismissed easily
 */
class AppBlockedActivity : ComponentActivity() {

    private val devicePolicyManager by lazy {
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    private val adminComponent by lazy {
        ComponentName(this, DeviceAdminReceiver::class.java)
    }
    private var isPinDialogOpen = false
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SecurePrefs.get(this).edit().putBoolean("block_screen_open", true).apply()

        // Acquire wake lock to keep screen on while blocking screen is shown
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "AppBlockedActivity::blocking_screen_lock"
        ).apply {
            acquire()
        }

        // Prevent user from dismissing this easily
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_SECURE or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_SECURE or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        val packageName = intent.getStringExtra("package_name") ?: "Unknown App"
        val blockedUntilRaw = intent.getStringExtra("blocked_until") ?: ""
        val blockedUntilEpoch = intent.getLongExtra("blocked_until_epoch", 0L)
        val blockedUntilHour = intent.getIntExtra("blocked_until_hour", -1)
        val blockedUntilMinute = intent.getIntExtra("blocked_until_minute", -1)
        val blockReason = intent.getStringExtra("block_reason") ?: ""
        val blockedUntil =
                if (blockedUntilRaw.isNotBlank() && blockedUntilRaw != "null") {
                    blockedUntilRaw
                } else if (blockedUntilHour >= 0 && blockedUntilMinute >= 0) {
                    String.format("%02d:%02d", blockedUntilHour, blockedUntilMinute)
                } else {
                    val rule = RuleStorage.load(this).firstOrNull { it.packageName == packageName }
                    if (rule != null) {
                        String.format("%02d:%02d", rule.endHour, rule.endMinute)
                    } else {
                        "18:00"
                    }
                }

        // Get app name from package manager
        // Get the app's preferred language from SharedPreferences
        val prefs = getSharedPreferences("safetimeguard_settings", MODE_PRIVATE)
        val appLanguage = prefs.getString("language", LanguageManager.ENGLISH) ?: LanguageManager.ENGLISH

        val appName =
                if (blockReason.startsWith("screen_time")) {
                    LanguageManager.getString("screen_time_title", appLanguage)
                } else {
                    try {
                        val pm = packageManager
                        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
                    } catch (e: Exception) {
                        packageName
                    }
                }
        

        setContent {
            SafeTimeGuardTheme {
                AppBlockedScreen(
                        appName = appName,
                        blockedUntilTime = blockedUntil,
                        language = appLanguage,
                        blockReason = blockReason,
                        blockedUntilEpoch = blockedUntilEpoch,
                        onExitUnlocked = {
                            val intent =
                                    Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                            startActivity(intent)
                            finish()
                        },
                        onPinDialogStateChanged = { isOpen ->
                            isPinDialogOpen = isOpen
                            // Mark in SharedPreferences to prevent AccessibilityService from blocking again
                            val prefs = SecurePrefs.get(this)
                            prefs.edit().putBoolean("pin_dialog_open", isOpen).apply()
                            
                            // Only remove FLAG_SECURE when dialog opens to allow keyboard
                            // Keep FLAG_SHOW_WHEN_LOCKED to prevent going to launcher
                            if (isOpen) {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                            } else {
                                window.setFlags(
                                    WindowManager.LayoutParams.FLAG_SECURE,
                                    WindowManager.LayoutParams.FLAG_SECURE
                                )
                            }
                        }
                )
            }
        }
    }

    override fun onBackPressed() {
        if (!isPinDialogOpen) {
            lockDeviceOrHome()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isPinDialogOpen) {
            lockDeviceOrHome()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear PIN dialog flag when activity is destroyed
        val prefs = SecurePrefs.get(this)
        prefs.edit()
            .putBoolean("pin_dialog_open", false)
            .putBoolean("block_screen_open", false)
            .apply()
        
        // Release wake lock to allow device to sleep
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {
            // Ignore release errors
        }
    }

    private fun lockDeviceOrHome() {
        try {
            // Only return to home - do NOT lock device as it causes unwanted sleep
            val intent =
                    Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
            startActivity(intent)
            // Give system time to show home screen before finishing
            finish()
        } catch (_: Exception) {
            // Safe fallback
            finish()
        }
    }
}

