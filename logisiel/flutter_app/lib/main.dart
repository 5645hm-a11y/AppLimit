import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:io';
import 'package:process_run/shell.dart';
import 'theme.dart';
import 'localization.dart';

void main() {
  runApp(const AppLimitApp());
}

class AppLimitApp extends StatelessWidget {
  const AppLimitApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AppLimit',
      theme: AppTheme.darkTheme,
      home: const LanguageSelectionScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}

// Language Selection Screen
class LanguageSelectionScreen extends StatefulWidget {
  const LanguageSelectionScreen({super.key});

  @override
  State<LanguageSelectionScreen> createState() => _LanguageSelectionScreenState();
}

class _LanguageSelectionScreenState extends State<LanguageSelectionScreen> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  String? _selectedLanguage;

  @override
  void initState() {
    super.initState();
    // Set to English by default until user selects a language
    Localization.setLanguage('en-US');
    _selectedLanguage = 'en-US';
    
    _controller = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeIn),
    );
    
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _continueToApp() {
    if (_selectedLanguage != null) {
      Localization.setLanguage(_selectedLanguage!);
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const SplashScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final languages = Localization.getSupportedLanguages();
    
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              AppTheme.background,
              AppTheme.primary.withOpacity(0.1),
            ],
          ),
        ),
        child: SafeArea(
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Padding(
              padding: const EdgeInsets.all(32),
              child: Column(
                children: [
                  const SizedBox(height: 16),
                  
                  // Logo
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(20),
                      image: const DecorationImage(
                        image: AssetImage('assets/icon.png'),
                        fit: BoxFit.cover,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: AppTheme.primary.withOpacity(0.3),
                          blurRadius: 20,
                          spreadRadius: 5,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),
                  
                  // Title
                  Text(
                    Localization.get('splashTitle'),
                    style: Theme.of(context).textTheme.headlineLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 4),
                  
                  // Subtitle
                  Text(
                    Localization.get('splashSubtitle'),
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.onSurface.withOpacity(0.7),
                    ),
                  ),
                  const SizedBox(height: 24),
                  
                  // Language selection title
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.language,
                        size: 32,
                        color: AppTheme.primary,
                      ),
                      const SizedBox(width: 12),
                      Text(
                        Localization.get('selectLanguage'),
                        style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                        textAlign: TextAlign.center,
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  
                  // Language list
                  Expanded(
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppTheme.surface.withOpacity(0.3),
                        borderRadius: BorderRadius.circular(16),
                        border: Border.all(
                          color: AppTheme.primary.withOpacity(0.3),
                          width: 1,
                        ),
                      ),
                      padding: const EdgeInsets.symmetric(vertical: 8),
                      child: ListView.builder(
                      itemCount: languages.length,
                      itemBuilder: (context, index) {
                        final langCode = languages[index];
                        final langName = Localization.getLanguageName(langCode);
                        final isSelected = _selectedLanguage == langCode;
                        
                        return Padding(
                          padding: const EdgeInsets.symmetric(vertical: 6),
                          child: Material(
                            color: Colors.transparent,
                            child: InkWell(
                              onTap: () {
                                setState(() {
                                  _selectedLanguage = langCode;
                                  Localization.setLanguage(langCode);
                                });
                              },
                              borderRadius: BorderRadius.circular(12),
                              child: Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 20,
                                  vertical: 16,
                                ),
                                decoration: BoxDecoration(
                                  color: isSelected 
                                    ? AppTheme.primary.withOpacity(0.2)
                                    : AppTheme.surface,
                                  borderRadius: BorderRadius.circular(12),
                                  border: Border.all(
                                    color: isSelected 
                                      ? AppTheme.primary 
                                      : Colors.transparent,
                                    width: 2,
                                  ),
                                ),
                                child: Row(
                                  children: [
                                    Expanded(
                                      child: Text(
                                        langName,
                                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                          color: isSelected ? AppTheme.primary : AppTheme.onSurface,
                                          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                        ),
                                      ),
                                    ),
                                    if (isSelected)
                                      Icon(
                                        Icons.check_circle,
                                        color: AppTheme.primary,
                                        size: 28,
                                      ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        );
                      },
                      ),
                    ),
                  ),
                  
                  const SizedBox(height: 24),
                  
                  // Continue button
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _selectedLanguage != null ? _continueToApp : null,
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 20),
                      ),
                      child: Text(
                        Localization.get('continue'),
                        style: const TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

// Splash Screen
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  double _progress = 0.0;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeIn),
    );
    
    _startSplash();
  }

  Future<void> _startSplash() async {
    await Future.delayed(const Duration(milliseconds: 200));
    _controller.forward();
    
    // Animate progress
    for (int i = 0; i <= 100; i += 10) {
      await Future.delayed(const Duration(milliseconds: 200));
      if (mounted) {
        setState(() => _progress = i / 100);
      }
    }
    
    await Future.delayed(const Duration(milliseconds: 500));
    if (mounted) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const SetupScreen()),
      );
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Logo
              Container(
                width: 140,
                height: 140,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(28),
                  image: const DecorationImage(
                    image: AssetImage('assets/icon.png'),
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              const SizedBox(height: 24),
              
              // Title
              Text(
                Localization.get('splashTitle'),
                style: Theme.of(context).textTheme.displayLarge,
              ),
              const SizedBox(height: 8),
              
              // Subtitle
              Text(
                Localization.get('splashSubtitle'),
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: AppTheme.onSurface.withOpacity(0.7),
                ),
              ),
              const SizedBox(height: 48),
              
              // Progress
              SizedBox(
                width: 240,
                child: Column(
                  children: [
                    LinearProgressIndicator(value: _progress),
                    const SizedBox(height: 12),
                    Text(
                      Localization.get('initializing'),
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.onSurface.withOpacity(0.6),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// Setup Screen
class SetupScreen extends StatelessWidget {
  const SetupScreen({super.key});

  void _openVideoTutorial(BuildContext context) async {
    try {
      // Open video with Windows default player
      final videoPath = r'C:\Users\HM\Documents\AppLimit\logisiel\Developer options USB debugging.mp4';
      final shell = Shell();
      await shell.run('cmd /c start "" "$videoPath"');
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error opening video: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    children: [
                      const SizedBox(height: 20),
                      
                      // Phone Icon
                      const Icon(
                        Icons.smartphone,
                        size: 96,
                        color: AppTheme.primary,
                      ),
                      const SizedBox(height: 28),
                      
                      // Title
                      Text(
                        Localization.get('setupTitle'),
                        style: Theme.of(context).textTheme.headlineLarge,
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 20),
                      
                      // Description
                      Text(
                        Localization.get('setupDescription'),
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          color: AppTheme.onSurface.withOpacity(0.85),
                        ),
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 28),
                      
                      // Instructions Card
                      Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                Localization.get('steps'),
                                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                  color: AppTheme.primary,
                                ),
                              ),
                              const SizedBox(height: 12),
                              Text(
                                Localization.get('instructions'),
                                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                  height: 1.8),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              
              // Buttons
              Column(
                children: [
                  // Video Button
                  SizedBox(
                    width: double.infinity,
                    child: FilledButton.icon(
                      onPressed: () => _openVideoTutorial(context),
                      icon: const Icon(Icons.play_circle_outline),
                      label: Text(Localization.get('watchTutorial')),
                    ),
                  ),
                  const SizedBox(height: 16),
                  
                  // Continue Button
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(builder: (_) => const DeviceDetectionScreen()),
                        );
                      },
                      child: Text(Localization.get('continue')),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}



// Device Detection Screen
class DeviceDetectionScreen extends StatefulWidget {
  const DeviceDetectionScreen({super.key});

  @override
  State<DeviceDetectionScreen> createState() => _DeviceDetectionScreenState();
}

class _DeviceDetectionScreenState extends State<DeviceDetectionScreen> with SingleTickerProviderStateMixin {
  bool _deviceConnected = false;
  String _deviceName = '';
  late AnimationController _pulseController;
  Timer? _detectionTimer;

  @override
  void initState() {
    super.initState();
    _pulseController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    )..repeat(reverse: true);
    
    _startDetection();
  }

  void _startDetection() {
    _detectionTimer = Timer.periodic(const Duration(seconds: 2), (_) async {
      final deviceInfo = await _checkAdbDevice();
      if (deviceInfo['connected'] && !_deviceConnected) {
        setState(() {
          _deviceConnected = true;
          _deviceName = deviceInfo['name'] ?? '';
        });
        _detectionTimer?.cancel();
        
        // Animate checkmark
        await Future.delayed(const Duration(milliseconds: 600));
      }
    });
  }

  Future<Map<String, dynamic>> _checkAdbDevice() async {
    try {
      // Check for adb in platform tools
      String adbPath = 'adb';
      if (Platform.isWindows) {
        // Try common paths
        final paths = [
          r'C:\platform-tools\adb.exe',
          r'C:\Android\platform-tools\adb.exe',
          '${Platform.environment['LOCALAPPDATA']}\\Android\\Sdk\\platform-tools\\adb.exe',
        ];
        for (final path in paths) {
          if (File(path).existsSync()) {
            adbPath = path;
            break;
          }
        }
      }
      
      final shell = Shell();
      final result = await shell.run('$adbPath devices -l');
      final output = result.outText;
      
      // Check if device is connected
      if (output.contains('device') && !output.contains('offline') && !output.contains('unauthorized')) {
        // Extract device name from output
        // Format: "SERIAL device product:MODEL model:NAME device:CODE"
        final lines = output.split('\n');
        for (final line in lines) {
          if (line.contains('device') && !line.contains('List of')) {
            // Try to extract model name
            final modelMatch = RegExp(r'model:([^\s]+)').firstMatch(line);
            final productMatch = RegExp(r'product:([^\s]+)').firstMatch(line);
            
            String deviceName = modelMatch?.group(1) ?? productMatch?.group(1) ?? 'Android Device';
            return {'connected': true, 'name': deviceName};
          }
        }
        return {'connected': true, 'name': 'Android Device'};
      }
      return {'connected': false, 'name': ''};
    } catch (e) {
      return {'connected': false, 'name': ''};
    }
  }

  @override
  void dispose() {
    _pulseController.dispose();
    _detectionTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Device Icon
                AnimatedBuilder(
                  animation: _pulseController,
                  builder: (context, child) {
                    return Transform.scale(
                      scale: 1.0 + (_pulseController.value * 0.1),
                      child: Icon(
                        Icons.smartphone,
                        size: 108,
                        color: _deviceConnected ? AppTheme.success : AppTheme.primary,
                      ),
                    );
                  },
                ),
                const SizedBox(height: 36),
                
                // Title
                Text(
                  Localization.get('detectTitle'),
                  style: Theme.of(context).textTheme.headlineLarge,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 36),
                
                // Status
                Text(
                  _deviceConnected 
                    ? Localization.get('detectConnected')
                    : Localization.get('detectWaiting'),
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    color: AppTheme.onSurface.withOpacity(0.75),
                  ),
                  textAlign: TextAlign.center,
                ),
                
                // Device name
                if (_deviceConnected && _deviceName.isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(top: 12),
                    child: Text(
                      _deviceName,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                const SizedBox(height: 20),
                
                // Checkmark
                if (_deviceConnected)
                  TweenAnimationBuilder<double>(
                    tween: Tween(begin: 0.0, end: 1.0),
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.elasticOut,
                    builder: (context, value, child) {
                      return Transform.scale(
                        scale: value,
                        child: const Icon(
                          Icons.check_circle,
                          size: 72,
                          color: AppTheme.success,
                        ),
                      );
                    },
                  ),
                
                const Spacer(),
                
                // Continue Button
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _deviceConnected
                      ? () {
                          Navigator.of(context).push(
                            MaterialPageRoute(builder: (_) => const ProcessingScreen()),
                          );
                        }
                      : null,
                    child: Text(Localization.get('continue')),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// Processing Screen
class ProcessingScreen extends StatefulWidget {
  const ProcessingScreen({super.key});

  @override
  State<ProcessingScreen> createState() => _ProcessingScreenState();
}

class _ProcessingScreenState extends State<ProcessingScreen> with SingleTickerProviderStateMixin {
  double _progress = 0.0;
  bool _started = false;
  late AnimationController _rotationController;

  @override
  void initState() {
    super.initState();
    _rotationController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat();
  }

  Future<String?> _downloadAndSetupAdb() async {
    try {
      // Check if ADB already exists in appdata
      final appDataPath = Platform.environment['APPDATA'] ?? '';
      final adbCacheDir = '$appDataPath\\AppLimit\\platform-tools';
      final adbExePath = '$adbCacheDir\\adb.exe';
      
      if (File(adbExePath).existsSync()) {
        return adbExePath;
      }
      
      if (mounted) {
        setState(() => _progress = 0.05);
      }
      
      // Create directory
      await Directory(adbCacheDir).create(recursive: true);
      
      // Download using PowerShell
      final downloadUrl = 'https://dl.google.com/android/repository/platform-tools-latest-windows.zip';
      final zipPath = '$adbCacheDir\\platform-tools.zip';
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Downloading Android Platform Tools... Please wait'),
            duration: Duration(seconds: 60),
          ),
        );
      }
      
      // Download with PowerShell - properly escaped
      final downloadCmd = r'''
(New-Object System.Net.WebClient).DownloadFile('https://dl.google.com/android/repository/platform-tools-latest-windows.zip', '{zip_path}')
'''.replaceAll('{zip_path}', zipPath.replaceAll('\\', '\\\\'));
      
      final shell = Shell();
      await shell.run('powershell -NoProfile -ExecutionPolicy Bypass -Command "$downloadCmd"');
      
      if (!File(zipPath).existsSync()) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Download failed - check internet connection')),
          );
        }
        return null;
      }
      
      if (mounted) {
        setState(() => _progress = 0.08);
      }
      
      // Extract ZIP using PowerShell
      final extractCmd = r'''
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory('{zip}', '{dir}')
'''.replaceAll('{zip}', zipPath.replaceAll('\\', '\\\\')).replaceAll('{dir}', adbCacheDir.replaceAll('\\', '\\\\'));
      
      await shell.run('powershell -NoProfile -ExecutionPolicy Bypass -Command "$extractCmd"');
      
      if (mounted) {
        setState(() => _progress = 0.09);
      }
      
      // Move adb.exe to correct location
      final extractedAdbPath = '$adbCacheDir\\platform-tools\\adb.exe';
      if (File(extractedAdbPath).existsSync()) {
        await File(extractedAdbPath).copy(adbExePath);
        
        // Cleanup
        try {
          await File(zipPath).delete();
        } catch (e) {
          // Cleanup failed, but that's okay
        }
        
        return adbExePath;
      }
      
      return null;
    } catch (e) {
      print('ADB Download Error: $e');
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
      return null;
    }
  }

  Future<void> _executeGrayscale() async {
    setState(() => _started = true);
    
    String? adbPath;
    if (Platform.isWindows) {
      final paths = [
        r'C:\platform-tools\adb.exe',
        r'C:\Android\platform-tools\adb.exe',
        '${Platform.environment['LOCALAPPDATA']}\\Android\\Sdk\\platform-tools\\adb.exe',
        '${Platform.environment['USERPROFILE']}\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe',
        '${Platform.environment['ProgramFiles']}\\Android\\android-sdk\\platform-tools\\adb.exe',
        '${Platform.environment['ProgramFiles(x86)']}\\Android\\android-sdk\\platform-tools\\adb.exe',
      ];
      
      for (final path in paths) {
        if (File(path).existsSync()) {
          adbPath = path;
          break;
        }
      }
      
      // If not found in standard paths, check if it's in PATH
      if (adbPath == null) {
        try {
          final shell = Shell();
          final result = await shell.run('where.exe adb');
          if (result.outText.isNotEmpty) {
            adbPath = result.outText.split('\n').first.trim();
          }
        } catch (e) {
          // where.exe failed, adb not in PATH
        }
      }
      
      // If still not found, download automatically
      if (adbPath == null) {
        adbPath = await _downloadAndSetupAdb();
      }
      
      // If download also failed, show helpful error
      if (adbPath == null) {
        if (mounted) {
          setState(() {
            _started = false;
            _progress = 0.0;
          });
          
          await showDialog(
            context: context,
            builder: (context) => AlertDialog(
              title: const Row(
                children: [
                  Icon(Icons.error_outline, color: Colors.red, size: 32),
                  SizedBox(width: 16),
                  Text('Connection Error'),
                ],
              ),
              content: const Text(
                'Failed to download Android Platform Tools.\n\n'
                'Please check your internet connection and try again.\n\n'
                'Or download manually from:\n'
                'https://developer.android.com/tools/releases/platform-tools',
                style: TextStyle(height: 1.5),
              ),
              actions: [
                FilledButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('OK'),
                ),
              ],
            ),
          );
        }
        return;
      }
    } else {
      adbPath = 'adb';
    }
    
    // At this point adbPath is guaranteed to be non-null
    final String adb = adbPath;
    
    final shell = Shell();
    
    try {
      // Step 1: Check if app is installed (10%)
      await Future.delayed(const Duration(milliseconds: 200));
      setState(() => _progress = 0.1);
      
      // Check if device is connected first
      final deviceCheck = await shell.run('$adb devices');
      if (!deviceCheck.outText.contains('device') || deviceCheck.outText.split('\n').length < 3) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('No device connected. Please connect your Android device.')),
          );
        }
        return;
      }
      
      // Check if app is installed
      final checkResult = await shell.run('$adb shell pm list packages');
      final isInstalled = checkResult.outText.contains('com.applimit');
      
      if (!isInstalled) {
        // Automatically install APK without asking
        setState(() => _progress = 0.2);
        
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Installing AppLimit app... Please wait'),
              duration: Duration(seconds: 30),
            ),
          );
        }
        
        // Try Release APK first, then Debug APK
        var apkPath = r'C:\Users\HM\Documents\AppLimit\AppLimit\app\build\outputs\apk\release\app-release.apk';
        
        if (!File(apkPath).existsSync()) {
          // Try debug APK if release not found
          apkPath = r'C:\Users\HM\Documents\AppLimit\AppLimit\app\build\outputs\apk\debug\app-debug.apk';
        }
        
        if (!File(apkPath).existsSync()) {
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('APK file not found: $apkPath')),
            );
          }
          return;
        }
        
        try {
          final installResult = await shell.run('$adb install -r "$apkPath"');
          print('Install APK result:');
          print('STDOUT: ${installResult.outText}');
          print('STDERR: ${installResult.errText}');
          
          if (installResult.outText.toLowerCase().contains('failure') || 
              installResult.outText.toLowerCase().contains('error')) {
            if (mounted) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Installation failed: ${installResult.outText}')),
              );
            }
            return;
          }
        } catch (e) {
          print('Install APK error: $e');
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('Installation error: $e')),
            );
          }
          return;
        }
        
        await Future.delayed(const Duration(milliseconds: 1000));
      }
      
      setState(() => _progress = 0.3);
      
      // Verify app is installed and running
      await Future.delayed(const Duration(milliseconds: 300));
      try {
        final verifyResult = await shell.run('$adb shell pm list packages | findstr com.applimit');
        print('Verify installation: ${verifyResult.outText}');
        
        if (!verifyResult.outText.contains('com.applimit')) {
          print('App still not installed after install command!');
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Warning: App installation may have failed')),
            );
          }
        }
      } catch (e) {
        print('Verify installation error: $e');
      }
      
      // Step 2: Grant WRITE_SECURE_SETTINGS permission (40%)
      await Future.delayed(const Duration(milliseconds: 300));
      try {
        // Note: This requires the app to be a system app or the device to be rooted
        // For non-system apps, we'll use a workaround
        final grantResult = await shell.run('$adb shell "pm grant com.applimit android.permission.WRITE_SECURE_SETTINGS"');
        print('Grant WRITE_SECURE_SETTINGS result: ${grantResult.outText}${grantResult.errText}');
      } catch (e) {
        print('Grant WRITE_SECURE_SETTINGS error: $e');
      }
      setState(() => _progress = 0.5);
      
      // Step 3: Grant SYSTEM_ALERT_WINDOW permission (60%)
      await Future.delayed(const Duration(milliseconds: 300));
      try {
        final appopsResult = await shell.run('$adb shell "appops set com.applimit SYSTEM_ALERT_WINDOW allow"');
        print('Grant SYSTEM_ALERT_WINDOW result: ${appopsResult.outText}${appopsResult.errText}');
      } catch (e) {
        print('Grant SYSTEM_ALERT_WINDOW error: $e');
      }
      setState(() => _progress = 0.7);
      
      // Step 4: Open accessibility settings for manual enabling
      await Future.delayed(const Duration(milliseconds: 300));
      try {
        final settingsResult = await shell.run('$adb shell "am start -a android.settings.ACCESSIBILITY_SETTINGS"');
        print('Open Accessibility Settings result: ${settingsResult.outText}${settingsResult.errText}');
      } catch (e) {
        print('Open Accessibility Settings error: $e');
      }
      setState(() => _progress = 0.9);
      
      // Step 5: Complete (100%)
      await Future.delayed(const Duration(milliseconds: 500));
      setState(() => _progress = 1.0);
      
      await Future.delayed(const Duration(milliseconds: 500));
      if (mounted) {
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (_) => const SuccessScreen()),
        );
      }
    } catch (e) {
      // Handle error
      if (mounted) {
        setState(() {
          _started = false;
          _progress = 0.0;
        });
        
        String errorMessage = 'Error: ${e.toString()}';
        if (e.toString().contains('device')) {
          errorMessage = 'Device connection error. Please ensure:\n1. USB debugging is enabled\n2. Device is connected\n3. You authorized the computer on your phone';
        } else if (e.toString().contains('adb')) {
          errorMessage = 'ADB error. Please check if Android platform-tools are installed.';
        }
        
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: const Icon(Icons.error_outline, color: Colors.red, size: 64),
            content: Text(errorMessage, textAlign: TextAlign.center),
            actions: [
              FilledButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('OK'),
              ),
            ],
          ),
        );
      }
    }
  }

  @override
  void dispose() {
    _rotationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Processing Icon
                AnimatedBuilder(
                  animation: _rotationController,
                  builder: (context, child) {
                    return Transform.rotate(
                      angle: _rotationController.value * 2 * 3.14159,
                      child: const Icon(
                        Icons.settings,
                        size: 96,
                        color: AppTheme.primary,
                      ),
                    );
                  },
                ),
                const SizedBox(height: 36),
                
                // Title
                Text(
                  Localization.get('processTitle'),
                  style: Theme.of(context).textTheme.headlineLarge,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 40),
                
                // Progress
                if (_started) ...[
                  SizedBox(
                    width: 320,
                    child: Column(
                      children: [
                        LinearProgressIndicator(value: _progress),
                        const SizedBox(height: 24),
                        
                        Text(
                          '${(_progress * 100).toInt()}%',
                          style: Theme.of(context).textTheme.displayMedium?.copyWith(
                            color: AppTheme.primary,
                          ),
                        ),
                        const SizedBox(height: 24),
                        
                        Text(
                          Localization.get('processing'),
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.onSurface.withOpacity(0.75),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
                
                const Spacer(),
                
                // Start Button
                if (!_started)
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _executeGrayscale,
                      child: Text(Localization.get('start')),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// Success Screen
class SuccessScreen extends StatefulWidget {
  const SuccessScreen({super.key});

  @override
  State<SuccessScreen> createState() => _SuccessScreenState();
}

class _SuccessScreenState extends State<SuccessScreen> with TickerProviderStateMixin {
  late AnimationController _scaleController;
  late AnimationController _fadeController;
  late Animation<double> _scaleAnimation;
  late Animation<double> _fadeAnimation;
  
  @override
  void initState() {
    super.initState();
    
    _scaleController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _scaleAnimation = CurvedAnimation(
      parent: _scaleController,
      curve: Curves.elasticOut,
    );
    
    _fadeAnimation = CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeIn,
    );
    
    // Start animations
    _scaleController.forward();
    Future.delayed(const Duration(milliseconds: 200), () {
      _fadeController.forward();
    });
  }
  
  @override
  void dispose() {
    _scaleController.dispose();
    _fadeController.dispose();
    super.dispose();
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              AppTheme.background,
              AppTheme.success.withOpacity(0.1),
            ],
          ),
        ),
        child: SafeArea(
          child: Center(
            child: Padding(
              padding: const EdgeInsets.all(32),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Success Icon with scale animation
                  ScaleTransition(
                    scale: _scaleAnimation,
                    child: Container(
                      padding: const EdgeInsets.all(24),
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: AppTheme.success.withOpacity(0.2),
                      ),
                      child: const Icon(
                        Icons.check_circle,
                        size: 108,
                        color: AppTheme.success,
                      ),
                    ),
                  ),
                  const SizedBox(height: 28),
                  
                  // Title with fade animation
                  FadeTransition(
                    opacity: _fadeAnimation,
                    child: Text(
                      Localization.get('successTitle'),
                      style: Theme.of(context).textTheme.displaySmall?.copyWith(
                        color: AppTheme.success,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  const SizedBox(height: 20),
                  
                  // Message
                  FadeTransition(
                    opacity: _fadeAnimation,
                    child: Text(
                      Localization.get('successMessage'),
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        color: AppTheme.onSurface.withOpacity(0.85),
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  const SizedBox(height: 24),
                  
                  // Detail Card
                  FadeTransition(
                    opacity: _fadeAnimation,
                    child: Card(
                      elevation: 4,
                      child: Padding(
                        padding: const EdgeInsets.all(24),
                        child: Column(
                          children: [
                            Icon(
                              Icons.celebration,
                              size: 48,
                              color: AppTheme.primary,
                            ),
                            const SizedBox(height: 16),
                            Text(
                              Localization.get('successDetail'),
                              style: Theme.of(context).textTheme.bodyMedium,
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  
                  const Spacer(),
                  
                  // Done Button
                  FadeTransition(
                    opacity: _fadeAnimation,
                    child: SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () {
                          // Close the application
                          if (Platform.isWindows) {
                            exit(0);
                          } else {
                            SystemNavigator.pop();
                          }
                        },
                        icon: const Icon(Icons.done_all),
                        label: Text(Localization.get('done')),
                        style: ElevatedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 20),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
