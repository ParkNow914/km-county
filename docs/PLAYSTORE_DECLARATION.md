# Google Play Store - Declaração de AccessibilityService

Este documento contém as informações necessárias para submissão do **Assistente R$/km** na Google Play Store, especificamente relacionadas ao uso de AccessibilityService.

## 📋 Informações da Play Console

### App Title
```
Assistente R$/km - Preços de Corridas
```

### Short Description
```
Calcule R$/km e R$/min de corridas em tempo real. 100% local, sem automação.
```

### Long Description
```
O Assistente R$/km é uma ferramenta informativa que ajuda motoristas a analisar preços de corridas calculando R$/km e R$/min em tempo real.

🔍 CARACTERÍSTICAS PRINCIPAIS:
• Cálculo automático de R$ por quilômetro e por minuto
• Overlay flutuante com indicadores visuais (verde/laranja/vermelho)
• Suporte para Uber, 99, Cabify e outros apps de transporte
• 100% processamento local - nenhum dado enviado para servidores
• Filtros automáticos de informações pessoais (PII)
• Código fonte aberto para auditabilidade completa

🛡️ PRIVACIDADE E SEGURANÇA:
• Nenhuma coleta de dados pessoais
• Todo processamento feito no dispositivo
• Compatível com LGPD
• Logs opcionais e sempre locais
• Política de privacidade transparente

⚠️ IMPORTANTE:
• Apenas LEITURA de informações - nunca aceita ou recusa corridas
• Não interfere com o funcionamento de outros apps
• Ferramenta informativa - use seu julgamento para decisões

O app requer permissões de Acessibilidade e Overlay, utilizadas exclusivamente para leitura de preços e exibição de cálculos. É uma ferramenta de apoio à decisão, não um sistema automatizado.
```

### Category
```
Navigation
```

### Content Rating
```
Everyone (suitable for all ages)
```

## 🔐 Declaração de AccessibilityService

### Seção: "Accessibility Service Declaration"

#### 1. Does your app use AccessibilityService?
```
Yes
```

#### 2. What is the primary purpose of your AccessibilityService?
```
The AccessibilityService is used exclusively to read visible text content from ride-hailing apps (Uber, 99, Cabify, etc.) to extract pricing information, distances, and estimated times for calculation purposes. It is used as an assistive tool for drivers to make informed decisions about ride requests.
```

#### 3. Which user groups need your AccessibilityService?
```
Professional drivers and ride-hailing service providers who need to quickly analyze ride profitability by calculating price per kilometer and price per minute ratios. This helps drivers make informed decisions about which rides to accept based on economic viability.
```

#### 4. How does your AccessibilityService help users with disabilities?
```
While our primary target is drivers, the app can assist users with visual or cognitive disabilities by:
- Providing clear visual indicators (green/red/orange) for price evaluation
- Displaying large, easy-to-read price calculations in a floating overlay
- Reducing cognitive load by automatically calculating complex price ratios
- Offering audio feedback (vibration) when rides are detected
```

#### 5. Provide evidence of how your AccessibilityService is used
```
Our AccessibilityService implementation:

1. ONLY reads text content from specific transport apps (package names listed in accessibility_service_config.xml)
2. Filters out ALL personal information automatically using PII detection algorithms
3. Processes only price, distance, and time information
4. NEVER performs actions (clicks, gestures) in target apps
5. Displays results in read-only overlay window

Evidence:
- Open source code available at: [GitHub Repository URL]
- AccessibilityService configuration limited to specific package names
- Code review showing read-only operations
- PII filtering implementation visible in source code
- No performGlobalAction() or performAction() calls in the codebase
```

#### 6. Is there an alternative way to achieve the same functionality?
```
Technical alternatives considered but rejected:

1. SCREEN CAPTURE: Requires MEDIA_PROJECTION permission and constant screen recording, which is more privacy-invasive and battery-intensive.

2. OVERLAY DETECTION: Not possible without system-level access that regular apps don't have.

3. MANUAL INPUT: Would require users to manually enter prices, distances, and times, defeating the purpose of real-time assistance.

4. API INTEGRATION: Transport companies don't provide public APIs for this type of integration.

AccessibilityService is the most privacy-respecting and efficient method because:
- It only reads visible text (what user already sees)
- Works with existing app interfaces
- No need for screen recording or screenshots
- Minimal battery impact
- Can filter sensitive information before processing
```

#### 7. Describe the alternative user experience
```
Without AccessibilityService, users would need to:

1. Manually note down ride price, distance, and estimated time
2. Use a calculator or mental math to compute R$/km and R$/min
3. Compare values against their personal thresholds
4. Make decision based on manual calculations

This alternative is:
- Time-consuming (10-15 seconds vs instant)
- Error-prone (manual calculation mistakes)
- Impractical during high-demand periods
- Creates safety risks (driver attention diverted to calculations)
- Reduces earning efficiency for drivers
```

## 📱 App Permissions Explanation

### BIND_ACCESSIBILITY_SERVICE
```
Required to access text content from ride-hailing apps to extract pricing information. Used exclusively for reading price, distance, and time data. Never performs actions in other apps.
```

### SYSTEM_ALERT_WINDOW
```
Required to display a floating overlay showing calculated R$/km and R$/min values. The overlay is read-only and does not interfere with the underlying apps.
```

### FOREGROUND_SERVICE
```
Required for the overlay service to remain active while monitoring ride information. Ensures consistent price monitoring without interruption.
```

### VIBRATE
```
Optional feedback when new ride information is detected. Can be disabled in settings. Used to alert drivers without visual distraction.
```

## 🔍 Security and Privacy Details

### Data Collection
```
The app does NOT collect:
- Personal information (names, addresses, phone numbers)
- Location data
- Financial information beyond visible ride prices
- User behavior or analytics
- Device identifiers

The app ONLY processes:
- Visible ride prices (e.g., "R$ 12.50")
- Distance information (e.g., "3.2 km")
- Estimated time (e.g., "15 minutes")
- App source identification (e.g., "Uber", "99")
```

### PII Filtering
```
Implemented automatic PII detection and filtering:
- Phone number patterns
- CPF/document numbers  
- Email addresses
- Street addresses
- Personal names
- Any detected PII is immediately discarded before processing
```

### Local Processing
```
All data processing occurs locally on the device:
- No data transmission to external servers
- No cloud storage or backup
- No analytics or tracking
- Optional local logs (disabled by default, user-controlled)
```

## 🧪 Testing and Validation

### Test Scenarios Provided
```
1. Install app and complete onboarding
2. Enable required permissions (Accessibility + Overlay)
3. Open Uber app and request a ride quote
4. Verify overlay appears with calculated R$/km
5. Verify no interference with Uber app functionality
6. Test with 99 app for compatibility
7. Verify PII filtering by checking logs (if enabled)
8. Confirm no data transmission (network monitoring)
```

### Review Testing Instructions
```
For Play Store review team:

1. SETUP:
   - Install APK on Android 8.0+ device
   - Grant Accessibility permission in Settings > Accessibility
   - Grant Overlay permission in Settings > Special app access

2. TESTING:
   - Open Uber or 99 app
   - Request ride quote (don't actually request)
   - Look for small overlay in top-right corner
   - Overlay should show "R$ X,XX/km"
   - App should not interfere with transport app

3. VERIFICATION:
   - Check no automatic acceptance/rejection of rides
   - Verify overlay is read-only (no buttons affecting transport app)  
   - Confirm app only reads visible information
```

## 📞 Contact Information

### Developer Contact
```
Primary: GitHub Issues at [Repository URL]/issues
Emergency: Create issue tagged with "security" or "privacy"
Documentation: Available in app and GitHub repository
```

### Support Resources
```
- Installation guide: docs/INSTALL.md
- Privacy policy: docs/PRIVACY_POLICY.md  
- Source code: Full repository (MIT license)
- User manual: README.md
```

## 📄 Additional Declarations

### Open Source Commitment
```
This app is fully open source under MIT license. All code is available for inspection at [GitHub Repository URL]. Build instructions and reproducible builds are provided for transparency.
```

### Compliance Statements
```
- LGPD (Brazilian Data Protection Law) compliant
- No COPPA concerns (app for professional drivers 18+)
- No GDPR data collection (no personal data processed)
- Accessibility guidelines followed (clear UI, large text, color indicators)
```

### Update Policy
```
Regular updates provided via GitHub releases. Critical security updates released within 72 hours. Parser updates for new app layouts provided as needed. Users notified through app update mechanism.
```

---

## ✅ Pre-Submission Checklist

- [ ] AccessibilityService declaration completed
- [ ] All permissions explained with justification
- [ ] Privacy policy links provided
- [ ] PII filtering demonstrated in code
- [ ] No automation/interaction code present
- [ ] Open source repository public
- [ ] Test instructions provided
- [ ] Contact information updated
- [ ] Compliance statements included
- [ ] Screenshots and video demo prepared

**📧 For Play Store questions, create an issue in the GitHub repository tagged with "play-store".**
