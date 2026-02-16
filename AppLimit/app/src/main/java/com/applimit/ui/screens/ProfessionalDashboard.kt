package com.applimit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager

/**
 * Professional dashboard with Material 3 design
 * - Status overview
 * - Active rules display
 * - Quick actions
 * - Rule management
 * - Settings access
 */
@Composable
fun ProfessionalDashboard(
        onNavigateToRules: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToGrayscale: () -> Unit,
        language: String = "en"
) {
    val gradientBrush =
            Brush.verticalGradient(
                    colors =
                            listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.background
                            )
            )

        LazyColumn(
                        modifier = Modifier.fillMaxSize().background(gradientBrush).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Header
                item { DashboardHeader(language) }

                // Status Cards
                item { StatusOverviewSection(language) }

                // Quick Actions
                item {
                        QuickActionsSection(
                                        onNavigateToRules = onNavigateToRules,
                                        onNavigateToGrayscale = onNavigateToGrayscale,
                                        onNavigateToSettings = onNavigateToSettings,
                                        language = language
                        )
                }

                // Active Rules
                item { ActiveRulesSection(language) }

                // Scheduled Events
                item { ScheduledEventsSection(language) }
        }
}

@Composable
private fun DashboardHeader(language: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
                                                                text = LanguageManager.getString("welcome_title", language),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
        )
        Text(
                                                                text = LanguageManager.getString("welcome_subtitle", language),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusOverviewSection(language: String) {
    Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusCard(
                title = LanguageManager.getString("active_rules", language),
                value = "5",
                icon = Icons.Default.Lock,
                color = MaterialTheme.colorScheme.primary
        )

        StatusCard(
                title = LanguageManager.getString("blocked_today", language),
                value = "23",
                icon = Icons.Default.Block,
                color = MaterialTheme.colorScheme.error
        )

        StatusCard(
                title = LanguageManager.getString("device_status", language),
                value = "Protected",
                icon = Icons.Default.Security,
                color = MaterialTheme.colorScheme.tertiary
        )

        StatusCard(
                title = LanguageManager.getString("uptime", language),
                value = "99.9%",
                icon = Icons.Default.CheckCircle,
                color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StatusCard(
        title: String,
        value: String,
        icon: ImageVector,
        color: androidx.compose.ui.graphics.Color
) {
    Surface(
            modifier = Modifier.width(160.dp).height(120.dp).clip(RoundedCornerShape(16.dp)),
            color = color.copy(alpha = 0.1f)
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = color
                )
                Text(
                        text = title,
                        fontSize = 12.sp,
                        color = color,
                        fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun RememberScrollState(): androidx.compose.foundation.ScrollState {
    return androidx.compose.foundation.rememberScrollState()
}

@Composable
private fun QuickActionsSection(
        onNavigateToRules: () -> Unit,
        onNavigateToGrayscale: () -> Unit,
        onNavigateToSettings: () -> Unit
        ,
        language: String
) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
                        text = LanguageManager.getString("quick_stats", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
        )

        QuickActionButton(
                icon = Icons.Default.Add,
                label = LanguageManager.getString("create_rule", language),
                onClick = onNavigateToRules
        )

        QuickActionButton(
                icon = Icons.Default.Palette,
                label = LanguageManager.getString("grayscale_mode", language),
                onClick = onNavigateToGrayscale
        )

        QuickActionButton(
                icon = Icons.Default.Settings,
                label = LanguageManager.getString("settings", language),
                onClick = onNavigateToSettings
        )
    }
}

@Composable
private fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surface,
            onClick = onClick
    ) {
        Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
            )
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActiveRulesSection(language: String) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = LanguageManager.getString("active_rules", language), fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Mock rules
        repeat(3) { index ->
            RuleItem(
                    appName = listOf("Instagram", "TikTok", "YouTube")[index],
                    timeRange = "09:00 - 17:00",
                    days = "Mon-Fri"
            )
        }
    }
}

@Composable
private fun RuleItem(appName: String, timeRange: String, days: String) {
    Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surface
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                        text = "$timeRange • $days",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Active",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ScheduledEventsSection(language: String) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = LanguageManager.getString("scheduled_events", language),
                    tint = MaterialTheme.colorScheme.primary
            )
            Text(text = LanguageManager.getString("scheduled_events", language), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
                text = LanguageManager.getString("scheduled_tomorrow", language),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
                text = LanguageManager.getString("scheduled_friday", language),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

