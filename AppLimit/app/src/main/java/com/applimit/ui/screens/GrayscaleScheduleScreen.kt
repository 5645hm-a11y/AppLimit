package com.applimit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import com.applimit.data.BlockRule
import com.applimit.data.BlockType
import com.applimit.data.GrayscaleRuleStorage
import com.applimit.grayscale.GrayscaleController
import com.applimit.ui.theme.Shapes
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

@Composable
fun GrayscaleScheduleScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("grayscale_settings", Context.MODE_PRIVATE)
    }
    val openAccessibilitySettings = {
        try {
            context.startActivity(Intent("android.settings.ACCESSIBILITY_COLOR_SPACE_SETTINGS"))
        } catch (_: Exception) {
            try {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (_: Exception) {
            }
        }
    }

    var isEnabled by remember { mutableStateOf(false) }
    var startHour by remember { mutableStateOf(22) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(8) }
    var endMinute by remember { mutableStateOf(0) }
    var applyEveryDay by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf(5, 6)) }
    val savedRules = remember { mutableStateListOf<BlockRule>() }
    
    // Info dialog state
    var showInfoDialog by remember { mutableStateOf(false) }
    var dontShowAgain by remember { mutableStateOf(false) }
    var hasSecureSettingsPermission by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }

    // Check permission on initial load and when refreshKey changes
    LaunchedEffect(refreshKey) {
        val savedEnabled = prefs.getBoolean("enabled", false)
        val savedApplyEveryDay = prefs.getBoolean("apply_every_day", false)
        isEnabled = savedEnabled
        applyEveryDay = savedApplyEveryDay

        // Always recheck permission
        hasSecureSettingsPermission = context.checkSelfPermission(
            Manifest.permission.WRITE_SECURE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasSecureSettingsPermission) {
            isEnabled = false
        }

        val rules = GrayscaleRuleStorage.load(context)
        savedRules.clear()
        savedRules.addAll(rules)
        
        // Check if this is first time visiting grayscale screen (only on initial load)
        if (refreshKey == 0) {
            val hasSeenInfo = prefs.getBoolean("has_seen_grayscale_info", false)
            if (!hasSeenInfo) {
                showInfoDialog = true
            }
        }
    }

    // Periodic permission check every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            hasSecureSettingsPermission = context.checkSelfPermission(
                Manifest.permission.WRITE_SECURE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
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

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F1419),
            Color(0xFF1A2A3F)
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush)) {
        if (!hasSecureSettingsPermission) {
            // Full blocking screen when permission is not granted
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F1419),
                                Color(0xFF1A2A3F)
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                // Top header with back button
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = LanguageManager.getString("go_back", language),
                            tint = Color(0xFFFFFFFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Main blocking message
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF6B6B).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 16.dp)
                        )
                        
                        Text(
                            text = LanguageManager.getString("grayscale_inactive_title", language),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 16.dp)
                        )
                        
                        Text(
                            text = LanguageManager.getString("grayscale_inactive_body", language),
                            fontSize = 14.sp,
                            color = Color(0xFFFFFFFF).copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Text(
                            text = LanguageManager.getString("grayscale_inactive_note", language),
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Download buttons
                var downloadUrl by remember { mutableStateOf("") }
                
                LaunchedEffect(Unit) {
                    try {
                        val configUrl = "https://kiosklimit.web.app/config.json"
                        val connection = java.net.URL(configUrl).openConnection() as java.net.HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.connectTimeout = 5000
                        if (connection.responseCode == 200) {
                            val response = connection.inputStream.bufferedReader().use { it.readText() }
                            val urlValue = response.substringAfter("\"desktopAppDownloadUrl\": \"").substringBefore("\"")
                            if (urlValue.isNotEmpty()) {
                                downloadUrl = urlValue
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback to direct Firebase Storage URL
                        downloadUrl = "https://firebasestorage.googleapis.com/v0/b/kiosklimit.firebasestorage.app/o/AppLimit-Desktop-Setup.zip?alt=media&token=0c54cc8d-7d7b-4295-938d-a927b0b77832"
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                context.startActivity(intent)
                                Toast.makeText(
                                    context,
                                    LanguageManager.getString("download_started", language),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                // Fallback: copy link if browser fails
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("AppLimit", downloadUrl))
                                Toast.makeText(
                                    context,
                                    LanguageManager.getString("link_copied", language),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22D3EE)
                        )
                    ) {
                        Text(
                            text = LanguageManager.getString("download_app", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("AppLimit", downloadUrl))
                            Toast.makeText(
                                context,
                                LanguageManager.getString("link_copied", language),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF22D3EE)
                        )
                    ) {
                        Text(LanguageManager.getString("copy_download_link", language))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Info box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF00B4DB).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 " + LanguageManager.getString("tip", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22D3EE),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = LanguageManager.getString("grayscale_inactive_body", language),
                            fontSize = 11.sp,
                            color = Color(0xFFFFFFFF).copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        } else {
            // Normal content when permission is granted
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
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
                                contentDescription = LanguageManager.getString("go_back", language),
                                tint = Color(0xFFFFFFFF)
                            )
                        }
                        Text(
                            text = LanguageManager.getString("grayscale_mode", language),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                // Enable/Disable toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF00D4AA).copy(alpha = 0.1f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${LanguageManager.getString("enable", language)} ${LanguageManager.getString("grayscale_mode", language)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            enabled = hasSecureSettingsPermission,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00D4AA),
                                checkedTrackColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                                uncheckedTrackColor = Color(0xFFFFFFFF).copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                if (hasSecureSettingsPermission && isEnabled) {
                    // Apply every day toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF22D3EE).copy(alpha = 0.1f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = LanguageManager.getString("apply_every_day", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                            Switch(
                                checked = applyEveryDay,
                                onCheckedChange = { applyEveryDay = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF22D3EE),
                                    checkedTrackColor = Color(0xFF22D3EE).copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                                    uncheckedTrackColor = Color(0xFFFFFFFF).copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    // Time selection
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00B4DB).copy(alpha = 0.1f),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                LanguageManager.getString("active_hours", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                            Text(
                                text = LanguageManager.getString("grayscale_active_hours_desc", language),
                                fontSize = 12.sp,
                                color = Color(0xFF22D3EE),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        LanguageManager.getString("from_label", language),
                                        fontSize = 12.sp,
                                        color = Color(0xFF22D3EE)
                                    )
                                    GrayscaleTimePickerButton(
                                        hour = startHour,
                                        minute = startMinute,
                                        onTimeChange = { h, m ->
                                            startHour = h
                                            startMinute = m
                                        },
                                        language = language,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        LanguageManager.getString("to_label", language),
                                        fontSize = 12.sp,
                                        color = Color(0xFF22D3EE)
                                    )
                                    GrayscaleTimePickerButton(
                                        hour = endHour,
                                        minute = endMinute,
                                        onTimeChange = { h, m ->
                                            endHour = h
                                            endMinute = m
                                        },
                                        language = language,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Day selection (only if not apply every day)
                    if (!applyEveryDay) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF00D4AA).copy(alpha = 0.1f),
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    LanguageManager.getString("apply_on_days", language),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    dayNames.forEachIndexed { index, day ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                day,
                                                fontSize = 14.sp,
                                                color = Color(0xFFFFFFFF)
                                            )
                                            Checkbox(
                                                checked = index in selectedDays,
                                                onCheckedChange = { isChecked ->
                                                    selectedDays = if (isChecked) {
                                                        selectedDays + index
                                                    } else {
                                                        selectedDays - index
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = Color(0xFF00D4AA),
                                                    uncheckedColor = Color(0xFFFFFFFF).copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add button with validation
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (!applyEveryDay && selectedDays.isEmpty()) {
                                android.widget.Toast.makeText(
                                    context,
                                    LanguageManager.getString("select_at_least_one_day", language),
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            val days = if (applyEveryDay) (1..7).toSet() else selectedDays.map { it + 1 }.toSet()
                            val rule = BlockRule(
                                packageName = "grayscale",
                                days = days,
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
                                type = BlockType.GRAYSCALE
                            )
                            savedRules.add(rule)
                            GrayscaleRuleStorage.save(context, savedRules.toList())
                            GrayscaleController.update(context)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D4AA),
                            contentColor = Color(0xFF0F1419)
                        ),
                        enabled = applyEveryDay || selectedDays.isNotEmpty()
                    ) {
                        Text(
                            LanguageManager.getString("add_time_slot", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Existing schedules
                if (savedRules.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1A2A3F).copy(alpha = 0.6f),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                LanguageManager.getString("saved_schedules", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            savedRules.forEach { rule ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            startHour = rule.startHour
                                            startMinute = rule.startMinute
                                            endHour = rule.endHour
                                            endMinute = rule.endMinute
                                            if (rule.days.size == 7) {
                                                applyEveryDay = true
                                            } else {
                                                applyEveryDay = false
                                                selectedDays = rule.days.map { it - 1 }.toSet()
                                            }
                                            savedRules.remove(rule)
                                            GrayscaleRuleStorage.save(context, savedRules.toList())
                                            GrayscaleController.update(context)
                                        },
                                    color = Color(0xFF22D3EE).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${String.format("%02d:%02d", rule.startHour, rule.startMinute)} - ${String.format("%02d:%02d", rule.endHour, rule.endMinute)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF22D3EE)
                                            )
                                            if (rule.days.size < 7) {
                                                Text(
                                                    text = rule.days.sorted().joinToString(", ") { dayIndex ->
                                                        dayNames[dayIndex - 1]
                                                    },
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                        IconButton(onClick = {
                                            savedRules.remove(rule)
                                            GrayscaleRuleStorage.save(context, savedRules.toList())
                                            GrayscaleController.update(context)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = LanguageManager.getString("delete_rule", language),
                                                tint = Color(0xFFFF6B6B)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Preview
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF22D3EE).copy(alpha = 0.1f),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            LanguageManager.getString("preview", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Grayscale preview
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    color = androidx.compose.ui.graphics.Color(0xFF808080),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = LanguageManager.getString("preview_desc", language),
                                color = androidx.compose.ui.graphics.Color(0xFF404040),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format(
                                LanguageManager.getString("grayscale_from_to", language),
                                String.format("%02d:%02d", startHour, startMinute),
                                String.format("%02d:%02d", endHour, endMinute)
                            ),
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE)
                        )
                    }
                }

                // Save button
                Button(
                    onClick = {
                        prefs.edit()
                            .putBoolean("enabled", isEnabled)
                            .putBoolean("apply_every_day", applyEveryDay)
                            .apply()

                        if (!isEnabled) {
                            GrayscaleRuleStorage.save(context, emptyList())
                            onBack()
                            return@Button
                        }

                        GrayscaleRuleStorage.save(context, savedRules.toList())
                        GrayscaleController.update(context)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D4AA),
                        contentColor = Color(0xFF0F1419)
                    )
                ) {
                    Text(
                        LanguageManager.getString("save_settings", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                }
            }
        }
        
        // Info Dialog - shown on first visit
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { 
                    if (dontShowAgain) {
                        prefs.edit().putBoolean("has_seen_grayscale_info", true).apply()
                    }
                    showInfoDialog = false
                },
                title = {
                    Text(
                        text = LanguageManager.getString("grayscale_info_title", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = LanguageManager.getString("grayscale_info_body", language),
                            fontSize = 14.sp,
                            color = Color(0xFFFFFFFF).copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = dontShowAgain,
                                onCheckedChange = { dontShowAgain = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF00D4AA),
                                    uncheckedColor = Color(0xFFFFFFFF).copy(alpha = 0.6f)
                                )
                            )
                            Text(
                                text = LanguageManager.getString("grayscale_dont_show_again", language),
                                fontSize = 14.sp,
                                color = Color(0xFFFFFFFF).copy(alpha = 0.8f),
                                modifier = Modifier.clickable { dontShowAgain = !dontShowAgain }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (dontShowAgain) {
                                prefs.edit().putBoolean("has_seen_grayscale_info", true).apply()
                            }
                            showInfoDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D4AA),
                            contentColor = Color(0xFF0F1419)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            LanguageManager.getString("grayscale_info_ok", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                containerColor = Color(0xFF1E2A3F),
                textContentColor = Color(0xFFFFFFFF)
            )
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

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { showDialog = true },
            color = Color(0xFF00D4AA).copy(alpha = 0.15f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${String.format("%02d:%02d", hour, minute)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF1A2A3F),
            title = {
                Text(
                    LanguageManager.getString("select_time", language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Display current time
                    Text(
                        "${String.format("%02d:%02d", tempHour, tempMinute)}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00D4AA)
                    )

                    // Input fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour input
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                LanguageManager.getString("hour", language),
                                fontSize = 12.sp,
                                color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = hourText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        hourText = newValue
                                        newValue.toIntOrNull()?.let { h ->
                                            if (h in 0..23) tempHour = h
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00D4AA),
                                    unfocusedBorderColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                                    cursorColor = Color(0xFF00D4AA)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        tempHour = (tempHour - 1 + 24) % 24
                                        hourText = String.format("%02d", tempHour)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Color(0xFF00D4AA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        tempHour = (tempHour + 1) % 24
                                        hourText = String.format("%02d", tempHour)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF00D4AA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            ":",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA),
                            modifier = Modifier.padding(bottom = 40.dp)
                        )

                        // Minute input
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                LanguageManager.getString("minute", language),
                                fontSize = 12.sp,
                                color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = minuteText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        minuteText = newValue
                                        newValue.toIntOrNull()?.let { m ->
                                            if (m in 0..59) tempMinute = m
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00D4AA),
                                    unfocusedBorderColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                                    cursorColor = Color(0xFF00D4AA)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        tempMinute = (tempMinute - 1 + 60) % 60
                                        minuteText = String.format("%02d", tempMinute)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Color(0xFF00D4AA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        tempMinute = (tempMinute + 1) % 60
                                        minuteText = String.format("%02d", tempMinute)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF00D4AA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTimeChange(tempHour, tempMinute)
                        showDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D4AA),
                        contentColor = Color(0xFF0F1419)
                    )
                ) {
                    Text(
                        LanguageManager.getString("set_time", language),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        LanguageManager.getString("cancel", language),
                        color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                    )
                }
            }
        )
    }
}
