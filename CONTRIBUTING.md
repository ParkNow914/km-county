# Guia de Contribuição

Obrigado pelo seu interesse em contribuir para o KM County! Este guia fornece informações sobre como você pode ajudar a melhorar este projeto.

## Como Contribuir

### Reportando Bugs

1. Verifique se o bug já não foi reportado [aqui](https://github.com/seu-usuario/km-county/issues).
2. Se não encontrar, crie uma nova issue com uma descrição clara e detalhada do problema.
3. Inclua etapas para reproduzir o bug, comportamento esperado e comportamento real.
4. Adicione informações sobre seu dispositivo e versão do Android.

### Sugerindo Melhorias

1. Verifique se a melhoria já não foi sugerida [aqui](https://github.com/seu-usuario/km-county/issues).
2. Crie uma nova issue com uma descrição clara do que você gostaria de ver implementado.
3. Explique por que você acha que esta melhoria seria útil.

### Enviando Pull Requests

1. Crie um fork do repositório.
2. Crie uma branch para sua feature/correção: `git checkout -b feature/nova-funcionalidade`
3. Faça commit das suas alterações: `git commit -m 'Adiciona nova funcionalidade'`
4. Envie para o seu fork: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request para a branch `main`

## Padrões de Código

- Siga o [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use nomes descritivos para variáveis e funções
- Documente funções públicas com KDoc
- Mantenha as funções pequenas e focadas em uma única responsabilidade
- Escreva testes unitários para novas funcionalidades

## Ambiente de Desenvolvimento

### Requisitos

- Android Studio Flamingo (2022.2.1) ou superior
- JDK 17
- Android SDK 34
- Kotlin 1.9.0

### Configuração

1. Faça um fork do repositório
2. Clone o repositório: `git clone https://github.com/seu-usuario/km-county.git`
3. Abra o projeto no Android Studio
4. Sincronize o projeto com os arquivos do Gradle
5. Execute o aplicativo em um emulador ou dispositivo físico

## Testes

Certifique-se de executar os testes antes de enviar um Pull Request:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Diretrizes de Commit

- Use mensagens de commit descritivas
- Referencie issues relacionadas quando aplicável
- Siga o padrão: `tipo(escopo): descrição`

Exemplos:
- `feat(parser): adiciona suporte a novo formato de preço`
- `fix(overlay): corrige posicionamento em telas grandes`
- `docs(readme): atualiza instruções de instalação`

## Código de Conduta

Este projeto segue o [Código de Conduta de Código Aberto](https://www.contributor-covenant.org/version/2/0/code_of_conduct/).
Ao participar, você concorda em seguir este código.

## Licença

Ao contribuir, você concorda que suas contribuições serão licenciadas sob a licença MIT.
