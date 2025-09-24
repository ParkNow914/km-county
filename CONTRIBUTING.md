# Guia de Contribuição - Assistente R$/km

Obrigado pelo interesse em contribuir com o **Assistente R$/km**! Este documento fornece diretrizes para contribuições ao projeto.

## 🚀 Como Contribuir

### 1. Reportar Bugs

Antes de reportar um bug:
- Verifique se já existe uma issue similar
- Teste na versão mais recente
- Colete informações de debug

**Template para Bug Report:**
```markdown
## 🐛 Descrição do Bug
[Descrição clara do problema]

## 📱 Ambiente
- Versão do app: 
- Versão do Android: 
- Modelo do dispositivo: 
- App de transporte testado: 

## 🔄 Passos para Reproduzir
1. 
2. 
3. 

## 🎯 Comportamento Esperado
[O que deveria acontecer]

## ❌ Comportamento Atual
[O que realmente acontece]

## 📸 Screenshots/Logs
[Se aplicável]
```

### 2. Sugerir Funcionalidades

**Template para Feature Request:**
```markdown
## 💡 Descrição da Funcionalidade
[Descrição clara da funcionalidade desejada]

## 🎯 Problema que Resolve
[Qual problema esta funcionalidade resolve]

## 💭 Solução Proposta
[Como você imagina que deveria funcionar]

## 🔄 Alternativas Consideradas
[Outras formas de resolver o problema]

## 📋 Critérios de Aceitação
- [ ] Critério 1
- [ ] Critério 2
```

## 🛠️ Desenvolvimento

### Configuração do Ambiente

1. **Pré-requisitos:**
   ```bash
   - Android Studio Arctic Fox+
   - JDK 11+
   - Android SDK 23-34
   - Git
   ```

2. **Setup inicial:**
   ```bash
   git clone https://github.com/[usuario]/kmcounty.git
   cd kmcounty
   git checkout -b feature/sua-funcionalidade
   ```

3. **Instalar dependências:**
   ```bash
   ./gradlew build
   ```

### Estrutura do Projeto

```
/app                 # Módulo principal Android
├── /accessibility   # AccessibilityService
├── /data           # Repositórios e modelos
├── /overlay        # Sistema de overlay
├── /ui             # Interface de usuário
└── /utils          # Utilitários

/core               # Parser e lógica de negócio
├── /model          # Modelos de dados
└── /parser         # Parsers de texto

/ml                 # Módulo de ML Kit OCR
└── /                # Serviços de reconhecimento

/docs               # Documentação
/scripts            # Scripts de build e deploy
/.github            # Workflows CI/CD
```

### Padrões de Código

#### Kotlin Style Guide

Seguimos o [Kotlin Coding Convention](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Correto
class RideDetectionService : AccessibilityService() {
    private val parser: RideParser by inject()
    
    private fun processEvent(event: AccessibilityEvent) {
        // Implementation
    }
}

// ❌ Incorreto
class rideDetectionService:AccessibilityService(){
    private val parser:RideParser by inject()
    private fun processEvent(event:AccessibilityEvent){
        // Implementation
    }
}
```

#### Compose Guidelines

```kotlin
// ✅ Correto - Preview separado
@Preview
@Composable
private fun MainScreenPreview() {
    RidePricingTheme {
        MainScreen(/* parameters */)
    }
}

// ✅ Correto - Estados hoisted
@Composable
fun MainScreen(
    uiState: MainUiState,
    onEvent: (MainEvent) -> Unit
) {
    // Implementation
}
```

#### Documentação de Código

```kotlin
/**
 * Serviço de acessibilidade que monitora apps de transporte
 * 
 * Este serviço:
 * - Monitora mudanças de tela em apps específicos
 * - Extrai informações de preços e distâncias
 * - Filtra automaticamente PII
 * - Nunca interage com outros apps
 */
@AndroidEntryPoint
class RideDetectionService : AccessibilityService() {
    // Implementation
}
```

### Testes

#### Unit Tests (Obrigatório)

```kotlin
@Test
fun `parseRideInfo should extract correct values from Uber text`() {
    // Given
    val texts = listOf("R$ 12,50", "3,2 km", "corrida")
    
    // When
    val result = parser.parseRideInfo(texts, "com.ubercab")
    
    // Then
    assertThat(result).isNotNull()
    assertThat(result!!.totalPrice).isEqualTo(12.50f)
    assertThat(result.distance).isEqualTo(3.2f)
}
```

#### Instrumented Tests

```kotlin
@Test
fun onboardingFlow_completesSuccessfully() {
    composeTestRule.setContent {
        OnboardingScreen(/* parameters */)
    }
    
    composeTestRule
        .onNodeWithText("Próximo")
        .performClick()
    
    composeTestRule
        .onNodeWithText("Concluir")
        .assertIsDisplayed()
}
```

### Commits e Pull Requests

#### Formato de Commit

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

```bash
feat: adicionar suporte ao app Cabify
fix: corrigir parsing de preços com vírgula
docs: atualizar guia de instalação
test: adicionar testes para PiiFilter
refactor: simplificar lógica do overlay
```

Tipos válidos:
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `test`: Testes
- `refactor`: Refatoração
- `perf`: Melhoria de performance
- `chore`: Manutenção

#### Pull Request Template

```markdown
## 📋 Descrição
[Breve descrição das mudanças]

## 🔗 Issue Relacionada
Fixes #123

## 🧪 Testes
- [ ] Unit tests adicionados/atualizados
- [ ] Instrumented tests adicionados/atualizados
- [ ] Testes manuais realizados

## 📱 Teste Manual
- [ ] Testado no Android 8.0+
- [ ] Testado com Uber
- [ ] Testado com 99
- [ ] Overlay funcionando
- [ ] Sem vazamentos de PII

## 📋 Checklist
- [ ] Código segue padrões do projeto
- [ ] Documentação atualizada
- [ ] Testes passando
- [ ] Sem warnings de lint
- [ ] Performance não impactada
```

## 🔒 Segurança e Privacidade

### Diretrizes de Segurança

1. **Nunca commitar:**
   - Chaves API
   - Senhas ou tokens
   - Dados pessoais de teste
   - Keystores de produção

2. **PII (Informações Pessoais):**
   - Sempre usar `PiiFilter` antes de processar textos
   - Testar filtros com dados variados
   - Nunca logar informações pessoais

3. **Permissões:**
   - Documentar uso de permissões sensíveis
   - Justificar necessidade no código
   - Implementar modo de degradação

### Code Review de Segurança

Checklist para revisores:

- [ ] Nenhum dado pessoal é persistido
- [ ] Filtros PII funcionando corretamente
- [ ] Permissões justificadas e documentadas
- [ ] Logs não contêm informações sensíveis
- [ ] Processamento apenas local

## 🎨 UI/UX Guidelines

### Material Design 3

- Usar componentes Material 3
- Seguir guidelines de acessibilidade
- Temas claro/escuro suportados
- Tipografia consistente

### Acessibilidade

```kotlin
// ✅ Correto
Icon(
    imageVector = Icons.Default.Settings,
    contentDescription = "Configurações",
    modifier = Modifier.semantics {
        role = Role.Button
    }
)

// ❌ Incorreto
Icon(
    imageVector = Icons.Default.Settings,
    contentDescription = null // Sem descrição
)
```

### Responsividade

- Testar em diferentes tamanhos de tela
- Usar `Modifier.fillMaxWidth()` apropriadamente
- Considerar orientação landscape

## 🚀 Release Process

### Preparação para Release

1. **Testes completos:**
   ```bash
   ./scripts/run-tests.sh
   ```

2. **Build de release:**
   ```bash
   ./scripts/build-release.sh
   ```

3. **Validação:**
   - Teste em dispositivos reais
   - Verificar assinatura digital
   - Teste de instalação limpa

### Versionamento

Seguimos [Semantic Versioning](https://semver.org/):

- `MAJOR`: Mudanças incompatíveis
- `MINOR`: Funcionalidades compatíveis
- `PATCH`: Correções de bugs

Exemplo: `1.2.3`

## 📚 Recursos Úteis

### Documentação

- [README.md](README.md) - Visão geral do projeto
- [INSTALL.md](docs/INSTALL.md) - Guia de instalação
- [PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md) - Política de privacidade

### Ferramentas de Desenvolvimento

- **ktlint**: Formatação de código
- **detekt**: Análise estática
- **Timber**: Logging estruturado
- **Hilt**: Injeção de dependência

### Links Externos

- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

## 💬 Comunicação

### Canais

- **Issues**: Bugs e feature requests
- **Discussions**: Perguntas e ideias
- **Pull Requests**: Code review e discussões técnicas

### Código de Conduta

Este projeto segue o [Contributor Covenant](https://www.contributor-covenant.org/):

- Seja respeitoso e inclusivo
- Use linguagem construtiva
- Foque no que é melhor para a comunidade
- Aceite feedback construtivo

## 🏆 Reconhecimento

Contribuidores são reconhecidos em:

- README.md (seção Contributors)
- Release notes
- Documentação relevante

## ❓ Dúvidas?

- Abra uma [Discussion](../../discussions)
- Crie uma [Issue](../../issues) com label `question`
- Consulte a documentação em `/docs`

---

**Obrigado por contribuir com o Assistente R$/km! 🚗💰**
