@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.applimit.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager
import com.applimit.ui.components.SafeTimeGuardTopAppBar

@Composable
fun SettingsScreen(language: String, onBack: () -> Unit, onLanguageChange: (String) -> Unit) {
    val context = LocalContext.current
    var showChangePIN by remember { mutableStateOf(false) }
    var showLanguageSelection by remember { mutableStateOf(false) }
    var showWeekStartSelection by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(language) }
    val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
    var currentWeekStart by remember { mutableStateOf(prefs.getString("week_start", "monday") ?: "monday") }

    if (showChangePIN) {
        ChangePINDialog(onDismiss = { showChangePIN = false }, onPINChanged = { showChangePIN = false })
    }
    if (showLanguageSelection) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { newLang ->
                prefs.edit().putString("language", newLang).apply()
                currentLanguage = newLang
                onLanguageChange(newLang)
                showLanguageSelection = false
            },
            onDismiss = { showLanguageSelection = false }
        )
    }
    if (showWeekStartSelection) {
        WeekStartSelectionDialog(
            currentLanguage = currentLanguage,
            currentWeekStart = currentWeekStart,
            onWeekStartSelected = { newVal ->
                prefs.edit().putString("week_start", newVal).apply()
                currentWeekStart = newVal
                showWeekStartSelection = false
            },
            onDismiss = { showWeekStartSelection = false }
        )
    }

    Scaffold(
        topBar = {
            SafeTimeGuardTopAppBar(
                title = LanguageManager.getString("settings", currentLanguage),
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section: Security
            SectionLabel(LanguageManager.getString("security", currentLanguage).ifEmpty { "Security" })
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                SettingItem(
                    icon = Icons.Default.Edit,
                    title = LanguageManager.getString("change_master_pin", currentLanguage),
                    subtitle = LanguageManager.getString("pin_description", currentLanguage),
                    onClick = { showChangePIN = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section: Preferences
            SectionLabel(LanguageManager.getString("preferences", currentLanguage).ifEmpty { "Preferences" })
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                SettingItem(
                    icon = Icons.Default.Language,
                    title = LanguageManager.getString("change_language", currentLanguage),
                    subtitle = LanguageManager.SUPPORTED_LANGUAGES[currentLanguage] ?: currentLanguage,
                    onClick = { showLanguageSelection = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                SettingItem(
                    icon = Icons.Default.Edit,
                    title = LanguageManager.getString("week_start_setting", currentLanguage),
                    subtitle = getWeekStartLabel(currentWeekStart, currentLanguage),
                    onClick = { showWeekStartSelection = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section: About
            SectionLabel(LanguageManager.getString("app_information", currentLanguage).ifEmpty { "About" })
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LanguageManager.getString("app_information", currentLanguage), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                    AppInfoRow("App", "AppLimit")
                    AppInfoRow("Version", "1.0.0")
                    AppInfoRow("Package", "com.applimit")
                    AppInfoRow("Min SDK", "API 26 (Android 8)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun getWeekStartLabel(weekStart: String, language: String): String =
    if (weekStart == "sunday") LanguageManager.getString("week_start_sunday", language)
    else LanguageManager.getString("week_start_monday", language)

@Composable
private fun ChangePINDialog(onDismiss: () -> Unit, onPINChanged: () -> Unit) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf("en") }
    var oldPIN by remember { mutableStateOf("") }
    var newPIN by remember { mutableStateOf("") }
    var confirmPIN by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        currentLanguage = prefs.getString("language", "en") ?: "en"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text(LanguageManager.getString("change_master_pin", currentLanguage)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = oldPIN, onValueChange = { oldPIN = it; errorMessage = "" }, label = { Text(LanguageManager.getString("current_pin", currentLanguage)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newPIN, onValueChange = { newPIN = it; errorMessage = "" }, label = { Text(LanguageManager.getString("new_pin", currentLanguage)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = confirmPIN, onValueChange = { confirmPIN = it; errorMessage = "" }, label = { Text(LanguageManager.getString("confirm_pin_text", currentLanguage)) }, modifier = Modifier.fillMaxWidth())
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (!SecurePinManager.verifyPin(context, oldPIN.trim())) {
                    errorMessage = LanguageManager.getString("incorrect_current_pin", currentLanguage); return@Button
                }
                if (newPIN.trim().length < 6) {
                    errorMessage = LanguageManager.getString("pin_min_digits_error", currentLanguage); return@Button
                }
                if (newPIN.trim() != confirmPIN.trim()) {
                    errorMessage = LanguageManager.getString("pins_mismatch_error", currentLanguage); return@Button
                }
                try {
                    SecurePinManager.setupPin(context, newPIN.trim())
                    onPINChanged()
                } catch (_: Exception) {
                    errorMessage = LanguageManager.getString("failed_change_pin", currentLanguage)
                }
            }) { Text(LanguageManager.getString("change", currentLanguage)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LanguageManager.getString("cancel", currentLanguage)) }
        }
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Text(
                LanguageManager.getString("select_language", currentLanguage),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                LanguageManager.SUPPORTED_LANGUAGES.forEach { (code, name) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onLanguageSelected(code) }.padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name, style = MaterialTheme.typography.bodyMedium)
                        if (currentLanguage == code) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun WeekStartSelectionDialog(
    currentLanguage: String,
    currentWeekStart: String,
    onWeekStartSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text(LanguageManager.getString("week_start_title", currentLanguage)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "monday" to LanguageManager.getString("week_start_monday", currentLanguage),
                    "sunday" to LanguageManager.getString("week_start_sunday", currentLanguage)
                ).forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onWeekStartSelected(value) }.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                        RadioButton(selected = currentWeekStart == value, onClick = { onWeekStartSelected(value) })
                    }
                }
            }
        },
        confirmButton = {}
    )
}
