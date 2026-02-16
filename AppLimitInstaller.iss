; Inno Setup Script for AppLimit Desktop Application
; This script creates a professional installer for Windows

[Setup]
AppName={cm:AppName}
AppVersion=1.0.0
AppPublisher=AppLimit Team
AppPublisherURL=https://applimit.io
AppSupportURL=https://applimit.io/support
AppUpdatesURL=https://applimit.io/updates
DefaultDirName={pf}\AppLimit
DefaultGroupName={cm:AppName}
OutputBaseFilename=AppLimit-Desktop-Setup
OutputDir=C:\Users\HM\Documents\AppLimit\Installers
Compression=lzma2
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
MinVersion=0,10.0.10240
LanguageDetectionMethod=locale
ShowLanguageDialog=yes
UsePreviousLanguage=no
CloseApplications=yes
RestartIfNeededByRun=yes
DisableProgramGroupPage=no
AllowNoIcons=yes
UninstallDisplayIcon={app}\AppLimit.exe
WizardStyle=modern

[Languages]
Name: "hebrew"; MessagesFile: "compiler:Languages\Hebrew.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"
Name: "italian"; MessagesFile: "compiler:Languages\Italian.isl"
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"
Name: "portuguese"; MessagesFile: "compiler:Languages\Portuguese.isl"
Name: "japanese"; MessagesFile: "compiler:Languages\Japanese.isl"
Name: "korean"; MessagesFile: "compiler:Languages\Korean.isl"
Name: "turkish"; MessagesFile: "compiler:Languages\Turkish.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"
Name: "dutch"; MessagesFile: "compiler:Languages\Dutch.isl"
Name: "swedish"; MessagesFile: "compiler:Languages\Swedish.isl"
Name: "danish"; MessagesFile: "compiler:Languages\Danish.isl"
Name: "norwegian"; MessagesFile: "compiler:Languages\Norwegian.isl"
Name: "finnish"; MessagesFile: "compiler:Languages\Finnish.isl"
Name: "hungarian"; MessagesFile: "compiler:Languages\Hungarian.isl"
Name: "czech"; MessagesFile: "compiler:Languages\Czech.isl"
Name: "slovak"; MessagesFile: "compiler:Languages\Slovak.isl"

[Types]
Name: "full"; Description: "{cm:InstallTypeFull}"
Name: "compact"; Description: "{cm:InstallTypeCompact}"
Name: "custom"; Description: "{cm:InstallTypeCustom}"; Flags: iscustom

[Components]
Name: "program"; Description: "{cm:ComponentMain}"; Types: full compact custom; Flags: fixed
Name: "associations"; Description: "{cm:ComponentAssociations}"; Types: full
Name: "desktopicon"; Description: "{cm:ComponentDesktopShortcut}"; Types: full
Name: "quicklaunch"; Description: "{cm:ComponentQuickLaunch}"; Types: full

[Files]
; Main application files
Source: "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\build\windows\x64\runner\Release\applimit_desktop.exe"; DestDir: "{app}"; DestName: "AppLimit.exe"; Components: program; Flags: ignoreversion
Source: "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\build\windows\x64\runner\Release\flutter_windows.dll"; DestDir: "{app}"; Components: program; Flags: ignoreversion
Source: "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\build\windows\x64\runner\Release\data\*"; DestDir: "{app}\data"; Components: program; Flags: ignoreversion recursesubdirs createallsubdirs

; Assets and resources
Source: "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\assets\icon.png"; DestDir: "{app}\assets"; Flags: ignoreversion
Source: "C:\Users\HM\Documents\AppLimit\MARKETING_POST.md"; DestDir: "{app}\docs"; DestName: "README.md"; Flags: ignoreversion

[Icons]
Name: "{group}\AppLimit Desktop"; Filename: "{app}\AppLimit.exe"; Comment: "AppLimit Desktop Control Panel"
Name: "{commondesktop}\AppLimit Desktop"; Filename: "{app}\AppLimit.exe"; Comment: "AppLimit Desktop Control Panel"; Components: desktopicon; Tasks: desktoptask
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\AppLimit"; Filename: "{app}\AppLimit.exe"; Components: quicklaunch

[Tasks]
Name: "desktoptask"; Description: "{cm:TaskDesktopIcon}"; GroupDescription: "{cm:TaskGroupAdditionalShortcuts}"; Components: desktopicon; Flags: unchecked

[Run]
Filename: "{app}\AppLimit.exe"; Description: "{cm:RunNow}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: dirifempty; Name: "{app}\data"
Type: dirifempty; Name: "{app}\assets"
Type: dirifempty; Name: "{app}\docs"
Type: dirifempty; Name: "{app}"

[CustomMessages]
hebrew.AppName=AppLimit דסקטופ
english.AppName=AppLimit Desktop
spanish.AppName=AppLimit Escritorio
french.AppName=AppLimit Bureau
german.AppName=AppLimit Desktop
italian.AppName=AppLimit Desktop
russian.AppName=AppLimit Рабочий стол
portuguese.AppName=AppLimit Desktop
japanese.AppName=AppLimit デスクトップ
korean.AppName=AppLimit 데스크톱
turkish.AppName=AppLimit Masaüstü
polish.AppName=AppLimit Pulpit
dutch.AppName=AppLimit Bureaublad
swedish.AppName=AppLimit Skrivbord
danish.AppName=AppLimit Skrivebord
norwegian.AppName=AppLimit Skrivebord
finnish.AppName=AppLimit Työpöytä
hungarian.AppName=AppLimit Asztal
czech.AppName=AppLimit Plocha
slovak.AppName=AppLimit Plocha

hebrew.InstallTypeFull=ביצוע מלא
english.InstallTypeFull=Full Installation
spanish.InstallTypeFull=Instalación Completa
french.InstallTypeFull=Installation Complète
german.InstallTypeFull=Vollständige Installation
italian.InstallTypeFull=Installazione Completa
russian.InstallTypeFull=Полная установка
portuguese.InstallTypeFull=Instalação Completa
japanese.InstallTypeFull=フル インストール
korean.InstallTypeFull=전체 설치
turkish.InstallTypeFull=Tam Kurulum
polish.InstallTypeFull=Pełna Instalacja
dutch.InstallTypeFull=Volledige Installatie
swedish.InstallTypeFull=Fullständig Installation
danish.InstallTypeFull=Fuld Installation
norwegian.InstallTypeFull=Komplett Installasjon
finnish.InstallTypeFull=Täysi Asennus
hungarian.InstallTypeFull=Teljes Telepítés
czech.InstallTypeFull=Úplná Instalace
slovak.InstallTypeFull=Úplná Inštalácia

hebrew.InstallTypeCompact=ביצוע מינימלי
english.InstallTypeCompact=Compact Installation
spanish.InstallTypeCompact=Instalación Compacta
french.InstallTypeCompact=Installation Compacte
german.InstallTypeCompact=Kompakte Installation
italian.InstallTypeCompact=Installazione Compatta
russian.InstallTypeCompact=Компактная установка
portuguese.InstallTypeCompact=Instalação Compacta
japanese.InstallTypeCompact=コンパクト インストール
korean.InstallTypeCompact=간단 설치
turkish.InstallTypeCompact=Kompakt Kurulum
polish.InstallTypeCompact=Instalacja Kompaktowa
dutch.InstallTypeCompact=Compacte Installatie
swedish.InstallTypeCompact=Kompakt Installation
danish.InstallTypeCompact=Kompakt Installation
norwegian.InstallTypeCompact=Kompakt Installasjon
finnish.InstallTypeCompact=Kompakti Asennus
hungarian.InstallTypeCompact=Kompakt Telepítés
czech.InstallTypeCompact=Kompaktní Instalace
slovak.InstallTypeCompact=Kompaktná Inštalácia

hebrew.InstallTypeCustom=ביצוע מותאם אישית
english.InstallTypeCustom=Custom Installation
spanish.InstallTypeCustom=Instalación Personalizada
french.InstallTypeCustom=Installation Personnalisée
german.InstallTypeCustom=Benutzerdefinierte Installation
italian.InstallTypeCustom=Installazione Personalizzata
russian.InstallTypeCustom=Пользовательская установка
portuguese.InstallTypeCustom=Instalação Personalizada
japanese.InstallTypeCustom=カスタム インストール
korean.InstallTypeCustom=사용자 지정 설치
turkish.InstallTypeCustom=Özel Kurulum
polish.InstallTypeCustom=Instalacja Niestandardowa
dutch.InstallTypeCustom=Aangepaste Installatie
swedish.InstallTypeCustom=Anpassad Installation
danish.InstallTypeCustom=Brugerdefineret Installation
norwegian.InstallTypeCustom=Tilpasset Installasjon
finnish.InstallTypeCustom=Mukautettu Asennus
hungarian.InstallTypeCustom=Egyéni Telepítés
czech.InstallTypeCustom=Vlastní Instalace
slovak.InstallTypeCustom=Vlastná Inštalácia

hebrew.ComponentMain=תוכנה הראשית
english.ComponentMain=Main Application
spanish.ComponentMain=Aplicación Principal
french.ComponentMain=Application Principale
german.ComponentMain=Hauptanwendung
italian.ComponentMain=Applicazione Principale
russian.ComponentMain=Основное приложение
portuguese.ComponentMain=Aplicativo Principal
japanese.ComponentMain=メイン アプリケーション
korean.ComponentMain=주요 애플리케이션
turkish.ComponentMain=Ana Uygulama
polish.ComponentMain=Główna Aplikacja
dutch.ComponentMain=Hoofdapplicatie
swedish.ComponentMain=Huvudprogram
danish.ComponentMain=Hovedprogram
norwegian.ComponentMain=Hovedprogram
finnish.ComponentMain=Pääsovellus
hungarian.ComponentMain=Fő alkalmazás
czech.ComponentMain=Hlavní Aplikace
slovak.ComponentMain=Hlavná Aplikácia

hebrew.ComponentAssociations=אסוציאציות קבצים
english.ComponentAssociations=File Associations
spanish.ComponentAssociations=Asociaciones de Archivos
french.ComponentAssociations=Associations de Fichiers
german.ComponentAssociations=Dateizuordnungen
italian.ComponentAssociations=Associazioni di File
russian.ComponentAssociations=Связи файлов
portuguese.ComponentAssociations=Associações de Arquivos
japanese.ComponentAssociations=ファイルの関連付け
korean.ComponentAssociations=파일 연결
turkish.ComponentAssociations=Dosya İlişkilendirmeleri
polish.ComponentAssociations=Skojarzenia Plików
dutch.ComponentAssociations=Bestandskoppelingen
swedish.ComponentAssociations=Filassociationer
danish.ComponentAssociations=Filtilknytninger
norwegian.ComponentAssociations=Filtilknytninger
finnish.ComponentAssociations=Tiedostoliitokset
hungarian.ComponentAssociations=Fájltársítások
czech.ComponentAssociations=Přiřazení Souborů
slovak.ComponentAssociations=Priradenia Súborov

hebrew.ComponentDesktopShortcut=צור קיצור דרך בשולחן העבודה
english.ComponentDesktopShortcut=Create Desktop Shortcut
spanish.ComponentDesktopShortcut=Crear acceso directo en el escritorio
french.ComponentDesktopShortcut=Créer un raccourci sur le bureau
german.ComponentDesktopShortcut=Desktopverknüpfung erstellen
italian.ComponentDesktopShortcut=Crea collegamento sul desktop
russian.ComponentDesktopShortcut=Создать ярлык на рабочем столе
portuguese.ComponentDesktopShortcut=Criar atalho na área de trabalho
japanese.ComponentDesktopShortcut=デスクトップ ショートカットを作成
korean.ComponentDesktopShortcut=바탕화면 바로가기 생성
turkish.ComponentDesktopShortcut=Masaüstü Kısayolu Oluştur
polish.ComponentDesktopShortcut=Utwórz skrót na pulpicie
dutch.ComponentDesktopShortcut=Bureaublad-snelkoppeling maken
swedish.ComponentDesktopShortcut=Skapa genväg på skrivbordet
danish.ComponentDesktopShortcut=Opret skrivebordsgenvej
norwegian.ComponentDesktopShortcut=Opprett skrivebordssnarvei
finnish.ComponentDesktopShortcut=Luo työpöydän pikakuvake
hungarian.ComponentDesktopShortcut=Asztali parancsikon létrehozása
czech.ComponentDesktopShortcut=Vytvořit zástupce na ploše
slovak.ComponentDesktopShortcut=Vytvoriť zástupcu na ploche

hebrew.ComponentQuickLaunch=צור צלמית בסרגל ההפעלה המהיר
english.ComponentQuickLaunch=Quick Launch Icon
spanish.ComponentQuickLaunch=Icono de inicio rápido
french.ComponentQuickLaunch=Icône de lancement rapide
german.ComponentQuickLaunch=Schnellstartsymbol
italian.ComponentQuickLaunch=Icona di avvio rapido
russian.ComponentQuickLaunch=Значок быстрого запуска
portuguese.ComponentQuickLaunch=Ícone de inicialização rápida
japanese.ComponentQuickLaunch=クイック起動アイコン
korean.ComponentQuickLaunch=빠른 실행 아이콘
turkish.ComponentQuickLaunch=Hızlı Başlat Simgesi
polish.ComponentQuickLaunch=Ikona szybkiego uruchamiania
dutch.ComponentQuickLaunch=Quick Launch-pictogram
swedish.ComponentQuickLaunch=Snabbstartikon
danish.ComponentQuickLaunch=Hurtigstart-ikon
norwegian.ComponentQuickLaunch=Hurtigstart-ikon
finnish.ComponentQuickLaunch=Pikakäynnistyskuvake
hungarian.ComponentQuickLaunch=Gyorsindítás ikon
czech.ComponentQuickLaunch=Ikona rychlého spuštění
slovak.ComponentQuickLaunch=Ikona rýchleho spustenia

hebrew.TaskDesktopIcon=צור קיצור דרך בשולחן העבודה
english.TaskDesktopIcon=Create a desktop icon
spanish.TaskDesktopIcon=Crear un icono en el escritorio
french.TaskDesktopIcon=Créer une icône sur le bureau
german.TaskDesktopIcon=Desktop-Symbol erstellen
italian.TaskDesktopIcon=Crea un'icona sul desktop
russian.TaskDesktopIcon=Создать значок на рабочем столе
portuguese.TaskDesktopIcon=Criar um ícone na área de trabalho
japanese.TaskDesktopIcon=デスクトップ アイコンを作成
korean.TaskDesktopIcon=바탕화면 아이콘 만들기
turkish.TaskDesktopIcon=Masaüstü simgesi oluştur
polish.TaskDesktopIcon=Utwórz ikonę na pulpicie
dutch.TaskDesktopIcon=Een bureaubladpictogram maken
swedish.TaskDesktopIcon=Skapa en skrivbordsikon
danish.TaskDesktopIcon=Opret et skrivebordsikon
norwegian.TaskDesktopIcon=Opprett et skrivebordsikon
finnish.TaskDesktopIcon=Luo työpöytäkuvake
hungarian.TaskDesktopIcon=Asztali ikon létrehozása
czech.TaskDesktopIcon=Vytvořit ikonu na ploše
slovak.TaskDesktopIcon=Vytvoriť ikonu na ploche

hebrew.TaskGroupAdditionalShortcuts=קיצורי דרך נוספים
english.TaskGroupAdditionalShortcuts=Additional Shortcuts
spanish.TaskGroupAdditionalShortcuts=Accesos directos adicionales
french.TaskGroupAdditionalShortcuts=Raccourcis supplémentaires
german.TaskGroupAdditionalShortcuts=Zusätzliche Verknüpfungen
italian.TaskGroupAdditionalShortcuts=Scorciatoie aggiuntive
russian.TaskGroupAdditionalShortcuts=Дополнительные ярлыки
portuguese.TaskGroupAdditionalShortcuts=Atalhos adicionais
japanese.TaskGroupAdditionalShortcuts=追加のショートカット
korean.TaskGroupAdditionalShortcuts=추가 바로가기
turkish.TaskGroupAdditionalShortcuts=Ek Kısayollar
polish.TaskGroupAdditionalShortcuts=Dodatkowe skróty
dutch.TaskGroupAdditionalShortcuts=Extra snelkoppelingen
swedish.TaskGroupAdditionalShortcuts=Ytterligare genvägar
danish.TaskGroupAdditionalShortcuts=Yderligere genveje
norwegian.TaskGroupAdditionalShortcuts=Ekstra snarveier
finnish.TaskGroupAdditionalShortcuts=Lisäpikakuvakkeet
hungarian.TaskGroupAdditionalShortcuts=További parancsikonok
czech.TaskGroupAdditionalShortcuts=Další zástupci
slovak.TaskGroupAdditionalShortcuts=Ďalšie zástupce

hebrew.RunNow=הפעל את AppLimit עכשיו
english.RunNow=Run AppLimit now
spanish.RunNow=Ejecutar AppLimit ahora
french.RunNow=Exécuter AppLimit maintenant
german.RunNow=AppLimit jetzt starten
italian.RunNow=Avvia AppLimit ora
russian.RunNow=Запустить AppLimit сейчас
portuguese.RunNow=Executar AppLimit agora
japanese.RunNow=今すぐAppLimitを実行
korean.RunNow=지금 AppLimit 실행
turkish.RunNow=AppLimit'i şimdi çalıştır
polish.RunNow=Uruchom AppLimit teraz
dutch.RunNow=AppLimit nu uitvoeren
swedish.RunNow=Starta AppLimit nu
danish.RunNow=Kør AppLimit nu
norwegian.RunNow=Kjør AppLimit nå
finnish.RunNow=Käynnistä AppLimit nyt
hungarian.RunNow=AppLimit indítása most
czech.RunNow=Spustit AppLimit nyní
slovak.RunNow=Spustiť AppLimit teraz

hebrew.SuccessMsg=AppLimit הותקן בהצלחה! התקנה הושלמה. לחץ על סיום כדי לסיים.
english.SuccessMsg=AppLimit installed successfully! The installation is complete. Click Finish to exit.
spanish.SuccessMsg=¡AppLimit instalado correctamente! La instalación se completó. Haga clic en Finalizar para salir.
french.SuccessMsg=AppLimit installé avec succès ! L'installation est terminée. Cliquez sur Terminer pour quitter.
german.SuccessMsg=AppLimit erfolgreich installiert! Die Installation ist abgeschlossen. Klicken Sie auf Fertig stellen, um zu beenden.
italian.SuccessMsg=AppLimit installato con successo! L'installazione è stata completata. Fare clic su Fine per uscire.
russian.SuccessMsg=AppLimit успешно установлен! Установка завершена. Нажмите "Готовo" чтобы выйти.
portuguese.SuccessMsg=AppLimit instalado com sucesso! A instalação foi concluída. Clique em Concluir para sair.
japanese.SuccessMsg=AppLimitが正常にインストールされました！インストールが完了しました。終了をクリックして終了します。
korean.SuccessMsg=AppLimit이 성공적으로 설치되었습니다! 설치가 완료되었습니다. 마치기를 클릭하여 종료합니다.
turkish.SuccessMsg=AppLimit başarıyla kuruldu! Kurulum tamamlandı. Çıkmak için Bitir'e tıklayın.
polish.SuccessMsg=AppLimit zainstalowany pomyślnie! Instalacja została ukończona. Kliknij Zakończ, aby wyjść.
dutch.SuccessMsg=AppLimit succesvol geïnstalleerd! De installatie is voltooid. Klik op Voltooien om af te sluiten.
swedish.SuccessMsg=AppLimit har installerats! Installationen är klar. Klicka på Slutför för att avsluta.
danish.SuccessMsg=AppLimit installeret succesfuldt! Installationen er gennemført. Klik Afslut for at afslutte.
norwegian.SuccessMsg=AppLimit installert! Installasjonen er fullført. Klikk Ferdig for å avslutte.
finnish.SuccessMsg=AppLimit asennettu onnistuneesti! Asennus on valmis. Napsauta Valmis sulkeaksesi.
hungarian.SuccessMsg=Az AppLimit sikeresen telepítve! A telepítés befejezve. Kattintson a Befejezésre a kilépéshez.
czech.SuccessMsg=AppLimit úspěšně nainstalován! Instalace je dokončena. Klikněte na Dokončení a zavřete.
slovak.SuccessMsg=AppLimit úspešne nainštalovaný! Inštalácia je dokončená. Kliknite na Dokončenie a zatvorte.

[Code]
procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpFinished then
  begin
    MsgBox(CustomMessage('SuccessMsg'), mbInformation, MB_OK);
  end;
end;

procedure InitializeWizard();
begin
  // You can add custom initialization here
end;

[InstallDelete]
; Remove old data if needed during reinstall
Type: filesandordirs; Name: "{app}\data"

[Registry]
; Optional: Register file type associations
Root: HKCU; Subkey: "Software\AppLimit"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"; Flags: uninsdeletekey
Root: HKCU; Subkey: "Software\AppLimit"; ValueType: string; ValueName: "Version"; ValueData: "1.0.0"

[Messages]
; Hebrew translations
hebrew.WelcomeLabel1=ברוכים הבאים להתקנה של AppLimit Desktop
hebrew.WelcomeLabel2=AppLimit היא התוכנה המלאה לניהול זמן המסך בטלפון האנדרואיד שלך.
hebrew.WizardSelectComponents=בחרו את הרכיבים שברצונכם להתקין:
hebrew.WizardReady=התוכנה מוכנה להתקנה
hebrew.FinishedHeadingLabel=התקנה הושלמה
hebrew.FinishedLabel=התקנת AppLimit הושלמה בהצלחה! תוכלו להתחיל להשתמש בתוכנה כעת.

; English translations (overrides defaults)
english.WelcomeLabel1=Welcome to AppLimit Desktop Setup
english.WelcomeLabel2=AppLimit is the complete solution for managing screen time on your Android device.
english.WizardSelectComponents=Select the components you want to install:
english.WizardReady=Ready to Install
english.FinishedHeadingLabel=Setup Completed
english.FinishedLabel=AppLimit Desktop has been installed successfully! You can now start using the application.

; Spanish translations
spanish.WelcomeLabel1=Bienvenido a la instalación de AppLimit Desktop
spanish.WelcomeLabel2=AppLimit es la solución completa para gestionar el tiempo de pantalla en tu dispositivo Android.

; French translations
french.WelcomeLabel1=Bienvenue dans l'installation d'AppLimit Desktop
french.WelcomeLabel2=AppLimit est la solution complète pour gérer le temps d'écran sur votre appareil Android.

; German translations
german.WelcomeLabel1=Willkommen zur AppLimit Desktop-Installation
german.WelcomeLabel2=AppLimit ist die komplette Lösung zur Verwaltung der Bildschirmzeit auf Ihrem Android-Gerät.

; Italian translations
italian.WelcomeLabel1=Benvenuto nell'installazione di AppLimit Desktop
italian.WelcomeLabel2=AppLimit è la soluzione completa per gestire il tempo dello schermo sul tuo dispositivo Android.

; Russian translations
russian.WelcomeLabel1=Добро пожаловать на установку AppLimit Desktop
russian.WelcomeLabel2=AppLimit - это полное решение для управления экранным временем на вашем устройстве Android.

; Portuguese translations
portuguese.WelcomeLabel1=Bem-vindo à instalação do AppLimit Desktop
portuguese.WelcomeLabel2=AppLimit é a solução completa para gerenciar o tempo de tela em seu dispositivo Android.

; Japanese translations
japanese.WelcomeLabel1=AppLimit Desktopインストールへようこそ
japanese.WelcomeLabel2=AppLimitはAndroidデバイスの画面時間を管理するための完全なソリューションです。

; Korean translations
korean.WelcomeLabel1=AppLimit Desktop 설치에 오신 것을 환영합니다
korean.WelcomeLabel2=AppLimit은 Android 장치에서 화면 시간을 관리하는 완벽한 솔루션입니다.

; Turkish translations
turkish.WelcomeLabel1=AppLimit Desktop Kurulumuna Hoşgeldiniz
turkish.WelcomeLabel2=AppLimit, Android cihazınızda ekran süresini yönetmek için tam bir çözümdür.

; Polish translations
polish.WelcomeLabel1=Witamy w instalacji AppLimit Desktop
polish.WelcomeLabel2=AppLimit to kompletne rozwiązanie do zarządzania czasem ekranu na urządzeniu Android.

; Dutch translations
dutch.WelcomeLabel1=Welkom bij de installatie van AppLimit Desktop
dutch.WelcomeLabel2=AppLimit is de complete oplossing voor het beheren van schermtijd op uw Android-apparaat.

; Swedish translations
swedish.WelcomeLabel1=Välkommen till AppLimit Desktop-installation
swedish.WelcomeLabel2=AppLimit är den kompletta lösningen för att hantera skärmtid på din Android-enhet.

; Danish translations
danish.WelcomeLabel1=Velkommen til AppLimit Desktop-installation
danish.WelcomeLabel2=AppLimit er den komplette løsning til at administrere skormtid på din Android-enhed.

; Norwegian translations
norwegian.WelcomeLabel1=Velkommen til AppLimit Desktop-installasjon
norwegian.WelcomeLabel2=AppLimit er den komplette løsningen for å administrere skjermtid på Android-enheten din.

; Finnish translations
finnish.WelcomeLabel1=Tervetuloa AppLimit Desktop -asennukseen
finnish.WelcomeLabel2=AppLimit on täydellinen ratkaisu Android-laitteesi näyttöaikakauden hallintaan.

; Hungarian translations
hungarian.WelcomeLabel1=Üdvözöljük az AppLimit Desktop telepítésben
hungarian.WelcomeLabel2=Az AppLimit a teljes megoldás az Android-eszköz képernyőidejének kezeléséhez.

; Czech translations
czech.WelcomeLabel1=Vítejte v instalaci aplikace AppLimit Desktop
czech.WelcomeLabel2=AppLimit je kompletní řešení pro správu doby obrazovky na zařízení Android.

; Slovak translations
slovak.WelcomeLabel1=Vitajte v inštalácii AppLimit Desktop
slovak.WelcomeLabel2=AppLimit je kompletné riešenie na správu času obrazovky na zariadení Android.
