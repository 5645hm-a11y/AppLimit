package com.applimit.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.applimit.AppBlockedActivity
import com.applimit.SettingsProtectionActivity
import com.applimit.data.BlockRule
import com.applimit.data.RuleStorage
import com.applimit.security.SecurePrefs
import com.applimit.tracking.UsageRepository
import com.applimit.notifications.ScreenTimeNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * System-level accessibility service that intercepts app launches and blocks them based on
 * configured rules
 *
 * Features:
 * - Real-time app launch interception
 * - Time-based blocking enforcement
 * - Automatic block screen display
 * - Device admin integration
 */
class AppBlockerAccessibilityService : AccessibilityService() {

    private val TAG = "AppBlockerService"
    private var isEnabled = false
    private val handler = Handler(Looper.getMainLooper())
    private val trackingScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var usageRepository: UsageRepository
    private var lastPackageName: String? = null
    private var lastTimestamp: Long = 0L
    private var isScreenOn: Boolean = true
    private var isShowingSettingsProtection = false
    private var lastScreenTimeBlockShownAt: Long = 0L
    private var lastRuleBlockShownAt: Long = 0L
    private var lastRuleBlockedPackage: String? = null
    private val statePrefs by lazy {
        getSharedPreferences("usage_tracking_state", Context.MODE_PRIVATE)
    }

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> handleScreenOff()
                Intent.ACTION_SCREEN_ON -> handleScreenOn()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isEnabled = true
        Log.d(TAG, "AccessibilityService created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        usageRepository = UsageRepository.getInstance(applicationContext)
        com.applimit.tracking.TrackingForegroundService.start(applicationContext)
        ScreenTimeNotificationManager.createNotificationChannel(applicationContext)
        isScreenOn =
            (getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive ?: true

        registerReceiver(
            screenStateReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        )

        restoreLastSession()

        val info =
                AccessibilityServiceInfo().apply {
                    eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or 
                                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                                AccessibilityEvent.TYPE_VIEW_FOCUSED or
                                AccessibilityEvent.TYPE_VIEW_CLICKED
                    feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                    flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or 
                           AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                           AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                    packageNames = null // Monitor all packages
                    notificationTimeout = 50
                }
        setServiceInfo(info)

        Log.i(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !isEnabled) return

        try {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    handleWindowStateChanged(event)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing accessibility event: ${e.message}", e)
        }
    }

    /** Intercept and block app launches based on configured rules */
    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        // Don't block if PIN dialog is open
        val prefs = SecurePrefs.get(this)
        if (prefs.getBoolean("pin_dialog_open", false)) return
        if (prefs.getBoolean("block_screen_open", false)) return
        
        // Check if grace period is active (60 seconds of free usage after correct PIN)
        val gracePeriodStartTime = prefs.getLong("grace_period_start_time", 0L)
        if (gracePeriodStartTime > 0L) {
            val elapsedSeconds = (System.currentTimeMillis() - gracePeriodStartTime) / 1000
            if (elapsedSeconds < 60) return
            prefs.edit().remove("grace_period_start_time").apply()
        }
        
        val packageName = event.packageName?.toString() ?: return

        trackAppSwitch(packageName)
        
        // IMPORTANT: Check Settings access BEFORE ignoring system packages
        checkSettingsAccess(packageName, event)

        val screenTimeBlock = getScreenTimeBlock()
        if (screenTimeBlock != null && !isSystemPackage(packageName) && packageName != "com.applimit") {
            blockForScreenTimeLimit(packageName, screenTimeBlock)
            return
        }

        // Ignore system packages and our own app (except for Settings which we already checked above)
        if (isSystemPackage(packageName) || packageName == "com.applimit") {
            Log.d(TAG, "Ignoring system/own package: $packageName")
            return
        }

        // Check if app should be blocked
        val rules = RuleStorage.load(this)
        
        val matchingRule = rules.firstOrNull {
            it.packageName == packageName && shouldBlockApp(it)
        }
        if (matchingRule == null) {
            return
        }

        blockApp(packageName, matchingRule)
    }

    private data class ScreenTimeBlock(
        val reason: String,
        val blockedUntilEpoch: Long
    )

    private fun getScreenTimeBlock(): ScreenTimeBlock? {
        val prefs = getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val dailyEnabled = prefs.getBoolean("screen_time_daily_enabled", false)
        val dailyLimitMinutes = prefs.getInt("screen_time_daily_limit_minutes", 0)
        val weeklyEnabled = prefs.getBoolean("screen_time_weekly_enabled", false)
        val weeklyLimitMinutes = prefs.getInt("screen_time_weekly_limit_minutes", 0)

        if (!dailyEnabled && !weeklyEnabled) return null

        val todayMs = if (dailyEnabled && dailyLimitMinutes > 0) {
            runBlocking(Dispatchers.IO) { usageRepository.getTodayUsage() }
        } else {
            0L
        }

        val weekMs = if (weeklyEnabled && weeklyLimitMinutes > 0) {
            runBlocking(Dispatchers.IO) { usageRepository.getWeeklyUsage() }
        } else {
            0L
        }

        // Check for daily limit and send notifications
        if (dailyEnabled && dailyLimitMinutes > 0) {
            val dailyLimitMs = dailyLimitMinutes * 60_000L
            val remainingMs = dailyLimitMs - todayMs
            
            if (remainingMs > 0) {
                // Send notifications at appropriate intervals
                ScreenTimeNotificationManager.checkAndNotify(this, remainingMs)
            }
            
            if (todayMs >= dailyLimitMs) {
                return ScreenTimeBlock("screen_time_daily", getDailyResetEpoch())
            }
        }

        if (weeklyEnabled && weeklyLimitMinutes > 0 && weekMs >= weeklyLimitMinutes * 60_000L) {
            return ScreenTimeBlock("screen_time_weekly", getWeeklyResetEpoch())
        }

        return null
    }

    private fun getDailyResetEpoch(): Long {
        val zone = ZoneId.systemDefault()
        val tomorrow = LocalDate.now(zone).plusDays(1)
        return tomorrow.atStartOfDay(zone).toInstant().toEpochMilli()
    }

    private fun getWeeklyResetEpoch(): Long {
        val prefs = getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
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

    private fun blockForScreenTimeLimit(packageName: String, screenTimeBlock: ScreenTimeBlock) {
        val now = System.currentTimeMillis()
        if (now - lastScreenTimeBlockShownAt < 1500L) return
        lastScreenTimeBlockShownAt = now

        handler.post {
            try {
                val intent =
                        Intent(Intent.ACTION_MAIN).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            addCategory(Intent.CATEGORY_HOME)
                        }
                startActivity(intent)

                handler.postDelayed({
                    val untilTime =
                        Instant.ofEpochMilli(screenTimeBlock.blockedUntilEpoch)
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime()
                    val untilText = String.format("%02d:%02d", untilTime.hour, untilTime.minute)
                    val blockIntent =
                        Intent(this, AppBlockedActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                                        Intent.FLAG_ACTIVITY_NO_HISTORY
                            putExtra("package_name", packageName)
                            putExtra("blocked_until", untilText)
                            putExtra("blocked_until_epoch", screenTimeBlock.blockedUntilEpoch)
                            putExtra("block_reason", screenTimeBlock.reason)
                        }
                    startActivity(blockIntent)
                }, 500)
            } catch (e: Exception) {
                Log.e(TAG, "Error blocking for screen time limit: ${e.message}", e)
            }
        }
    }
    
    /** Check if user is accessing Settings or Accessibility for our app */
    private fun checkSettingsAccess(packageName: String, event: AccessibilityEvent) {
        // Check if we're in Settings app
        if (packageName != "com.android.settings") return
        
        try {
            // Get all available information from the event
            val className = event.className?.toString() ?: ""
            val contentDescription = event.contentDescription?.toString() ?: ""
            val packageNameFromEvent = event.packageName?.toString() ?: ""
            
            // Build searchable text from all event text + check window content
            val allText = buildString {
                event.text.forEach { append(it).append(" ") }
                append(contentDescription).append(" ")
                append(className).append(" ")
                append(packageNameFromEvent).append(" ")
                
                // Also check the root window for more text content
                try {
                    rootInActiveWindow?.let { root ->
                        extractAllText(root).forEach { append(it).append(" ") }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error extracting window text: ${e.message}")
                }
            }.lowercase()
            
            // DEBUG: Log EVERYTHING to see what's happening
            Log.d(TAG, "🔍 SETTINGS EVENT - className: [$className]")
            Log.d(TAG, "🔍 SETTINGS EVENT - allText length: ${allText.length}")
            Log.d(TAG, "🔍 SETTINGS EVENT - allText preview: [${allText.take(300)}]")
            Log.d(TAG, "🔍 SETTINGS EVENT - eventType: ${event.eventType}")
            
            // Check if this is our app's settings page  
            val hasOurAppIdentifier = allText.contains("applimit") ||
                                     allText.contains("com.applimit") ||
                                     allText.contains("app limit") ||
                                     allText.contains("אפליקציה")
            
            // Detection 1: App Details page - explicit page types
            val isExplicitAppDetailsPage = className.contains("InstalledAppDetails", ignoreCase = true) ||
                                          className.contains("AppDetailSummary", ignoreCase = true) ||
                                          className.contains("AppInfo", ignoreCase = true)
            
            // Detection 2: App List with our app visible - could lead to clicking our app
            val isAppListPage = className.contains("RecyclerView", ignoreCase = true) ||
                               className.contains("ListView", ignoreCase = true)
            
            // Don't block if it's just the "All apps" list page header
            val isJustAppListHeader = (allText.contains("all apps", ignoreCase = true) ||
                                      allText.contains("toutes les applis", ignoreCase = true)) &&
                                     !className.contains("InstalledApp", ignoreCase = true)
            
            // Block if: (1) Explicit app details page OR (2) App list showing our app (user about to click)
            val isOurAppSettings = hasOurAppIdentifier && 
                                  !isJustAppListHeader &&
                                  (isExplicitAppDetailsPage || isAppListPage)
            
            // Detection 3: Accessibility settings page
            val isAccessibilityRelated = allText.contains("accessibility") ||
                                        allText.contains("נגישות") ||
                                        className.contains("Accessibility", ignoreCase = true)
            
            val isAccessibilitySettings = isAccessibilityRelated && 
                                         (hasOurAppIdentifier || 
                                          className.contains("AccessibilitySettings", ignoreCase = true) ||
                                          allText.contains("services") ||
                                          allText.contains("installed services"))
            
            Log.d(TAG, "🔍 DETECTION - hasOurApp: $hasOurAppIdentifier, explicitDetails: $isExplicitAppDetailsPage, appList: $isAppListPage")
            Log.d(TAG, "🔍 DETECTION - isAccessibilityRelated: $isAccessibilityRelated")
            Log.d(TAG, "🔍 DETECTION - FINAL - isOurAppSettings: $isOurAppSettings, isAccessibilitySettings: $isAccessibilitySettings")
            
            if (!isOurAppSettings && !isAccessibilitySettings) return
            
            Log.w(TAG, "⚠️ BLOCKING SECURITY THREAT - className: $className, isOurApp: $isOurAppSettings, isAccessibility: $isAccessibilitySettings")
            
            // Check if access is currently granted (within 30 seconds window)
            val prefs = getSharedPreferences("settings_protection", Context.MODE_PRIVATE)
            val accessGrantedUntil = prefs.getLong("access_granted_until", 0)
            val now = System.currentTimeMillis()
            
            if (now < accessGrantedUntil) {
                val remainingSeconds = (accessGrantedUntil - now) / 1000
                Log.d(TAG, "Settings access allowed - ${remainingSeconds}s remaining")
                return
            }
            
            // Access not granted - show PIN screen
            Log.d(TAG, "Blocking settings access - PIN required")
            showSettingsProtection()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking settings access: ${e.message}", e)
        }
    }
    
    /** Show settings protection PIN screen */
    private fun showSettingsProtection() {
        // Prevent multiple simultaneous displays
        if (isShowingSettingsProtection) {
            Log.d(TAG, "Settings protection already showing - skipping")
            return
        }
        
        isShowingSettingsProtection = true
        
        handler.post {
            try {
                val intent = Intent("com.applimit.SETTINGS_PROTECTION").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                           Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                Log.d(TAG, "🔒 Launching SettingsProtectionActivity via ACTION intent")
                startActivity(intent)
                
                // Reset flag after 5 seconds
                handler.postDelayed({
                    isShowingSettingsProtection = false
                }, 5000)
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR launching settings protection: ${e.message}", e)
                // Force close Settings as punishment
                performGlobalAction(GLOBAL_ACTION_BACK)
                performGlobalAction(GLOBAL_ACTION_BACK)
                isShowingSettingsProtection = false
            }
        }
    }

    /** Check if app should be blocked based on rule and current time */
    private fun shouldBlockApp(rule: BlockRule): Boolean {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = now

        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(java.util.Calendar.MINUTE)
        val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        // Convert Android Calendar day (1=Sunday, 7=Saturday) to 0-indexed (0=Monday, 6=Sunday)
        val dayIndex = if (currentDayOfWeek == 1) 6 else currentDayOfWeek - 2

        Log.d(TAG, "Current time: ${currentHour}:${currentMinute} on day $dayIndex, rule days: ${rule.days}")

        // Check if today is in the blocked days
        if (dayIndex !in rule.days) {
            Log.d(TAG, "Today ($dayIndex) not in blocked days")
            return false
        }

        // Check if current time is within blocked time range
        val currentTime = currentHour * 60 + currentMinute
        val startTime = rule.startHour * 60 + rule.startMinute
        val endTime = rule.endHour * 60 + rule.endMinute

        val isInRange = currentTime in startTime..endTime
        Log.d(TAG, "Current time $currentTime in range $startTime-$endTime: $isInRange")
        
        return isInRange
    }

    /** Block an app by showing overlay and closing it */
    private fun blockApp(packageName: String, rule: BlockRule) {
        val now = System.currentTimeMillis()
        if (packageName == lastRuleBlockedPackage && now - lastRuleBlockShownAt < 1500L) {
            return
        }
        lastRuleBlockedPackage = packageName
        lastRuleBlockShownAt = now

        handler.post {
            try {
                // Close the app
                val intent =
                        Intent(Intent.ACTION_MAIN).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            addCategory(Intent.CATEGORY_HOME)
                        }
                startActivity(intent)

                // Show block screen immediately - no delay for instant response
                handler.postDelayed({ showBlockedScreen(packageName, rule) }, 50)
            } catch (e: Exception) {
                Log.e(TAG, "Error blocking app: ${e.message}", e)
            }
        }
    }

    /** Show app blocked screen overlay */
    private fun showBlockedScreen(packageName: String, rule: BlockRule) {
        try {
            val intent =
                    Intent(this, AppBlockedActivity::class.java).apply {
                        flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                                        Intent.FLAG_ACTIVITY_NO_HISTORY or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("package_name", packageName)
                        putExtra("blocked_until", formatBlockedTime(rule))
                        putExtra("blocked_until_hour", rule.endHour)
                        putExtra("blocked_until_minute", rule.endMinute)
                    }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing blocked screen: ${e.message}", e)
        }
    }

    /** Format blocked until time string */
    private fun formatBlockedTime(rule: BlockRule): String {
        val (hour, minute) = parseTime(rule.endHour, rule.endMinute)
        return String.format("%02d:%02d", hour, minute)
    }

    /** Parse time components */
    private fun parseTime(hour: Int, minute: Int): Pair<Int, Int> {
        return Pair(hour, minute)
    }

    /** Check if package is a system package */
    private fun isSystemPackage(packageName: String): Boolean {
        val systemPackages =
                listOf(
                        "com.android",
                        "android.",
                        "com.google.android.systemui",
                        "com.google.android.gms",
                        "com.applimit" // Our app
                )
        return systemPackages.any { packageName.startsWith(it) }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        isEnabled = false
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (_: Exception) {
        }
        trackingScope.cancel()
        Log.d(TAG, "Accessibility service destroyed")
    }

    private fun trackAppSwitch(newPackageName: String) {
        if (!isScreenOn) return

        val now = System.currentTimeMillis()
        if (newPackageName != lastPackageName) {
            closeCurrentSession(now)

            lastPackageName = newPackageName
            lastTimestamp = now

            persistLastSession()
        }
    }

    private fun closeCurrentSession(endTime: Long) {
        val packageName = lastPackageName ?: return
        val startTime = lastTimestamp
        if (startTime <= 0 || endTime <= startTime) return

        trackingScope.launch {
            usageRepository.addUsageSession(packageName, startTime, endTime)
        }

        lastPackageName = null
        lastTimestamp = 0L
        persistLastSession()
    }

    private fun handleScreenOff() {
        isScreenOn = false
        persistScreenState()
        closeCurrentSession(System.currentTimeMillis())
    }

    private fun handleScreenOn() {
        isScreenOn = true
        persistScreenState()
        lastPackageName = null
        lastTimestamp = 0L
        persistLastSession()
    }

    private fun persistLastSession() {
        statePrefs.edit()
            .putString("last_package", lastPackageName)
            .putLong("last_timestamp", lastTimestamp)
            .apply()
    }

    private fun persistScreenState() {
        statePrefs.edit()
            .putBoolean("screen_on", isScreenOn)
            .apply()
    }
    
    /** Recursively extract all text from accessibility node */
    private fun extractAllText(node: AccessibilityNodeInfo): List<String> {
        val texts = mutableListOf<String>()
        try {
            node.text?.toString()?.let { if (it.isNotBlank()) texts.add(it) }
            node.contentDescription?.toString()?.let { if (it.isNotBlank()) texts.add(it) } 
            node.viewIdResourceName?.toString()?.let { if (it.isNotBlank()) texts.add(it) }
            
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { child ->
                    texts.addAll(extractAllText(child))
                    child.recycle()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting text from node: ${e.message}")
        }
        return texts
    }

    private fun restoreLastSession() {
        lastPackageName = statePrefs.getString("last_package", null)
        lastTimestamp = statePrefs.getLong("last_timestamp", 0L)
        isScreenOn = statePrefs.getBoolean("screen_on", true)
    }
}
