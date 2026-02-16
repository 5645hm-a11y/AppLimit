# 🎨 SafeTimeGuard - Material Design 3 (Material You) Design System

## 📖 סקירה כללית

**SafeTimeGuard** עוברת עדכון עיצוב מלא לפי **Material Design 3** (Material You), עם עיצוב דינמי וגישה קלה.

---

## 🎯 עקרונות עיצוב

### Material Design 3 (Material You) - 3 עקרונות מרכזיים:

1. **Expressive** (ביטוי עצמי) - צבעים וודים, צורות גדולות
2. **Accessible** (נגישות) - ניגודיות גבוהה, ברירות ברורות
3. **Cohesive** (כללי וקוהרנטי) - עיצוב אחיד בכל הפלטפורמה

---

## 🎨 Color System (צבעים)

### Primary Color Family (כחול ווייברנט)
```
Primary:          #3366FF (Blue - Vibrant)
On Primary:       #FFFFFF (White text)
Primary Container: #004BD6 (Darker blue)
```

### Secondary Color Family (סגול)
```
Secondary:         #7C63F6 (Purple - Vibrant)
On Secondary:      #FFFFFF (White text)
Secondary Container: #5C4FBF (Darker purple)
```

### Tertiary Color Family (ציאן - Accent)
```
Tertiary:         #06B6D4 (Cyan)
On Tertiary:      #FFFFFF (White text)
Tertiary Container: #0891B2 (Darker cyan)
```

### Neutral Colors (Background & Surface)
```
Background:       #0F1419 (Dark Navy)
Surface:          #121217 (Darker)
Surface Container: #1C1F26 (Mid-tone)
Outline:          #79747E (Border)
```

### Status Colors
```
Error:            #FF5555 (Red)
Success:          #10B981 (Green)
Warning:          #FB923C (Orange)
Info:             #3B82F6 (Light Blue)
```

---

## 📐 Spacing & Dimensions

### Spacing Scale (4dp base unit)
```
xs   = 4dp      (micro spacing)
sm   = 8dp      (small)
md   = 12-16dp  (medium)
lg   = 20-24dp  (large)
xl   = 28-32dp  (extra large)
2xl  = 36-40dp  (double large)
```

### Component Sizes
```
Icon Small:       16x16 dp
Icon Medium:      24x24 dp (default)
Icon Large:       32x32 dp
Button Height:    40 dp (standard)
Top Bar Height:   56 dp
```

---

## 🔤 Typography Scale

### Display Styles (Headlines)
```
Display Large:    57sp, Bold
Display Medium:   45sp, Bold
Display Small:    36sp, Bold
```

### Headline Styles
```
Headline Large:   32sp, Bold
Headline Medium:  28sp, Semi-Bold
Headline Small:   24sp, Semi-Bold
```

### Title Styles
```
Title Large:      22sp, Semi-Bold (section headers)
Title Medium:     16sp, Semi-Bold
Title Small:      14sp, Semi-Bold
```

### Body Styles (Main Content)
```
Body Large:       16sp, Normal (paragraphs)
Body Medium:      14sp, Normal
Body Small:       12sp, Normal
```

### Label Styles (Buttons, Chips)
```
Label Large:      14sp, Semi-Bold
Label Medium:     12sp, Semi-Bold
Label Small:      11sp, Bold
```

---

## 🔲 Shape System (Corner Radius)

### Rounded Corners (Material 3 Standard)
```
Extra Small:      4dp   (chips, small icons)
Small:            8dp   (buttons, fields)
Medium:           12dp  (cards, dialogs)
Large:            16dp  (expanded cards)
Extra Large:      20dp  (rounded buttons)
Full:             50dp  (pills, circles)
```

---

## 🎚️ Elevation System

### Surface Elevations (Material 3)
```
Level 0:  0dp   (No elevation, flat)
Level 1:  1dp   (Raised surfaces)
Level 2:  3dp   (Cards, chips)
Level 3:  6dp   (Floating buttons, dialogs)
Level 4:  8dp   (FAB)
Level 5:  12dp  (Top App Bar, menus)
```

---

## 🧩 Component Library (Material 3 Official)

### TopAppBar
- **Usage**: Header with back button, title, and actions
- **Elevation**: 1dp
- **Height**: 56dp
- **Colors**: Surface container

### Card
- **Usage**: Content containers
- **Elevation**: 1dp (default), 3dp (pressed)
- **Corner Radius**: 12dp
- **Padding**: 16dp internal

### Button
```
Filled Button:     Primary color background
Outlined Button:   Border with transparent background
Text Button:       Minimal, text-only
Icon Button:       Icons in 24x24dp
FAB:              56x56dp, elevated
```

### TextField
- **Shape**: 8dp rounded
- **Height**: 56dp
- **Container**: Surface container
- **Border**: 1dp outline or gradient when focused

### Chip
- **Types**: Filter, Input, Suggestion
- **Height**: 32dp
- **Padding**: 8dp horizontal, 4dp vertical
- **Corner Radius**: 8dp

### NavigationBar
- **Height**: 80dp
- **Items**: Up to 5
- **Icons**: 24x24dp
- **Labels**: Optional

### Dialog
- **Max Width**: 560dp
- **Corner Radius**: 28dp (Material 3 standard)
- **Padding**: 24dp
- **Buttons**: FilledTonal or Outlined

### Switch
- **Height**: 32dp
- **Width**: 52dp
- **Thumb Size**: 16x16dp

---

## 🎨 Application-Specific Colors

### For SafeTimeGuard Features
```
Blocking Indicator:    #EF4444 (Red)
Screen Time Tracking:  #3B82F6 (Blue)
Focus Mode:           #8B5CF6 (Purple)
Healthy Balance:      #10B981 (Green)
```

---

## 📱 Figma Design File Structure

### Recommended Figma Organization:

```
SafeTimeGuard Design System
├── 📐 Colors
│   ├── Primary Colors
│   ├── Secondary Colors
│   ├── Tertiary Colors
│   ├── Neutral Colors
│   └── Status Colors
│
├── 🔤 Typography
│   ├── Display Styles
│   ├── Headline Styles
│   ├── Title Styles
│   ├── Body Styles
│   └── Label Styles
│
├── 🔲 Components
│   ├── TopAppBar (variations)
│   ├── Buttons (Filled, Outlined, Text, Icon)
│   ├── Cards (default, clickable)
│   ├── TextFields
│   ├── Chips
│   ├── Switches
│   ├── Dialog Boxes
│   └── NavigationBar
│
├── 📐 Layouts
│   ├── Dashboard Screen
│   ├── Settings Screen
│   ├── App Blocking Rules
│   ├── Grayscale Schedule
│   └── Onboarding Screens
│
└── 📝 Patterns
    ├── Card Patterns
    ├── Form Patterns
    ├── Modal Patterns
    └── Navigation Patterns
```

---

## 🎯 Figma Best Practices for SafeTimeGuard

### 1. **Component Setup**
- Use Figma Components for all reusable elements
- Create variants for different states (hover, pressed, disabled)
- Use Auto Layout for responsive components

### 2. **Color Styles**
- Create Figma color styles for all colors in Color.kt
- Use naming convention: `Primary`, `On Primary`, `Primary Container`

### 3. **Text Styles**
- Create Figma text styles matching Typography scale
- Include font weight, size, line height, letter spacing

### 4. **Grids & Guides**
- Use 4dp grid for alignment
- Create 16dp margin guides for screen edges
- Use 12-column layout grid for responsive design

### 5. **Version Control**
- Use Figma's version history for design iterations
- Document design decisions in comments
- Tag screens with version numbers

---

## 📋 Implementation Checklist

### Phase 1: Design Tokens ✅
- [x] Color system defined in `Color.kt`
- [x] Typography scale in `Type.kt`
- [x] Spacing/Dimensions in `DesignTokens.kt`
- [x] Shape system (corner radius)

### Phase 2: Components Library ✅
- [x] Material3Components.kt created
- [x] TopAppBar component
- [x] Card components (clickable & non-clickable)
- [x] Button components (Filled, Outlined)
- [x] TextField component
- [x] Chip component
- [x] Divider & Toggle components
- [x] Snackbar component

### Phase 3: Screen Implementation (To Do)
- [ ] Update DashboardScreen with Material 3 components
- [ ] Update SettingsScreen with Material 3 components
- [ ] Update AppBlockingRulesScreen
- [ ] Update GrayscaleScheduleScreen
- [ ] Update OnboardingScreen
- [ ] Update AppBlockedScreen
- [ ] Update SplashScreen

### Phase 4: Figma Export (To Do)
- [ ] Create Figma design file
- [ ] Upload component library to Figma
- [ ] Create screen prototypes
- [ ] Generate design handoff documentation

---

## 🔗 Integration with Figma

### To Create Design System in Figma:

1. **Create New Figma File**: "SafeTimeGuard Design System"

2. **Import Color Palette**:
   - Create color styles matching Color.kt
   - Use same naming conventions
   - Tag with "Color/" prefix

3. **Set Up Typography**:
   - Create text styles for each typography scale
   - Include all font variations
   - Tag with "Typography/" prefix

4. **Build Components**:
   - Create reusable components for each UI element
   - Set up component variants (normal, hover, pressed, disabled)
   - Document component usage in description

5. **Create Screens**:
   - Design each app screen using components
   - Use master components for consistency
   - Create interactive prototypes

6. **Documentation**:
   - Add design guidelines documentation
   - Include spacing, alignment, and interaction rules
   - Provide usage examples

---

## 💻 Code Integration

All Material Design 3 components are implemented in:

```
app/src/main/java/com/example/safetimeguard/ui/
├── theme/
│   ├── Color.kt              (Color system)
│   ├── Type.kt               (Typography)
│   ├── Theme.kt              (Theme composable)
│   └── DesignTokens.kt       (Spacing, shapes)
│
├── components/
│   └── Material3Components.kt (Reusable components)
│
└── screens/
    ├── DashboardScreen.kt    (To be updated)
    ├── SettingsScreen.kt     (To be updated)
    └── ... (other screens)
```

---

## 📖 Usage Example

```kotlin
// In your Screen composable:
SafeTimeGuardTheme {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        SafeTimeGuardTopAppBar(
            title = "Dashboard",
            onNavigateBack = { /* navigate back */ }
        )
        
        // Card with Material 3 styling
        Material3Card(
            modifier = Modifier.padding(16.dp),
            onClick = { /* handle click */ }
        ) {
            Column {
                Text(
                    "Feature Title",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Feature description",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Material 3 Button
        Material3FilledButton(
            text = "Save Changes",
            onClick = { /* save */ },
            modifier = Modifier.padding(16.dp)
        )
    }
}
```

---

## 🔄 Next Steps

1. **Update all screens** to use Material3Components
2. **Create Figma design file** with component library
3. **Generate design handoff** documentation
4. **Implement animations** and interactions
5. **Test across devices** for Material You compliance

---

## 📚 References

- [Material Design 3 Documentation](https://m3.material.io/)
- [Material You Guidelines](https://material.io/blog/material-you)
- [Figma Material 3 Community Kit](https://www.figma.com/@materialdesign)
- [Android Material 3 Jetpack Compose](https://developer.android.com/jetpack/compose/designsystems)

---

**Design System Version**: 1.0 (Material Design 3)  
**Last Updated**: February 3, 2026  
**Status**: Ready for Implementation ✅
