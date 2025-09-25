#!/bin/bash

# Script para executar testes do KM County
# Uso: ./scripts/run-tests.sh [tipo]

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

success() {
    echo -e "${GREEN}✅ $1${NC}"
}

error() {
    echo -e "${RED}❌ $1${NC}"
}

warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Verificar se estamos no diretório correto
if [ ! -f "build.gradle.kts" ]; then
    error "Execute este script do diretório raiz do projeto"
    exit 1
fi

# Tipo de teste (padrão: all)
TEST_TYPE=${1:-"all"}

log "Executando testes do KM County (tipo: $TEST_TYPE)"

# Criar diretório de relatórios se não existir
mkdir -p test-reports

# Função para executar testes unitários
run_unit_tests() {
    log "Executando testes unitários..."

    if ./gradlew :core:test :ml:test; then
        success "Testes unitários passaram"

        # Copiar relatórios
        find . -name "*TEST-*.xml" -path "*/build/test-results/*" -exec cp {} test-reports/ \; 2>/dev/null || true

        return 0
    else
        error "Testes unitários falharam"
        return 1
    fi
}

# Função para executar testes instrumentados
run_instrumented_tests() {
    log "Executando testes instrumentados..."

    if ./gradlew :app:connectedAndroidTest; then
        success "Testes instrumentados passaram"

        # Copiar relatórios
        find . -name "*TEST-*.xml" -path "*/build/outputs/androidTest-results/*" -exec cp {} test-reports/ \; 2>/dev/null || true

        return 0
    else
        error "Testes instrumentados falharam"
        return 1
    fi
}

# Função para executar lint
run_lint() {
    log "Executando linting..."

    if ./gradlew lint; then
        success "Linting passou"

        # Copiar relatórios
        find . -name "lint-results*.html" -exec cp {} test-reports/ \; 2>/dev/null || true

        return 0
    else
        warning "Linting encontrou problemas"
        return 1
    fi
}

# Função para executar detekt (se configurado)
run_detekt() {
    log "Executando detekt..."

    if ./gradlew detekt; then
        success "Detekt passou"
        return 0
    else
        warning "Detekt encontrou problemas"
        return 1
    fi
}

# Função para executar testes OCR (se houver dataset)
run_ocr_tests() {
    log "Executando testes OCR..."

    if [ -d "tests/images" ] && [ "$(ls tests/images/*.png 2>/dev/null | wc -l)" -gt 0 ]; then
        log "Dataset de imagens encontrado (${ls tests/images/*.png 2>/dev/null | wc -l} imagens)"

        # TODO: Implementar testes OCR reais
        warning "Testes OCR ainda não implementados - usar dataset manualmente"
        return 0
    else
        warning "Dataset de imagens não encontrado, pulando testes OCR"
        return 0
    fi
}

# Executar testes baseado no tipo
case $TEST_TYPE in
    "unit")
        run_unit_tests
        ;;
    "instrumented")
        run_instrumented_tests
        ;;
    "lint")
        run_lint
        ;;
    "ocr")
        run_ocr_tests
        ;;
    "all")
        log "Executando todos os testes..."

        FAILED=0

        run_unit_tests || FAILED=1
        run_instrumented_tests || FAILED=1
        run_lint || FAILED=1
        run_detekt || FAILED=1
        run_ocr_tests || FAILED=1

        if [ $FAILED -eq 0 ]; then
            success "🎉 Todos os testes passaram!"
        else
            error "Alguns testes falharam. Verifique os relatórios em test-reports/"
            exit 1
        fi
        ;;
    *)
        error "Tipo de teste inválido: $TEST_TYPE"
        echo "Uso: $0 [unit|instrumented|lint|ocr|all]"
        exit 1
        ;;
esac

# Gerar relatório final
log "Gerando relatório de testes..."

REPORT_FILE="test-reports/TEST_REPORT_$(date +'%Y%m%d_%H%M%S').txt"

cat > "$REPORT_FILE" << EOF
Relatório de Testes - KM County
================================

Data: $(date)
Tipo de teste: $TEST_TYPE
Diretório: $(pwd)

Resumo dos Testes:
------------------

EOF

# Adicionar informações dos testes executados
echo "✅ Testes concluídos" >> "$REPORT_FILE"
echo "📊 Relatórios salvos em: test-reports/" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

if [ -d "test-reports" ]; then
    echo "Arquivos de relatório gerados:" >> "$REPORT_FILE"
    ls -la test-reports/ >> "$REPORT_FILE" 2>/dev/null || echo "Nenhum arquivo encontrado" >> "$REPORT_FILE"
fi

success "Relatório de testes gerado: $REPORT_FILE"

echo ""
echo "Para ver relatórios detalhados:"
echo "  - Unit tests: ./gradlew :core:test --info"
echo "  - Instrumented: ./gradlew :app:connectedAndroidTest --info"
echo "  - Lint: ./gradlew lint"
echo "  - Relatórios HTML: test-reports/"

exit 0
