package com.applimit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.applimit.LanguageManager
import com.applimit.ui.theme.Shapes
import com.applimit.ui.components.Material3Card
import com.applimit.ui.components.SafeTimeGuardTopAppBar
import com.applimit.ui.theme.*
import com.applimit.data.RuleStorage
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStats
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(language: String = "en", onLanguageChange: (String) -> Unit = {}) {
    val currentScreen = remember { mutableStateOf<String?>(null) }

    when (currentScreen.value) {
        "app_blocking" -> AppBlockingRulesScreen(language) { currentScreen.value = null }
        "grayscale" -> GrayscaleScheduleScreen(language) { currentScreen.value = null }
        "screen_time" -> ScreenTimeLimitsScreen(language) { currentScreen.value = null }
        "settings" -> SettingsScreen(language, { currentScreen.value = null }) { newLang ->
            onLanguageChange(newLang)
        }
        else -> DashboardContent(language) { screen -> currentScreen.value = screen }
    }
}

@Composable
private fun DashboardContent(language: String, onNavigate: (String) -> Unit) {
    android.util.Log.d("Dashboard", "DashboardContent composable called!")
    SafeTimeGuardTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1419),
                            Color(0xFF1A2A3F)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Material 3 styling and animations
            AnimatedHeader(language)

            Spacer(modifier = Modifier.height(24.dp))

            // Feature Cards using Material 3 Card component
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App Blocker Card
                Material3FeatureCard(
                    title = LanguageManager.getString("feature_blocker_title", language),
                    description = LanguageManager.getString("feature_blocker_desc", language),
                    icon = Icons.Filled.Block,
                    containerColor = Color(0xFF00D4AA).copy(alpha = 0.15f),
                    contentColor = Color(0xFF00D4AA),
                    onClick = { onNavigate("app_blocking") }
                )

                // Grayscale Card
                Material3FeatureCard(
                    title = LanguageManager.getString("feature_grayscale_title", language),
                    description = LanguageManager.getString("feature_grayscale_desc", language),
                    icon = Icons.Filled.DesktopMac,
                    containerColor = Color(0xFF22D3EE).copy(alpha = 0.15f),
                    contentColor = Color(0xFF22D3EE),
                    onClick = { onNavigate("grayscale") }
                )

                // Screen Time Limits Card
                Material3FeatureCard(
                    title = LanguageManager.getString("screen_time_limits", language),
                    description = LanguageManager.getString("screen_time_limits_desc", language),
                    icon = Icons.Filled.Lock,
                    containerColor = Color(0xFFFFB703).copy(alpha = 0.15f),
                    contentColor = Color(0xFFFFB703),
                    onClick = { onNavigate("screen_time") }
                )

                // Settings Card
                Material3FeatureCard(
                    title = LanguageManager.getString("feature_settings_title", language),
                    description = LanguageManager.getString("feature_settings_desc", language),
                    icon = Icons.Filled.Settings,
                    containerColor = Color(0xFF00B4DB).copy(alpha = 0.15f),
                    contentColor = Color(0xFF00B4DB),
                    onClick = { onNavigate("settings") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Stats Section
            QuickStatsSection(language)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Material3FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A2A3F).copy(alpha = 0.9f),
                            Color(0xFF0F1419).copy(alpha = 0.95f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF00D4AA).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon Container - מינימליסטי וקל
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            color = containerColor.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = containerColor.copy(alpha = 0.85f),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Text Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFFFFF),
                        fontFamily = PrimaryFontFamily
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFF22D3EE).copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp,
                        fontFamily = PrimaryFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsSection(language: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val usageRepository = remember { com.applimit.tracking.UsageRepository.getInstance(context) }
    
    // Real-time stats from ScreenTimeReceiver
    var appsBlockedCount by remember { mutableStateOf(0) }
    var sessionTimeToday by remember { mutableStateOf(0L) }
    var sessionTimeWeek by remember { mutableStateOf(0L) }
    
    // Update stats in real-time
    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Count blocked apps from RuleStorage
                val rules = RuleStorage.load(context)
                appsBlockedCount = rules.size
                
                sessionTimeToday = usageRepository.getTodayUsage()
                sessionTimeWeek = usageRepository.getWeeklyUsage()
            } catch (e: Exception) {
                android.util.Log.e("ScreenTime", "Error reading screen time: ${e.message}", e)
            }
            
            // Update every 5 seconds
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
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = LanguageManager.getString("quick_stats", language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFFFFF),
            fontFamily = PrimaryFontFamily,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = LanguageManager.getString("apps_blocked", language),
                value = appsBlockedCount.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = LanguageManager.getString("today_label", language),
                value = formatTime(sessionTimeToday),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = LanguageManager.getString("this_week", language),
                value = formatTime(sessionTimeWeek),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(Shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00D4AA).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00D4AA),
                fontFamily = PrimaryFontFamily
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF22D3EE),
                fontFamily = PrimaryFontFamily
            )
        }
    }
}

@Composable
private fun AnimatedHeader(language: String) {
    // Shimmer-like gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "header_shimmer")
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_position"
    )
    
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "header_scale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00B4DB).copy(alpha = 0.12f),
                        Color(0xFF00D4AA).copy(alpha = 0.08f)
                    )
                )
            )
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LanguageManager.getString("dashboard_subtitle", language),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF),
            fontFamily = PrimaryFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(scaleAnimation)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = LanguageManager.getString("dashboard_subtitle_desc", language),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF22D3EE),
            fontFamily = PrimaryFontFamily,
            textAlign = TextAlign.Center
        )
    }
}





