#!/bin/bash

# Script de Build e Release do KM County
# Uso: ./scripts/build-release.sh [version]

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

# Verificar versão
VERSION=${1:-"1.0.0"}
VERSION_CODE=$(echo $VERSION | tr -d '.' | tr -d '-')

log "Iniciando build de release v$VERSION (code: $VERSION_CODE)"

# Verificar se temos as chaves necessárias
if [ ! -f "keystore.properties" ]; then
    error "Arquivo keystore.properties não encontrado!"
    error "Configure suas chaves de assinatura antes de fazer o build."
    exit 1
fi

# Verificar se o keystore existe
KEYSTORE_PATH=$(grep "storeFile" keystore.properties | cut -d'=' -f2)
if [ ! -f "$KEYSTORE_PATH" ]; then
    warning "Keystore não encontrado: $KEYSTORE_PATH"
    warning "Certifique-se de que o caminho no keystore.properties está correto."
fi

# Limpar builds anteriores
log "Limpando builds anteriores..."
./gradlew clean

# Executar testes
log "Executando testes..."
if ./gradlew test; then
    success "Testes passaram com sucesso"
else
    error "Testes falharam! Corrija os erros antes de continuar."
    exit 1
fi

# Executar lint
log "Executando linting..."
if ./gradlew lint; then
    success "Linting passou com sucesso"
else
    warning "Linting encontrou problemas, mas continuando..."
fi

# Build de release
log "Fazendo build de release..."
if ./gradlew assembleRelease; then
    success "Build de release concluído com sucesso"
else
    error "Build falhou! Verifique os erros acima."
    exit 1
fi

# Verificar artefatos gerados
APK_PATH="app/build/outputs/apk/release/app-release.apk"
AAB_PATH="app/build/outputs/bundle/release/app-release.aab"

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(stat -f%z "$APK_PATH" 2>/dev/null || stat -c%s "$APK_PATH" 2>/dev/null || echo "unknown")
    success "APK gerado: $APK_PATH (${APK_SIZE} bytes)"
fi

if [ -f "$AAB_PATH" ]; then
    AAB_SIZE=$(stat -f%z "$AAB_PATH" 2>/dev/null || stat -c%s "$AAB_PATH" 2>/dev/null || echo "unknown")
    success "AAB gerado: $AAB_PATH (${AAB_SIZE} bytes)"
fi

# Verificar assinatura
log "Verificando assinatura do APK..."
if command -v apksigner &> /dev/null; then
    if apksigner verify --print-certs "$APK_PATH" &>/dev/null; then
        success "APK está corretamente assinado"
    else
        error "APK não está assinado ou assinatura é inválida!"
        exit 1
    fi
else
    warning "apksigner não encontrado, pulando verificação de assinatura"
fi

# Criar diretório de release
RELEASE_DIR="releases/v$VERSION"
mkdir -p "$RELEASE_DIR"

# Copiar artefatos
if [ -f "$APK_PATH" ]; then
    cp "$APK_PATH" "$RELEASE_DIR/"
    success "APK copiado para $RELEASE_DIR/"
fi

if [ -f "$AAB_PATH" ]; then
    cp "$AAB_PATH" "$RELEASE_DIR/"
    success "AAB copiado para $RELEASE_DIR/"
fi

# Gerar changelog se existir
if [ -f "CHANGELOG.md" ]; then
    cp CHANGELOG.md "$RELEASE_DIR/"
fi

# Gerar checksums
log "Gerando checksums..."
cd "$RELEASE_DIR"
if command -v sha256sum &> /dev/null; then
    sha256sum * > checksums.sha256
    success "Checksums SHA256 gerados"
elif command -v shasum &> /dev/null; then
    shasum -a 256 * > checksums.sha256
    success "Checksums SHA256 gerados"
else
    warning "Ferramenta de checksum não encontrada"
fi
cd ../..

# Criar arquivo de release notes
cat > "$RELEASE_DIR/README.md" << EOF
# KM County v$VERSION

**Data de lançamento:** $(date +'%Y-%m-%d')
**Versão:** $VERSION
**Código da versão:** $VERSION_CODE

## Arquivos

$(if [ -f "app-release.apk" ]; then echo "- **APK:** \`app-release.apk\` - Para instalação direta"; fi)
$(if [ -f "app-release.aab" ]; then echo "- **AAB:** \`app-release.aab\` - Para Google Play Store"; fi)
- **Checksums:** \`checksums.sha256\` - Para verificação de integridade

## Instalação

### Via APK (dispositivos com Android)
1. Baixe o arquivo \`app-release.apk\`
2. Verifique o checksum SHA256
3. Habilite "Instalação de apps desconhecidos" nas configurações do Android
4. Instale o APK baixado

### Via Google Play Store
O arquivo AAB será enviado para a Play Store através do console de desenvolvedor.

## Verificação de Segurança

Para verificar a integridade dos arquivos:

\`\`\`bash
# Verificar checksum
sha256sum -c checksums.sha256

# Verificar assinatura do APK
apksigner verify --print-certs app-release.apk
\`\`\`

## Changelog

$(if [ -f "CHANGELOG.md" ]; then cat CHANGELOG.md; else echo "Ver CHANGELOG.md no repositório principal."; fi)

## Suporte

- **Issues:** [GitHub Issues](https://github.com/seu-usuario/km-county/issues)
- **Discussões:** [GitHub Discussions](https://github.com/seu-usuario/km-county/discussions)

---
*Build gerado automaticamente em $(date)*

EOF

success "Release v$VERSION criado em: $RELEASE_DIR"
success "🎉 Build e empacotamento concluídos com sucesso!"
success "📦 Artefatos prontos para distribuição"

echo ""
echo "Próximos passos recomendados:"
echo "1. Teste o APK em dispositivos reais"
echo "2. Faça upload do AAB para Google Play Console (se aplicável)"
echo "3. Publique no GitHub Releases"
echo "4. Atualize a documentação se necessário"

exit 0
