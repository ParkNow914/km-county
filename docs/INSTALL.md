# Guia de Instalação - Assistente R$/km

Este guia fornece instruções detalhadas para instalar e configurar o Assistente R$/km.

## 📋 Requisitos do Sistema

- **Android 6.0+** (API 23 ou superior)
- **100 MB** de espaço livre
- **Conexão com internet** (apenas para download inicial)

## 🚀 Instalação via GitHub Releases (Recomendado)

### 1. Download do APK

1. Acesse a [página de releases](../../releases) do projeto
2. Baixe o arquivo `assistente-ridepricing-vX.X.X.apk` mais recente
3. **Verifique a assinatura digital** (veja seção de segurança abaixo)

### 2. Habilitar Instalação de Fontes Desconhecidas

#### Android 8.0+ (API 26+):
1. Abra **Configurações** → **Apps e notificações**
2. Toque em **Acesso especial a apps** → **Instalar apps desconhecidos**
3. Selecione o navegador usado para baixar (ex: Chrome)
4. Ative **Permitir desta fonte**

#### Android 6.0-7.1:
1. Abra **Configurações** → **Segurança**
2. Ative **Origens desconhecidas**
3. Confirme tocando em **OK**

### 3. Instalar o APK

1. Abra o gerenciador de arquivos
2. Navegue até a pasta **Downloads**
3. Toque no arquivo APK baixado
4. Toque em **Instalar**
5. Aguarde a conclusão da instalação

## 🔒 Verificação de Segurança

### Verificar Assinatura Digital

```bash
# No computador, use aapt para verificar
aapt dump badging assistente-ridepricing-vX.X.X.apk | grep "application-label"

# Verifique o certificado
jarsigner -verify -verbose -certs assistente-ridepricing-vX.X.X.apk
```

### Checksums Esperados

Cada release inclui arquivos de checksum (SHA256):

```bash
# Verificar integridade no Linux/Mac
sha256sum assistente-ridepricing-vX.X.X.apk

# Verificar no Windows
certutil -hashfile assistente-ridepricing-vX.X.X.apk SHA256
```

## ⚙️ Configuração Inicial

### 1. Primeiro Acesso

1. Abra o app **Assistente R$/km**
2. Siga o **onboarding** (4 telas)
3. Leia e aceite a **Política de Privacidade**

### 2. Configurar Permissão de Acessibilidade

#### Passo a Passo:
1. No onboarding, toque em **"Ativar Acessibilidade"**
2. Você será direcionado para **Configurações** → **Acessibilidade**
3. Procure por **"Assistente R$/km"** na lista
4. Toque no serviço e ative o **interruptor**
5. Confirme tocando em **"OK"** no diálogo de aviso
6. **Volte ao app** (botão Voltar)

#### Se não encontrar nas configurações:
1. Vá para **Configurações** → **Aplicativos**
2. Encontre **"Assistente R$/km"**
3. Toque em **Permissões**
4. Procure por **"Acessibilidade"** e ative

### 3. Configurar Permissão de Overlay

#### Passo a Passo:
1. No onboarding, toque em **"Ativar Overlay"**
2. Você será direcionado para **Configurações** → **Apps especiais**
3. Toque em **"Exibir sobre outros apps"**
4. Encontre **"Assistente R$/km"** na lista
5. Ative o **interruptor**
6. **Volte ao app**

### 4. Finalizar Configuração

1. No onboarding, verifique se ambas as permissões estão **verdes**
2. Toque em **"Concluir"**
3. O app está pronto para usar!

## 🔧 Configurações Opcionais

### Ajustar Thresholds de Preços

1. Abra **Configurações** no app
2. Ajuste os valores:
   - **Preço Bom (Verde)**: Padrão R$ 1,50/km
   - **Preço Neutro (Laranja)**: Padrão R$ 0,80/km

### Ativar Logs (Para Debug)

⚠️ **Apenas se solicitado pelo suporte**

1. Vá em **Configurações** → **Logs de Debug**
2. Ative o interruptor
3. Para apagar: **"Apagar Todos os Dados"**

## 📱 Testando a Instalação

### 1. Verificar Status dos Serviços

1. Abra o app principal
2. Verifique se o status mostra **"Ativo e Monitorando"**
3. Se não, revise as permissões acima

### 2. Teste com App de Transporte

1. Abra um app de transporte (Uber, 99, etc.)
2. Simule uma solicitação de corrida
3. O overlay deve aparecer no canto superior direito
4. Deve mostrar **R$/km** calculado

## 🚨 Solução de Problemas

### Problema: "Permissão de Acessibilidade não funciona"

**Soluções:**
1. **Reinicie o dispositivo** após ativar a permissão
2. Desative e reative a permissão de acessibilidade
3. Verifique se o app não está sendo "otimizado" pela bateria:
   - **Configurações** → **Bateria** → **Otimização de bateria**
   - Encontre o app e selecione **"Não otimizar"**

### Problema: "Overlay não aparece"

**Soluções:**
1. Verifique se a permissão de overlay está ativa
2. Teste em outro app de transporte
3. Reinicie o serviço nas configurações do app

### Problema: "App não detecta corridas"

**Possíveis causas:**
1. App de transporte não suportado (veja lista no README)
2. Layout do app mudou (aguarde atualização)
3. Modo conservador muito restritivo (ajuste nas configurações)

### Problema: "Instalação bloqueada"

**Soluções:**
1. Verifique se "Fontes desconhecidas" está ativado
2. Desative temporariamente antivírus de terceiros
3. Baixe novamente o APK (pode estar corrompido)

## 🔄 Atualizações

### Atualização Manual

1. Baixe a nova versão dos [releases](../../releases)
2. Instale sobre a versão existente
3. **Configurações são preservadas**
4. **Não precisa reconfigurar permissões**

### Notificações de Atualização

O app não verifica atualizações automaticamente. Para se manter informado:

1. **⭐ Adicione o repositório aos favoritos** no GitHub
2. **👁️ "Watch"** o repositório para notificações
3. Siga as [releases](../../releases) para novas versões

## 📚 Instalação para Desenvolvedores

### Build a partir do Código Fonte

```bash
# Clonar repositório
git clone https://github.com/[usuario]/kmcounty.git
cd kmcounty

# Build debug
./gradlew assembleDebug

# Build release (requer keystore)
./gradlew assembleRelease
```

### Dependências de Desenvolvimento

- **Android Studio Arctic Fox+**
- **JDK 11+**
- **Android SDK 23-34**
- **Kotlin 1.9+**

## 🆘 Suporte

### Precisa de Ajuda?

1. **Consulte o [README.md](../README.md)** primeiro
2. **Verifique [Issues](../../issues)** para problemas conhecidos
3. **Abra uma nova issue** se necessário
4. **Forneça informações**:
   - Versão do Android
   - Modelo do dispositivo
   - Versão do app
   - Logs de erro (se disponível)

### Contato

- **GitHub Issues**: [Repositório]/issues
- **Documentação**: Pasta `/docs`
- **Código fonte**: Repositório público

---

## ✅ Checklist de Instalação

- [ ] Android 6.0+ verificado
- [ ] APK baixado e verificado
- [ ] Fontes desconhecidas habilitadas
- [ ] App instalado com sucesso
- [ ] Onboarding concluído
- [ ] Permissão de acessibilidade ativada
- [ ] Permissão de overlay ativada
- [ ] Status "Ativo e Monitorando"
- [ ] Teste com app de transporte realizado
- [ ] Overlay funcionando corretamente

**🎉 Parabéns! O Assistente R$/km está pronto para usar!**
