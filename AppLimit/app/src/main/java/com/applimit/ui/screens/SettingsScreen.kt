package com.applimit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import com.applimit.ui.theme.PrimaryFontFamily
import com.applimit.security.SecurePinManager

import android.content.Context

@Composable
fun SettingsScreen(language: String, onBack: () -> Unit, onLanguageChange: (String) -> Unit) {
        val context = androidx.compose.ui.platform.LocalContext.current
    var showChangePIN by remember { mutableStateOf(false) }
    var showLanguageSelection by remember { mutableStateOf(false) }
        var showWeekStartSelection by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(language) }
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        var currentWeekStart by remember {
                mutableStateOf(prefs.getString("week_start", "monday") ?: "monday")
        }

    val gradientBrush =
            Brush.verticalGradient(
                    colors =
                            listOf(
                                    Color(0xFF0F1419),
                                    Color(0xFF1A2A3F)
                            )
            )

    if (showChangePIN) {
        ChangePINDialog(
                onDismiss = { showChangePIN = false },
                onPINChanged = { showChangePIN = false }
        )
    }

    if (showLanguageSelection) {
                LanguageSelectionDialog(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { newLanguage ->
                            // Persist selection and notify parent
                            prefs.edit().putString("language", newLanguage).apply()
                            currentLanguage = newLanguage
                            onLanguageChange(newLanguage)
                            showLanguageSelection = false
                        },
                        onDismiss = { showLanguageSelection = false }
                )
    }

        if (showWeekStartSelection) {
                WeekStartSelectionDialog(
                                currentLanguage = currentLanguage,
                                currentWeekStart = currentWeekStart,
                                onWeekStartSelected = { newWeekStart ->
                                        prefs.edit().putString("week_start", newWeekStart).apply()
                                        currentWeekStart = newWeekStart
                                        showWeekStartSelection = false
                                },
                                onDismiss = { showWeekStartSelection = false }
                )
        }

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .background(
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                    Color(0xFF00B4DB).copy(alpha = 0.1f),
                                                    Color(0xFF00D4AA).copy(alpha = 0.08f)
                                            )
                            )
                    )
                    .padding(16.dp),
            contentAlignment = Alignment.CenterStart
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = LanguageManager.getString("back", language),
                        tint = Color(0xFFFFFFFF)
                )
            }
            Text(
                    text = LanguageManager.getString("settings", language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.weight(1f)
            )
        }
    }

            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Change PIN Section
                SettingItem(
                        icon = Icons.Default.Edit,
                        title = LanguageManager.getString("change_master_pin", currentLanguage),
                        subtitle = LanguageManager.getString("pin_description", currentLanguage),
                        onClick = { showChangePIN = true }
                )

                // Change Language Section
                SettingItem(
                        icon = Icons.Default.Language,
                        title = LanguageManager.getString("change_language", currentLanguage),
                        subtitle =
                                "Currently: ${LanguageManager.SUPPORTED_LANGUAGES[currentLanguage]}",
                        onClick = { showLanguageSelection = true }
                )

                SettingItem(
                        icon = Icons.Default.Edit,
                        title = LanguageManager.getString("week_start_setting", currentLanguage),
                        subtitle = String.format(
                                LanguageManager.getString("week_start_current", currentLanguage),
                                getWeekStartLabel(currentWeekStart, currentLanguage)
                        ),
                        onClick = { showWeekStartSelection = true }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // App Info Section
                Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00B4DB).copy(alpha = 0.1f),
                        shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = LanguageManager.getString("app_information", currentLanguage),
                                    tint = Color(0xFF00B4DB),
                                    modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                    text = LanguageManager.getString("app_information", currentLanguage),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                            )
                        }

                        AppInfoRow("App Name", "AppLimit")
                        AppInfoRow("Version", "1.0.0")
                        AppInfoRow("Developer", "Security Team")
                        AppInfoRow("Package Name", "com.applimit")
                        AppInfoRow("Min Android Version", "API 29")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit
) {
    Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            color = Color(0xFF00D4AA).copy(alpha = 0.1f),
            shadowElevation = 2.dp
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF00D4AA),
                    modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    fontFamily = PrimaryFontFamily
                )
                Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF22D3EE),
                        fontFamily = PrimaryFontFamily
                )
            }
            Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate",
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(18.dp).graphicsLayer(rotationZ = 180f)
            )
        }
    }
}

@Composable
private fun AppInfoRow(label: String, value: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, 
            fontSize = 12.sp, 
            color = Color(0xFF22D3EE),
            fontFamily = PrimaryFontFamily
        )
        Text(
            text = value, 
            fontSize = 12.sp, 
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF),
            fontFamily = PrimaryFontFamily
        )
    }
}

private fun getWeekStartLabel(weekStart: String, language: String): String {
        return if (weekStart == "sunday") {
                LanguageManager.getString("week_start_sunday", language)
        } else {
                LanguageManager.getString("week_start_monday", language)
        }
}

@Composable
private fun ChangePINDialog(onDismiss: () -> Unit, onPINChanged: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentLanguage by remember { mutableStateOf("en") }
    var oldPIN by remember { mutableStateOf("") }
    var newPIN by remember { mutableStateOf("") }
    var confirmPIN by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // Get language from preferences
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        currentLanguage = prefs.getString("language", "en") ?: "en"
    }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(LanguageManager.getString("change_master_pin", currentLanguage)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                            value = oldPIN,
                            onValueChange = { oldPIN = it },
                            label = { Text(LanguageManager.getString("current_pin", currentLanguage)) },
                            modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                            value = newPIN,
                            onValueChange = { newPIN = it },
                            label = { Text(LanguageManager.getString("new_pin", currentLanguage)) },
                            modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                            value = confirmPIN,
                            onValueChange = { confirmPIN = it },
                            label = { Text(LanguageManager.getString("confirm_pin_text", currentLanguage)) },
                            modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage.isNotEmpty()) {
                        Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                        onClick = {
                            val cleanedOldPin = oldPIN.trim()
                            val cleanedNewPin = newPIN.trim()
                            val cleanedConfirmPin = confirmPIN.trim()
                            
                            // Validate current PIN
                            if (!SecurePinManager.verifyPin(context, cleanedOldPin)) {
                                errorMessage = LanguageManager.getString("incorrect_current_pin", currentLanguage)
                                return@Button
                            }

                            // Validate new PIN
                            if (cleanedNewPin.length < 6) {
                                errorMessage = LanguageManager.getString("pin_min_digits_error", currentLanguage)
                                return@Button
                            }

                            if (cleanedNewPin != cleanedConfirmPin) {
                                errorMessage = LanguageManager.getString("pins_mismatch_error", currentLanguage)
                                return@Button
                            }

                            // Save new PIN
                            try {
                                SecurePinManager.setupPin(context, cleanedNewPin)
                                onPINChanged()
                            } catch (e: Exception) {
                                errorMessage = LanguageManager.getString("failed_change_pin", currentLanguage)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                ) { Text(LanguageManager.getString("change", currentLanguage), fontFamily = PrimaryFontFamily) }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(LanguageManager.getString("cancel", currentLanguage), fontFamily = PrimaryFontFamily)
                }
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
                        title = { Text(
                                LanguageManager.getString("select_language", currentLanguage),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                        ) },
                        text = {
                                Column(
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                                ) {
                                        LanguageManager.SUPPORTED_LANGUAGES.forEach { (code, name) ->
                                                Row(
                                                                modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                                .clickable { onLanguageSelected(code) }
                                                                                                .padding(12.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                        Text(name, fontSize = 14.sp)
                                                        if (currentLanguage == code) {
                                                                Icon(
                                                                                imageVector = Icons.Default.Check,
                                                                                contentDescription = LanguageManager.getString("back", currentLanguage),
                                                                                tint = MaterialTheme.colorScheme.primary
                                                                )
                                                        }
                                                }
                                        }
                                }
                        },
                        confirmButton = { /* Dialog closes on selection */}
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
            title = { Text(LanguageManager.getString("week_start_title", currentLanguage)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                            "monday" to LanguageManager.getString("week_start_monday", currentLanguage),
                            "sunday" to LanguageManager.getString("week_start_sunday", currentLanguage)
                    ).forEach { (value, label) ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clickable { onWeekStartSelected(value) }
                                                .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 14.sp)
                            RadioButton(
                                    selected = currentWeekStart == value,
                                    onClick = { onWeekStartSelected(value) }
                            )
                        }
                    }
                }
            },
            confirmButton = { /* Dialog closes on selection */ }
    )
}

