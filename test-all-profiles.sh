#!/bin/bash

# Script de tests complets pour le Maven Deploy Manifest Plugin
# Teste tous les profils et options sur deux projets

set -e

PLUGIN_VERSION="2.9.0-SNAPSHOT"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
TEST_REPORT="test-report-${TIMESTAMP}.md"

# Couleurs pour l'output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "=========================================="
echo "Maven Deploy Manifest Plugin - Test Suite"
echo "Version: ${PLUGIN_VERSION}"
echo "Date: $(date)"
echo "=========================================="
echo ""

# Fonction pour logger
log_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
    echo "### $1" >> "$TEST_REPORT"
}

log_success() {
    echo -e "${GREEN}[✓]${NC} $1"
    echo "✅ **SUCCESS**: $1" >> "$TEST_REPORT"
    echo "" >> "$TEST_REPORT"
}

log_error() {
    echo -e "${RED}[✗]${NC} $1"
    echo "❌ **FAILED**: $1" >> "$TEST_REPORT"
    echo "" >> "$TEST_REPORT"
}

log_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
    echo "$1" >> "$TEST_REPORT"
}

# Initialiser le rapport
cat > "$TEST_REPORT" << EOF
# Maven Deploy Manifest Plugin - Test Report

**Date**: $(date)  
**Plugin Version**: ${PLUGIN_VERSION}  
**Tester**: Automated Test Suite

---

## Test Summary

EOF

# Compteurs
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Fonction pour exécuter un test
run_test() {
    local test_name="$1"
    local project_dir="$2"
    local command="$3"
    local expected_files="$4"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    log_test "$test_name"
    log_info "Project: $project_dir"
    log_info "Command: $command"
    
    # Nettoyer les anciens fichiers
    cd "$project_dir"
    rm -f target/deployment-manifest-report.* 2>/dev/null || true
    
    # Exécuter la commande
    if eval "$command" > /tmp/test-output.log 2>&1; then
        # Vérifier les fichiers générés
        local all_files_exist=true
        for file in $expected_files; do
            if [ ! -f "$file" ]; then
                log_error "Missing expected file: $file"
                all_files_exist=false
            fi
        done
        
        if [ "$all_files_exist" = true ]; then
            log_success "$test_name"
            PASSED_TESTS=$((PASSED_TESTS + 1))
            
            # Logger les fichiers générés
            log_info "Generated files:"
            for file in $expected_files; do
                if [ -f "$file" ]; then
                    local size=$(ls -lh "$file" | awk '{print $5}')
                    echo "  - $file ($size)" >> "$TEST_REPORT"
                fi
            done
            echo "" >> "$TEST_REPORT"
        else
            FAILED_TESTS=$((FAILED_TESTS + 1))
        fi
    else
        log_error "$test_name - Command failed"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        echo '```' >> "$TEST_REPORT"
        tail -20 /tmp/test-output.log >> "$TEST_REPORT"
        echo '```' >> "$TEST_REPORT"
        echo "" >> "$TEST_REPORT"
    fi
}

echo ""
echo "=========================================="
echo "PROJET 1: maven-flow (multi-module)"
echo "=========================================="
echo ""
echo "## Project 1: maven-flow (multi-module)" >> "$TEST_REPORT"
echo "" >> "$TEST_REPORT"

PROJECT1="/Users/mtoure/dev/maven-flow"

# Test 1: Profile basic (default)
run_test "Profile: basic (default)" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate" \
    "target/deployment-manifest-report.json"

# Test 2: Profile standard
run_test "Profile: standard" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=standard" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 3: Profile full
run_test "Profile: full" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=full" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.yaml target/deployment-manifest-report.html"

# Test 4: Profile ci
run_test "Profile: ci" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=ci" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 5: Override profile - standard + licenses
run_test "Override: standard + licenses" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=standard -Dmanifest.includeLicenses=true" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 6: Override profile - standard + deep tree
run_test "Override: standard + deep dependency tree" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=standard -Dmanifest.dependencyTreeDepth=5" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 7: Options individuelles - HTML only
run_test "Individual options: HTML generation" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.generateHtml=true" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 8: Options individuelles - YAML export
run_test "Individual options: YAML export" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.exportFormat=yaml" \
    "target/deployment-manifest-report.yaml"

# Test 9: Options individuelles - Both formats
run_test "Individual options: JSON + YAML" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.exportFormat=both" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.yaml"

# Test 10: Dependency tree options
run_test "Dependency tree: depth=2, tree format" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.includeDependencyTree=true -Dmanifest.dependencyTreeDepth=2 -Dmanifest.dependencyTreeFormat=tree -Dmanifest.generateHtml=true" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 11: Complete metadata
run_test "Complete metadata: licenses + properties + plugins" \
    "$PROJECT1" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.includeLicenses=true -Dmanifest.includeProperties=true -Dmanifest.includePlugins=true -Dmanifest.generateHtml=true" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

echo ""
echo "=========================================="
echo "PROJET 2: config-preflight (multi-module)"
echo "=========================================="
echo ""
echo "## Project 2: config-preflight (multi-module)" >> "$TEST_REPORT"
echo "" >> "$TEST_REPORT"

PROJECT2="/Users/mtoure/dev/config-preflight"

# Test 12: Profile basic
run_test "Profile: basic (default)" \
    "$PROJECT2" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate" \
    "target/deployment-manifest-report.json"

# Test 13: Profile standard
run_test "Profile: standard" \
    "$PROJECT2" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=standard" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 14: Profile full
run_test "Profile: full" \
    "$PROJECT2" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=full" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.yaml target/deployment-manifest-report.html"

# Test 15: Profile ci
run_test "Profile: ci" \
    "$PROJECT2" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=ci" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.html"

# Test 16: Complete with all options
run_test "Complete: all metadata + HTML" \
    "$PROJECT2" \
    "mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.profile=full -Dmanifest.checkPluginUpdates=true" \
    "target/deployment-manifest-report.json target/deployment-manifest-report.yaml target/deployment-manifest-report.html"

echo ""
echo "=========================================="
echo "TESTS ADDITIONNELS"
echo "=========================================="
echo ""
echo "## Additional Tests" >> "$TEST_REPORT"
echo "" >> "$TEST_REPORT"

# Test 17: Dry-run mode (summary)
log_test "Dry-run mode: summary only"
cd "$PROJECT1"
if mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:generate -Dmanifest.summary=true > /tmp/test-output.log 2>&1; then
    if grep -q "Deployment Manifest Summary" /tmp/test-output.log; then
        log_success "Dry-run mode: summary displayed"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        log_error "Dry-run mode: summary not found in output"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    log_error "Dry-run mode: command failed"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

# Test 18: Analyze dependencies
log_test "Goal: analyze-dependencies"
cd "$PROJECT1"
rm -f target/dependency-analysis.* 2>/dev/null || true
if mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:analyze-dependencies -Dmanifest.generateHtml=true > /tmp/test-output.log 2>&1; then
    if [ -f "target/dependency-analysis.json" ] && [ -f "target/dependency-analysis.html" ]; then
        log_success "analyze-dependencies goal"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        log_info "Generated files:"
        ls -lh target/dependency-analysis.* | awk '{print "  - " $9 " (" $5 ")"}' >> "$TEST_REPORT"
        echo "" >> "$TEST_REPORT"
    else
        log_error "analyze-dependencies: missing output files"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    log_error "analyze-dependencies: command failed"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

# Test 19: Dependency report
log_test "Goal: dependency-report"
cd "$PROJECT1"
rm -f target/dependency-report.* 2>/dev/null || true
if mvn io.github.tourem:deploy-manifest-plugin:${PLUGIN_VERSION}:dependency-report -Ddependency.report.formats=json,html > /tmp/test-output.log 2>&1; then
    if [ -f "target/dependency-report.json" ] && [ -f "target/dependency-report.html" ]; then
        log_success "dependency-report goal"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        log_info "Generated files:"
        ls -lh target/dependency-report.* | awk '{print "  - " $9 " (" $5 ")"}' >> "$TEST_REPORT"
        echo "" >> "$TEST_REPORT"
    else
        log_error "dependency-report: missing output files"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    log_error "dependency-report: command failed"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

echo ""
echo "=========================================="
echo "TEST SUMMARY"
echo "=========================================="
echo ""

cat >> "$TEST_REPORT" << EOF

---

## Final Summary

**Total Tests**: ${TOTAL_TESTS}  
**Passed**: ${PASSED_TESTS} ✅  
**Failed**: ${FAILED_TESTS} ❌  
**Success Rate**: $(awk "BEGIN {printf \"%.1f\", (${PASSED_TESTS}/${TOTAL_TESTS})*100}")%

EOF

echo "Total Tests:   $TOTAL_TESTS"
echo "Passed:        $PASSED_TESTS ✅"
echo "Failed:        $FAILED_TESTS ❌"
echo "Success Rate:  $(awk "BEGIN {printf \"%.1f\", (${PASSED_TESTS}/${TOTAL_TESTS})*100}")%"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}All tests passed! 🎉${NC}"
    echo "**Status**: ✅ All tests passed! 🎉" >> "$TEST_REPORT"
else
    echo -e "${RED}Some tests failed. Check the report for details.${NC}"
    echo "**Status**: ❌ Some tests failed. Review details above." >> "$TEST_REPORT"
fi

echo ""
echo "Test report saved to: $TEST_REPORT"
echo ""

# Afficher un résumé des fichiers générés
echo "=========================================="
echo "GENERATED FILES SUMMARY"
echo "=========================================="
echo ""
echo "### Generated Files Summary" >> "$TEST_REPORT"
echo "" >> "$TEST_REPORT"

for project in "$PROJECT1" "$PROJECT2"; do
    project_name=$(basename "$project")
    echo "Project: $project_name"
    echo "#### Project: $project_name" >> "$TEST_REPORT"
    echo "" >> "$TEST_REPORT"
    if [ -d "$project/target" ]; then
        echo '```' >> "$TEST_REPORT"
        ls -lh "$project/target" | grep -E "(deployment-manifest|dependency-analysis|dependency-report)" | awk '{print $9 " (" $5 ")"}'
        ls -lh "$project/target" | grep -E "(deployment-manifest|dependency-analysis|dependency-report)" | awk '{print $9 " (" $5 ")"}' >> "$TEST_REPORT"
        echo '```' >> "$TEST_REPORT"
        echo "" >> "$TEST_REPORT"
    fi
    echo ""
done

exit $FAILED_TESTS
