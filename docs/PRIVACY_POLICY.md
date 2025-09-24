# Política de Privacidade - Assistente R$/km

**Última atualização**: 24 de setembro de 2024  
**Versão**: 1.0

## 1. Introdução

O **Assistente R$/km** é um aplicativo Android que ajuda motoristas a analisar preços de corridas calculando R$/km e R$/min em tempo real. Esta política descreve como tratamos informações no aplicativo.

## 2. Compromisso com a Privacidade

### 🔒 **Processamento 100% Local**
- Todas as análises são feitas exclusivamente no seu dispositivo
- **Nenhum dado é enviado para servidores externos**
- Não temos servidores ou infraestrutura na nuvem

### 🛡️ **Filtros Automáticos de PII**
- O aplicativo identifica e descarta automaticamente informações pessoais como:
  - Nomes de pessoas
  - Números de telefone
  - Endereços residenciais
  - CPF e documentos
  - Emails
- **PII é filtrado antes de qualquer processamento**

## 3. Dados Coletados

### ✅ **Dados que Processamos (Localmente)**
- **Preços de corridas** (ex: R$ 12,50)
- **Distâncias** (ex: 3,2 km)
- **Tempos estimados** (ex: 15 minutos)
- **Nome do app de origem** (ex: "Uber", "99")

### ❌ **Dados que NÃO Coletamos**
- Informações pessoais (nomes, telefones, endereços)
- Localização GPS
- Contatos
- Histórico de navegação
- Dados financeiros pessoais
- Conversas ou mensagens

## 4. Como Funciona

### 📱 **AccessibilityService**
- Lê texto visível na tela de apps de transporte
- **Não interage com outros apps** (apenas leitura)
- Filtra automaticamente informações pessoais
- Processa apenas dados relacionados a preços e distâncias

### 🔍 **OCR (Reconhecimento de Texto)**
- Usado como fallback quando AccessibilityService não funciona
- **ML Kit on-device** (Google) - processamento local
- Nunca envia imagens para servidores externos

## 5. Armazenamento de Dados

### 💾 **Configurações do App**
- Preferências de thresholds de preços
- Configurações de interface
- Status de onboarding
- **Armazenado localmente no dispositivo**

### 📝 **Logs (Opcionais)**
- **Desabilitados por padrão**
- Se habilitados pelo usuário, salvam apenas dados numéricos anonimizados
- Nunca contêm informações pessoais
- **Armazenados localmente** - nunca enviados

### 🗑️ **Botão "Apagar Tudo"**
- Remove todas as configurações e logs
- Disponível nas configurações do app

## 6. Permissões Necessárias

### 🔓 **Acessibilidade**
- **Finalidade**: Ler informações de apps de transporte
- **Uso**: Extração de preços, distâncias e tempos
- **Limitação**: Apenas leitura, nunca ações automáticas

### 📱 **Overlay**
- **Finalidade**: Mostrar janela flutuante com cálculos
- **Uso**: Exibir R$/km e R$/min sobre outros apps
- **Limitação**: Somente leitura, não interfere com apps

## 7. Compartilhamento de Dados

### ❌ **Não Compartilhamos Dados**
- Não vendemos informações
- Não compartilhamos com terceiros
- Não enviamos dados para analytics
- Não usamos rastreamento publicitário

### 🔒 **Sem Servidores Externos**
- Todo processamento é local
- Não temos base de dados externa
- Não fazemos backup na nuvem (por padrão)

## 8. Direitos do Usuário (LGPD)

### 📋 **Seus Direitos**
- **Acesso**: Todos os dados estão no seu dispositivo
- **Correção**: Ajuste configurações no app
- **Exclusão**: Use o botão "Apagar Tudo"
- **Portabilidade**: Dados ficam sempre com você
- **Revogação**: Desinstale o app a qualquer momento

### 📞 **Exercer Direitos**
- Use as configurações do próprio app
- Para questões complexas: abra uma issue no GitHub

## 9. Segurança

### 🛡️ **Medidas de Proteção**
- Código fonte aberto para auditoria
- Filtros de PII implementados
- Processamento local apenas
- Sem transmissão de dados

### 🔍 **Auditoria**
- Código disponível em: [GitHub Repository]
- Qualquer pessoa pode verificar o funcionamiento
- Builds reproducíveis para verificação

## 10. Menores de Idade

- Aplicativo destinado a motoristas profissionais (18+)
- Não coletamos dados de menores intencionalmente
- Se identificado uso por menores, dados serão apagados

## 11. Alterações na Política

### 📝 **Notificação de Mudanças**
- Alterações serão comunicadas via update do app
- Versão sempre disponível no app e GitHub
- Mudanças substanciais requerem nova aceitação

## 12. Lei Aplicável

Esta política é regida pela **Lei Geral de Proteção de Dados (LGPD)** brasileira e demais leis aplicáveis no Brasil.

## 13. Contato

### 📧 **Para Questões de Privacidade**
- **GitHub Issues**: [Repositório do Projeto]/issues
- **Emergências de Segurança**: Abra uma issue marcada como "security"

### 🌐 **Recursos**
- **Código fonte**: GitHub (público)
- **Documentação**: README.md no repositório
- **Reportar bugs**: GitHub Issues

---

## 📄 **Resumo Executivo**

✅ **100% processamento local - nenhum dado enviado para servidores**  
✅ **Filtros automáticos de informações pessoais**  
✅ **Código aberto para auditoria pública**  
✅ **Compatível com LGPD**  
✅ **Logs opcionais e locais apenas**  
✅ **Botão "apagar tudo" disponível**  

❌ **Não coletamos dados pessoais**  
❌ **Não enviamos informações para terceiros**  
❌ **Não fazemos rastreamento**  
❌ **Não temos analytics**  

---

**Esta política é efetiva a partir da instalação do aplicativo. Ao usar o Assistente R$/km, você concorda com estes termos.**
