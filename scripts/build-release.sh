#!/bin/bash

# Build Release Script for Assistente R$/km
# This script builds signed release APK and AAB

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$PROJECT_DIR/build/outputs"
KEYSTORE_PATH="$PROJECT_DIR/keystore.jks"
VERSION_FILE="$PROJECT_DIR/app/build.gradle.kts"

echo -e "${BLUE}🚀 Assistente R$/km - Build Release${NC}"
echo "======================================"

# Check if we're in the right directory
if [ ! -f "$PROJECT_DIR/settings.gradle.kts" ]; then
    echo -e "${RED}❌ Error: Not in project root directory${NC}"
    exit 1
fi

# Check Java version
echo -e "${BLUE}☕ Checking Java version...${NC}"
java -version 2>&1 | head -1
if ! java -version 2>&1 | grep -q "11\|17"; then
    echo -e "${YELLOW}⚠️  Warning: Java 11+ recommended${NC}"
fi

# Check Android SDK
echo -e "${BLUE}📱 Checking Android SDK...${NC}"
if [ -z "$ANDROID_HOME" ]; then
    echo -e "${RED}❌ Error: ANDROID_HOME not set${NC}"
    exit 1
fi
echo "Android SDK: $ANDROID_HOME"

# Check for keystore (for signing)
if [ ! -f "$KEYSTORE_PATH" ]; then
    echo -e "${YELLOW}⚠️  Warning: No keystore found at $KEYSTORE_PATH${NC}"
    echo "Building unsigned release (debug signing)"
    SIGN_RELEASE="false"
else
    echo -e "${GREEN}🔐 Keystore found${NC}"
    SIGN_RELEASE="true"
fi

# Clean previous builds
echo -e "${BLUE}🧹 Cleaning previous builds...${NC}"
cd "$PROJECT_DIR"
./gradlew clean

# Check code quality first
echo -e "${BLUE}🔍 Running code quality checks...${NC}"
echo "Running ktlint..."
./gradlew ktlintCheck || {
    echo -e "${RED}❌ ktlint failed. Fix formatting issues first.${NC}"
    exit 1
}

echo "Running detekt..."
./gradlew detekt || {
    echo -e "${RED}❌ detekt failed. Fix code quality issues first.${NC}"
    exit 1
}

# Run tests
echo -e "${BLUE}🧪 Running unit tests...${NC}"
./gradlew test || {
    echo -e "${RED}❌ Tests failed. Fix failing tests first.${NC}"
    exit 1
}

# Extract version information
echo -e "${BLUE}📋 Extracting version information...${NC}"
VERSION_NAME=$(grep 'versionName = ' "$VERSION_FILE" | sed 's/.*versionName = "\(.*\)".*/\1/')
VERSION_CODE=$(grep 'versionCode = ' "$VERSION_FILE" | sed 's/.*versionCode = \(.*\)/\1/')

echo "Version Name: $VERSION_NAME"
echo "Version Code: $VERSION_CODE"

# Build release
echo -e "${BLUE}🏗️  Building release APK and AAB...${NC}"

if [ "$SIGN_RELEASE" = "true" ]; then
    echo "Building signed release..."
    
    # Check for signing environment variables
    if [ -z "$SIGNING_KEY_ALIAS" ] || [ -z "$SIGNING_KEY_PASSWORD" ] || [ -z "$SIGNING_STORE_PASSWORD" ]; then
        echo -e "${RED}❌ Error: Missing signing environment variables${NC}"
        echo "Required: SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD, SIGNING_STORE_PASSWORD"
        exit 1
    fi
    
    # Build signed APK
    ./gradlew assembleRelease
    
    # Build signed AAB
    ./gradlew bundleRelease
    
else
    echo "Building debug-signed release..."
    ./gradlew assembleRelease
    ./gradlew bundleRelease
fi

# Check build outputs
APK_PATH="$PROJECT_DIR/app/build/outputs/apk/release/app-release.apk"
AAB_PATH="$PROJECT_DIR/app/build/outputs/bundle/release/app-release.aab"

if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ Error: APK not found at $APK_PATH${NC}"
    exit 1
fi

if [ ! -f "$AAB_PATH" ]; then
    echo -e "${RED}❌ Error: AAB not found at $AAB_PATH${NC}"
    exit 1
fi

# Create release directory
RELEASE_DIR="$PROJECT_DIR/release/v$VERSION_NAME"
mkdir -p "$RELEASE_DIR"

# Copy files to release directory
echo -e "${BLUE}📦 Copying release files...${NC}"
cp "$APK_PATH" "$RELEASE_DIR/assistente-ridepricing-v$VERSION_NAME.apk"
cp "$AAB_PATH" "$RELEASE_DIR/assistente-ridepricing-v$VERSION_NAME.aab"

# Generate checksums
echo -e "${BLUE}🔐 Generating checksums...${NC}"
cd "$RELEASE_DIR"
sha256sum *.apk > checksums.txt
sha256sum *.aab >> checksums.txt

# Generate build info
echo -e "${BLUE}📄 Generating build info...${NC}"
cat > build-info.txt << EOF
Assistente R$/km - Build Information
=====================================

Version: $VERSION_NAME ($VERSION_CODE)
Build Date: $(date -u +"%Y-%m-%d %H:%M:%S UTC")
Build Environment: $(uname -a)
Java Version: $(java -version 2>&1 | head -1)
Gradle Version: $(cd "$PROJECT_DIR" && ./gradlew --version | grep "Gradle" | head -1)

Files:
- assistente-ridepricing-v$VERSION_NAME.apk
- assistente-ridepricing-v$VERSION_NAME.aab
- checksums.txt (SHA256)

Verification:
- Code quality checks: PASSED
- Unit tests: PASSED
- Signed: $([ "$SIGN_RELEASE" = "true" ] && echo "YES" || echo "NO (debug key)")

Installation:
1. Enable "Unknown sources" in Android settings
2. Install APK file
3. Follow onboarding to setup permissions
4. Verify with test ride request

Support:
- GitHub: [Repository URL]
- Issues: [Repository URL]/issues
- Documentation: docs/ folder
EOF

# Generate release notes template
echo -e "${BLUE}📝 Generating release notes template...${NC}"
cat > release-notes-template.md << EOF
# Assistente R$/km v$VERSION_NAME

## 🚀 What's New

- [Add new features here]
- [Add improvements here]
- [Add bug fixes here]

## 🔧 Technical Changes

- Updated to version $VERSION_NAME (build $VERSION_CODE)
- [Add technical changes here]

## 📱 Installation

### APK Installation
1. Download \`assistente-ridepricing-v$VERSION_NAME.apk\`
2. Enable "Unknown sources" in Android settings
3. Install the APK file
4. Follow the onboarding process

### Requirements
- Android 6.0+ (API 23)
- 100MB free space
- Accessibility and Overlay permissions

## 🛡️ Security

- **SHA256 Checksums available in \`checksums.txt\`**
- **Signed with verified certificate**
- **Open source code available for audit**

## ⚠️ Important Notes

- This app only READS information from transport apps
- No automatic ride acceptance/rejection
- All processing is done locally on your device
- No personal data is collected or transmitted

## 🆘 Support

- **Issues**: [Repository URL]/issues
- **Documentation**: Available in docs/ folder
- **Installation Guide**: docs/INSTALL.md

---

**Full Changelog**: [Previous Release]...v$VERSION_NAME
EOF

# Summary
echo ""
echo -e "${GREEN}✅ BUILD SUCCESSFUL!${NC}"
echo "======================================"
echo "📦 Release files created in: $RELEASE_DIR"
echo ""
echo "Files:"
echo "  📱 APK: assistente-ridepricing-v$VERSION_NAME.apk"
echo "  📦 AAB: assistente-ridepricing-v$VERSION_NAME.aab"
echo "  🔐 Checksums: checksums.txt"
echo "  📄 Build info: build-info.txt"
echo "  📝 Release notes template: release-notes-template.md"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "1. Test the APK on a device"
echo "2. Update release-notes-template.md"
echo "3. Create GitHub release"
echo "4. Upload files to release"
echo ""
echo -e "${YELLOW}⚠️  Remember to test thoroughly before publishing!${NC}"
