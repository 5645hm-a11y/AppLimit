using System;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Globalization;

namespace GrayscaleDesktopApp
{
    public partial class MainWindow : Window
    {
        private int currentStep = 0;
        private bool deviceConnected = false;
        private CancellationTokenSource deviceDetectionCts;

        public MainWindow()
        {
            try
            {
                InitializeComponent();
                
                // Set language
                try
                {
                    Localization.SetLanguage(CultureInfo.CurrentCulture.Name);
                }
                catch
                {
                    Localization.SetLanguage("en-US");
                }

                // Hide logo for now
                if (LogoImage != null)
                    LogoImage.Visibility = Visibility.Collapsed;

                Loaded += async (s, e) => 
                {
                    try
                    {
                        await DisplaySplashScreen();
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show($"Display Error: {ex.Message}");
                    }
                };
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Constructor Error: {ex.Message}\n{ex.StackTrace}");
                throw;
            }
        }

        private async Task DisplaySplashScreen()
        {
            await Task.Delay(200); // Brief delay for window render

            // Animate logo
            if (LogoImage != null)
            {
                AnimateOpacity(LogoImage, 0, 1, 800, QuadraticEase());
                ScaleAnimation(LogoImage, 0.8, 1, 800, QuadraticEase());
            }

            // Animate title
            if (SplashTitle != null)
            {
                SplashTitle.Text = "AppLimit";
                AnimateOpacity(SplashTitle, 0, 1, 900, QuadraticEase());
                await Task.Delay(100);
            }

            // Animate subtitle
            if (SplashSubtitle != null)
            {
                AnimateOpacity(SplashSubtitle, 0, 1, 800, QuadraticEase());
            }

            // Animate progress
            if (SplashProgress != null && InitText != null)
            {
                AnimateOpacity(InitText, 0, 1, 600, QuadraticEase());
                await AnimateProgressAsync(SplashProgress, 0, 100, 2000);
            }

            await Task.Delay(500);

            // Fade out splash
            FadeOutElement(SplashScreen, 400);
            await Task.Delay(500);

            // Show step 1
            ShowStep(1);
        }

        private void ShowStep(int step)
        {
            currentStep = step;

            // Hide all steps
            HideAllSteps();

            Grid currentGrid = step switch
            {
                1 => Step1,
                2 => Step2,
                3 => Step3,
                4 => Step4,
                _ => Step1
            };

            // Update localized text
            UpdateLocalizedText(step);

            currentGrid.Visibility = Visibility.Visible;
            FadeInElement(currentGrid, 500, QuadraticEase());

            if (step == 2)
            {
                _ = DetectDevice();
            }
        }

        private void UpdateLocalizedText(int step)
        {
            try
            {
                switch (step)
                {
                    case 1:
                        if (Title1 != null) Title1.Text = Localization.Get("EnableDebugMode");
                        if (Desc1 != null) Desc1.Text = Localization.Get("EnableDebugDesc");
                        if (StepsLabel != null) StepsLabel.Text = Localization.Get("Steps");
                        if (Instructions1 != null) Instructions1.Text = Localization.Get("Step1Instructions");
                        if (VideoButtonText != null) VideoButtonText.Text = Localization.Get("WatchTutorial");
                        if (Step1Button != null) Step1Button.Content = Localization.Get("IveEnabledIt");
                        break;

                    case 2:
                        if (Title2 != null) Title2.Text = Localization.Get("ConnectDevice");
                        if (DeviceStatusText != null) DeviceStatusText.Text = Localization.Get("WaitingForDevice");
                        if (Step2Button != null) Step2Button.Content = Localization.Get("ContinueButton");
                        break;

                    case 3:
                        if (Title3 != null) Title3.Text = Localization.Get("EnablingGrayscale");
                        if (ProgressStatus != null) ProgressStatus.Text = Localization.Get("ConnectingToDevice");
                        if (Step3Button != null) Step3Button.Content = Localization.Get("StartButton");
                        break;

                    case 4:
                        if (Title4 != null) Title4.Text = Localization.Get("Success");
                        if (Desc4 != null) Desc4.Text = Localization.Get("GrayscaleModeEnabled");
                        if (Detail4 != null) Detail4.Text = Localization.Get("SuccessDetail");
                        if (Step4Button != null) Step4Button.Content = Localization.Get("DoneButton");
                        break;
                }
            }
            catch (Exception ex)
            {
                // Silently ignore localization errors
            }
        }

        private void HideAllSteps()
        {
            try
            {
                if (Step1 != null) Step1.Visibility = Visibility.Collapsed;
                if (Step2 != null) Step2.Visibility = Visibility.Collapsed;
                if (Step3 != null) Step3.Visibility = Visibility.Collapsed;
                if (Step4 != null) Step4.Visibility = Visibility.Collapsed;
            }
            catch { }
        }

        private void OnStep1Complete(object sender, RoutedEventArgs e)
        {
            FadeOutElement(Step1, 300);
            Task.Delay(350).ContinueWith(_ => Dispatcher.Invoke(() => ShowStep(2)));
        }

        private void OnStep2Complete(object sender, RoutedEventArgs e)
        {
            FadeOutElement(Step2, 300);
            Task.Delay(350).ContinueWith(_ => Dispatcher.Invoke(() => ShowStep(3)));
        }

        private void OnStep3Complete(object sender, RoutedEventArgs e)
        {
            Step3Button.IsEnabled = false;
            _ = ExecuteGrayscaleCommand();
        }

        private void OnStep4Complete(object sender, RoutedEventArgs e)
        {
            Application.Current.Shutdown();
        }

        private void OnWatchVideo(object sender, RoutedEventArgs e)
        {
            try
            {
                // Try to play tutorial video from logisiel folder
                string videoPath = System.IO.Path.Combine(
                    System.IO.Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location),
                    "tutorial.mp4"
                );

                if (System.IO.File.Exists(videoPath))
                {
                    ProcessStartInfo psi = new()
                    {
                        FileName = videoPath,
                        UseShellExecute = true
                    };
                    Process.Start(psi);
                }
                else
                {
                    MessageBox.Show("Tutorial video not found.", "Video", MessageBoxButton.OK, MessageBoxImage.Information);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Could not open video: {ex.Message}", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private async Task DetectDevice()
        {
            deviceDetectionCts = new CancellationTokenSource();
            var token = deviceDetectionCts.Token;

            while (!token.IsCancellationRequested && !deviceConnected)
            {
                try
                {
                    if (await CheckDeviceConnected())
                    {
                        deviceConnected = true;
                        Dispatcher.Invoke(() =>
                        {
                            DeviceStatusText.Text = "Device Found!";
                            AnimateOpacity(CheckMarkBox, 0, 1, 600, QuadraticEase());
                            ScaleAnimation(CheckMarkBox, 0.5, 1, 600, QuadraticEase());
                            Step2Button.IsEnabled = true;
                        });
                        break;
                    }
                }
                catch { }

                await Task.Delay(1000);
            }
        }

        private async Task<bool> CheckDeviceConnected()
        {
            try
            {
                string adbPath = GetAdbPath();
                if (string.IsNullOrEmpty(adbPath)) return false;

                var result = await ExecuteCommandAsync(adbPath, "devices");
                return result.Contains("device") && !result.Contains("List of attached devices");
            }
            catch
            {
                return false;
            }
        }

        private async Task ExecuteGrayscaleCommand()
        {
            try
            {
                string adbPath = GetAdbPath();
                if (string.IsNullOrEmpty(adbPath))
                {
                    ShowError("ADB not found");
                    return;
                }

                // Animate progress
                for (int i = 0; i <= 100; i += 10)
                {
                    ProgressBar.Value = i;
                    ProgressText.Text = $"{i}%";
                    await Task.Delay(150);
                }

                // Enable grayscale
                await ExecuteCommandAsync(adbPath, "shell settings put secure accessibility_display_daltonizer_enabled 1");
                await Task.Delay(200);
                await ExecuteCommandAsync(adbPath, "shell settings put secure accessibility_display_daltonizer 0");

                // Complete progress
                ProgressBar.Value = 100;
                ProgressText.Text = "100%";
                await Task.Delay(500);

                // Show success screen
                FadeOutElement(Step3, 300);
                await Task.Delay(400);
                ShowStep(4);

                // Animate success icon
                await Task.Delay(400);
                if (SuccessIconBox != null)
                {
                    AnimateOpacity(SuccessIconBox, 0, 1, 500, BounceEase());
                    ScaleAnimation(SuccessIconBox, 0.5, 1, 500, BounceEase());
                }
            }
            catch (Exception ex)
            {
                ShowError($"Error executing command: {ex.Message}");
                Step3Button.IsEnabled = true;
            }
        }

        private string GetAdbPath()
        {
            // Check multiple common locations
            string[] possiblePaths = new[]
            {
                @"C:\Android\sdk\platform-tools\adb.exe",
                @"C:\Program Files\Android\sdk\platform-tools\adb.exe",
                @"C:\Program Files (x86)\Android\sdk\platform-tools\adb.exe",
                System.IO.Path.Combine(
                    Environment.GetEnvironmentVariable("LOCALAPPDATA") ?? "",
                    @"Android\Sdk\platform-tools\adb.exe"
                ),
            };

            foreach (var path in possiblePaths)
            {
                if (System.IO.File.Exists(path))
                    return path;
            }

            return null;
        }

        private async Task<string> ExecuteCommandAsync(string command, string arguments)
        {
            return await Task.Run(() =>
            {
                try
                {
                    using (var process = new Process())
                    {
                        process.StartInfo.FileName = command;
                        process.StartInfo.Arguments = arguments;
                        process.StartInfo.UseShellExecute = false;
                        process.StartInfo.RedirectStandardOutput = true;
                        process.StartInfo.CreateNoWindow = true;

                        process.Start();
                        string output = process.StandardOutput.ReadToEnd();
                        process.WaitForExit();

                        return output;
                    }
                }
                catch
                {
                    return string.Empty;
                }
            });
        }

        private void ShowError(string message)
        {
            Dispatcher.Invoke(() =>
            {
                MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            });
        }

        #region Animation Helpers

        private void AnimateOpacity(UIElement element, double from, double to, int duration, EasingFunctionBase easing = null)
        {
            var animation = new DoubleAnimation(from, to, new Duration(TimeSpan.FromMilliseconds(duration)))
            {
                EasingFunction = easing ?? new QuadraticEase()
            };
            element.BeginAnimation(OpacityProperty, animation);
        }

        private void FadeInElement(UIElement element, int duration = 400, EasingFunctionBase easing = null)
        {
            AnimateOpacity(element, 0, 1, duration, easing ?? new QuadraticEase());
        }

        private void FadeOutElement(UIElement element, int duration = 300, EasingFunctionBase easing = null)
        {
            AnimateOpacity(element, 1, 0, duration, easing ?? new QuadraticEase());
        }

        private void ScaleAnimation(UIElement element, double fromScale, double toScale, int duration, EasingFunctionBase easing = null)
        {
            var group = new TransformGroup();
            var scale = new System.Windows.Media.ScaleTransform(fromScale, fromScale);
            group.Children.Add(scale);
            element.RenderTransform = group;
            element.RenderTransformOrigin = new System.Windows.Point(0.5, 0.5);

            var animation = new DoubleAnimation(fromScale, toScale, new Duration(TimeSpan.FromMilliseconds(duration)))
            {
                EasingFunction = easing ?? new QuadraticEase()
            };

            scale.BeginAnimation(System.Windows.Media.ScaleTransform.ScaleXProperty, animation);
            scale.BeginAnimation(System.Windows.Media.ScaleTransform.ScaleYProperty, animation);
        }

        private async Task AnimateProgressAsync(ProgressBar progressBar, double from, double to, int duration)
        {
            int steps = 50;
            double step = (to - from) / steps;
            int delayPerStep = duration / steps;

            for (int i = 0; i < steps; i++)
            {
                progressBar.Value = from + (step * i);
                await Task.Delay(delayPerStep);
            }
            progressBar.Value = to;
        }

        private EasingFunctionBase QuadraticEase()
        {
            return new QuadraticEase() { EasingMode = EasingMode.EaseInOut };
        }

        private EasingFunctionBase BounceEase()
        {
            return new BackEase() { Amplitude = 0.5, EasingMode = EasingMode.EaseOut };
        }

        #endregion

        private void Window_Closed(object sender, EventArgs e)
        {
            deviceDetectionCts?.Cancel();
        }
    }
}
