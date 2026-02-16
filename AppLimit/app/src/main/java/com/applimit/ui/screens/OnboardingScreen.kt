package com.applimit.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager

/**
 * Modern Onboarding screens with Material 3 design
 * - Welcome screen with gradient
 * - PIN setup screen with validation
 * - Language selection with 11 languages
 * - Permissions request
 */
@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(OnboardingStep.LANGUAGE) }
    var selectedLanguage by remember { mutableStateOf(LanguageManager.ENGLISH) }

    AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300)))
            },
            label = "onboarding_transition"
    ) { screen ->
        when (screen) {
            OnboardingStep.LANGUAGE ->
                    LanguageSelectionScreen(
                            currentLanguage = selectedLanguage,
                            onLanguageSelected = {
                                selectedLanguage = it
                                // Save language immediately
                                val prefs =
                                        context.getSharedPreferences(
                                                "safetimeguard_settings",
                                                Context.MODE_PRIVATE
                                        )
                                prefs.edit().putString("language", it).apply()
                                android.util.Log.d("OnboardingScreen", "Language selected and saved: $it")
                                currentScreen = OnboardingStep.WELCOME
                            }
                    )
            OnboardingStep.WELCOME ->
                    WelcomeScreen(
                            language = selectedLanguage,
                            onNext = { currentScreen = OnboardingStep.PIN_SETUP }
                    )
            OnboardingStep.PIN_SETUP ->
                    PinSetupScreen(
                            language = selectedLanguage,
                            onNext = { pin ->
                                // Save PIN using SecurePinManager
                                try {
                                    SecurePinManager.setupPin(context, pin)
                                    currentScreen = OnboardingStep.PERMISSIONS
                                } catch (e: Exception) {
                                    android.util.Log.e("OnboardingScreen", "Failed to save PIN", e)
                                }
                            }
                    )
            OnboardingStep.PERMISSIONS ->
                    PermissionsScreen(
                            language = selectedLanguage,
                            onNext = { currentScreen = OnboardingStep.WEEK_START }
                    )
            OnboardingStep.WEEK_START ->
                    WeekStartScreen(
                            language = selectedLanguage,
                            onComplete = onOnboardingComplete
                    )
        }
    }
}

enum class OnboardingStep {
    LANGUAGE,
    WELCOME,
    PIN_SETUP,
        PERMISSIONS,
        WEEK_START
}

@Composable
private fun WelcomeScreen(language: String, onNext: () -> Unit) {
    val gradientBrush =
            Brush.verticalGradient(
                    colors =
                            listOf(
                                    Color(0xFF0F1419),
                                    Color(0xFF1A2A3F)
                            )
            )

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(gradientBrush)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        // Large lock icon
        Surface(
                modifier = Modifier.size(120.dp).clip(CircleShape),
                color = Color(0xFF00D4AA).copy(alpha = 0.15f)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "SafeTimeGuard",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF00D4AA)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
                text = LanguageManager.getString("welcome_title", language),
                fontSize = 36.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
                text = LanguageManager.getString("welcome_subtitle", language),
                fontSize = 16.sp,
                color = Color(0xFF22D3EE),
                modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
                text = LanguageManager.getString("welcome_description", language),
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF).copy(alpha = 0.7f),
                modifier =
                        Modifier.background(
                                        Color(0xFF00D4AA).copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4AA),
                    contentColor = Color(0xFF0F1419)
                )
        ) { Text(LanguageManager.getString("get_started", language), fontSize = 16.sp) }
    }
}

@Composable
private fun PinSetupScreen(language: String, onNext: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Surface(
                modifier = Modifier.size(100.dp).clip(CircleShape),
                color = Color(0xFF00D4AA).copy(alpha = 0.15f)
        ) {
            Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Setup PIN",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF00D4AA)
            )
        }

        Text(
                text = LanguageManager.getString("create_master_pin", language),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Text(
                text = LanguageManager.getString("pin_description", language),
                fontSize = 14.sp,
                color = Color(0xFF22D3EE),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN Input
        OutlinedTextField(
                value = pin,
                onValueChange = {
                    pin = it
                    errorMessage = ""
                },
                label = { Text(LanguageManager.getString("enter_pin", language)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                        if (showPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                                imageVector =
                                        if (showPassword) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle PIN visibility",
                                tint = Color(0xFF00D4AA)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D4AA),
                    unfocusedBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.3f),
                    focusedLabelColor = Color(0xFF00D4AA),
                    unfocusedLabelColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                    cursorColor = Color(0xFF00D4AA),
                    focusedTextColor = Color(0xFFFFFFFF),
                    unfocusedTextColor = Color(0xFFFFFFFF)
                )
        )

        // Confirm PIN Input
        OutlinedTextField(
                value = confirmPin,
                onValueChange = {
                    confirmPin = it
                    errorMessage = ""
                },
                label = { Text(LanguageManager.getString("confirm_pin", language)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                        if (showConfirmPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                                imageVector =
                                        if (showConfirmPassword) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle confirm PIN visibility",
                                tint = Color(0xFF00D4AA)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D4AA),
                    unfocusedBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.3f),
                    focusedLabelColor = Color(0xFF00D4AA),
                    unfocusedLabelColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                    cursorColor = Color(0xFF00D4AA),
                    focusedTextColor = Color(0xFFFFFFFF),
                    unfocusedTextColor = Color(0xFFFFFFFF)
                )
        )

        if (errorMessage.isNotEmpty()) {
            Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFD32F2F).copy(alpha = 0.15f)
            ) {
                Text(
                        text = errorMessage,
                        color = Color(0xFFFF5252),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
                onClick = {
                    when {
                        pin.trim().length < 6 ->
                                errorMessage = LanguageManager.getString("pin_min_length", language)
                        pin != confirmPin ->
                                errorMessage = LanguageManager.getString("pins_not_match", language)
                        else -> onNext(pin.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = pin.isNotEmpty() && confirmPin.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4AA),
                    contentColor = Color(0xFF0F1419),
                    disabledContainerColor = Color(0xFF00D4AA).copy(alpha = 0.3f),
                    disabledContentColor = Color(0xFF0F1419).copy(alpha = 0.5f)
                )
        ) { Text(LanguageManager.getString("continue", language), fontSize = 16.sp) }
    }
}

@Composable
private fun LanguageSelectionScreen(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
                text = LanguageManager.getString("select_language", selectedLanguage),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display all supported languages
        LanguageManager.SUPPORTED_LANGUAGES.forEach { (code, name) ->
            Surface(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedLanguage = code },
                    color =
                            if (selectedLanguage == code)
                                    Color(0xFF00D4AA).copy(alpha = 0.2f)
                            else Color(0xFFFFFFFF).copy(alpha = 0.1f),
            ) {
                Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                            selected = selectedLanguage == code,
                            onClick = { selectedLanguage = code },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00D4AA),
                                unselectedColor = Color(0xFFFFFFFF).copy(alpha = 0.6f)
                            )
                    )
                    Text(
                        text = name, 
                        modifier = Modifier.padding(start = 12.dp), 
                        fontSize = 16.sp,
                        color = Color(0xFFFFFFFF)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
                onClick = { onLanguageSelected(selectedLanguage) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4AA),
                    contentColor = Color(0xFF0F1419)
                )
        ) { Text(LanguageManager.getString("next", selectedLanguage), fontSize = 16.sp) }
    }
}

@Composable
private fun PermissionsScreen(language: String, onNext: () -> Unit) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(setOf<String>()) }
    var accessibilityEnabled by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Check accessibility service status - also check periodically when user opens settings
    LaunchedEffect(refreshTrigger) {
        val enabled = isAccessibilityServiceEnabled(context)
        accessibilityEnabled = enabled
        // Check every 500ms while accessibility is not enabled
        if (!enabled) {
            kotlinx.coroutines.delay(500)
            refreshTrigger++
        }
    }

    // Permission launcher for regular permissions
    val permissionLauncher =
            rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                val granted = result.filterValues { it }.keys
                permissionsGranted = permissionsGranted + granted
                // Re-check accessibility status after permission dialog
                val enabled = isAccessibilityServiceEnabled(context)
                accessibilityEnabled = enabled
            }

    val requiredPermissions =
            listOf(
                    android.Manifest.permission.POST_NOTIFICATIONS to "send_notifications"
            )

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Surface(
                modifier = Modifier.size(100.dp).clip(CircleShape),
                color = Color(0xFF00D4AA).copy(alpha = 0.15f)
        ) {
            Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Permissions",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF00D4AA)
            )
        }

        Text(
                text = LanguageManager.getString("required_permissions", language),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Text(
                text = LanguageManager.getString("permissions_description", language),
                fontSize = 14.sp,
                color = Color(0xFF22D3EE),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display permissions
        requiredPermissions.forEach { (permission, labelKey) ->
            val isGranted = permissionsGranted.contains(permission)
            val labelText = LanguageManager.getString(labelKey, language)
            Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color =
                            if (isGranted) Color(0xFF00D4AA).copy(alpha = 0.15f)
                            else Color(0xFFFFFFFF).copy(alpha = 0.1f)
            ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isGranted) {
                        Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFF00D4AA)
                        )
                    } else {
                        Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFFFFFFF).copy(alpha = 0.4f)
                        )
                    }
                    Text(
                            text = labelText,
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            fontSize = 14.sp
                    )
                    if (!isGranted) {
                        Button(
                                onClick = {
                                    permissionLauncher.launch(arrayOf(permission))
                                },
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00D4AA),
                                    contentColor = Color(0xFF0F1419)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(LanguageManager.getString("ask", language), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessibility Service (CRITICAL for blocking to work)
        Surface(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (!accessibilityEnabled) {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            }
                        },
                color =
                        if (accessibilityEnabled) Color(0xFF00D4AA).copy(alpha = 0.15f)
                        else Color(0xFFFFFFFF).copy(alpha = 0.1f)
        ) {
            Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (accessibilityEnabled) {
                    Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF00D4AA)
                    )
                } else {
                    Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFFFFFFFF).copy(alpha = 0.4f)
                    )
                }
                Text(
                        text = LanguageManager.getString("enable_accessibility", language),
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                        fontSize = 14.sp,
                        color = Color(0xFFFFFFFF)
                )
                if (!accessibilityEnabled) {
                    Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D4AA),
                                contentColor = Color(0xFF0F1419)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(LanguageManager.getString("enable", language), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
                onClick = {
                                        onNext()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = accessibilityEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4AA),
                    contentColor = Color(0xFF0F1419),
                    disabledContainerColor = Color(0xFF00D4AA).copy(alpha = 0.3f),
                    disabledContentColor = Color(0xFF0F1419).copy(alpha = 0.5f)
                )
        ) { Text(LanguageManager.getString("continue", language), fontSize = 16.sp) }
    }
}

@Composable
private fun WeekStartScreen(language: String, onComplete: () -> Unit) {
    val context = LocalContext.current
    var selectedWeekStart by remember { mutableStateOf("monday") }

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
                text = LanguageManager.getString("week_start_title", language),
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Text(
                text = LanguageManager.getString("week_start_description", language),
                fontSize = 14.sp,
                color = Color(0xFF22D3EE),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        listOf(
                "monday" to LanguageManager.getString("week_start_monday", language),
                "sunday" to LanguageManager.getString("week_start_sunday", language)
        ).forEach { (value, label) ->
            Surface(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedWeekStart = value },
                    color =
                            if (selectedWeekStart == value)
                                    Color(0xFF00D4AA).copy(alpha = 0.2f)
                            else Color(0xFFFFFFFF).copy(alpha = 0.1f)
            ) {
                Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                            selected = selectedWeekStart == value,
                            onClick = { selectedWeekStart = value },
                            colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF00D4AA),
                                    unselectedColor = Color(0xFFFFFFFF).copy(alpha = 0.6f)
                            )
                    )
                    Text(
                            text = label,
                            modifier = Modifier.padding(start = 12.dp),
                            fontSize = 16.sp,
                            color = Color(0xFFFFFFFF)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
                onClick = {
                    val prefs =
                            context.getSharedPreferences(
                                    "safetimeguard_settings",
                                    Context.MODE_PRIVATE
                            )
                    prefs.edit().putString("week_start", selectedWeekStart).apply()
                    prefs.edit().putBoolean("onboarding_complete", true).apply()
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D4AA),
                        contentColor = Color(0xFF0F1419)
                )
        ) { Text(LanguageManager.getString("complete_setup", language), fontSize = 16.sp) }
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val accessibilityEnabled = android.provider.Settings.Secure.getInt(
        context.contentResolver,
        android.provider.Settings.Secure.ACCESSIBILITY_ENABLED,
        0
    ) == 1
    
    if (!accessibilityEnabled) return false
    
    val enabledServices = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    
    return enabledServices.contains("com.applimit/com.applimit.services.AppBlockerAccessibilityService")
}
