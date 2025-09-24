#!/bin/bash

# Test Runner Script for Assistente R$/km
# Runs all tests (unit, instrumented, lint)

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORTS_DIR="$PROJECT_DIR/build/reports"

echo -e "${BLUE}🧪 Assistente R$/km - Test Suite${NC}"
echo "=================================="

# Check if we're in the right directory
if [ ! -f "$PROJECT_DIR/settings.gradle.kts" ]; then
    echo -e "${RED}❌ Error: Not in project root directory${NC}"
    exit 1
fi

cd "$PROJECT_DIR"

# Clean previous reports
echo -e "${BLUE}🧹 Cleaning previous reports...${NC}"
rm -rf "$REPORTS_DIR"
./gradlew clean

# Function to run a test suite
run_test_suite() {
    local test_name="$1"
    local gradle_task="$2"
    local required="$3"
    
    echo ""
    echo -e "${BLUE}▶️  Running $test_name...${NC}"
    echo "----------------------------------------"
    
    if ./gradlew "$gradle_task" --stacktrace; then
        echo -e "${GREEN}✅ $test_name: PASSED${NC}"
        return 0
    else
        echo -e "${RED}❌ $test_name: FAILED${NC}"
        if [ "$required" = "required" ]; then
            echo -e "${RED}💥 $test_name is required - stopping execution${NC}"
            exit 1
        else
            echo -e "${YELLOW}⚠️  $test_name failed but continuing...${NC}"
            return 1
        fi
    fi
}

# Initialize results
TOTAL_SUITES=0
PASSED_SUITES=0
FAILED_SUITES=0

# 1. Code Style - ktlint
echo -e "${BLUE}📝 Code Style Checks${NC}"
((TOTAL_SUITES++))
if run_test_suite "ktlint (Code Formatting)" "ktlintCheck" "required"; then
    ((PASSED_SUITES++))
else
    ((FAILED_SUITES++))
fi

# 2. Static Analysis - detekt
((TOTAL_SUITES++))
if run_test_suite "detekt (Static Analysis)" "detekt" "required"; then
    ((PASSED_SUITES++))
else
    ((FAILED_SUITES++))
fi

# 3. Unit Tests - All modules
echo -e "${BLUE}🔬 Unit Tests${NC}"
((TOTAL_SUITES++))
if run_test_suite "Unit Tests (All Modules)" "test" "required"; then
    ((PASSED_SUITES++))
else
    ((FAILED_SUITES++))
fi

# 4. Instrumented Tests (if device available)
echo -e "${BLUE}📱 Instrumented Tests${NC}"
echo "Checking for connected Android devices..."

if adb devices | grep -q "device$"; then
    echo "✅ Android device found"
    ((TOTAL_SUITES++))
    if run_test_suite "Instrumented Tests" "connectedAndroidTest" "optional"; then
        ((PASSED_SUITES++))
    else
        ((FAILED_SUITES++))
    fi
else
    echo -e "${YELLOW}⚠️  No Android device connected - skipping instrumented tests${NC}"
    echo "   Connect a device or start an emulator to run instrumented tests"
fi

# 5. Generate Test Reports
echo ""
echo -e "${BLUE}📊 Generating comprehensive test report...${NC}"
./gradlew testReport || echo -e "${YELLOW}⚠️  Test report generation failed${NC}"

# 6. Code Coverage (if available)
echo -e "${BLUE}🔍 Code Coverage Analysis${NC}"
if ./gradlew jacocoTestReport 2>/dev/null; then
    echo -e "${GREEN}✅ Coverage report generated${NC}"
else
    echo -e "${YELLOW}⚠️  Coverage report not available (jacoco not configured)${NC}"
fi

# 7. Lint Checks
echo -e "${BLUE}🔎 Android Lint Checks${NC}"
((TOTAL_SUITES++))
if run_test_suite "Android Lint" "lint" "optional"; then
    ((PASSED_SUITES++))
else
    ((FAILED_SUITES++))
fi

# Summary Report
echo ""
echo -e "${BLUE}📋 TEST SUMMARY${NC}"
echo "==============================="
echo "Total test suites: $TOTAL_SUITES"
echo -e "Passed: ${GREEN}$PASSED_SUITES${NC}"
echo -e "Failed: ${RED}$FAILED_SUITES${NC}"

if [ $FAILED_SUITES -eq 0 ]; then
    echo ""
    echo -e "${GREEN}🎉 ALL TESTS PASSED!${NC}"
    echo -e "${GREEN}✅ Ready for release build${NC}"
    OVERALL_STATUS="success"
else
    echo ""
    echo -e "${RED}❌ SOME TESTS FAILED${NC}"
    echo -e "${RED}🚨 Fix issues before release${NC}"
    OVERALL_STATUS="failure"
fi

# Report locations
echo ""
echo -e "${BLUE}📁 Report Locations:${NC}"
echo "==============================="

# Find and list all generated reports
find "$PROJECT_DIR" -name "*.html" -path "*/build/reports/*" 2>/dev/null | while read -r report; do
    rel_path=$(realpath --relative-to="$PROJECT_DIR" "$report" 2>/dev/null || echo "$report")
    echo "  📊 $rel_path"
done

# Specific key reports
echo ""
echo "Key reports:"
echo "  🧪 Unit tests: app/build/reports/tests/testDebugUnitTest/index.html"
echo "  📱 Instrumented: app/build/reports/androidTests/connected/index.html"
echo "  📝 ktlint: build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.html"
echo "  🔍 detekt: build/reports/detekt/detekt.html"
echo "  🔎 Android lint: app/build/reports/lint-results-debug.html"

# Performance stats
echo ""
echo -e "${BLUE}⏱️  Performance Stats:${NC}"
echo "==============================="
echo "Test execution time: $(date)"
echo "Project size:"
find "$PROJECT_DIR" -name "*.kt" -o -name "*.java" | wc -l | xargs echo "  Source files:"
find "$PROJECT_DIR" -name "*Test.kt" -o -name "*Test.java" | wc -l | xargs echo "  Test files:"

# OCR Test Results (if available)
OCR_TEST_DIR="$PROJECT_DIR/tests/images"
if [ -d "$OCR_TEST_DIR" ]; then
    echo ""
    echo -e "${BLUE}🖼️  OCR Test Dataset:${NC}"
    echo "==============================="
    image_count=$(find "$OCR_TEST_DIR" -name "*.png" -o -name "*.jpg" -o -name "*.jpeg" | wc -l)
    echo "  Test images: $image_count"
    
    if [ -f "$PROJECT_DIR/build/reports/ocr-test-results.txt" ]; then
        echo "  Results: build/reports/ocr-test-results.txt"
    else
        echo "  Results: Not generated (run OCR tests separately)"
    fi
fi

# GitHub Actions integration
if [ "$CI" = "true" ]; then
    echo ""
    echo -e "${BLUE}🚀 CI/CD Integration:${NC}"
    echo "==============================="
    echo "Running in CI environment"
    
    # Generate JUnit XML for CI
    echo "Converting reports to CI format..."
    
    # Set GitHub Actions outputs
    if [ "$GITHUB_ACTIONS" = "true" ]; then
        echo "test_status=$OVERALL_STATUS" >> $GITHUB_OUTPUT
        echo "passed_suites=$PASSED_SUITES" >> $GITHUB_OUTPUT
        echo "failed_suites=$FAILED_SUITES" >> $GITHUB_OUTPUT
        echo "total_suites=$TOTAL_SUITES" >> $GITHUB_OUTPUT
    fi
fi

# Final status
echo ""
if [ "$OVERALL_STATUS" = "success" ]; then
    echo -e "${GREEN}🏆 Test suite completed successfully!${NC}"
    exit 0
else
    echo -e "${RED}💥 Test suite completed with failures!${NC}"
    echo ""
    echo -e "${YELLOW}Next steps:${NC}"
    echo "1. Check the failed test reports above"
    echo "2. Fix the identified issues"
    echo "3. Re-run: ./scripts/run-tests.sh"
    echo "4. Only proceed to build after all tests pass"
    exit 1
fi
