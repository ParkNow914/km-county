# Dataset de Testes OCR - KM County

Este diretório contém dados para testes do sistema OCR (Optical Character Recognition) usado como fallback quando o AccessibilityService não consegue extrair dados suficientes.

## Estrutura

```
tests/
├── images/          # Dataset de imagens de teste
├── unit/           # Testes unitários
├── instrumented/   # Testes instrumentados
└── README.md       # Este arquivo
```

## Dataset de Imagens

O dataset deve conter capturas de tela reais de pedidos de corrida de diferentes apps de transporte, com as seguintes características:

### Critérios para Imagens de Teste

1. **Apps Suportados:**
   - Uber Driver
   - 99 Driver
   - Cabify Driver
   - Outros apps de transporte

2. **Condições de Captura:**
   - Resolução nativa do dispositivo
   - Boa iluminação
   - Texto legível (sem blur excessivo)
   - Diferentes densidades de tela (mdpi, hdpi, xhdpi, xxhdpi)

3. **Conteúdo Relevante:**
   - Valor total da corrida
   - Distância estimada
   - Tempo estimado (se disponível)
   - Informações do passageiro (endereço, nome - para testes de PII filtering)

### Formato das Imagens

- **Formato:** PNG (preferencial) ou JPEG
- **Nome:** `app_nome_descricao.png`
  - Exemplos:
    - `uber_corrida_basica.png`
    - `99_corrida_com_tempo.png`
    - `cabify_corrida_longa.png`

### Dados Esperados por Imagem

Para cada imagem, deve haver um arquivo JSON correspondente com os dados esperados:

```json
{
  "image": "uber_corrida_basica.png",
  "expected": {
    "price": 25.50,
    "distance": 12.5,
    "time": 20,
    "confidence_threshold": 0.8
  },
  "description": "Corrida básica do Uber com preço, distância e tempo",
  "app": "uber",
  "device_density": "xhdpi"
}
```

## Como Usar o Dataset

### Testes Automatizados

```kotlin
// Exemplo de teste OCR
@Test
fun testUberRideParsing() {
    val image = loadImage("uber_corrida_basica.png")
    val result = ocrProcessor.recognizeText(image)

    assertEquals(25.50, result.price, 0.01)
    assertEquals(12.5, result.distance, 0.1)
    assertTrue(result.confidence > 0.8)
}
```

### Testes Manuais

Para desenvolvimento e debugging:

1. Execute o app em modo debug
2. Ative o OCR fallback nas configurações
3. Use as imagens do dataset para testar
4. Compare resultados com dados esperados

## Métricas de Qualidade

### Acurácia Mínima Requerida

- **Preço:** ≥ 90% de acurácia na extração
- **Distância:** ≥ 85% de acurácia na extração
- **Tempo:** ≥ 80% de acurácia na extração
- **Confiança geral:** ≥ 75% de confiança média

### Relatório de Acurácia

Após executar testes no dataset completo:

```
================================
RELATÓRIO DE ACURÁCIA OCR
================================

Dataset: 50 imagens
Preço - Acurácia: 92.3% (46/50 corretos)
Distância - Acurácia: 88.0% (44/50 corretos)
Tempo - Acurácia: 82.0% (41/50 corretos)
Confiança Média: 78.5%

Principais erros:
- Textos muito pequenos: 4 casos
- Fontes customizadas: 3 casos
- Overlays escuros: 2 casos
```

## Contribuição

### Adicionando Novas Imagens

1. Capture telas reais de pedidos de corrida
2. Remova dados pessoais (nomes, endereços, CPFs)
3. Adicione metadados no JSON correspondente
4. Teste com o parser atual
5. Faça commit das mudanças

### Melhorando o Parser

Se a acurácia estiver abaixo do mínimo:
1. Identifique padrões de falha
2. Ajuste regex patterns
3. Adicione casos especiais
4. Teste novamente no dataset

## Considerações Éticas

- **Privacidade:** Nunca inclua dados reais de passageiros
- **Consentimento:** Use apenas dados de corridas próprias
- **Anonimização:** Remova todas as informações de identificação
- **Uso ético:** Dataset apenas para desenvolvimento e testes

## Scripts Úteis

```bash
# Executar testes OCR
./scripts/run-tests.sh ocr

# Validar dataset
./scripts/validate-dataset.sh

# Gerar relatório de acurácia
./scripts/generate-accuracy-report.sh
```
