package com.applimit.ui.screens

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
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager
import com.applimit.security.SecurePrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@Composable
fun PinVerificationScreen(
    language: String = "en",
    onPinVerified: () -> Unit,
    onCancel: () -> Unit,
    context: android.content.Context
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var pinLockoutSecsRemaining by remember { mutableStateOf(0) }
    val securityPrefs = SecurePrefs.get(context)
    val coroutineScope = rememberCoroutineScope()
    
    // PIN lockout countdown - updates every second
    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
            val secsRemaining = ((lockoutUntil - currentTime) / 1000).toInt().coerceAtLeast(0)
            pinLockoutSecsRemaining = secsRemaining
            delay(1000L)
        }
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
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E2A3F)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Lock Icon
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF00D4AA).copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = LanguageManager.getString("verify_pin", language),
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF00D4AA)
                        )
                    }
                }

                // Title
                Text(
                    text = LanguageManager.getString("verify_pin", language),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Message
                Text(
                    text = LanguageManager.getString("enter_your_pin", language),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // PIN Lockout Display or Input Field
                if (pinLockoutSecsRemaining > 0) {
                    // PIN Lockout Countdown
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(70.dp))
                            .background(Color(0xFFFF5252).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = pinLockoutSecsRemaining.toString(),
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5252)
                            )
                            Text(
                                text = LanguageManager.getString("seconds", language),
                                fontSize = 12.sp,
                                color = Color(0xFFFF5252)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = LanguageManager.getString("pin_locked_explanation", language),
                        fontSize = 14.sp,
                        color = Color(0xFFFF5252),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // PIN Input Field
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { 
                            pinInput = it
                            errorMessage = ""
                        },
                        label = { Text(LanguageManager.getString("enter_pin", language), maxLines = 1) },
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

                    // Error Message
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Single Verify Button - no cancel option for security
                    Button(
                        onClick = {
                            val cleanedPin = pinInput.trim()
                            if (cleanedPin.isEmpty()) {
                                errorMessage = LanguageManager.getString("enter_pin", language)
                                return@Button
                            }
                            
                            // Check if PIN is locked out
                            val currentTime = System.currentTimeMillis()
                            val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
                            if (lockoutUntil > currentTime) {
                                val remaining = ((lockoutUntil - currentTime) / 1000).toInt().coerceAtLeast(1)
                                errorMessage = String.format(
                                    LanguageManager.getString("pin_locked_try_later", language),
                                    remaining
                                )
                                return@Button
                            }
                            
                            isLoading = true
                            coroutineScope.launch {
                                val isValid = withContext(Dispatchers.Default) {
                                    SecurePinManager.verifyPin(context, cleanedPin)
                                }
                                
                                if (isValid) {
                                    // Clear failed attempts on successful verification
                                    securityPrefs.edit()
                                        .remove("pin_fail_count")
                                        .remove("pin_lockout_until")
                                        .apply()
                                    onPinVerified()
                                } else {
                                    // Increment failed attempts
                                    val failCount = securityPrefs.getInt("pin_fail_count", 0) + 1
                                    if (failCount >= 3) {
                                        // Lock for 60 seconds
                                        val newLockoutUntil = currentTime + 60_000L
                                        securityPrefs.edit()
                                            .putInt("pin_fail_count", 0)
                                            .putLong("pin_lockout_until", newLockoutUntil)
                                            .apply()
                                        errorMessage = String.format(
                                            LanguageManager.getString("pin_locked_try_later", language),
                                            60
                                        )
                                    } else {
                                        securityPrefs.edit().putInt("pin_fail_count", failCount).apply()
                                        errorMessage = LanguageManager.getString("incorrect_pin", language)
                                    }
                                    pinInput = ""
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading && pinInput.isNotEmpty() && pinLockoutSecsRemaining <= 0
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                LanguageManager.getString("verify", language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Warning message - no bypass available
                Text(
                    text = LanguageManager.getString("pin_required_continue", language),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        }
    }
}
