package com.applimit.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ===== SPACING =====
data class Dimensions(
    val spacing2: Int = 2,
    val spacing4: Int = 4,
    val spacing8: Int = 8,
    val spacing12: Int = 12,
    val spacing16: Int = 16,
    val spacing20: Int = 20,
    val spacing24: Int = 24,
    val spacing28: Int = 28,
    val spacing32: Int = 32,
    val spacing36: Int = 36,
    val spacing40: Int = 40,
    val spacing48: Int = 48,
    val spacing56: Int = 56,
    val spacing64: Int = 64
)

// ===== SHAPES =====
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small       = RoundedCornerShape(8.dp),
    medium      = RoundedCornerShape(12.dp),
    large       = RoundedCornerShape(16.dp),
    extraLarge  = RoundedCornerShape(28.dp)
)

// ===== ICON SIZES =====
object IconSizes {
    val extraSmall = 16.dp
    val small = 20.dp
    val medium = 24.dp
    val large = 32.dp
    val extraLarge = 48.dp
    val mega = 64.dp
}

// ===== BUTTON SIZES =====
object ButtonSizes {
    val small = 32.dp
    val medium = 40.dp
    val large = 48.dp
    val extraLarge = 56.dp
}

// ===== ELEVATIONS =====
object Elevations {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
    val level4 = 8.dp
    val level5 = 12.dp
}

// ===== STROKE WIDTHS =====
object StrokeWidths {
    val thin = 1.dp
    val medium = 2.dp
    val bold = 3.dp
}

// ===== OPACITY =====
object OpacityValues {
    val disabled = 0.38f
    val hovered = 0.08f
    val focused = 0.12f
    val pressed = 0.12f
    val placeholder = 0.38f
    val divider = 0.12f
}

// ===== CORNER RADIUS =====
object CornerRadius {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 28.dp
    val full = 50.dp
}

// ===== CONTENT PADDING =====
object ContentPadding {
    val small = 8.dp
    val normal = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
}

// ===== ANIMATION DURATIONS =====
object AnimationDurations {
    val veryFast = 50
    val fast = 100
    val normal = 300
    val slow = 500
    val verySlow = 800
}

// ===== ANIMATION SPECS (reusable AnimationSpec instances) =====
object AppAnimations {
    val enterSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val stateChange = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
    val exitTween = tween<Float>(durationMillis = 200, easing = LinearOutSlowInEasing)
    val numericCounter = tween<Float>(durationMillis = 800, easing = FastOutSlowInEasing)
    val ringFill = tween<Float>(durationMillis = 800, easing = FastOutSlowInEasing)
}

// ===== PROGRESS RING SIZES =====
object ProgressSizes {
    val small  = 48.dp
    val medium = 80.dp
    val large  = 140.dp
    val strokeThin   = 6.dp
    val strokeMedium = 10.dp
    val strokeThick  = 14.dp
}

// ===== PIN DOT SIZES =====
object PinDotSizes {
    val containerSize = 16.dp
    val filled  = 16.dp
    val empty   = 12.dp
    val spacing = 20.dp
}

// ===== Z-INDEX LAYERS =====
object ZIndexLayers {
    val background = 0
    val content = 10
    val floating = 100
    val overlay = 1000
    val dialog = 10000
    val snackbar = 100000
}
