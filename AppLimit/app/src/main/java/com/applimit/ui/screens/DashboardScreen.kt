package com.applimit.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.data.RuleStorage
import com.applimit.ui.components.CircularProgressRing
import com.applimit.ui.components.Material3SectionHeader
import com.applimit.ui.theme.NumericDisplayStyle
import com.applimit.ui.theme.ProgressSizes
import com.applimit.ui.theme.SemanticBalance
import com.applimit.ui.theme.SemanticBlocking
import com.applimit.ui.theme.SemanticWarning
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(language: String = "en", onLanguageChange: (String) -> Unit = {}) {
    var currentScreen by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "app_blocking" -> AppBlockingRulesScreen(language) { currentScreen = null }
        "grayscale"    -> GrayscaleScheduleScreen(language) { currentScreen = null }
        "screen_time"  -> ScreenTimeLimitsScreen(language) { currentScreen = null }
        "settings"     -> SettingsScreen(language, { currentScreen = null }) { newLang -> onLanguageChange(newLang) }
        else           -> DashboardContent(language) { screen -> currentScreen = screen }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(language: String, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val usageRepository = remember { com.applimit.tracking.UsageRepository.getInstance(context) }

    var appsBlockedCount by remember { mutableIntStateOf(0) }
    var todayMs by remember { mutableLongStateOf(0L) }
    var weekMs  by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                appsBlockedCount = RuleStorage.load(context).size
                todayMs = usageRepository.getTodayUsage()
                weekMs  = usageRepository.getWeeklyUsage()
            } catch (_: Exception) {}
            delay(5000L)
        }
    }

    val todayHours   = (todayMs / 3_600_000f).coerceIn(0f, 8f)
    val weekHours    = (weekMs  / 3_600_000f).coerceIn(0f, 56f)
    val todayProgress  = (todayHours / 8f).coerceIn(0f, 1f)
    val weekProgress   = (weekHours  / 56f).coerceIn(0f, 1f)
    val blockProgress  = (appsBlockedCount / 10f).coerceIn(0f, 1f)

    fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        return if (h > 0) "${h}h ${m}m" else if (m > 0) "${m}m" else "0m"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppLimit", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { onNavigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = LanguageManager.getString("feature_settings_title", language))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Hero stats row ──────────────────────────────────────────────
            Material3SectionHeader(
                title = LanguageManager.getString("quick_stats", language),
                modifier = Modifier.padding(start = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatRing(
                    label = LanguageManager.getString("apps_blocked", language),
                    value = appsBlockedCount.toString(),
                    progress = blockProgress,
                    progressColor = SemanticBlocking,
                    trackColor = MaterialTheme.colorScheme.errorContainer
                )
                StatRing(
                    label = LanguageManager.getString("today_label", language),
                    value = formatTime(todayMs),
                    progress = todayProgress,
                    progressColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                StatRing(
                    label = LanguageManager.getString("this_week", language),
                    value = formatTime(weekMs),
                    progress = weekProgress,
                    progressColor = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Feature cards ───────────────────────────────────────────────
            Material3SectionHeader(
                title = LanguageManager.getString("features", language).ifEmpty { "Features" },
                modifier = Modifier.padding(start = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    title = LanguageManager.getString("feature_blocker_title", language),
                    description = LanguageManager.getString("feature_blocker_desc", language),
                    icon = Icons.Default.Block,
                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                    iconTint = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = { onNavigate("app_blocking") }
                )
                FeatureCard(
                    title = LanguageManager.getString("feature_grayscale_title", language),
                    description = LanguageManager.getString("feature_grayscale_desc", language),
                    icon = Icons.Default.BrightnessLow,
                    iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { onNavigate("grayscale") }
                )
                FeatureCard(
                    title = LanguageManager.getString("screen_time_limits", language),
                    description = LanguageManager.getString("screen_time_limits_desc", language),
                    icon = Icons.Default.Schedule,
                    iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = { onNavigate("screen_time") }
                )
                FeatureCard(
                    title = LanguageManager.getString("feature_settings_title", language),
                    description = LanguageManager.getString("feature_settings_desc", language),
                    icon = Icons.Default.Settings,
                    iconContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { onNavigate("settings") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatRing(
    label: String,
    value: String,
    progress: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        CircularProgressRing(
            progress = progress,
            progressColor = progressColor,
            trackColor = trackColor,
            strokeWidth = ProgressSizes.strokeMedium,
            modifier = Modifier.size(ProgressSizes.large)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconContainerColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
