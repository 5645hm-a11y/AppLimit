import 'package:flutter/material.dart';

class AppTheme {
  // Material Design 3 Color Palette
  static const Color primary = Color(0xFF00D4AA);
  static const Color onPrimary = Color(0xFF0F1419);
  static const Color primaryContainer = Color(0xFF004D42);
  static const Color onPrimaryContainer = Color(0xFF7FFFF0);
  
  static const Color secondary = Color(0xFF7DD4C7);
  static const Color onSecondary = Color(0xFF003A35);
  static const Color secondaryContainer = Color(0xFF005048);
  
  static const Color background = Color(0xFF0F1419);
  static const Color surface = Color(0xFF1A2A3F);
  static const Color surfaceVariant = Color(0xFF2A3F4F);
  static const Color onSurface = Color(0xFFFFFFFF);
  static const Color onBackground = Color(0xFFFFFFFF);
  
  static const Color error = Color(0xFFFF5555);
  static const Color success = Color(0xFF4CAF50);
  static const Color outline = Color(0xFF4A5A6A);

  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      colorScheme: const ColorScheme.dark(
        primary: primary,
        onPrimary: onPrimary,
        primaryContainer: primaryContainer,
        onPrimaryContainer: onPrimaryContainer,
        secondary: secondary,
        onSecondary: onSecondary,
        secondaryContainer: secondaryContainer,
        background: background,
        surface: surface,
        surfaceVariant: surfaceVariant,
        onSurface: onSurface,
        onBackground: onBackground,
        error: error,
        outline: outline,
      ),
      scaffoldBackgroundColor: background,
      fontFamily: 'Roboto',
      textTheme: const TextTheme(
        displayLarge: TextStyle(fontSize: 57, fontWeight: FontWeight.w400, height: 1.12, letterSpacing: -0.25),
        displayMedium: TextStyle(fontSize: 45, fontWeight: FontWeight.w400, height: 1.16),
        headlineLarge: TextStyle(fontSize: 32, fontWeight: FontWeight.w400, height: 1.25),
        headlineMedium: TextStyle(fontSize: 28, fontWeight: FontWeight.w500, height: 1.29),
        titleLarge: TextStyle(fontSize: 22, fontWeight: FontWeight.w500, height: 1.27),
        titleMedium: TextStyle(fontSize: 16, fontWeight: FontWeight.w500, height: 1.50, letterSpacing: 0.15),
        bodyLarge: TextStyle(fontSize: 16, fontWeight: FontWeight.w400, height: 1.50, letterSpacing: 0.5),
        bodyMedium: TextStyle(fontSize: 14, fontWeight: FontWeight.w500, height: 1.43, letterSpacing: 0.25),
        bodySmall: TextStyle(fontSize: 12, fontWeight: FontWeight.w500, height: 1.33, letterSpacing: 0.4),
        labelLarge: TextStyle(fontSize: 14, fontWeight: FontWeight.w500, letterSpacing: 0.1),
        labelSmall: TextStyle(fontSize: 11, fontWeight: FontWeight.w500, letterSpacing: 0.5),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primary,
          foregroundColor: onPrimary,
          minimumSize: const Size(120, 56),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          elevation: 2,
          textStyle: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: secondary,
          foregroundColor: onSecondary,
          minimumSize: const Size(120, 56),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          elevation: 1,
          textStyle: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
        ),
      ),
      cardTheme: CardThemeData(
        color: surfaceVariant,
        elevation: 0,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      ),
      progressIndicatorTheme: const ProgressIndicatorThemeData(
        color: primary,
        linearTrackColor: surfaceVariant,
      ),
    );
  }
}
