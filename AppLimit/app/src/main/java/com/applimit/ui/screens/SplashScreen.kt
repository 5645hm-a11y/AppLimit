package com.applimit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import kotlinx.coroutines.delay

/** Beautiful animated splash screen with Material Design */
@Composable
fun SplashScreen(onComplete: () -> Unit, language: String = "en") {
    val scaleAnimation = remember { Animatable(0.3f) }
    val alphaAnimation = remember { Animatable(0f) }
    val rotationAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate scale in
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = EaseInOutCubic)
        )
        
        // Animate alpha in
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = LinearEasing)
        )
        
        // Delay before completion - reduced for faster app launch
        delay(1500)
        onComplete()
    }

    // Rotation animation runs continuously
    LaunchedEffect(Unit) {
        while (true) {
            rotationAnimation.animateTo(
                targetValue = 360f,
                animationSpec = tween(2000, easing = LinearEasing)
            )
            rotationAnimation.snapTo(0f)
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated Icon
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .scale(scaleAnimation.value),
                color = Color(0xFF00D4AA).copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "AppLimit",
                        modifier = Modifier
                            .size(60.dp)
                            .scale(alphaAnimation.value),
                        tint = Color(0xFF00D4AA)
                    )
                }
            }

            // Title
            Text(
                text = "AppLimit",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            // Subtitle
            Text(
                text = LanguageManager.getString("welcome_subtitle", language),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading dots animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotScale = remember { Animatable(0.5f) }
                    
                    LaunchedEffect(Unit) {
                        delay(index * 200L)
                        while (true) {
                            dotScale.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(600, easing = EaseInOutCubic)
                            )
                            dotScale.animateTo(
                                targetValue = 0.5f,
                                animationSpec = tween(600, easing = EaseInOutCubic)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale.value),
                        shape = CircleShape,
                        color = Color(0xFF00D4AA)
                    ) {}
                }
            }
        }
    }
}
