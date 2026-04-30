package com.applimit.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.tracking.UsageRepository
import com.applimit.ui.components.CircularProgressRing
import com.applimit.ui.components.SafeTimeGuardTopAppBar
import com.applimit.ui.theme.ProgressSizes
import com.applimit.ui.theme.SemanticBalance
import com.applimit.ui.theme.SemanticBlocking
import com.applimit.ui.theme.SemanticWarning
import kotlinx.coroutines.delay
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeLimitsScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE) }
    val usageRepository = remember { UsageRepository.getInstance(context) }

    var dailyEnabled by remember { mutableStateOf(false) }
    var dailyHours by remember { mutableStateOf(6f) }
    var weeklyEnabled by remember { mutableStateOf(false) }
    var weeklyHours by remember { mutableStateOf(20f) }
    var sessionTimeToday by remember { mutableLongStateOf(0L) }
    var sessionTimeWeek by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        dailyEnabled = prefs.getBoolean("screen_time_daily_enabled", false)
        weeklyEnabled = prefs.getBoolean("screen_time_weekly_enabled", false)
        val dailyMin = prefs.getInt("screen_time_daily_limit_minutes", 360)
        val weeklyMin = prefs.getInt("screen_time_weekly_limit_minutes", 1200)
        dailyHours = (dailyMin / 60f).coerceIn(0.5f, 23f)
        weeklyHours = (weeklyMin / 60f).coerceIn(1f, 168f)
    }

    LaunchedEffect(dailyEnabled, dailyHours) {
        prefs.edit()
            .putBoolean("screen_time_daily_enabled", dailyEnabled)
            .putInt("screen_time_daily_limit_minutes", (dailyHours * 60).toInt())
            .apply()
    }

    LaunchedEffect(weeklyEnabled, weeklyHours) {
        prefs.edit()
            .putBoolean("screen_time_weekly_enabled", weeklyEnabled)
            .putInt("screen_time_weekly_limit_minutes", (weeklyHours * 60).toInt())
            .apply()
    }

    LaunchedEffect(Unit) {
        while (true) {
            sessionTimeToday = usageRepository.getTodayUsage()
            sessionTimeWeek = usageRepository.getWeeklyUsage()
            delay(5000L)
        }
    }

    fun formatTime(ms: Long): String {
        val s = ms / 1000; val h = s / 3600; val m = (s % 3600) / 60
        return if (h > 0) "${h}h ${m}m" else if (m > 0) "${m}m" else "0m"
    }

    val dailyLimitMs = (dailyHours * 3_600_000f).toLong()
    val weeklyLimitMs = (weeklyHours * 3_600_000f).toLong()
    val dailyProgress = if (dailyEnabled && dailyLimitMs > 0) min(1f, sessionTimeToday.toFloat() / dailyLimitMs) else 0f
    val weeklyProgress = if (weeklyEnabled && weeklyLimitMs > 0) min(1f, sessionTimeWeek.toFloat() / weeklyLimitMs) else 0f

    fun ringColor(progress: Float): Color = when {
        progress >= 0.9f -> SemanticBlocking
        progress >= 0.7f -> SemanticWarning
        else -> SemanticBalance
    }

    Scaffold(
        topBar = {
            SafeTimeGuardTopAppBar(
                title = LanguageManager.getString("screen_time_management", language),
                onNavigateBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LimitCard(
                title = LanguageManager.getString("daily_limit", language),
                description = LanguageManager.getString("daily_limit_desc", language),
                enabled = dailyEnabled,
                onEnabledChange = { dailyEnabled = it },
                hours = dailyHours,
                maxHours = 23f,
                onHoursChange = { dailyHours = it },
                usedLabel = LanguageManager.getString("today_label", language),
                usedValue = formatTime(sessionTimeToday),
                limitLabel = LanguageManager.getString("limit_label", language),
                limitValue = "${dailyHours.toInt()}h",
                progress = dailyProgress,
                ringColor = ringColor(dailyProgress)
            )

            LimitCard(
                title = LanguageManager.getString("weekly_limit", language),
                description = LanguageManager.getString("weekly_limit_desc", language),
                enabled = weeklyEnabled,
                onEnabledChange = { weeklyEnabled = it },
                hours = weeklyHours,
                maxHours = 168f,
                onHoursChange = { weeklyHours = it },
                usedLabel = LanguageManager.getString("this_week", language),
                usedValue = formatTime(sessionTimeWeek),
                limitLabel = LanguageManager.getString("limit_label", language),
                limitValue = "${weeklyHours.toInt()}h",
                progress = weeklyProgress,
                ringColor = ringColor(weeklyProgress)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LimitCard(
    title: String,
    description: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    hours: Float,
    maxHours: Float,
    onHoursChange: (Float) -> Unit,
    usedLabel: String,
    usedValue: String,
    limitLabel: String,
    limitValue: String,
    progress: Float,
    ringColor: Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title + toggle
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            if (enabled) {
                // Progress ring + stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CircularProgressRing(
                        progress = progress,
                        progressColor = ringColor,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        strokeWidth = ProgressSizes.strokeMedium,
                        modifier = Modifier.size(ProgressSizes.medium)
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text(text = usedLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = usedValue, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text(text = limitLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = limitValue, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Slider for hours
                Column {
                    Text(text = "${hours.toInt()}h", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Slider(
                        value = hours,
                        onValueChange = onHoursChange,
                        valueRange = 0.5f..maxHours,
                        steps = ((maxHours - 0.5f) / 0.5f - 1).toInt().coerceAtLeast(0),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0.5h", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${maxHours.toInt()}h", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
