package com.applimit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ========== MATERIAL 3 DESIGN TOKENS ==========
// Complete design token system following Material Design 3 (Material You)

// ===== SPACING SCALE =====
// Consistent spacing system based on 4dp base unit
data class Dimensions(
    // Micro spacing (0-8dp)
    val spacing2: Int = 2,
    val spacing4: Int = 4,
    val spacing8: Int = 8,
    
    // Small spacing (12-16dp)
    val spacing12: Int = 12,
    val spacing16: Int = 16,
    
    // Medium spacing (20-24dp)
    val spacing20: Int = 20,
    val spacing24: Int = 24,
    
    // Large spacing (28-32dp)
    val spacing28: Int = 28,
    val spacing32: Int = 32,
    
    // Extra large spacing (36+)
    val spacing36: Int = 36,
    val spacing40: Int = 40,
    val spacing48: Int = 48,
    val spacing56: Int = 56,
    val spacing64: Int = 64
)

// ===== SHAPE SYSTEM (Corner Radius) =====
// Material 3 defines corner radii for modern, polished design
// Increased values for more premium feel throughout the app
val Shapes = Shapes(
    // Extra Small - Used for small components, chips, badges
    extraSmall = RoundedCornerShape(6.dp),
    
    // Small - Used for buttons, text fields, small cards
    small = RoundedCornerShape(12.dp),
    
    // Medium - Used for cards, dialogs, sheets, input fields (most common)
    medium = RoundedCornerShape(16.dp),
    
    // Large - Used for expanded cards, large dialogs, feature cards
    large = RoundedCornerShape(20.dp),
)

// ===== ICON SIZES =====
object IconSizes {
    val extraSmall = 16.dp    // 16x16 - inline text icons
    val small = 20.dp         // 20x20 - small controls
    val medium = 24.dp        // 24x24 - standard icons (Material 3 default)
    val large = 32.dp         // 32x32 - large controls, images
    val extraLarge = 48.dp    // 48x48 - extra large controls, avatars
    val mega = 64.dp          // 64x64 - hero images
}

// ===== BUTTON SIZES =====
object ButtonSizes {
    val small = 32.dp         // Small button height
    val medium = 40.dp        // Standard button height (Material 3 default)
    val large = 48.dp         // Large button height
    val extraLarge = 56.dp    // Extra large button height
}

// ===== COMPONENT ELEVATIONS (Material 3) =====
object Elevations {
    val level0 = 0.dp         // No elevation
    val level1 = 1.dp         // Raised surfaces
    val level2 = 3.dp         // Cards, chips
    val level3 = 6.dp         // Floating buttons, dialogs
    val level4 = 8.dp         // Floating action buttons
    val level5 = 12.dp        // Top app bar, menus
}

// ===== STROKE WIDTHS =====
object StrokeWidths {
    val thin = 1.dp           // Subtle borders
    val medium = 2.dp         // Standard borders
    val bold = 3.dp           // Emphasized borders
}

// ===== OPACITY VALUES (Material 3) =====
object OpacityValues {
    val disabled = 0.38f      // Disabled state
    val hovered = 0.08f       // Hover overlay
    val focused = 0.12f       // Focus indicator
    val pressed = 0.12f       // Pressed state
    val draggedSource = 0.16f // Dragged component
    val placeholder = 0.38f   // Placeholder text
    val divider = 0.12f       // Divider lines
}

// ===== CORNER RADIUS PRESETS =====
object CornerRadius {
    val none = 0.dp           // Sharp corners
    val extraSmall = 4.dp     // Extra small (chips, small icons)
    val small = 8.dp          // Small (buttons, fields)
    val medium = 12.dp        // Medium (cards, dialogs)
    val large = 16.dp         // Large (expanded cards)
    val extraLarge = 20.dp    // Extra large (rounded buttons)
    val full = 50.dp          // Fully rounded (pills, circles)
}

// ===== CONTENT PADDING PRESETS =====
object ContentPadding {
    val small = 8.dp          // Small components
    val normal = 16.dp        // Standard padding
    val large = 24.dp         // Large sections
    val extraLarge = 32.dp    // Extra large sections
}

// ===== ANIMATION DURATIONS =====
object AnimationDurations {
    val veryFast = 50         // Very fast interactions (ms)
    val fast = 100            // Fast feedback (ms)
    val normal = 300          // Standard transitions (ms)
    val slow = 500            // Slow transitions (ms)
    val verySlow = 800        // Very slow transitions (ms)
}

// ===== Z-INDEX LAYERS =====
object ZIndexLayers {
    val background = 0        // Background elements
    val content = 10          // Normal content
    val floating = 100        // Floating buttons, badges
    val overlay = 1000        // Modal overlays
    val dialog = 10000        // Dialog boxes
    val snackbar = 100000     // Snackbars, toasts
}

