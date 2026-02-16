using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Windows;

namespace GrayscaleDesktopApp
{
    public partial class App : Application
    {
        private const string LogFile = @"C:\Users\HM\Documents\logisiel\debug_log.txt";
        
        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool AllocConsole();
        
        private static void Log(string message)
        {
            try
            {
                File.AppendAllText(LogFile, $"[{DateTime.Now:HH:mm:ss.fff}] {message}\r\n");
            }
            catch { }
        }

        protected override void OnStartup(StartupEventArgs e)
        {
            try
            {
                Log("OnStartup starting...");
                base.OnStartup(e);
                Log("OnStartup completed.");
            }
            catch (Exception ex)
            {
                Log($"Startup Error: {ex.Message}\n{ex.StackTrace}");
                MessageBox.Show($"Startup Error: {ex.Message}\n{ex.StackTrace}", "Critical Error");
            }

            DispatcherUnhandledException += (s, args) =>
            {
                Log($"Dispatcher Error: {args.Exception.Message}");
                MessageBox.Show($"Unhandled Error: {args.Exception.Message}", "Error");
                args.Handled = true;
            };
        }

        public App()
        {
            try
            {
                AllocConsole();
                if (File.Exists(LogFile)) File.Delete(LogFile);
                Log("App constructor starting...");
                InitializeComponent();
                Log("InitializeComponent completed.");
            }
            catch (Exception ex)
            {
                Log($"App Init Error: {ex.Message}\n{ex.StackTrace}");
                MessageBox.Show($"App Init Error: {ex.Message}");
            }
        }
    }
}
