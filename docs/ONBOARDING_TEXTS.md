# Textos de Onboarding - KM County

Este arquivo contém todos os textos exibidos durante o processo de onboarding e configuração inicial do aplicativo, conforme requisitos de transparência e consentimento (LGPD).

## Tela de Boas-Vindas

**Título:** Bem-vindo ao KM County

**Subtítulo:** Seu assistente inteligente para avaliar corridas

**Descrição:**
O KM County é um aplicativo que ajuda motoristas de aplicativo a avaliar rapidamente se uma corrida vale a pena, calculando automaticamente R$/km e R$/min baseado nas informações da tela.

**Características principais:**
- ✅ Detecção automática de pedidos de corrida
- ✅ Cálculo em tempo real de R$/km e R$/min
- ✅ Overlay discreto e não intrusivo
- ✅ Processamento 100% local no seu dispositivo
- ✅ Sem automação de aceitar/recusar corridas

## Tela de Consentimento - Permissões

**Título:** Permissões Necessárias

**Subtítulo:** Precisamos de algumas permissões para funcionar

### Permissão de Acessibilidade
**Ícone:** ♿
**Título:** Acesso à Acessibilidade
**Descrição:**
Esta permissão permite que o app leia informações da tela dos aplicativos de transporte (como Uber, 99, etc.) para identificar quando um pedido de corrida aparece e extrair os valores de preço e distância.

**Por que precisamos:**
- Identificar telas de pedido de corrida automaticamente
- Extrair preço total, distância e tempo estimado
- Calcular R$/km e R$/min para você

**Garantia:** O app NUNCA executa ações automáticas nos outros apps. Apenas lê informações visuais.

### Permissão de Sobrepor Outros Apps
**Ícone:** 📱
**Título:** Exibir Sobre Outros Apps
**Descrição:**
Esta permissão permite mostrar um pequeno overlay flutuante com os cálculos de R$/km e R$/min quando um pedido de corrida é detectado.

**Por que precisamos:**
- Mostrar os cálculos de forma rápida e discreta
- Não interferir no funcionamento dos apps de transporte
- Manter você informado sem abrir o KM County

**Garantia:** O overlay é sempre opcional e pode ser desativado a qualquer momento.

## Tela de Consentimento - Privacidade

**Título:** Privacidade e Segurança

**Subtítulo:** Seus dados ficam seguros no seu dispositivo

### Como Funcionamos
**Texto principal:**
Todo o processamento acontece exclusivamente no seu dispositivo Android. Nenhuma informação é enviada para servidores externos, exceto para os serviços do Google que você optar por ativar (Analytics e Crash Reporting).

### O que Coletamos (se ativado)
- **Logs de uso (opcional):** Apenas estatísticas anônimas sobre o uso do app
- **Relatórios de erro (opcional):** Informações técnicas sobre crashes para melhorar o app
- **Dados de performance (opcional):** Métricas de velocidade do app

**Importante:** Todos os dados são anonimizados e não incluem informações pessoais, localização ou dados das corridas.

### O que NÃO coletamos
- ❌ Dados pessoais (nome, CPF, telefone, endereço)
- ❌ Informações das corridas (valores, rotas, passageiros)
- ❌ Localização em tempo real
- ❌ Histórico de uso de outros apps
- ❌ Dados financeiros ou bancários

## Tela de Configuração Inicial

**Título:** Configurações Iniciais

**Subtítulo:** Personalize o app para suas necessidades

### Thresholds de Cor
**Descrição:** Configure os valores de referência para as cores do indicador:

- **🟢 Verde:** Corridas muito rentáveis (R$/km ≥ 1.50)
- **🟡 Laranja:** Corridas medianas (0.80 ≤ R$/km < 1.50)
- **🔴 Vermelho:** Corridas pouco rentáveis (R$/km < 0.80)

### Modo Conservador
**Descrição:** Ative para reduzir falsos positivos
Quando ativado, o app é mais rigoroso na detecção de telas de corrida, evitando mostrar overlays em situações duvidosas.

## Tela Final de Conclusão

**Título:** Tudo Pronto!

**Subtítulo:** O KM County está configurado e funcionando

**Texto de conclusão:**
Parabéns! O KM County está pronto para ajudar você a avaliar corridas de forma inteligente.

**Próximos passos:**
1. Abra seu app de transporte preferido
2. Quando um pedido de corrida aparecer, o KM County mostrará automaticamente R$/km e R$/min
3. Use as cores como guia para decidir aceitar ou não

**Dica:** Você pode ajustar as configurações a qualquer momento no menu do app.

---

## Textos de Erro e Avisos

### Erro: Permissão de Acessibilidade Não Concedida
**Título:** Permissão Necessária
**Mensagem:** Para funcionar, o KM County precisa da permissão de Acessibilidade. Toque em "Configurar" para ativar.

### Erro: Permissão de Overlay Não Concedida
**Título:** Permissão de Overlay Necessária
**Mensagem:** Para mostrar os cálculos, precisamos da permissão para exibir sobre outros apps. Toque em "Configurar" para ativar.

### Aviso: Modo Conservador Ativado
**Texto:** O modo conservador está ativado. Isso pode fazer com que algumas corridas não sejam detectadas automaticamente para evitar falsos positivos.

### Aviso: Logs Desativados
**Texto:** Os logs estão desativados. Se encontrar problemas, ative temporariamente para nos ajudar a melhorar o app.

---

## Textos de Configurações

### Seção: Aparência
- **Tema:** Claro / Escuro / Automático
- **Tamanho do Overlay:** Pequeno / Médio / Grande
- **Posição do Overlay:** Superior Esquerdo / Superior Direito / Inferior Esquerdo / Inferior Direito

### Seção: Detecção
- **Sensibilidade de Detecção:** Baixa / Média / Alta
- **OCR Fallback:** Ativado / Desativado
- **Modo Conservador:** Ativado / Desativado

### Seção: Thresholds
- **Verde (R$/km ≥ ):** [campo numérico]
- **Laranja (R$/km ≥ ):** [campo numérico]
- **Vermelho (R$/km < ):** [campo numérico]

### Seção: Privacidade
- **Analytics:** Ativado / Desativado
- **Crash Reporting:** Ativado / Desativado
- **Logs Locais:** Ativado / Desativado
- **Apagar Todos os Dados:** [botão vermelho]

### Seção: Sobre
- **Versão:** 1.0.0
- **Política de Privacidade:** [link]
- **Código Fonte:** [link]
- **Reportar Problema:** [link]

---

## Textos de Overlay

### Overlay Básico
```
R$/km: R$ 2,50
R$/min: R$ 0,60
```

### Overlay Expandido (ao tocar)
```
R$/km: R$ 2,50  🟢 Excelente
R$/min: R$ 0,60
Distância: 15,2 km
Valor Total: R$ 38,00
Tempo Estimado: 25 min
```

### Overlay com Baixa Confiança
```
R$/km: R$ 1,80  ❓ Baixa confiança
Verifique os valores
```

---

## Textos de Notificações

### Notificação de Primeira Inicialização
**Título:** KM County Ativado
**Texto:** Toque para configurar permissões necessárias

### Notificação de Atualização Disponível
**Título:** Nova Versão Disponível
**Texto:** Atualização de detecção de corridas disponível

### Notificação de Problema Detectado
**Título:** Problema de Detecção
**Texto:** Algumas corridas podem não estar sendo detectadas. Verifique as configurações.
