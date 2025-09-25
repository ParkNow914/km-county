# Proposta de Distribuição - KM County

## Visão Geral

O KM County é um aplicativo Android open-source que auxilia motoristas de aplicativos de transporte a avaliar corridas através de cálculos automáticos de R$/km e R$/min. Esta proposta detalha estratégias de distribuição, considerações legais e recomendações para lançamento bem-sucedido.

## Estratégia de Distribuição

### 1. Distribuição Primária: F-Droid

**Recomendação:** F-Droid como canal principal de distribuição

#### Justificativas
- **Filosofia alinhada:** F-Droid foca em software livre e privacidade
- **Público-alvo:** Usuários preocupados com privacidade (motoristas técnicos)
- **Sem restrições:** Não há limitações quanto ao uso de AccessibilityService
- **Build verification:** Permite verificação independente dos APKs

#### Processo de Inclusão
1. **Preparação do repositório:**
   - Criar `fastlane/metadata/android/pt-BR/` com metadados em português
   - Preparar ícones e gráficos promocionais
   - Criar changelog detalhado

2. **Metadata necessário:**
   ```
   fastlane/metadata/android/pt-BR/
   ├── title.txt
   ├── short_description.txt
   ├── full_description.txt
   ├── video.txt (opcional)
   └── images/
       ├── featureGraphic.png
       ├── icon.png
       └── screenshots/
   ```

3. **Submissão:**
   - Fork do repositório fdroiddata
   - Criar merge request com metadata
   - Aguardar review (tipicamente 1-2 semanas)

### 2. Distribuição Secundária: Google Play Store

**Recomendação:** Play Store como canal secundário com restrições

#### Riscos e Mitigações
- **Risco:** Rejeição por uso de AccessibilityService
- **Mitigação:** PLAYSTORE_DECLARATION.md detalhada e justificativa técnica sólida
- **Risco:** Interpretação errônea como "automação"
- **Mitigação:** Ênfase no read-only e ausência de performAction

#### Estratégia de Lançamento
1. **Beta testing interno** (100 usuários) por 2 semanas
2. **Lançamento gradual:** 20% → 50% → 100% dos usuários
3. **Monitoramento ativo** de rejeições e feedback

### 3. Distribuição Direta: GitHub Releases

**Recomendação:** Canal de contingência e para usuários avançados

#### Vantagens
- **Controle total:** Sem intermediação de stores
- **Usuários técnicos:** Podem verificar assinatura e código
- **Atualizações rápidas:** Deploy imediato após CI/CD

#### Implementação
- **APK assinado** em releases
- **Instruções claras** de instalação
- **Hashes para verificação** de integridade

## Considerações Legais

### 1. LGPD Compliance

#### Documentação Preparada
- ✅ Política de privacidade completa e LGPD-compliant
- ✅ Textos de consentimento em português
- ✅ Onboarding com explicações claras
- ✅ Opção "Apagar Tudo" implementada

#### Riscos Identificados
- **Processamento de dados pessoais:** Mitigado por filtragem automática de PII
- **Consentimento inadequado:** Mitigado por onboarding obrigatório
- **Transparência insuficiente:** Mitigado por código open-source

### 2. Direitos Autorais e Propriedade Intelectual

#### Licença MIT
- Permite uso comercial e modificações
- Preserva atribuição original
- Compatível com Android e bibliotecas usadas

#### Dependências
- **Google ML Kit:** Licença gratuita para uso não-comercial
- **Firebase:** Free tier suficiente para uso esperado
- **Outras libs:** Todas MIT/Apache compatíveis

### 3. Responsabilidades e Isenções

#### Isenção de Responsabilidades
**Texto recomendado para README:**
```
IMPORTANTE: O KM County é uma ferramenta auxiliar que fornece cálculos baseados em dados visíveis na tela. O usuário é o único responsável por decisões de aceitar ou recusar corridas. O desenvolvedor não se responsabiliza por perdas financeiras, decisões incorretas ou qualquer uso indevido do aplicativo.
```

#### Suporte e Manutenção
- **Suporte gratuito:** Issues no GitHub
- **Correções críticas:** 72 horas
- **Atualizações pagas:** Melhorias e novos recursos

## Estratégia de Marketing

### 1. Público-Alvo
- **Primário:** Motoristas de Uber, 99, Cabify, etc.
- **Secundário:** Desenvolvedores interessados em acessibilidade
- **Terciário:** Comunidade open-source

### 2. Canais de Comunicação
- **GitHub:** Repositório principal e documentação
- **Reddit:** r/uberdrivers, r/99app, r/brasil
- **Telegram/Grupos:** Comunidades de motoristas
- **LinkedIn:** Networking com empresas de transporte

### 3. Mensagem Central
```
"Decida corridas em segundos, não em minutos.
R$/km e R$/min automáticos, sem perder tempo."
```

## Plano de Contingência

### 1. Rejeição no Google Play
- **Ação:** Focar exclusivamente no F-Droid
- **Comunicação:** "Disponível no F-Droid por questões de privacidade"
- **Marketing:** Enfatizar aspecto open-source e auditável

### 2. Problemas Técnicos
- **OCR fallback:** Implementado como contingência
- **Modo offline:** Sempre funcionou sem internet
- **Atualizações:** Mecanismo para baixar regras atualizadas

### 3. Mudanças nos Apps de Transporte
- **Monitoramento:** Usuários reportam mudanças via Issues
- **Atualizações:** Release patches conforme necessário
- **Comunidade:** Incentivar contribuições para novos patterns

## Métricas de Sucesso

### 1. Adoção
- **Objetivo:** 1.000 usuários ativos mensais (6 meses)
- **Métrica:** Downloads + Issues ativos + Stars no GitHub

### 2. Satisfação
- **Objetivo:** Rating 4.5+ no F-Droid, baixo volume de issues
- **Métrica:** GitHub Issues, feedback qualitativo

### 3. Impacto
- **Objetivo:** Melhorar decisões econômicas dos motoristas
- **Métrica:** Feedback qualitativo, estudos de caso

## Cronograma Sugerido

### Fase 1: Preparação (Semanas 1-2)
- ✅ Finalizar código e testes
- ✅ Preparar documentação completa
- ✅ Configurar CI/CD

### Fase 2: Lançamento F-Droid (Semanas 3-4)
- Submeter para F-Droid
- Preparar materiais de marketing
- Criar comunidades de usuários

### Fase 3: Lançamento Play Store (Semanas 5-6)
- Submeter para Play Store
- Monitorar aprovação
- Coletar feedback inicial

### Fase 4: Otimização (Mês 2+)
- Analisar métricas de uso
- Implementar melhorias baseadas em feedback
- Expandir suporte a novos apps de transporte

## Considerações Técnicas para Distribuição

### 1. Build Variants
- **F-Droid:** Build sem Google Services
- **Play Store:** Build completo com Firebase
- **GitHub:** Build universal

### 2. Signing Keys
- **F-Droid:** Build reproduzível sem chave proprietária
- **Play Store:** Chave de produção protegida
- **GitHub:** Chave de desenvolvimento para releases

### 3. Versionamento
- **Semantic versioning:** MAJOR.MINOR.PATCH
- **Pre-releases:** alpha, beta, rc
- **Changelog detalhado** em cada release

## Riscos e Mitigações

### 1. Risco: Mudanças nos apps de transporte
**Mitigação:** Parser modular, comunidade contribuidora, OCR fallback

### 2. Risco: Rejeição nas stores
**Mitigação:** Estratégia multi-canal, foco no F-Droid

### 3. Risco: Problemas de privacidade
**Mitigação:** Código auditável, transparência total, conformidade LGPD

### 4. Risco: Concorrência
**Mitigação:** Foco em open-source, comunidade, diferenciação técnica

## Conclusão

O KM County representa uma solução inovadora para um problema real enfrentado por motoristas de aplicativo. A estratégia de distribuição multi-canal, com ênfase no F-Droid e respaldo no GitHub, maximiza o alcance enquanto mantém os princípios de privacidade e abertura.

A combinação de código open-source, documentação completa e estratégia legal sólida posiciona o projeto para sucesso sustentável na comunidade de motoristas e desenvolvedores.

---

**Data da proposta:** 25 de setembro de 2024
**Versão:** 1.0
