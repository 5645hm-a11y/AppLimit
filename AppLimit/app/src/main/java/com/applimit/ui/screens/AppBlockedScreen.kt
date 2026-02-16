package com.applimit.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager
import com.applimit.security.SecurePrefs
import kotlinx.coroutines.delay

/**
 * Blocking screen shown when user tries to open a blocked app
 * Displays for both regular app blocks and screen-time blocks
 * Features full-screen UI with animations and PIN protection
 */
@Composable
fun AppBlockedScreen(
    appName: String,
    appIcon: String? = null,
    blockedUntilTime: String,
    language: String = LanguageManager.ENGLISH,
    blockReason: String = "",
    blockedUntilEpoch: Long = 0L,
    onExitUnlocked: (() -> Unit)? = null,
    onPinDialogStateChanged: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val sharedPrefs = SecurePrefs.get(context)
    
    val infiniteTransition = rememberInfiniteTransition(label = "lock_animation")
    val lockRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock_rotation"
    )

    val lockScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val isScreenTimeBlock = blockReason.startsWith("screen_time")
    val isGrayscaleBlock = blockReason.startsWith("grayscale")
    val grayscalePrefs = context.getSharedPreferences("grayscale_settings", Context.MODE_PRIVATE)
    val isGrayscaleEnabled = grayscalePrefs.getBoolean("enabled", false)
    val showGrayscaleDownload = isGrayscaleBlock && !isGrayscaleEnabled
    var downloadUrl by remember { mutableStateOf("") }
    var remainingMs by remember { mutableStateOf(0L) }
    
    // Fetch download URL from Firebase Hosting config.json
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
    val canExit = onExitUnlocked != null
    var showExitDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }
    var pinLockoutSecsRemaining by remember { mutableStateOf(0) }
    
    // Update countdown timer for screen-time blocks
    LaunchedEffect(blockedUntilEpoch, isScreenTimeBlock) {
        if (!isScreenTimeBlock || blockedUntilEpoch <= 0L) return@LaunchedEffect
        while (true) {
            val now = System.currentTimeMillis()
            remainingMs = (blockedUntilEpoch - now).coerceAtLeast(0L)
            delay(1000L)
        }
    }
    
    // Update PIN lockout countdown
    LaunchedEffect(showExitDialog) {
        if (!showExitDialog) return@LaunchedEffect
        while (true) {
            val now = System.currentTimeMillis()
            val lockoutUntil = sharedPrefs.getLong("pin_lockout_until", 0L)
            val secsRemaining = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(0)
            pinLockoutSecsRemaining = secsRemaining
            if (secsRemaining <= 0) break
            delay(1000L)
        }
    }

    fun formatRemaining(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    val accentColor = if (isScreenTimeBlock) Color(0xFF00D4AA) else Color(0xFFFF6B6B)
    val secondaryColor = if (isScreenTimeBlock) Color(0xFF22D3EE) else Color(0xFFFFB86B)
    val backgroundColors =
        if (isScreenTimeBlock) {
            listOf(Color(0xFF0F1419), Color(0xFF1A2A3F))
        } else {
            listOf(Color(0xFF1A1010), Color(0xFF2B1712))
        }
    val titleKey = if (isScreenTimeBlock) "screen_time_exhausted" else "app_blocked"
    val descKey = if (isScreenTimeBlock) "screen_time_blocked_desc" else "app_restricted"
    val timeLabelKey = if (isScreenTimeBlock) "reconnect_at" else "will_reopen_at"

    // Main blocking screen: displays for ALL app blocks (regular and screen-time)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = backgroundColors
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated lock icon
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = pulseAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScreenTimeBlock) Icons.Default.Schedule else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .rotate(lockRotation)
                        .graphicsLayer(scaleX = lockScale, scaleY = lockScale),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Blocking message title
            Text(
                text = LanguageManager.getString(titleKey, language),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isScreenTimeBlock) {
                Text(
                    text = appName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Blocking description
            Text(
                text = LanguageManager.getString(descKey, language),
                fontSize = 14.sp,
                color = secondaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (showGrayscaleDownload) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = Color(0xFF111827).copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LanguageManager.getString("grayscale_not_enabled", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = LanguageManager.getString("grayscale_not_enabled_desc", language),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(LanguageManager.getString("download_app", language))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(LanguageManager.getString("copy_download_link", language))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Time information card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0xFF1A2A3F).copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LanguageManager.getString(timeLabelKey, language) + " " + blockedUntilTime,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    if (isScreenTimeBlock) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = LanguageManager.getString("time_remaining", language),
                            fontSize = 12.sp,
                            color = secondaryColor
                        )
                        Text(
                            text = formatRemaining(remainingMs),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Exit button at bottom (screen time blocks only)
        if (canExit && isScreenTimeBlock) {
            TextButton(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = LanguageManager.getString("exit", language),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }

    // PIN verification dialog (screen time blocks only)
    if (showExitDialog && canExit && isScreenTimeBlock) {
        LaunchedEffect(Unit) {
            onPinDialogStateChanged?.invoke(true)
        }

        Dialog(
            onDismissRequest = {
                // Intentionally empty - prevent accidental dismissal
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Dialog title
                    Text(
                        text = LanguageManager.getString("enter_your_pin", language),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    // Show PIN input or lockout countdown
                    if (pinLockoutSecsRemaining > 0) {
                        // Show lockout countdown
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = LanguageManager.getString("pin_locked_explanation", language),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
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
                                text = LanguageManager.getString("seconds", language),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // Show PIN input field
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                pinError = ""
                            },
                            label = { Text(LanguageManager.getString("pin_label", language)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = pinError.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Error message
                    if (pinError.isNotEmpty()) {
                        Text(
                            text = pinError,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Dialog buttons (only when not locked out)
                    if (pinLockoutSecsRemaining <= 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    sharedPrefs.edit().remove("grace_period_start_time").apply()
                                    showExitDialog = false
                                    pinInput = ""
                                    pinError = ""
                                    onPinDialogStateChanged?.invoke(false)
                                }
                            ) {
                                Text(LanguageManager.getString("cancel", language))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val cleanedPin = pinInput.trim()
                                    val now = System.currentTimeMillis()
                                    val lockoutUntil = sharedPrefs.getLong("pin_lockout_until", 0L)
                                    
                                    // Check if still in lockout period
                                    if (lockoutUntil > now) {
                                        val remaining = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(1)
                                        pinError = String.format(
                                            LanguageManager.getString("pin_locked_try_later", language),
                                            remaining
                                        )
                                        return@Button
                                    }

                                    // Check if PIN is set
                                    if (!SecurePinManager.isPinSet(context)) {
                                        pinError = LanguageManager.getString("pin_required_continue", language)
                                        return@Button
                                    }

                                    // Verify PIN
                                    if (SecurePinManager.verifyPin(context, cleanedPin)) {
                                        // Success - clear lockout counters
                                        sharedPrefs.edit()
                                            .remove("pin_fail_count")
                                            .remove("pin_lockout_until")
                                            .apply()

                                        showExitDialog = false
                                        pinInput = ""
                                        pinError = ""
                                        onPinDialogStateChanged?.invoke(false)

                                        // Call exit callback to dismiss blocking screen
                                        onExitUnlocked?.invoke()
                                    } else {
                                        // Wrong PIN - increment fail count
                                        val failCount = sharedPrefs.getInt("pin_fail_count", 0) + 1
                                        if (failCount >= 3) {
                                            // Lockout for 60 seconds
                                            val newLockoutUntil = now + 60_000L
                                            sharedPrefs.edit()
                                                .putInt("pin_fail_count", 0)
                                                .putLong("pin_lockout_until", newLockoutUntil)
                                                .apply()
                                            pinError = String.format(
                                                LanguageManager.getString("pin_locked_try_later", language),
                                                60
                                            )
                                        } else {
                                            sharedPrefs.edit().putInt("pin_fail_count", failCount).apply()
                                            pinError = LanguageManager.getString("incorrect_pin", language)
                                        }
                                        pinInput = ""
                                    }
                                }
                            ) {
                                Text(LanguageManager.getString("verify", language))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Preview of the app blocking screen
 */
@Composable
fun AppBlockedScreenPreview() {
    MaterialTheme {
        AppBlockedScreen(
            appName = "Instagram",
            blockedUntilTime = "18:00",
            language = LanguageManager.HEBREW
        )
    }
}


