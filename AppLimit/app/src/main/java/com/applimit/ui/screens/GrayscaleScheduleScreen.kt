@file:OptIn(ExperimentalMaterial3Api::class)
package com.applimit.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.data.BlockRule
import com.applimit.data.BlockType
import com.applimit.data.GrayscaleRuleStorage
import com.applimit.grayscale.GrayscaleController
import com.applimit.ui.components.DayChipRow
import com.applimit.ui.components.SafeTimeGuardTopAppBar

@Composable
fun GrayscaleScheduleScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("grayscale_settings", Context.MODE_PRIVATE) }

    var isEnabled by remember { mutableStateOf(false) }
    var startHour by remember { mutableStateOf(22) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(8) }
    var endMinute by remember { mutableStateOf(0) }
    var applyEveryDay by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf(5, 6)) }
    val savedRules = remember { mutableStateListOf<BlockRule>() }
    var showInfoDialog by remember { mutableStateOf(false) }
    var dontShowAgain by remember { mutableStateOf(false) }
    var hasSecureSettingsPermission by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }
    var downloadUrl by remember { mutableStateOf("") }

    LaunchedEffect(refreshKey) {
        isEnabled = prefs.getBoolean("enabled", false)
        applyEveryDay = prefs.getBoolean("apply_every_day", false)
        hasSecureSettingsPermission = context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        if (!hasSecureSettingsPermission) isEnabled = false
        val rules = GrayscaleRuleStorage.load(context)
        savedRules.clear(); savedRules.addAll(rules)
        if (refreshKey == 0 && !prefs.getBoolean("has_seen_grayscale_info", false)) showInfoDialog = true
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            hasSecureSettingsPermission = context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        try {
            val conn = java.net.URL("https://kiosklimit.web.app/config.json").openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "GET"; conn.connectTimeout = 5000
            if (conn.responseCode == 200) {
                val resp = conn.inputStream.bufferedReader().use { it.readText() }
                val url = resp.substringAfter("\"desktopAppDownloadUrl\": \"").substringBefore("\"")
                if (url.isNotEmpty()) downloadUrl = url
            }
        } catch (_: Exception) {
            downloadUrl = "https://firebasestorage.googleapis.com/v0/b/kiosklimit.firebasestorage.app/o/AppLimit-Desktop-Setup.zip?alt=media&token=0c54cc8d-7d7b-4295-938d-a927b0b77832"
        }
    }

    val dayNames = listOf(
        LanguageManager.getString("monday", language),
        LanguageManager.getString("tuesday", language),
        LanguageManager.getString("wednesday", language),
        LanguageManager.getString("thursday", language),
        LanguageManager.getString("friday", language),
        LanguageManager.getString("saturday", language),
        LanguageManager.getString("sunday", language)
    )

    // Info dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { if (dontShowAgain) prefs.edit().putBoolean("has_seen_grayscale_info", true).apply(); showInfoDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text(LanguageManager.getString("grayscale_info_title", language)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(LanguageManager.getString("grayscale_info_body", language), style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dontShowAgain, onCheckedChange = { dontShowAgain = it })
                        Text(LanguageManager.getString("grayscale_dont_show_again", language), style = MaterialTheme.typography.bodySmall, modifier = Modifier.clickable { dontShowAgain = !dontShowAgain })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { if (dontShowAgain) prefs.edit().putBoolean("has_seen_grayscale_info", true).apply(); showInfoDialog = false }) {
                    Text(LanguageManager.getString("grayscale_info_ok", language))
                }
            }
        )
    }

    Scaffold(
        topBar = { SafeTimeGuardTopAppBar(title = LanguageManager.getString("grayscale_mode", language), onNavigateBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasSecureSettingsPermission) {
                // Permission required card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(48.dp))
                        Text(LanguageManager.getString("grayscale_inactive_title", language), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer, textAlign = TextAlign.Center)
                        Text(LanguageManager.getString("grayscale_inactive_body", language), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                        Text(LanguageManager.getString("grayscale_inactive_note", language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                    }
                }
                Button(
                    onClick = {
                        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))) }
                        catch (_: Exception) {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("AppLimit", downloadUrl))
                            Toast.makeText(context, LanguageManager.getString("link_copied", language), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) { Text(LanguageManager.getString("download_app", language), fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("AppLimit", downloadUrl))
                        Toast.makeText(context, LanguageManager.getString("link_copied", language), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) { Text(LanguageManager.getString("copy_download_link", language)) }
            } else {
                // Enable toggle
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${LanguageManager.getString("enable", language)} ${LanguageManager.getString("grayscale_mode", language)}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                    }
                }

                if (isEnabled) {
                    // Apply every day toggle
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(LanguageManager.getString("apply_every_day", language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                            Switch(checked = applyEveryDay, onCheckedChange = { applyEveryDay = it })
                        }
                    }

                    // Time range
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(LanguageManager.getString("active_hours", language), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text(LanguageManager.getString("grayscale_active_hours_desc", language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(LanguageManager.getString("from_label", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    GrayscaleTimePickerButton(hour = startHour, minute = startMinute, onTimeChange = { h, m -> startHour = h; startMinute = m }, language = language, modifier = Modifier.fillMaxWidth())
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(LanguageManager.getString("to_label", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    GrayscaleTimePickerButton(hour = endHour, minute = endMinute, onTimeChange = { h, m -> endHour = h; endMinute = m }, language = language, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }

                    // Day selection
                    if (!applyEveryDay) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(LanguageManager.getString("apply_on_days", language), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                                DayChipRow(
                                    days = dayNames,
                                    selectedDays = selectedDays,
                                    onDayToggled = { idx ->
                                        selectedDays = if (idx in selectedDays) selectedDays - idx else selectedDays + idx
                                    }
                                )
                            }
                        }
                    }

                    // Add time slot button
                    Button(
                        onClick = {
                            if (!applyEveryDay && selectedDays.isEmpty()) {
                                Toast.makeText(context, LanguageManager.getString("select_at_least_one_day", language), Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val days = if (applyEveryDay) (1..7).toSet() else selectedDays.map { it + 1 }.toSet()
                            savedRules.add(BlockRule(packageName = "grayscale", days = days, startHour = startHour, startMinute = startMinute, endHour = endHour, endMinute = endMinute, type = BlockType.GRAYSCALE))
                            GrayscaleRuleStorage.save(context, savedRules.toList())
                            GrayscaleController.update(context)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        enabled = applyEveryDay || selectedDays.isNotEmpty()
                    ) { Text(LanguageManager.getString("add_time_slot", language), fontWeight = FontWeight.Bold) }
                }

                // Saved schedules
                if (savedRules.isNotEmpty()) {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(LanguageManager.getString("saved_schedules", language), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            savedRules.forEach { rule ->
                                Row(modifier = Modifier.fillMaxWidth().clickable {
                                    startHour = rule.startHour; startMinute = rule.startMinute; endHour = rule.endHour; endMinute = rule.endMinute
                                    applyEveryDay = rule.days.size == 7
                                    if (!applyEveryDay) selectedDays = rule.days.map { it - 1 }.toSet()
                                    savedRules.remove(rule); GrayscaleRuleStorage.save(context, savedRules.toList()); GrayscaleController.update(context)
                                }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${String.format("%02d:%02d", rule.startHour, rule.startMinute)} – ${String.format("%02d:%02d", rule.endHour, rule.endMinute)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                        if (rule.days.size < 7) {
                                            Text(rule.days.sorted().joinToString(", ") { dayNames[(it - 1).coerceIn(0, 6)] }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(onClick = { savedRules.remove(rule); GrayscaleRuleStorage.save(context, savedRules.toList()); GrayscaleController.update(context) }) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                // Save button
                if (isEnabled) {
                    Button(
                        onClick = {
                            prefs.edit().putBoolean("enabled", isEnabled).putBoolean("apply_every_day", applyEveryDay).apply()
                            GrayscaleRuleStorage.save(context, savedRules.toList())
                            GrayscaleController.update(context)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text(LanguageManager.getString("save_settings", language), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GrayscaleTimePickerButton(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    language: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempHour by remember(hour) { mutableStateOf(hour) }
    var tempMinute by remember(minute) { mutableStateOf(minute) }
    var hourText by remember(hour) { mutableStateOf(String.format("%02d", hour)) }
    var minuteText by remember(minute) { mutableStateOf(String.format("%02d", minute)) }

    ElevatedCard(
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { showDialog = true }
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(String.format("%02d:%02d", hour, minute), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text(LanguageManager.getString("select_time", language)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(String.format("%02d:%02d", tempHour, tempMinute), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(LanguageManager.getString("hour", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = hourText,
                                onValueChange = { v -> if (v.length <= 2 && v.all { it.isDigit() }) { hourText = v; v.toIntOrNull()?.let { h -> if (h in 0..23) tempHour = h } } },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                        Text(":", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(LanguageManager.getString("minute", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = minuteText,
                                onValueChange = { v -> if (v.length <= 2 && v.all { it.isDigit() }) { minuteText = v; v.toIntOrNull()?.let { m -> if (m in 0..59) tempMinute = m } } },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { onTimeChange(tempHour, tempMinute); showDialog = false }) { Text(LanguageManager.getString("confirm", language).ifEmpty { "OK" }) } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text(LanguageManager.getString("cancel", language)) } }
        )
    }
}
