using System;
using System.Collections.Generic;
using System.Globalization;

namespace GrayscaleDesktopApp
{
    public static class Localization
    {
        private static CultureInfo currentCulture = CultureInfo.GetCultureInfo("en-US");
        
        public static CultureInfo CurrentCulture
        {
            get => currentCulture;
            set => currentCulture = value ?? CultureInfo.GetCultureInfo("en-US");
        }

        public static List<(string Code, string Name)> AvailableLanguages => new()
        {
            ("en-US", "English"),
            ("es-ES", "Español"),
            ("fr-FR", "Français"),
            ("de-DE", "Deutsch"),
            ("it-IT", "Italiano"),
            ("pt-BR", "Português"),
            ("pl-PL", "Polski"),
            ("ru-RU", "Русский"),
            ("ja-JP", "日本語"),
            ("zh-CN", "中文"),
            ("ko-KR", "한국어"),
            ("ar-AE", "العربية"),
            ("he-IL", "עברית")
        };

        private static Dictionary<string, Dictionary<string, string>> Strings = new()
        {
            ["en-US"] = new()
            {
                ["AppTitle"] = "AppLimit - Grayscale Controller",
                ["EnableDebugMode"] = "Enable Debug Mode",
                ["EnableDebugDesc"] = "Enable USB Debugging on your Android device to continue",
                ["Steps"] = "Steps:",
                ["Step1Instructions"] = "1. Go to Settings > About Phone\n2. Tap Build Number 7 times\n3. Enable USB Debugging in Developer Options",
                ["WatchTutorial"] = "▶ Watch Tutorial",
                ["IveEnabledIt"] = "I've Enabled It",
                ["ConnectDevice"] = "Connect Your Device",
                ["WaitingForDevice"] = "Waiting for device...",
                ["ContinueButton"] = "Continue",
                ["EnablingGrayscale"] = "Enabling Grayscale",
                ["ConnectingToDevice"] = "Connecting to device...",
                ["StartButton"] = "Start",
                ["Success"] = "Success!",
                ["GrayscaleModeEnabled"] = "Grayscale mode has been enabled on your device!",
                ["SuccessDetail"] = "Your device will now display in grayscale mode. You can disable this anytime in Accessibility Settings.",
                ["DoneButton"] = "Done",
                ["Initializing"] = "Initializing...",
                ["InitializingPercent"] = "Initializing...",
            },
            ["es-ES"] = new()
            {
                ["AppTitle"] = "AppLimit - Controlador de Escala de Grises",
                ["EnableDebugMode"] = "Habilitar Modo de Depuración",
                ["EnableDebugDesc"] = "Habilita la depuración USB en tu dispositivo Android para continuar",
                ["Steps"] = "Pasos:",
                ["Step1Instructions"] = "1. Ve a Configuración > Acerca del Teléfono\n2. Toca Número de Compilación 7 veces\n3. Habilita Depuración USB en Opciones de Desarrollador",
                ["WatchTutorial"] = "▶ Ver Tutorial",
                ["IveEnabledIt"] = "Ya lo Habilitéé",
                ["ConnectDevice"] = "Conecta tu Dispositivo",
                ["WaitingForDevice"] = "Esperando dispositivo...",
                ["ContinueButton"] = "Continuar",
                ["EnablingGrayscale"] = "Habilitando Escala de Grises",
                ["ConnectingToDevice"] = "Conectando al dispositivo...",
                ["StartButton"] = "Iniciar",
                ["Success"] = "¡Éxito!",
                ["GrayscaleModeEnabled"] = "¡El modo de escala de grises se ha habilitado en tu dispositivo!",
                ["SuccessDetail"] = "Tu dispositivo ahora mostrará en modo de escala de grises. Puedes deshabilitarlo en cualquier momento en Configuración de Accesibilidad.",
                ["DoneButton"] = "Listo",
                ["Initializing"] = "Inicializando...",
                ["InitializingPercent"] = "Inicializando...",
            },
            ["fr-FR"] = new()
            {
                ["AppTitle"] = "AppLimit - Contrôleur d'Échelle de Gris",
                ["EnableDebugMode"] = "Activer le Mode Débogage",
                ["EnableDebugDesc"] = "Activez la Débogage USB sur votre appareil Android pour continuer",
                ["Steps"] = "Étapes :",
                ["Step1Instructions"] = "1. Allez à Paramètres > À Propos du Téléphone\n2. Appuyez sur Numéro de Build 7 fois\n3. Activez Débogage USB dans Options pour Développeurs",
                ["WatchTutorial"] = "▶ Regarder le Tutoriel",
                ["IveEnabledIt"] = "Je l'ai Activé",
                ["ConnectDevice"] = "Connectez Votre Appareil",
                ["WaitingForDevice"] = "En attente de l'appareil...",
                ["ContinueButton"] = "Continuer",
                ["EnablingGrayscale"] = "Activation de l'Échelle de Gris",
                ["ConnectingToDevice"] = "Connexion à l'appareil...",
                ["StartButton"] = "Démarrer",
                ["Success"] = "Succès !",
                ["GrayscaleModeEnabled"] = "Le mode de l'échelle de gris a été activé sur votre appareil !",
                ["SuccessDetail"] = "Votre appareil affichera désormais en mode d'échelle de gris. Vous pouvez le désactiver à tout moment dans les paramètres d'accessibilité.",
                ["DoneButton"] = "Terminé",
                ["Initializing"] = "Initialisation...",
                ["InitializingPercent"] = "Initialisation...",
            },
            ["de-DE"] = new()
            {
                ["AppTitle"] = "AppLimit - Graustufenregler",
                ["EnableDebugMode"] = "Fehlersuchemodus Aktivieren",
                ["EnableDebugDesc"] = "Aktivieren Sie das USB-Debugging auf Ihrem Android-Gerät, um fortzufahren",
                ["Steps"] = "Schritte:",
                ["Step1Instructions"] = "1. Gehen Sie zu Einstellungen > Telefoninfo\n2. Tippen Sie 7-mal auf Build-Nummer\n3. Aktivieren Sie USB-Debugging in Entwickleroptionen",
                ["WatchTutorial"] = "▶ Anleitung Ansehen",
                ["IveEnabledIt"] = "Ich habe es Aktiviert",
                ["ConnectDevice"] = "Gerät Verbinden",
                ["WaitingForDevice"] = "Warte auf Gerät...",
                ["ContinueButton"] = "Weiter",
                ["EnablingGrayscale"] = "Graustufen Aktivieren",
                ["ConnectingToDevice"] = "Verbinde mit Gerät...",
                ["StartButton"] = "Start",
                ["Success"] = "Erfolg!",
                ["GrayscaleModeEnabled"] = "Graustufen auf Ihrem Gerät aktiviert!",
                ["SuccessDetail"] = "Ihr Gerät zeigt jetzt im Graustufen-Modus an. Sie können dies jederzeit in den Barrierefreiheitseinstellungen deaktivieren.",
                ["DoneButton"] = "Fertig",
                ["Initializing"] = "Initialisierung...",
                ["InitializingPercent"] = "Initialisierung...",
            },
            ["it-IT"] = new()
            {
                ["AppTitle"] = "AppLimit - Controllore Scala di Grigi",
                ["EnableDebugMode"] = "Abilita Modalità Debug",
                ["EnableDebugDesc"] = "Abilita il Debug USB sul tuo dispositivo Android per continuare",
                ["Steps"] = "Passaggi:",
                ["Step1Instructions"] = "1. Vai a Impostazioni > Informazioni Telefono\n2. Tocca Numero Build 7 volte\n3. Abilita Debug USB nelle Opzioni Sviluppatore",
                ["WatchTutorial"] = "▶ Guarda il Tutorial",
                ["IveEnabledIt"] = "L'ho Abilitato",
                ["ConnectDevice"] = "Connetti il Tuo Dispositivo",
                ["WaitingForDevice"] = "In attesa del dispositivo...",
                ["ContinueButton"] = "Continua",
                ["EnablingGrayscale"] = "Abilitazione Scala di Grigi",
                ["ConnectingToDevice"] = "Connessione al dispositivo...",
                ["StartButton"] = "Avvia",
                ["Success"] = "Successo!",
                ["GrayscaleModeEnabled"] = "La modalità scala di grigi è stata abilitata sul tuo dispositivo!",
                ["SuccessDetail"] = "Il tuo dispositivo ora visualizzerà in modalità scala di grigi. Puoi disabilitarla in qualsiasi momento nelle Impostazioni Accessibilità.",
                ["DoneButton"] = "Fatto",
                ["Initializing"] = "Inizializzazione...",
                ["InitializingPercent"] = "Inizializzazione...",
            },
            ["pt-BR"] = new()
            {
                ["AppTitle"] = "AppLimit - Controlador de Escala de Cinza",
                ["EnableDebugMode"] = "Ativar Modo de Depuração",
                ["EnableDebugDesc"] = "Ative Depuração USB no seu dispositivo Android para continuar",
                ["Steps"] = "Passos:",
                ["Step1Instructions"] = "1. Vá para Configurações > Sobre o Telefone\n2. Toque em Número da Build 7 vezes\n3. Ative Depuração USB nas Opções do Desenvolvedor",
                ["WatchTutorial"] = "▶ Assistir Tutorial",
                ["IveEnabledIt"] = "Eu Ativei",
                ["ConnectDevice"] = "Conecte seu Dispositivo",
                ["WaitingForDevice"] = "Aguardando dispositivo...",
                ["ContinueButton"] = "Continuar",
                ["EnablingGrayscale"] = "Ativando Escala de Cinza",
                ["ConnectingToDevice"] = "Conectando ao dispositivo...",
                ["StartButton"] = "Iniciar",
                ["Success"] = "Sucesso!",
                ["GrayscaleModeEnabled"] = "Modo de escala de cinza foi ativado no seu dispositivo!",
                ["SuccessDetail"] = "Seu dispositivo agora exibirá em modo de escala de cinza. Você pode desativá-lo a qualquer momento nas Configurações de Acessibilidade.",
                ["DoneButton"] = "Pronto",
                ["Initializing"] = "Inicializando...",
                ["InitializingPercent"] = "Inicializando...",
            },
            ["pl-PL"] = new()
            {
                ["AppTitle"] = "AppLimit - Kontroler Skali Szarości",
                ["EnableDebugMode"] = "Włącz Tryb Debugowania",
                ["EnableDebugDesc"] = "Włącz Debugowanie USB na urządzeniu Android, aby kontynuować",
                ["Steps"] = "Kroki:",
                ["Step1Instructions"] = "1. Przejdź do Ustawienia > O Telefonie\n2. Stuknij w Numer Kompilacji 7 razy\n3. Włącz Debugowanie USB w Opcjach Dewelopera",
                ["WatchTutorial"] = "▶ Obejrz Poradnik",
                ["IveEnabledIt"] = "Włączyłem to",
                ["ConnectDevice"] = "Podłącz Urządzenie",
                ["WaitingForDevice"] = "Oczekiwanie na urządzenie...",
                ["ContinueButton"] = "Kontynuuj",
                ["EnablingGrayscale"] = "Włączanie Skali Szarości",
                ["ConnectingToDevice"] = "Łączenie z urządzeniem...",
                ["StartButton"] = "Start",
                ["Success"] = "Sukces!",
                ["GrayscaleModeEnabled"] = "Tryb skali szarości został włączony na Twoim urządzeniu!",
                ["SuccessDetail"] = "Twoje urządzenie będzie teraz wyświetlane w trybie skali szarości. Możesz go wyłączyć w dowolnym momencie w Ustawieniach Ułatwień Dostępu.",
                ["DoneButton"] = "Gotowe",
                ["Initializing"] = "Inicjowanie...",
                ["InitializingPercent"] = "Inicjowanie...",
            },
            ["ru-RU"] = new()
            {
                ["AppTitle"] = "AppLimit - Контроллер Оттенков Серого",
                ["EnableDebugMode"] = "Включить Режим Отладки",
                ["EnableDebugDesc"] = "Включите отладку по USB на вашем устройстве Android, чтобы продолжить",
                ["Steps"] = "Шаги:",
                ["Step1Instructions"] = "1. Перейдите в Настройки > О Телефоне\n2. Нажмите на Номер Сборки 7 раз\n3. Включите Отладку по USB в Параметрах Разработчика",
                ["WatchTutorial"] = "▶ Смотреть Обучение",
                ["IveEnabledIt"] = "Я Включил",
                ["ConnectDevice"] = "Подключите Устройство",
                ["WaitingForDevice"] = "Ожидание устройства...",
                ["ContinueButton"] = "Продолжить",
                ["EnablingGrayscale"] = "Включение Оттенков Серого",
                ["ConnectingToDevice"] = "Подключение к устройству...",
                ["StartButton"] = "Начать",
                ["Success"] = "Успешно!",
                ["GrayscaleModeEnabled"] = "Режим оттенков серого включен на вашем устройстве!",
                ["SuccessDetail"] = "Ваше устройство теперь будет отображаться в режиме оттенков серого. Вы можете отключить его в любой момент в Параметрах Специальных Возможностей.",
                ["DoneButton"] = "Готово",
                ["Initializing"] = "Инициализация...",
                ["InitializingPercent"] = "Инициализация...",
            },
            ["ja-JP"] = new()
            {
                ["AppTitle"] = "AppLimit - グレースケールコントローラー",
                ["EnableDebugMode"] = "デバッグモードを有効にする",
                ["EnableDebugDesc"] = "Androidデバイスで USB デバッグを有効にして続行します",
                ["Steps"] = "ステップ:",
                ["Step1Instructions"] = "1. 設定 > 電話について に移動\n2. ビルド番号を7回タップ\n3. 開発者向けオプションで USB デバッグを有効にする",
                ["WatchTutorial"] = "▶ チュートリアルを見る",
                ["IveEnabledIt"] = "有効にしました",
                ["ConnectDevice"] = "デバイスを接続",
                ["WaitingForDevice"] = "デバイスを待機中...",
                ["ContinueButton"] = "続ける",
                ["EnablingGrayscale"] = "グレースケールを有効にする",
                ["ConnectingToDevice"] = "デバイスに接続中...",
                ["StartButton"] = "開始",
                ["Success"] = "成功!",
                ["GrayscaleModeEnabled"] = "デバイスでグレースケールモードが有効になりました!",
                ["SuccessDetail"] = "デバイスはグレースケールモードで表示されます。ユーザー補助設定でいつでも無効にすることができます。",
                ["DoneButton"] = "完了",
                ["Initializing"] = "初期化中...",
                ["InitializingPercent"] = "初期化中...",
            },
            ["zh-CN"] = new()
            {
                ["AppTitle"] = "AppLimit - 灰度控制器",
                ["EnableDebugMode"] = "启用调试模式",
                ["EnableDebugDesc"] = "在 Android 设备上启用 USB 调试以继续",
                ["Steps"] = "步骤:",
                ["Step1Instructions"] = "1. 转到设置 > 关于手机\n2. 点击编译号 7 次\n3. 在开发者选项中启用 USB 调试",
                ["WatchTutorial"] = "▶ 观看教程",
                ["IveEnabledIt"] = "我已启用",
                ["ConnectDevice"] = "连接您的设备",
                ["WaitingForDevice"] = "等待设备...",
                ["ContinueButton"] = "继续",
                ["EnablingGrayscale"] = "启用灰度",
                ["ConnectingToDevice"] = "正在连接到设备...",
                ["StartButton"] = "开始",
                ["Success"] = "成功!",
                ["GrayscaleModeEnabled"] = "已在您的设备上启用灰度模式!",
                ["SuccessDetail"] = "您的设备现在将以灰度模式显示。您可以随时在辅助功能设置中将其禁用。",
                ["DoneButton"] = "完成",
                ["Initializing"] = "初始化中...",
                ["InitializingPercent"] = "初始化中...",
            },
            ["ko-KR"] = new()
            {
                ["AppTitle"] = "AppLimit - 그레이스케일 컨트롤러",
                ["EnableDebugMode"] = "디버그 모드 활성화",
                ["EnableDebugDesc"] = "Android 기기에서 USB 디버깅을 활성화하여 계속하세요",
                ["Steps"] = "단계:",
                ["Step1Instructions"] = "1. 설정 > 휴대폰 정보로 이동\n2. 빌드 번호 7번 탭\n3. 개발자 옵션에서 USB 디버깅 활성화",
                ["WatchTutorial"] = "▶ 튜토리얼 보기",
                ["IveEnabledIt"] = "활성화했습니다",
                ["ConnectDevice"] = "기기를 연결하세요",
                ["WaitingForDevice"] = "기기 대기 중...",
                ["ContinueButton"] = "계속",
                ["EnablingGrayscale"] = "그레이스케일 활성화",
                ["ConnectingToDevice"] = "기기에 연결 중...",
                ["StartButton"] = "시작",
                ["Success"] = "성공!",
                ["GrayscaleModeEnabled"] = "기기에서 그레이스케일 모드가 활성화되었습니다!",
                ["SuccessDetail"] = "기기가 이제 그레이스케일 모드로 표시됩니다. 언제든지 접근성 설정에서 비활성화할 수 있습니다.",
                ["DoneButton"] = "완료",
                ["Initializing"] = "초기화 중...",
                ["InitializingPercent"] = "초기화 중...",
            },
            ["ar-AE"] = new()
            {
                ["AppTitle"] = "AppLimit - جهاز التحكم بدرجات الرمادي",
                ["EnableDebugMode"] = "تفعيل وضع التصحيح",
                ["EnableDebugDesc"] = "قم بتفعيل تصحيح أخطاء USB على جهاز Android الخاص بك للمتابعة",
                ["Steps"] = "الخطوات:",
                ["Step1Instructions"] = "1. انتقل إلى الإعدادات > حول الهاتف\n2. انقر على رقم الإصدار 7 مرات\n3. قم بتفعيل تصحيح أخطاء USB في خيارات المطور",
                ["WatchTutorial"] = "▶ مشاهدة البرنامج التعليمي",
                ["IveEnabledIt"] = "قمت بتفعيله",
                ["ConnectDevice"] = "قم بتوصيل جهازك",
                ["WaitingForDevice"] = "في انتظار الجهاز...",
                ["ContinueButton"] = "متابعة",
                ["EnablingGrayscale"] = "تفعيل درجات الرمادي",
                ["ConnectingToDevice"] = "جاري الاتصال بالجهاز...",
                ["StartButton"] = "ابدأ",
                ["Success"] = "نجاح!",
                ["GrayscaleModeEnabled"] = "تم تفعيل وضع درجات الرمادي على جهازك!",
                ["SuccessDetail"] = "سيتم الآن عرض جهازك في وضع درجات الرمادي. يمكنك تعطيله في أي وقت من إعدادات الوصول.",
                ["DoneButton"] = "تم",
                ["Initializing"] = "جاري التهيئة...",
                ["InitializingPercent"] = "جاري التهيئة...",
            },
            ["he-IL"] = new()
            {
                ["AppTitle"] = "AppLimit - בקר סקאלת אפור",
                ["EnableDebugMode"] = "הפוך למצב ניפוי שגיאות",
                ["EnableDebugDesc"] = "אפשר ניפוי שגיאות USB בהתקן Android שלך כדי להמשיך",
                ["Steps"] = "שלבים:",
                ["Step1Instructions"] = "1. עבור להגדרות > אודות הטלפון\n2. הקש על מספר הבנייה 7 פעמים\n3. אפשר ניפוי שגיאות USB באפשרויות המפתח",
                ["WatchTutorial"] = "▶ צפה בהדרכה",
                ["IveEnabledIt"] = "אפשרתי זאת",
                ["ConnectDevice"] = "חבר את ההתקן שלך",
                ["WaitingForDevice"] = "מחכה להתקן...",
                ["ContinueButton"] = "המשך",
                ["EnablingGrayscale"] = "הפוך סקאלת אפור",
                ["ConnectingToDevice"] = "מתחבר להתקן...",
                ["StartButton"] = "התחל",
                ["Success"] = "הצלחה!",
                ["GrayscaleModeEnabled"] = "מצב סקאלת אפור הופעל בהתקן שלך!",
                ["SuccessDetail"] = "ההתקן שלך יוצג כעת במצב סקאלת אפור. אתה יכול להשביתו בכל עת בהגדרות נגישות.",
                ["DoneButton"] = "בוצע",
                ["Initializing"] = "בתחילת עבודה...",
                ["InitializingPercent"] = "בתחילת עבודה...",
            },
        };

        public static string Get(string key)
        {
            try
            {
                if (Strings.TryGetValue(currentCulture.Name, out var langStrings))
                {
                    if (langStrings.TryGetValue(key, out var value))
                        return value;
                }
                
                // Fallback to English
                if (Strings.TryGetValue("en-US", out var engStrings))
                {
                    if (engStrings.TryGetValue(key, out var value))
                        return value;
                }
                
                return $"[{key}]";
            }
            catch
            {
                return $"[{key}]";
            }
        }

        public static void SetLanguage(string languageCode)
        {
            try
            {
                CurrentCulture = CultureInfo.GetCultureInfo(languageCode);
            }
            catch
            {
                CurrentCulture = CultureInfo.GetCultureInfo("en-US");
            }
        }
    }
}
