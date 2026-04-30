package com.applimit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.ui.theme.ProgressSizes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onComplete: () -> Unit, language: String = "en") {
    val iconScale  = remember { Animatable(0.4f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleOffset = remember { Animatable(20f) }
    val subtitleAlpha  = remember { Animatable(0f) }
    var ringProgress by remember { mutableFloatStateOf(0f) }
    val ringAnim   = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Icon springs in
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
        )
        // 2. Title fades in + ring starts filling
        titleAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // 3. Subtitle slides up
        coroutineScope {
            launch { subtitleAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing)) }
            launch { subtitleOffset.animateTo(0f, animationSpec = tween(350, easing = FastOutSlowInEasing)) }
            launch { ringAnim.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing)) }
        }
        delay(400)
        onComplete()
    }

    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon + ring
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx = ProgressSizes.strokeThick.toPx()
                    val diameter = size.minDimension - strokePx
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize = Size(diameter, diameter)
                    drawArc(
                        color = primaryContainer,
                        startAngle = -90f, sweepAngle = 360f,
                        useCenter = false, topLeft = topLeft, size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                    if (ringAnim.value > 0f) {
                        drawArc(
                            color = primary,
                            startAngle = -90f, sweepAngle = ringAnim.value * 360f,
                            useCenter = false, topLeft = topLeft, size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "AppLimit",
                    modifier = Modifier
                        .size(52.dp)
                        .graphicsLayer {
                            scaleX = iconScale.value
                            scaleY = iconScale.value
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "AppLimit",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = titleAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = LanguageManager.getString("welcome_subtitle", language),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f * subtitleAlpha.value
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = subtitleOffset.value.dp
                ),
                maxLines = 2
            )
        }
    }
}
