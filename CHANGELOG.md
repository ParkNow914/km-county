# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/spec/v2.0.0.html).

## [Unreleased]

### Adicionado
- Suporte para mais apps de transporte
- Histórico local de corridas
- Estatísticas pessoais de uso
- Temas personalizáveis

### Mudado
- Melhorias na precisão do parser
- Interface mais responsiva
- Otimizações de performance

## [1.0.0] - 2024-09-24

### Adicionado
- **AccessibilityService** para leitura de tela de apps de transporte
- **Parser inteligente** com regex para extrair preços, distâncias e tempos
- **Overlay flutuante** não-intrusivo com indicadores visuais (verde/laranja/vermelho)
- **Filtros automáticos de PII** para proteger informações pessoais
- **Processamento 100% local** - nenhum dado enviado para servidores
- **Suporte inicial** para Uber, 99, Cabify, Easy Taxi e outros
- **Sistema de onboarding** com explicações detalhadas de permissões
- **Configurações personalizáveis** para thresholds de preços
- **ML Kit OCR** como fallback quando AccessibilityService não funciona
- **Modo conservador** para reduzir falsos positivos
- **Logs opcionais** e locais para debugging
- **Política de privacidade** compatível com LGPD
- **Código fonte aberto** (MIT License) para auditabilidade
- **Sistema completo de testes** (unit, instrumented, OCR)
- **CI/CD automatizado** com GitHub Actions
- **Documentação completa** para usuários e desenvolvedores

### Funcionalidades Principais
- ✅ Cálculo automático de R$/km e R$/min
- ✅ Overlay flutuante com cores indicativas
- ✅ Detecção automática de telas de corrida
- ✅ Filtros de privacidade para PII
- ✅ Configurações personalizáveis
- ✅ Suporte a múltiplos apps de transporte
- ✅ Processamento local sem envio de dados
- ✅ Interface de onboarding completa

### Segurança e Privacidade
- ✅ Nenhuma coleta de dados pessoais
- ✅ Processamento 100% on-device
- ✅ Filtros automáticos de informações sensíveis
- ✅ Logs opcionais e controláveis pelo usuário
- ✅ Política de privacidade transparente
- ✅ Código aberto para auditoria

### Compatibilidade
- **Android**: 6.0+ (API 23+)
- **Apps suportados**: Uber, 99, Cabify, Easy Taxi, iFood, Loggi
- **Idiomas**: Português brasileiro (pt-BR)
- **Arquiteturas**: ARM64, ARM32, x86_64

### Documentação
- [README.md](README.md) - Visão geral do projeto
- [INSTALL.md](docs/INSTALL.md) - Guia de instalação
- [PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md) - Política de privacidade
- [CONTRIBUTING.md](CONTRIBUTING.md) - Guia de contribuição
- [PLAYSTORE_DECLARATION.md](docs/PLAYSTORE_DECLARATION.md) - Declaração Play Store

### Arquivos de Build
- **APK**: assistente-ridepricing-v1.0.0.apk
- **AAB**: assistente-ridepricing-v1.0.0.aab
- **Checksums**: SHA256 para verificação de integridade
- **Assinatura**: Certificado digital verificável

## [0.1.0-alpha] - 2024-09-20

### Adicionado
- Estrutura básica do projeto Android
- Módulos core, ml e app
- AccessibilityService básico
- Parser inicial para Uber
- Overlay simples
- Testes unitários básicos

### Limitações Conhecidas
- Suporte limitado a formatos de preço
- Apenas Uber suportado
- Interface básica
- Sem filtros de PII

## Tipos de Mudanças

- `Adicionado` para novas funcionalidades
- `Mudado` para mudanças em funcionalidades existentes
- `Depreciado` para funcionalidades que serão removidas em breve
- `Removido` para funcionalidades removidas
- `Corrigido` para correções de bugs
- `Segurança` para vulnerabilidades corrigidas

---

## Links

- [Código fonte](https://github.com/[usuario]/kmcounty)
- [Issues](https://github.com/[usuario]/kmcounty/issues)
- [Releases](https://github.com/[usuario]/kmcounty/releases)
- [Documentação](https://github.com/[usuario]/kmcounty/tree/main/docs)
