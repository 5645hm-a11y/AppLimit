package com.applimit.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import com.applimit.tracking.UsageRepository
import com.applimit.ui.theme.PrimaryFontFamily
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun ScreenTimeLimitsScreen(language: String, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
    }
    val usageRepository = remember { UsageRepository.getInstance(context) }

    var dailyEnabled by remember { mutableStateOf(false) }
    var dailyHours by remember { mutableStateOf(6) }
    var dailyMinutes by remember { mutableStateOf(0) }
    var weeklyEnabled by remember { mutableStateOf(false) }
    var weeklyHours by remember { mutableStateOf(20) }
    var weeklyMinutes by remember { mutableStateOf(0) }

    var sessionTimeToday by remember { mutableStateOf(0L) }
    var sessionTimeWeek by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val dailyLimitMinutes = prefs.getInt("screen_time_daily_limit_minutes", 0)
        val weeklyLimitMinutes = prefs.getInt("screen_time_weekly_limit_minutes", 0)
        dailyEnabled = prefs.getBoolean("screen_time_daily_enabled", false)
        weeklyEnabled = prefs.getBoolean("screen_time_weekly_enabled", false)

        if (dailyLimitMinutes > 0) {
            dailyHours = dailyLimitMinutes / 60
            dailyMinutes = dailyLimitMinutes % 60
        }
        if (weeklyLimitMinutes > 0) {
            weeklyHours = weeklyLimitMinutes / 60
            weeklyMinutes = weeklyLimitMinutes % 60
        }
    }

    LaunchedEffect(dailyEnabled, dailyHours, dailyMinutes) {
        val totalMinutes = (dailyHours * 60 + dailyMinutes).coerceAtLeast(0)
        prefs.edit()
            .putBoolean("screen_time_daily_enabled", dailyEnabled)
            .putInt("screen_time_daily_limit_minutes", totalMinutes)
            .apply()
    }

    LaunchedEffect(weeklyEnabled, weeklyHours, weeklyMinutes) {
        val totalMinutes = (weeklyHours * 60 + weeklyMinutes).coerceAtLeast(0)
        prefs.edit()
            .putBoolean("screen_time_weekly_enabled", weeklyEnabled)
            .putInt("screen_time_weekly_limit_minutes", totalMinutes)
            .apply()
    }

    LaunchedEffect(Unit) {
        while (true) {
            sessionTimeToday = usageRepository.getTodayUsage()
            sessionTimeWeek = usageRepository.getWeeklyUsage()
            delay(5000L)
        }
    }

    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    val dailyLimitMinutes = dailyHours * 60 + dailyMinutes
    val weeklyLimitMinutes = weeklyHours * 60 + weeklyMinutes
    val dailyUsedMinutes = (sessionTimeToday / 60000L).toInt()
    val weeklyUsedMinutes = (sessionTimeWeek / 60000L).toInt()
    val dailyPercent = if (dailyEnabled && dailyLimitMinutes > 0) {
        min(1f, dailyUsedMinutes.toFloat() / dailyLimitMinutes)
    } else 0f
    val weeklyPercent = if (weeklyEnabled && weeklyLimitMinutes > 0) {
        min(1f, weeklyUsedMinutes.toFloat() / weeklyLimitMinutes)
    } else 0f

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F1419),
            Color(0xFF1A2A3F)
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF00B4DB).copy(alpha = 0.1f),
                                Color(0xFF00D4AA).copy(alpha = 0.08f)
                            )
                        )
                    ),
                color = Color.Transparent,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = LanguageManager.getString("go_back", language),
                            tint = Color(0xFFFFFFFF)
                        )
                    }
                    Text(
                        text = LanguageManager.getString("screen_time_management", language),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScreenTimeLimitCard(
                    title = LanguageManager.getString("daily_limit", language),
                    description = LanguageManager.getString("daily_limit_desc", language),
                    enabled = dailyEnabled,
                    onEnabledChange = { dailyEnabled = it },
                    hours = dailyHours,
                    minutes = dailyMinutes,
                    maxHours = 23,
                    onHoursChange = { dailyHours = it },
                    onMinutesChange = { dailyMinutes = it },
                    usedLabel = LanguageManager.getString("today_label", language),
                    usedValue = formatTime(sessionTimeToday),
                    usedOfLimit = String.format(
                        LanguageManager.getString("used_of_limit", language),
                        formatTime(sessionTimeToday),
                        if (dailyLimitMinutes > 0) "${dailyLimitMinutes / 60}h ${dailyLimitMinutes % 60}m" else "0m"
                    ),
                    percent = dailyPercent,
                    language = language
                )

                ScreenTimeLimitCard(
                    title = LanguageManager.getString("weekly_limit", language),
                    description = LanguageManager.getString("weekly_limit_desc", language),
                    enabled = weeklyEnabled,
                    onEnabledChange = { weeklyEnabled = it },
                    hours = weeklyHours,
                    minutes = weeklyMinutes,
                    maxHours = 168,
                    onHoursChange = { weeklyHours = it },
                    onMinutesChange = { weeklyMinutes = it },
                    usedLabel = LanguageManager.getString("this_week", language),
                    usedValue = formatTime(sessionTimeWeek),
                    usedOfLimit = String.format(
                        LanguageManager.getString("used_of_limit", language),
                        formatTime(sessionTimeWeek),
                        if (weeklyLimitMinutes > 0) "${weeklyLimitMinutes / 60}h ${weeklyLimitMinutes % 60}m" else "0m"
                    ),
                    percent = weeklyPercent,
                    language = language
                )
            }
        }
    }
}

@Composable
private fun ScreenTimeLimitCard(
    title: String,
    description: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    hours: Int,
    minutes: Int,
    maxHours: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    usedLabel: String,
    usedValue: String,
    usedOfLimit: String,
    percent: Float,
    language: String = LanguageManager.ENGLISH
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF00D4AA).copy(alpha = 0.1f),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        fontFamily = PrimaryFontFamily
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFF22D3EE),
                        fontFamily = PrimaryFontFamily
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00D4AA),
                        checkedTrackColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                        uncheckedTrackColor = Color(0xFFFFFFFF).copy(alpha = 0.3f)
                    )
                )
            }

            if (enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = usedLabel,
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE)
                        )
                        Text(
                            text = usedValue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LanguageManager.getString("limit_label", language),
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE)
                        )
                        Text(
                            text = "${hours}h ${minutes}m",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { percent },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Color(0xFF00D4AA),
                    trackColor = Color(0xFF00D4AA).copy(alpha = 0.2f)
                )

                Text(
                    text = usedOfLimit,
                    fontSize = 12.sp,
                    color = Color(0xFF22D3EE),
                    textAlign = TextAlign.Start
                )

                LimitStepper(
                    language = language,
                    hours = hours,
                    minutes = minutes,
                    maxHours = maxHours,
                    onHoursChange = onHoursChange,
                    onMinutesChange = onMinutesChange
                )
            }
        }
    }
}

@Composable
private fun LimitStepper(
    language: String,
    hours: Int,
    minutes: Int,
    maxHours: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperBox(
            label = LanguageManager.getString("hour", language),
            value = hours,
            minValue = 0,
            maxValue = maxHours,
            onValueChange = onHoursChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )
        StepperBox(
            label = LanguageManager.getString("minute", language),
            value = minutes,
            minValue = 0,
            maxValue = 59,
            onValueChange = onMinutesChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun StepperBox(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF1A2A3F).copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF22D3EE))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (value > minValue) onValueChange(value - 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        tint = Color(0xFFFFFFFF)
                    )
                }
                Text(
                    text = value.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { if (value < maxValue) onValueChange(value + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFFFFFFFF)
                    )
                }
            }
        }
    }
}
