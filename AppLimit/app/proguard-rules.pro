# AppLimit ProGuard / R8 rules

# ── Stack traces (useful for crash reports) ──────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Data classes used in JSON serialization (RuleStorage) ───────────────────
-keep class com.applimit.data.BlockRule { *; }
-keep class com.applimit.data.BlockType { *; }
-keepclassmembers class com.applimit.data.BlockRule { *; }

# ── Room database ─────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# ── Accessibility service ─────────────────────────────────────────────────────
-keep class com.applimit.services.AppBlockerAccessibilityService { *; }

# ── Security crypto (EncryptedSharedPreferences / MasterKey) ─────────────────
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# ── Kotlin coroutines ─────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Jetpack Compose (AGP + R8 handle most of this automatically) ─────────────
-dontwarn androidx.compose.**

# ── Coil image loader ─────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**

# ── Google Tink (transitive from security-crypto) ─────────────────────────────
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**

# ── Suppress common R8 warnings ───────────────────────────────────────────────
-dontwarn java.lang.invoke.StringConcatFactory
