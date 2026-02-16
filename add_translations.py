#!/usr/bin/env python3

file_path = r"c:\Users\HM\Documents\AppLimit\AppLimit\app\src\main\java\com\example\safetimeguard\LanguageManager.kt"

translations = '''                    "track_app_usage" to
                            mapOf(
                                    ENGLISH to "Track app usage",
                                    HEBREW to "עקוב אחר שימוש אפליקציות",
                                    FRENCH to "Suivre l'utilisation des applications",
                                    SPANISH to "Rastrear el uso de aplicaciones",
                                    GERMAN to "App-Nutzung verfolgen",
                                    ARABIC to "تتبع استخدام التطبيق",
                                    CHINESE to "跟踪应用使用情况",
                                    KOREAN to "앱 사용 추적",
                                    JAPANESE to "アプリ使用状況を追跡",
                                    PORTUGUESE to "Rastrear uso de aplicativo",
                                    HINDI to "ऐप उपयोग ट्रैक करें",
                                    RUSSIAN to "Отслеживать использование приложений",
                                    UKRAINIAN to "Відстежувати використання додатків"
                            ),
                    "show_system_alerts" to
                            mapOf(
                                    ENGLISH to "Show system alerts",
                                    HEBREW to "הצג התראות מערכת",
                                    FRENCH to "Afficher les alertes système",
                                    SPANISH to "Mostrar alertas del sistema",
                                    GERMAN to "Systemwarnungen anzeigen",
                                    ARABIC to "إظهار تنبيهات النظام",
                                    CHINESE to "显示系统警报",
                                    KOREAN to "시스템 경고 표시",
                                    JAPANESE to "システムアラートを表示",
                                    PORTUGUESE to "Mostrar alertas do sistema",
                                    HINDI to "सिस्टम सतर्कता दिखाएं",
                                    RUSSIAN to "Показывать системные уведомления",
                                    UKRAINIAN to "Показувати системні сповіщення"
                            ),
                    "send_notifications" to
                            mapOf(
                                    ENGLISH to "Send notifications",
                                    HEBREW to "שלח הודעות",
                                    FRENCH to "Envoyer les notifications",
                                    SPANISH to "Enviar notificaciones",
                                    GERMAN to "Benachrichtigungen senden",
                                    ARABIC to "إرسال الإخطارات",
                                    CHINESE to "发送通知",
                                    KOREAN to "알림 전송",
                                    JAPANESE to "通知を送信",
                                    PORTUGUESE to "Enviar notificações",
                                    HINDI to "सूचनाएं भेजें",
                                    RUSSIAN to "Отправлять уведомления",
                                    UKRAINIAN to "Надсилати сповіщення"
                            ),
                    "enable_accessibility" to
                            mapOf(
                                    ENGLISH to "Enable Accessibility Service",
                                    HEBREW to "הפעל שירות נגישות",
                                    FRENCH to "Activer le service d'accessibilité",
                                    SPANISH to "Habilitar servicio de accesibilidad",
                                    GERMAN to "Eingabehilfen aktivieren",
                                    ARABIC to "تفعيل خدمة الوصول",
                                    CHINESE to "启用无障碍服务",
                                    KOREAN to "접근성 서비스 활성화",
                                    JAPANESE to "アクセシビリティサービスを有効にする",
                                    PORTUGUESE to "Ativar serviço de acessibilidade",
                                    HINDI to "एक्सेसिबिलिटी सेवा सक्षम करें",
                                    RUSSIAN to "Включить службу специальных возможностей",
                                    UKRAINIAN to "Увімкнути службу доступності"
                            ),
                    "ask" to
                            mapOf(
                                    ENGLISH to "Ask",
                                    HEBREW to "בקש",
                                    FRENCH to "Demander",
                                    SPANISH to "Preguntar",
                                    GERMAN to "Fragen",
                                    ARABIC to "اطلب",
                                    CHINESE to "请求",
                                    KOREAN to "요청",
                                    JAPANESE to "承認",
                                    PORTUGUESE to "Pedir",
                                    HINDI to "पूछें",
                                    RUSSIAN to "Запросить",
                                    UKRAINIAN to "Запитати"
                            ),
                    "enable" to
                            mapOf(
                                    ENGLISH to "Enable",
                                    HEBREW to "הפעל",
                                    FRENCH to "Activer",
                                    SPANISH to "Habilitar",
                                    GERMAN to "Aktivieren",
                                    ARABIC to "تفعيل",
                                    CHINESE to "启用",
                                    KOREAN to "활성화",
                                    JAPANESE to "有効にする",
                                    PORTUGUESE to "Ativar",
                                    HINDI to "सक्षम करें",
                                    RUSSIAN to "Включить",
                                    UKRAINIAN to "Увімкнути"
                            )'''

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Find the location where we need to insert (before the closing mapOf)
# Replace the last ) ) with our new translations followed by ) )

# Find the pattern: scheduled_friday to ... UKRAINIAN to "П'ятниця: додаткові правила блокування активні"
#                            )
#            )

insertion_point = content.rfind('            )')
if insertion_point != -1:
    # Insert right before the final closing parenthesis
    new_content = content[:insertion_point] + translations + '\n' + content[insertion_point:]
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print("✓ Successfully added missing translation keys to LanguageManager.kt")
else:
    print("✗ Could not find insertion point")
