# Assistente R$/km - Sistema de Análise de Corridas On-Device

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Android](https://img.shields.io/badge/Android-23+-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)

Um aplicativo Android open-source que **lê** informações da tela de apps de transporte (Uber/99/similares) e apresenta **R$/km** e **R$/min** em um overlay flutuante. **100% on-device**, sem automação, sem envio de dados pessoais.

## 🚀 Funcionalidades

- ✅ **Detecção automática** de pedidos de corrida na tela
- ✅ **Cálculo em tempo real** de R$/km e R$/min  
- ✅ **Overlay flutuante** não-intrusivo com indicadores visuais
- ✅ **100% processamento local** - sem envio de dados
- ✅ **Sem automação** - apenas leitura e análise
- ✅ **Filtros de privacidade** - descarta automaticamente PII
- ✅ **Configurável** - thresholds personalizáveis por região
- ✅ **Open source** - código auditável (MIT License)

## 🛡️ Privacidade e Segurança

- **Processamento 100% local** usando AccessibilityService + ML Kit on-device
- **Nenhum dado enviado** para servidores externos
- **Filtros automáticos** para descartar informações pessoais (PII)
- **Logs opcionais** e sempre locais com opção "apagar tudo"
- **Código aberto** para auditabilidade completa
- **Compatível com LGPD** - política de privacidade incluída

## 📱 Como Funciona

1. **Detecção**: Monitora mudanças na tela via AccessibilityService
2. **Extração**: Identifica valores, distâncias e tempos estimados  
3. **Cálculo**: Computa R$/km e R$/min com validações
4. **Exibição**: Mostra resultado em overlay com cores indicativas:
   - 🟢 Verde: R$/km ≥ 1,50 (recomendado)
   - 🟠 Laranja: 0,80 ≤ R$/km < 1,50 (neutro)
   - 🔴 Vermelho: R$/km < 0,80 (não recomendado)

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ AccessibilityService │ → │   Core Parser   │ → │   Overlay UI    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ↓                       ↓                       ↓
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   ML Kit OCR    │    │  PII Filtering  │    │   Settings UI   │
│   (Fallback)    │    │   & Validation  │    │  & Onboarding   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 Instalação Rápida

### Via GitHub Releases (Recomendado)
1. Baixe o APK mais recente da [página de releases](../../releases)
2. Ative "Origens desconhecidas" nas configurações do Android
3. Instale o APK
4. Siga o onboarding para ativar permissões

### Via F-Droid
```bash
# Em breve - submissão em andamento
```

### Para Desenvolvedores
```bash
git clone https://github.com/[seu-usuario]/kmcounty.git
cd kmcounty
./gradlew assembleDebug
```

## 🔧 Build e Desenvolvimento

### Pré-requisitos
- Android Studio Arctic Fox+ 
- JDK 11+
- Android SDK 23+
- Kotlin 1.9+

### Comandos Úteis
```bash
# Build debug
./gradlew assembleDebug

# Executar testes
./scripts/run-tests.sh

# Build release
./scripts/build-release.sh

# Lint e verificações
./gradlew ktlintCheck detekt
```

### Estrutura do Projeto
```
/app                  # Módulo principal Android
/core                 # Parser e regras de negócio  
/ml                   # Wrappers ML Kit e OCR
/scripts              # Scripts de build e release
/ci                   # GitHub Actions workflows
/docs                 # Documentação detalhada
/tests                # Testes e datasets
```

## 📋 Permissões Necessárias

- **Accessibility Service**: Para ler conteúdo da tela
- **System Alert Window**: Para exibir overlay
- **Media Projection** (opcional): Fallback OCR se necessário

Todas as permissões são explicadas detalhadamente no onboarding.

## ⚙️ Configuração

- **Thresholds personalizáveis** para cores dos indicadores
- **Modo conservador** (reduz falsos positivos)
- **Confiança mínima OCR** configurável
- **Logs opcionais** para debugging
- **Região** para ajustes de preços locais

## 🧪 Testes e Qualidade

- **Cobertura de testes**: 70%+ no core parser
- **Testes instrumentados**: 20+ cenários de UI
- **Dataset OCR**: 100+ imagens anônimas  
- **Acurácia**: 90%+ preços, 85%+ distâncias
- **CI/CD**: Build, lint e testes automáticos

## 📄 Documentação

- [Instalação Detalhada](docs/INSTALL.md)
- [Política de Privacidade](docs/PRIVACY_POLICY.md) 
- [Declaração Play Store](docs/PLAYSTORE_DECLARATION.md)
- [Textos de Onboarding](docs/ONBOARDING_TEXTS.md)
- [Guia de Contribuição](CONTRIBUTING.md)

## 🛠️ Suporte e Contribuição

- **Issues**: [GitHub Issues](../../issues)
- **Contribuições**: Veja [CONTRIBUTING.md](CONTRIBUTING.md)
- **Discussões**: [GitHub Discussions](../../discussions)

## ⚖️ Legal e Compliance

- **Licença**: MIT (código 100% open source)
- **LGPD**: Política de privacidade completa incluída
- **Play Store**: Declaração de uso de AccessibilityService documentada
- **Não-responsabilidade**: Ferramenta informativa, use por sua conta

## 🔄 Roadmap

### v1.0.0 - MVP ✅
- [x] AccessibilityService básico
- [x] Parser de preços/distâncias 
- [x] Overlay flutuante
- [x] Configurações básicas

### v1.1.0 - Melhorias
- [ ] Suporte a mais apps de transporte
- [ ] Histórico de corridas (local)
- [ ] Estatísticas pessoais
- [ ] Melhorias na precisão do parser

### v1.2.0 - Avançado  
- [ ] Machine Learning para detecção
- [ ] Modo offline completo
- [ ] Export de dados (JSON)
- [ ] Temas personalizáveis

## 📞 Contato

- **GitHub**: [Issues](../../issues) e [Discussions](../../discussions)

---

**⚠️ Importante**: Este aplicativo é uma ferramenta informativa. Use por sua própria conta e risco. Recomendamos não usar para decisões automatizadas. Sempre verifique as informações manualmente antes de aceitar corridas.

**🔒 Privacidade**: Nenhum dado pessoal é coletado ou enviado. Todo processamento é feito localmente no seu dispositivo.