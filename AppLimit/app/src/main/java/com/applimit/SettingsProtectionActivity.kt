package com.applimit

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.security.SecurePinManager
import com.applimit.security.SecurePrefs
import com.applimit.ui.theme.SafeTimeGuardTheme

/**
 * Activity that shows PIN verification over Settings screen
 * Prevents access to app details/accessibility settings without PIN
 */
class SettingsProtectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Security: Prevent screenshots
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        
        // Make this activity appear over lockscreen and show immediately
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            SafeTimeGuardTheme {
                SettingsProtectionScreen(
                    onPinVerified = {
                        // Grant 30 seconds of access
                        val prefs = getSharedPreferences("settings_protection", Context.MODE_PRIVATE)
                        prefs.edit().putLong("access_granted_until", System.currentTimeMillis() + 30_000).apply()
                        finish()
                    },
                    onCancel = {
                        // Go back to home screen - prevent settings access
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Handled by BackHandler in Compose
        // Don't call super.onBackPressed()
    }
}

@Composable
private fun SettingsProtectionScreen(
    onPinVerified: () -> Unit,
    onCancel: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var pinLockoutSecsRemaining by remember { mutableStateOf(0) }
    
    // Get current language from SharedPreferences
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val currentLanguage = prefs.getString("language", "en") ?: "en"
    val securityPrefs = SecurePrefs.get(context)
    
    // PIN lockout countdown
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
            val secsRemaining = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(0)
            pinLockoutSecsRemaining = secsRemaining
            if (secsRemaining <= 0) break
            kotlinx.coroutines.delay(1000L)
        }
    }
    
    // Intercept back button - treat as cancel
    BackHandler {
        onCancel()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1419),
                        Color(0xFF1A2A3F)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1419))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00D4AA).copy(alpha = 0.15f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Settings Protection",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF00D4AA)
                        )
                    }
                }

                Text(
                    text = LanguageManager.getString("settings_protected", currentLanguage),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = LanguageManager.getString("enter_pin_to_access_settings", currentLanguage),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Show countdown or PIN input based on lockout state
                if (pinLockoutSecsRemaining > 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = LanguageManager.getString("pin_locked_explanation", currentLanguage),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Countdown timer display
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFF6B6B).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pinLockoutSecsRemaining.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B6B),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = LanguageManager.getString("seconds", currentLanguage),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    // PIN Input
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            errorMessage = ""
                        },
                        label = { Text(LanguageManager.getString("pin_label", currentLanguage), maxLines = 1) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D4AA),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFF00D4AA),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00D4AA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = errorMessage.isNotEmpty()
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Show buttons only when not locked out
                if (pinLockoutSecsRemaining <= 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                LanguageManager.getString("exit", currentLanguage),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                val cleanedPin = pinInput.trim()
                                val now = System.currentTimeMillis()
                                val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
                                if (lockoutUntil > now) {
                                    val remaining = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(1)
                                    errorMessage = String.format(
                                        LanguageManager.getString("pin_locked_try_later", currentLanguage),
                                        remaining
                                    )
                                    return@Button
                                }

                                if (cleanedPin.isBlank()) {
                                    errorMessage = LanguageManager.getString("enter_pin", currentLanguage)
                                    return@Button
                                }

                                isLoading = true
                                if (SecurePinManager.verifyPin(context, cleanedPin)) {
                                    securityPrefs.edit()
                                        .remove("pin_fail_count")
                                        .remove("pin_lockout_until")
                                        .apply()
                                    onPinVerified()
                                } else {
                                    val failCount = securityPrefs.getInt("pin_fail_count", 0) + 1
                                    if (failCount >= 3) {
                                        val newLockoutUntil = now + 60_000L
                                        securityPrefs.edit()
                                            .putInt("pin_fail_count", 0)
                                            .putLong("pin_lockout_until", newLockoutUntil)
                                            .apply()
                                        errorMessage = String.format(
                                            LanguageManager.getString("pin_locked_try_later", currentLanguage),
                                            60
                                        )
                                    } else {
                                        securityPrefs.edit().putInt("pin_fail_count", failCount).apply()
                                        errorMessage = LanguageManager.getString("incorrect_pin", currentLanguage)
                                    }
                                    pinInput = ""
                                }
                                isLoading = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = !isLoading && pinInput.isNotEmpty()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    LanguageManager.getString("unlock", currentLanguage),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Text(
                        text = LanguageManager.getString("access_granted_for_30s", currentLanguage),
                        fontSize = 11.sp,
                        color = Color(0xFF00D4AA).copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

