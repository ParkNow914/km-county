# KM County - Assistente de Preços de Corridas

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/minSdk-23-green)](https://developer.android.com/about/versions/android-6.0)
[![Firebase](https://img.shields.io/badge/Firebase-Enabled-orange)](https://firebase.google.com/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue)](https://github.com/features/actions)

O KM County é um aplicativo Android que ajuda motoristas de aplicativo a calcular rapidamente o valor por quilômetro (R$/km) e por minuto (R$/min) de corridas em apps de transporte como Uber e 99.

## ✨ Funcionalidades

- 🔍 Detecção automática de telas de pedido de corrida
- 🧮 Cálculo em tempo real de R$/km e R$/min
- 🎨 Overlay não intrusivo com código de cores
- 🔒 100% processamento local - sem coleta de dados pessoais
- 🛡️ Modo conservador para evitar falsos positivos
- 🌙 Suporte a tema escuro
- 📊 Analytics e monitoramento de performance com Firebase
- 🔔 Notificações push com Firebase Cloud Messaging
- 🚨 Relatórios de crash automáticos com Firebase Crashlytics

## 🏗️ Arquitetura de Produção

### Integrações Firebase

- **Firebase Analytics**: Acompanhamento de uso e comportamento dos usuários
- **Firebase Crashlytics**: Relatórios automáticos de crashes e erros
- **Firebase Performance Monitoring**: Monitoramento de performance do app
- **Firebase Remote Config**: Configurações remotas para updates sem nova versão
- **Firebase Cloud Messaging**: Notificações push para atualizações importantes

### CI/CD e Automação

- **GitHub Actions**: Pipeline completo de CI/CD
- **Fastlane**: Automação de build, teste e deploy para Google Play Store
- **Build Variants**: Debug, Staging e Release com configurações específicas
- **Product Flavors**: Ambientes de desenvolvimento e produção

### Segurança

- **Assinatura de APK**: Configuração segura com keystore protegida
- **ProGuard/R8**: Obfuscação de código em builds de produção
- **Variáveis de Ambiente**: Configurações sensíveis em arquivo .env
- **Network Security Config**: Configurações seguras de rede

## 📱 Requisitos

- Android 6.0 (API 23) ou superior
- Permissão de Acessibilidade (para leitura da tela)
- Permissão de Sobrepôr outros apps (para o overlay)

## 🚀 Instalação

1. Baixe o APK mais recente na seção [Releases](https://github.com/seu-usuario/km-county/releases)
2. Habilite "Fontes desconhecidas" nas configurações do Android
3. Instale o APK baixado
4. Siga o assistente de configuração inicial

## 🔧 Como usar

1. Abra o KM County
2. Ative a permissão de Acessibilidade nas configurações do Android
3. Ative a permissão para sobrepor outros apps
4. Abra seu app de transporte preferido
5. Quando um pedido de corrida aparecer, o KM County mostrará automaticamente o R$/km e R$/min

## 🛠️ Desenvolvimento

### Pré-requisitos

- Android Studio Flamingo (2022.2.1) ou superior
- JDK 17
- Android SDK 33+
- Git

### Configuração Inicial

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/km-county.git
   cd km-county
   ```

2. Configure as variáveis de ambiente:

   ```bash
   cp .env.example .env
   # Edite o arquivo .env com suas chaves de API
   ```

3. Configure o Firebase:
   - Baixe o arquivo `google-services.json` do Firebase Console
   - Substitua o arquivo `app/google-services.json` existente

4. Configure a assinatura (para builds de produção):

   ```bash
   # Crie uma keystore ou use uma existente
   # Atualize o arquivo keystore.properties com suas credenciais
   ```

5. Abra o projeto no Android Studio e aguarde a sincronização do Gradle

### Build Variants

O projeto suporta diferentes build variants:

- **Debug**: Para desenvolvimento local
- **Staging**: Para testes internos (sem debug, com minify)
- **Release**: Para produção (obfuscação completa)

Para selecionar um variant, use o menu Build Variants no Android Studio ou execute:

```bash
./gradlew assembleDebug      # Build debug
./gradlew assembleStaging    # Build staging
./gradlew assembleRelease    # Build release
```

### Product Flavors

- **dev**: Ambiente de desenvolvimento
- **prod**: Ambiente de produção

### CI/CD com GitHub Actions

O projeto inclui um pipeline completo de CI/CD que:

- Executa testes automatizados
- Builda APKs para diferentes variants
- Faz upload dos artefatos
- Deploy automático para Google Play Store (branch main)

Para configurar o CI/CD:

1. Configure os secrets no repositório GitHub:
   - `GOOGLE_PLAY_JSON_KEY`: Chave de serviço do Google Play
   - `KEYSTORE_FILE`: Arquivo de keystore (base64 encoded)
   - `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`: Credenciais da keystore

2. Configure o Fastlane:

   ```bash
   cd fastlane
   bundle install
   ```

### Deploy Manual

Para deploy manual usando Fastlane:

```bash
# Build e deploy para internal testing
bundle exec fastlane beta

# Build e deploy para production
bundle exec fastlane deploy
```

## 🤝 Contribuição

Contribuições são bem-vindas! Por favor, leia nosso [guia de contribuição](CONTRIBUTING.md) antes de enviar um pull request.

## 📝 Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📄 Política de Privacidade

O KM County não coleta, armazena ou compartilha nenhum dado pessoal. Todo o processamento é feito localmente no seu dispositivo. Consulte nossa [Política de Privacidade](PRIVACY_POLICY.md) para mais informações.

## 📧 Contato

Seu Nome - [@seu_twitter](https://twitter.com/seu_twitter) - [seu.email@exemplo.com](mailto:seu.email@exemplo.com)

Link do Projeto: [https://github.com/seu-usuario/km-county](https://github.com/seu-usuario/km-county)
