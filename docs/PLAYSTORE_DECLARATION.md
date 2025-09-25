# Declaração para Google Play Store - KM County

**Aplicativo:** KM County - Assistente de Preços de Corridas
**Versão:** 1.0.0
**Data:** 25 de setembro de 2024

Este documento contém todas as informações necessárias para a submissão do KM County na Google Play Store, incluindo justificativas para permissões especiais e conformidade com políticas.

## 1. Visão Geral do Aplicativo

O KM County é um aplicativo open-source que auxilia motoristas de aplicativos de transporte (Uber, 99, etc.) a avaliar rapidamente a rentabilidade de corridas através do cálculo automático de R$/km e R$/min.

**Características principais:**
- Detecção automática de telas de pedido de corrida
- Cálculo em tempo real de métricas de rentabilidade
- Overlay não-intrusivo com indicadores visuais
- Processamento 100% local (on-device)
- Sem automação de aceitar/recusar corridas

## 2. Permissões e Justificativas

### 2.1. AccessibilityService (android.permission.BIND_ACCESSIBILITY_SERVICE)

**Permissão solicitada:** BIND_ACCESSIBILITY_SERVICE
**Uso:** Essencial para a funcionalidade principal do aplicativo

#### Justificativa Técnica
O AccessibilityService é usado exclusivamente para **LER** informações visíveis na tela dos aplicativos de transporte. O KM County:

1. **Monitora mudanças na tela** (TYPE_WINDOW_STATE_CHANGED, TYPE_WINDOW_CONTENT_CHANGED)
2. **Extrai texto estruturado** de elementos da interface
3. **Identifica padrões** que indicam tela de pedido de corrida
4. **Calcula métricas** baseadas nos dados extraídos

#### Por que AccessibilityService é necessário
- **Alternativa técnica não viável:** Não existe outra API do Android que permita leitura estruturada de conteúdo de outros apps
- **Limitação intencional:** O app NUNCA executa ações (performAction) nos outros aplicativos
- **Escopo mínimo:** Apenas leitura de texto, sem acesso a senhas, dados financeiros ou ações do usuário

#### Implementação Segura
- **Consentimento obrigatório:** Onboarding explica claramente o uso antes da ativação
- **Modo conservador:** Por padrão, prefere falsos negativos a falsos positivos
- **Transparência total:** Código open-source permite auditoria independente
- **Sem automação:** Nunca clica botões ou interage automaticamente

### 2.2. SYSTEM_ALERT_WINDOW (android.permission.SYSTEM_ALERT_WINDOW)

**Permissão solicitada:** SYSTEM_ALERT_WINDOW
**Uso:** Exibir overlay flutuante com cálculos

#### Justificativa
- **Overlay informativo:** Mostra apenas R$/km, R$/min e indicador de cor
- **Não intrusivo:** Pequeno, posicionável, sempre opcional
- **Sem interação:** Read-only, não captura toques
- **Consentimento:** Explicado claramente no onboarding

### 2.3. READ_EXTERNAL_STORAGE (para OCR fallback - opcional)

**Permissão solicitada:** READ_EXTERNAL_STORAGE (API < 29) / READ_MEDIA_IMAGES (API >= 29)
**Uso:** Opcional, apenas se usuário ativar OCR fallback

#### Justificativa
- **Funcionalidade opcional:** Usada apenas se MediaProjection + ML Kit falhar
- **Consentimento explícito:** Usuário deve ativar manualmente nas configurações
- **Processamento local:** Imagens processadas apenas no dispositivo
- **Descartadas imediatamente:** Não armazenadas permanentemente

## 3. Política de Privacidade e Conformidade

### 3.1. LGPD Compliance
- **Política completa:** Disponível em `/docs/PRIVACY_POLICY.md`
- **Processamento local:** 100% on-device, sem transmissão de dados
- **Dados pessoais:** Nenhum coletado ou transmitido
- **Consentimento:** Opt-in para funcionalidades opcionais

### 3.2. Tratamento de Dados
- **Coleta:** Apenas dados técnicos anônimos (se opt-in ativado)
- **Armazenamento:** Exclusivamente local no dispositivo
- **Compartilhamento:** Nenhum, exceto Google Firebase (opcional e anonimizado)
- **Exclusão:** Opção "Apagar Tudo" remove todos os dados locais

## 4. Funcionalidades Sensíveis

### 4.1. Acessibilidade
**Declaração obrigatória:**
> Este aplicativo usa o AccessibilityService para ler informações visuais de outros aplicativos de transporte, exclusivamente para calcular métricas de rentabilidade. NUNCA executa ações automáticas, não armazena dados pessoais, e todo processamento é local.

### 4.2. Captura de Tela (Opcional)
**Declaração:**
> A captura de tela é opcional e só ocorre com consentimento explícito do usuário. É usada apenas para OCR fallback quando o AccessibilityService não consegue extrair dados suficientes, e as imagens são processadas localmente e descartadas imediatamente.

## 5. Conteúdo do Aplicativo

### 5.1. Descrição na Play Store
```
KM County - Seu assistente inteligente para avaliar corridas

Calcule rapidamente se uma corrida vale a pena com R$/km e R$/min automáticos!

✅ Detecção automática de pedidos de corrida
✅ Cálculo em tempo real de rentabilidade
✅ Overlay discreto e informativo
✅ Processamento 100% local
✅ Sem automação de aceitar/recusar

O KM County lê informações da tela dos apps de transporte (como Uber, 99, etc.) e calcula automaticamente o valor por quilômetro e por minuto, ajudando você a decidir rapidamente se aceitar a corrida.

IMPORTANTE: Este app apenas CALCULA e INFORMA. Você decide se aceita ou recusa a corrida.

Características:
• Detecção automática de telas de pedido
• Cálculos precisos de R$/km e R$/min
• Indicadores visuais de rentabilidade
• Modo conservador para evitar falsos positivos
• Privacidade total - dados ficam no seu dispositivo
• Open source e auditável

Permissões necessárias para funcionar:
• Acessibilidade (para ler informações da tela)
• Sobrepor outros apps (para mostrar cálculos)

O app não automatiza nenhuma ação nos outros aplicativos.
```

### 5.2. Capturas de Tela Necessárias
1. **Tela de onboarding** - Explicação das permissões
2. **Tela de configurações** - Thresholds e preferências
3. **Simulação de uso** - Overlay mostrando cálculos
4. **Política de privacidade** - Tela de informações

### 5.3. Gráfico Promocional
- Ícone do app com sobreposição de cálculo "R$/km: R$ 2,50"
- Texto: "Avalie corridas em segundos"

## 6. Classificação de Conteúdo

**Classificação Indicativa:** Livre (L)
**Justificativa:** Aplicativo utilitário sem conteúdo impróprio

## 7. Testes e QA

### 7.1. Dispositivos Testados
- **Mínimo:** Android 6.0 (API 23)
- **Dispositivos:** Samsung Galaxy S9, Moto G7, Pixel 4a, Redmi Note 9
- **Versões:** Android 8, 9, 10, 11, 12, 13

### 7.2. Cenários de Teste
- ✅ Detecção de pedidos simulados
- ✅ Cálculos de R$/km e R$/min
- ✅ Overlay em diferentes posições
- ✅ Modo conservador ativado/desativado
- ✅ OCR fallback (opcional)
- ✅ Apagar dados funcionais

## 8. Suporte e Atualizações

### 8.1. Informações de Contato
- **Website:** https://github.com/seu-usuario/km-county
- **E-mail:** support@kmcounty.app (criar quando necessário)
- **Issues:** GitHub Issues para suporte

### 8.2. Plano de Atualizações
- **Frequência:** Correções críticas em até 72 horas
- **Atualizações de detecção:** Conforme mudanças nos apps de transporte
- **Política:** Sempre backward-compatible

## 9. Código Fonte e Open Source

### 9.1. Repositório Público
- **URL:** https://github.com/seu-usuario/km-county
- **Licença:** MIT License
- **Documentação:** README completo com instruções de build

### 9.2. Build Reproducible
- **Gradle:** Configurado para builds determinísticos
- **Dependencies:** Todas declaradas explicitamente
- **Signing:** Instruções claras para reproduzir builds oficiais

## 10. Considerações Finais

### 10.1. Conformidade com Políticas
- ✅ Não viola políticas de automação
- ✅ Respeita privacidade do usuário
- ✅ Não interfere em outros apps
- ✅ Código auditável e transparente

### 10.2. Benefício ao Usuário
O KM County oferece valor real aos motoristas, ajudando na tomada de decisões econômicas informadas sem automatizar processos ou comprometer a privacidade.

### 10.3. Compromisso de Desenvolvimento
- Manter código open-source
- Atualizações regulares baseadas em feedback
- Correções de segurança prioritárias
- Transparência total sobre mudanças

---

**Declaração de Veracidade:**
Todas as informações fornecidas são verdadeiras e precisas. O aplicativo foi desenvolvido seguindo as melhores práticas de privacidade e segurança, e está pronto para distribuição na Google Play Store.

**Data da declaração:** 25 de setembro de 2024
**Responsável:** Desenvolvedor KM County
