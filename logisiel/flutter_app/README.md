# AppLimit Desktop - Flutter Application

## תיאור
אפליקציית Windows desktop מבוססת Flutter עם עיצוב Material Design 3 לשליטה במצב Grayscale של מכשירי Android.

## תכונות
✅ עיצוב Material Design 3 מלא
✅ תמיכה ב-13 שפות (עברית, ערבית, אנגלית, ספרדית, צרפתית, גרמנית, איטלקית, פורטוגזית, פולנית, רוסית, יפנית, סינית, קוריאנית)
✅ אייקונים של Google Material Design
✅ גופן Roboto
✅ אנימציות חלקות ויפות
✅ 4 מסכים: Splash, Setup, Device Detection, Processing, Success
✅ פקודות ADB מובנות

## התקנה

### דרישות מקדימות
1. התקן Flutter SDK: https://docs.flutter.dev/get-started/install/windows
2. הוסף את Flutter ל-PATH
3. וודא ש-ADB (Android Debug Bridge) מותקן

### בדיקה
```bash
flutter doctor
```

### הרצה
```bash
cd "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app"
flutter pub get
flutter run -d windows
```

### בניית EXE
```bash
flutter build windows --release
```

הקובץ יהיה ב: `build\windows\runner\Release\applimit_desktop.exe`

## מבנה הפרויקט
```
flutter_app/
├── lib/
│   ├── main.dart              # קובץ ראשי עם כל המסכים
│   ├── theme.dart             # Material Design 3 theme
│   └── localization.dart      # תרגומים ל-13 שפות
├── assets/
│   ├── icon.png              # לוגו האפליקציה
│   └── Developer options USB debugging.mp4
└── pubspec.yaml              # תלויות הפרויקט
```

## שפות נתמכות
- English (en-US)
- עברית (he-IL)
- العربية (ar-AE)
- Español (es-ES)
- Français (fr-FR)
- Deutsch (de-DE)
- Italiano (it-IT)
- Português (pt-BR)
- Polski (pl-PL)
- Русский (ru-RU)
- 日本語 (ja-JP)
- 中文 (zh-CN)
- 한국어 (ko-KR)

## שימוש
1. הפעל את האפליקציה
2. עקוב אחר ההוראות להפעלת USB debugging
3. חבר את המכשיר Android
4. לחץ על "Start" להפעלת מצב Grayscale

## טכנולוגיות
- Flutter 3.x
- Material Design 3
- Dart 3.x
- ADB (Android Debug Bridge)

## פיתוח נוסף
לשינוי צבעי העיצוב, ערוך את `lib/theme.dart`
לתרגומים נוספים, עדכן את `lib/localization.dart`
