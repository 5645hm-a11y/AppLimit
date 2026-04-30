@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
package com.applimit.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager
import com.applimit.ui.components.PinDotInput
import com.applimit.ui.components.PinKeypad

private const val ONBOARDING_PIN_LENGTH = 6

enum class OnboardingStep { LANGUAGE, WELCOME, PIN_SETUP, PERMISSIONS, WEEK_START }

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(OnboardingStep.LANGUAGE) }
    var selectedLanguage by remember { mutableStateOf(LanguageManager.ENGLISH) }
    val steps = OnboardingStep.values()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
            (slideInHorizontally(tween(350)) { it * direction } + fadeIn(tween(350))) togetherWith
            (slideOutHorizontally(tween(350)) { -it * direction } + fadeOut(tween(200)))
        },
        label = "onboarding_transition"
    ) { screen ->
        OnboardingScaffold(currentStep = screen.ordinal, totalSteps = steps.size) {
            when (screen) {
                OnboardingStep.LANGUAGE -> LanguageSelectionScreen(
                    currentLanguage = selectedLanguage,
                    onLanguageSelected = {
                        selectedLanguage = it
                        context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
                            .edit().putString("language", it).apply()
                        currentScreen = OnboardingStep.WELCOME
                    }
                )
                OnboardingStep.WELCOME -> WelcomeScreen(language = selectedLanguage) { currentScreen = OnboardingStep.PIN_SETUP }
                OnboardingStep.PIN_SETUP -> PinSetupScreen(language = selectedLanguage) { pin ->
                    try {
                        SecurePinManager.setupPin(context, pin)
                        currentScreen = OnboardingStep.PERMISSIONS
                    } catch (_: Exception) {}
                }
                OnboardingStep.PERMISSIONS -> PermissionsScreen(language = selectedLanguage) { currentScreen = OnboardingStep.WEEK_START }
                OnboardingStep.WEEK_START -> WeekStartScreen(language = selectedLanguage, onComplete = onOnboardingComplete)
            }
        }
    }
}

@Composable
private fun OnboardingScaffold(
    currentStep: Int,
    totalSteps: Int,
    content: @Composable () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step progress dots
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val primary = MaterialTheme.colorScheme.primary
                val outline = MaterialTheme.colorScheme.outlineVariant
                repeat(totalSteps) { idx ->
                    Canvas(modifier = Modifier.padding(horizontal = 4.dp).size(if (idx == currentStep) 24.dp else 8.dp, 8.dp)) {
                        drawRoundRect(
                            color = if (idx == currentStep) primary else outline,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) { content() }
        }
    }
}

@Composable
private fun LanguageSelectionScreen(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = LanguageManager.getString("select_language", selectedLanguage),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LanguageManager.SUPPORTED_LANGUAGES.forEach { (code, name) ->
                FilterChip(
                    selected = selectedLanguage == code,
                    onClick = { selectedLanguage = code },
                    label = { Text(name, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLanguageSelected(selectedLanguage) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) { Text(LanguageManager.getString("next", selectedLanguage), style = MaterialTheme.typography.labelLarge) }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WelcomeScreen(language: String, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(120.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(LanguageManager.getString("welcome_title", language), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(12.dp))

        Text(LanguageManager.getString("welcome_subtitle", language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
            Text(LanguageManager.getString("welcome_description", language), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(20.dp), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.extraLarge) {
            Text(LanguageManager.getString("get_started", language), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PinSetupScreen(language: String, onNext: (String) -> Unit) {
    var pinStep by remember { mutableStateOf(0) } // 0=enter, 1=confirm
    var enteredPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var triggerShake by remember { mutableStateOf(false) }

    val isConfirmStep = pinStep == 1
    val currentInput = if (isConfirmStep) confirmPin else enteredPin

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isConfirmStep) LanguageManager.getString("confirm_pin", language) else LanguageManager.getString("create_master_pin", language),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = LanguageManager.getString("pin_description", language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(28.dp))

        PinDotInput(
            digitCount = ONBOARDING_PIN_LENGTH,
            enteredDigits = currentInput.length,
            isError = errorMessage.isNotEmpty(),
            triggerShake = triggerShake
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(32.dp))

        PinKeypad(
            onDigitEntered = { digit ->
                if (currentInput.length < ONBOARDING_PIN_LENGTH) {
                    errorMessage = ""
                    if (isConfirmStep) {
                        confirmPin += digit
                        if (confirmPin.length == ONBOARDING_PIN_LENGTH) {
                            if (confirmPin == enteredPin) {
                                onNext(enteredPin)
                            } else {
                                triggerShake = !triggerShake
                                errorMessage = LanguageManager.getString("pins_not_match", language)
                                confirmPin = ""
                                enteredPin = ""
                                pinStep = 0
                            }
                        }
                    } else {
                        enteredPin += digit
                        if (enteredPin.length == ONBOARDING_PIN_LENGTH) {
                            pinStep = 1
                        }
                    }
                }
            },
            onBackspace = {
                errorMessage = ""
                if (isConfirmStep && confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                else if (!isConfirmStep && enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isConfirmStep) {
            TextButton(onClick = { pinStep = 0; confirmPin = "" }) {
                Text(LanguageManager.getString("back", language))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PermissionsScreen(language: String, onNext: () -> Unit) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(setOf<String>()) }
    var accessibilityEnabled by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        val enabled = isAccessibilityServiceEnabled(context)
        accessibilityEnabled = enabled
        if (!enabled) { kotlinx.coroutines.delay(500); refreshTrigger++ }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionsGranted = permissionsGranted + result.filterValues { it }.keys
        accessibilityEnabled = isAccessibilityServiceEnabled(context)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(LanguageManager.getString("required_permissions", language), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(20.dp))

        // Notifications permission
        PermissionRow(
            label = LanguageManager.getString("send_notifications", language),
            isGranted = android.Manifest.permission.POST_NOTIFICATIONS in permissionsGranted,
            onGrant = { permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Accessibility service
        PermissionRow(
            label = LanguageManager.getString("enable_accessibility", language),
            isGranted = accessibilityEnabled,
            onGrant = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = accessibilityEnabled
        ) { Text(LanguageManager.getString("continue", language), style = MaterialTheme.typography.labelLarge) }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PermissionRow(label: String, isGranted: Boolean, onGrant: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isGranted) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isGranted) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            if (!isGranted) {
                Button(onClick = onGrant, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text(LanguageManager.getString("enable", language = "en"), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun WeekStartScreen(language: String, onComplete: () -> Unit) {
    val context = LocalContext.current
    var selectedWeekStart by remember { mutableStateOf("monday") }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(LanguageManager.getString("week_start_title", language), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        listOf("monday" to LanguageManager.getString("week_start_monday", language), "sunday" to LanguageManager.getString("week_start_sunday", language)).forEach { (value, label) ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { selectedWeekStart = value }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                RadioButton(selected = selectedWeekStart == value, onClick = { selectedWeekStart = value })
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE).edit().putString("week_start", selectedWeekStart).apply()
                onComplete()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) { Text(LanguageManager.getString("continue", language), style = MaterialTheme.typography.labelLarge) }
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val settingValue = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
    return settingValue.contains(context.packageName, ignoreCase = true)
}
