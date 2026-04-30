package com.applimit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.security.SecurePinManager
import com.applimit.security.SecurePrefs
import com.applimit.ui.components.PinInputWithSystemKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PIN_LENGTH = 6

@Composable
fun PinVerificationScreen(
    language: String = "en",
    onPinVerified: () -> Unit,
    onCancel: () -> Unit,
    context: android.content.Context
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var triggerShake by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var lockoutSecsRemaining by remember { mutableIntStateOf(0) }
    val securityPrefs = SecurePrefs.get(context)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
            lockoutSecsRemaining = ((lockoutUntil - now) / 1000).toInt().coerceAtLeast(0)
            delay(1000L)
        }
    }

    fun verifyPin() {
        val cleaned = pinInput
        if (cleaned.length < PIN_LENGTH) return
        val now = System.currentTimeMillis()
        val lockoutUntil = securityPrefs.getLong("pin_lockout_until", 0L)
        if (lockoutUntil > now) return

        isLoading = true
        coroutineScope.launch {
            val isValid = withContext(Dispatchers.Default) {
                SecurePinManager.verifyPin(context, cleaned)
            }
            if (isValid) {
                securityPrefs.edit()
                    .remove("pin_fail_count")
                    .remove("pin_lockout_until")
                    .apply()
                onPinVerified()
            } else {
                val failCount = securityPrefs.getInt("pin_fail_count", 0) + 1
                if (failCount >= 3) {
                    securityPrefs.edit()
                        .putInt("pin_fail_count", 0)
                        .putLong("pin_lockout_until", now + 60_000L)
                        .apply()
                    errorMessage = LanguageManager.getString("pin_locked_try_later", language)
                } else {
                    securityPrefs.edit().putInt("pin_fail_count", failCount).apply()
                    errorMessage = LanguageManager.getString("incorrect_pin", language)
                }
                pinInput = ""
                isError = true
                triggerShake = !triggerShake
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isSmall = maxHeight < 600.dp
            val topSpacing = if (isSmall) 48.dp else 88.dp
            val iconSize = if (isSmall) 96.dp else 120.dp
            val iconContentSize = if (isSmall) 38.dp else 48.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Spacer(modifier = Modifier.height(topSpacing))

                Surface(
                    modifier = Modifier.size(iconSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(iconContentSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isSmall) 16.dp else 28.dp))

                Text(
                    text = LanguageManager.getString("verify_pin", language),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = LanguageManager.getString("enter_your_pin", language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (lockoutSecsRemaining > 0) {
                    val lockoutTotal = 60f
                    LinearProgressIndicator(
                        progress = { lockoutSecsRemaining / lockoutTotal },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.errorContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$lockoutSecsRemaining ${LanguageManager.getString("seconds", language)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = LanguageManager.getString("pin_locked_explanation", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    PinInputWithSystemKeyboard(
                        value = pinInput,
                        onValueChange = { new ->
                            if (!isLoading) {
                                isError = false
                                errorMessage = ""
                                pinInput = new
                                if (new.length == PIN_LENGTH) verifyPin()
                            }
                        },
                        digitCount = PIN_LENGTH,
                        isError = isError,
                        triggerShake = triggerShake,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = LanguageManager.getString("pin_required_continue", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}
