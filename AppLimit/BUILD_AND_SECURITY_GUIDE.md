# SafeTimeGuard - מדריך בנייה וטיפול

## 📋 סקירה

SafeTimeGuard היא אפליקציית מתקדמת להגבלת אפליקציות עם:
- **Backend C++**: ליבת לוגיקה בשפת C++ ללביצוע מהיר וביטוח חזק
- **UI Compose + Material 3**: ממשק משתמש מודרני ופרקטי
- **JNI Bridge**: תקשורת חלקה בין Kotlin ו-C++
- **בדיקות אבטחה מקיפות**: ולידציה רציפה של כל מערכת האבטחה

## 🛠️ דרישות בנייה

### דרישות מערכת:
- Android SDK 26+ (minSdk)
- Android SDK 34+ (targetSdk)
- Android NDK (r25 ומעלה) - יוריד אוטומטית דרך gradle
- CMake 3.22.1+
- Kotlin 1.5+
- Gradle 8.0+

### התקנה:

```bash
# Clone repository
git clone [repo-url]
cd AppLimit

# Sync gradle
./gradlew sync

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease
```

## 📁 מבנה הפרוייקט

```
app/
├── src/
│   ├── main/
│   │   ├── cpp/                    # קוד C++ לוגיקה עיקרית
│   │   │   ├── CMakeLists.txt
│   │   │   ├── native-lib.cpp      # JNI bridge
│   │   │   ├── block_rule_engine.cpp
│   │   │   ├── time_manager.cpp
│   │   │   ├── security_validator.cpp
│   │   │   ├── pin_manager.cpp
│   │   │   ├── grayscale_controller.cpp
│   │   │   └── include/            # Header files
│   │   ├── java/
│   │   │   └── com/example/safetimeguard/
│   │   │       ├── MainActivity.kt
│   │   │       ├── MainScreen.kt
│   │   │       ├── SecurityChecker.kt    # בדיקות אבטחה
│   │   │       ├── NativeLib.kt          # JNI Bindings
│   │   │       ├── DashboardScreen.kt
│   │   │       ├── SettingsScreen.kt
│   │   │       └── ...
│   │   ├── res/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   ├── test/
│   │   └── java/com/example/safetimeguard/
│   │       └── SecurityTests.kt    # יחידות בדיקה
│   └── androidTest/
├── build.gradle.kts
└── proguard-rules.pro

SECURITY_REPORT.md                 # דוח אבטחה מלא
README.md                          # זה!
```

## 🔐 רכיבי אבטחה

### 1. BlockRuleEngine (C++)
מנהל את כל הכללים לחסימת אפליקציות:
- הוספה/מחיקה/עדכון כללים
- בדיקה אם אפליקציה צריכה להיחסם
- קביעת סוג החסימה (APP_BLOCK או GRAYSCALE)

### 2. SecurityValidator (C++)
ולידציה מקיפה:
- ולידציה של PIN
- בדיקות הצפנה
- השוואה בטוחה (no timing attacks)
- זיהוי root/emulator

### 3. PinManager (C++)
ניהול PIN בטוח:
- הגדרת PIN עם ולידציה
- ולידציה עם מונה ניסיונות כושלים
- נעילה אוטומטית אחרי 5 ניסיונות כושלים
- טיהור נתונים רגיש מהזיכרון

### 4. TimeManager (C++)
חישובי זמן מדויקים:
- בדיקה אם הזמן בטווח מסוים
- זיהוי יום השבוע (1=שני...7=ראשון)
- חישוב הבדלי זמן
- פורמט הדפסה

### 5. GrayscaleController (C++)
בקרה על מסנן הסקאלה האפור:
- הגדרת כללי סקאלה אפור לאפליקציה
- קביעת עוצמת הסינון
- בדיקה אם צריך להחיל סקאלה אפור

### 6. SecurityChecker (Kotlin)
בדיקות אבטחה כללות:
- ולידציה של PIN
- בדיקת שלמות החוסם
- ולידציה של הרשאות
- בדיקת אבטחת ההתקן

## 🚀 הפעלה וביצוע

### בנייה בסיסית:
```bash
# Debug build
./gradlew build

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### הפעלה באפליקציה:

```kotlin
// Initialize at app start
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize native C++ library
        NativeLib.initialize()
        
        // Run security check
        val report = SecurityChecker.generateSecurityReport(this)
        Log.d("SecurityCheck", report)
    }
}

// Check if app should be blocked
val shouldBlock = NativeLib.shouldBlockApp("com.example.app", System.currentTimeMillis())

// Verify PIN
val isValid = NativeLib.verifyPin("1234")

// Check grayscale
val shouldGrayscale = NativeLib.shouldApplyGrayscale("com.example.app", System.currentTimeMillis())
```

## ✅ בדיקות אבטחה

### הפעלת בדיקות:
```bash
# Run unit tests
./gradlew test

# Run specific test class
./gradlew test --tests SecurityTests

# Run with coverage
./gradlew test --coverage
```

### בדיקות המוקדות:
- ✓ ולידציה של PIN
- ✓ ולידציה של טווח זמן
- ✓ ולידציה של ימי השבוע
- ✓ שלמות כללי חסימה
- ✓ ולידציה של עוצמת סקאלה אפור
- ✓ ולידציה של הרשאות
- ✓ מנגנון נעילה

### דוח אבטחה:
```kotlin
val checker = SecurityChecker
val result = checker.performSecurityCheck(context)

println("Overall Secure: ${result.isSecure}")
println("PIN Status: ${result.pinSecurityStatus}")
println("Blocker Status: ${result.blockerIntegrity}")
println("Vulnerabilities: ${result.vulnerabilities}")
println("Recommendations: ${result.recommendations}")
```

## 🔧 תצורה וקונפיגורציה

### build.gradle.kts:
```kotlin
android {
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}
```

### CMakeLists.txt:
```cmake
cmake_minimum_required(VERSION 3.22.1)
project(SafeTimeGuard)

set(CMAKE_CXX_STANDARD 17)

# Source files
set(SOURCE_FILES
    native-lib.cpp
    block_rule_engine.cpp
    time_manager.cpp
    security_validator.cpp
    pin_manager.cpp
    grayscale_controller.cpp
)

add_library(safetimeguard SHARED ${SOURCE_FILES})
target_link_libraries(safetimeguard log)
```

## 🛡️ תכונות אבטחה

### PIN:
- ✓ 4-20 תווים
- ✓ Hashing בטוח
- ✓ ניסיונות מוגבלים (5 max)
- ✓ נעילה אוטומטית (5 דקות)

### Permissions:
- ✓ PACKAGE_USAGE_STATS
- ✓ QUERY_ALL_PACKAGES
- ✓ RECEIVE_BOOT_COMPLETED
- ✓ FOREGROUND_SERVICE
- ✓ ACCESS_ACCESSIBILITY_SERVICES

### Device Admin:
- ✓ בדיקת הפעלה
- ✓ בדיקות כוח טיים

### בדיקות התקן:
- ✓ Root detection
- ✓ Emulator detection
- ✓ Security status verification

## 📊 דוח אבטחה

דוח אבטחה מלא ניתן להפקה:

```kotlin
val report = SecurityChecker.generateSecurityReport(context)
// Output:
// === SafeTimeGuard Security Report ===
// Overall Security: SECURE ✓
// PIN Security: PROTECTED
// Blocker Integrity: INTACT
// Permissions: COMPLETE
// Device Security: SECURE
// Vulnerabilities Found: None
// Recommendations: None
```

## 🐛 פתרון בעיות

### בעיה: NDK לא נמצא
```bash
# פתרון:
./gradlew --version
# Update Android SDK > Tools > NDK
```

### בעיה: CMake שגיאה
```bash
# Update CMake version
# In SDK Manager: Tools > CMake > 3.22.1+
```

### בעיה: Native library not loaded
```bash
# Check logs
adb logcat | grep SafeTimeGuard-JNI

# Verify library in APK
unzip app-release.apk -d apk_contents
ls apk_contents/lib/arm64-v8a/
```

## 📱 עמידות בפלטפורמה

- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 34 (Android 14)
- **ABIs**: arm64-v8a, armeabi-v7a, x86, x86_64

## 📝 הערות ושינויים

### Version 2.0 (אדריכלות C++):
- ✓ מעבר לוגיקה ל-C++
- ✓ JNI Bridge
- ✓ Material 3 UI
- ✓ SecurityChecker
- ✓ יחידות בדיקה מקיפות

### Security Enhancements:
- ✓ Secure PIN comparison
- ✓ Memory clearing for sensitive data
- ✓ Root/Emulator detection
- ✓ Runtime permission checks
- ✓ Rule validation

## 📞 תמיכה

לשאלות או בעיות:
1. בדוק את `SECURITY_REPORT.md`
2. הפעל את `SecurityTests.kt`
3. בדוק את logs דרך `adb logcat`
4. עיין בהערות בקוד

---

**Last Updated**: January 22, 2026
**Status**: PRODUCTION READY ✓
**Security Level**: HIGH ✓
