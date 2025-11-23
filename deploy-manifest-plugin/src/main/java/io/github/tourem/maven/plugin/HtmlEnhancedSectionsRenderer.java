package io.github.tourem.maven.plugin;

import io.github.tourem.maven.descriptor.model.BuildMetrics;
import io.github.tourem.maven.descriptor.model.DeployableModule;
import io.github.tourem.maven.descriptor.model.ExternalDependencies;
import io.github.tourem.maven.descriptor.model.TestingInfo;

/**
 * Helper class to render enhanced HTML sections for new features.
 * 
 * @author tourem
 */
public class HtmlEnhancedSectionsRenderer {
    
    /**
     * Generate CSS styles for the new sections.
     */
    public static String generateStyles() {
        return """
            /* External Dependencies Styles */
            .external-deps-section {
              margin: 30px 0;
              padding: 25px;
              background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
              border-radius: 15px;
              box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }
            
            .deps-grid {
              display: grid;
              grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
              gap: 20px;
              margin-top: 20px;
            }
            
            .dep-card {
              background: white;
              padding: 20px;
              border-radius: 12px;
              box-shadow: 0 2px 10px rgba(0,0,0,0.08);
              transition: transform 0.3s, box-shadow 0.3s;
            }
            
            .dep-card:hover {
              transform: translateY(-5px);
              box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            }
            
            .dep-type-badge {
              display: inline-block;
              padding: 6px 14px;
              border-radius: 20px;
              font-size: 0.85em;
              font-weight: 600;
              margin-bottom: 12px;
            }
            
            .dep-database { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
            .dep-cache { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; }
            .dep-queue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
            .dep-service { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; }
            
            .dep-info {
              margin: 8px 0;
              font-size: 0.9em;
              color: #555;
            }
            
            .dep-required {
              display: inline-block;
              padding: 3px 10px;
              border-radius: 12px;
              font-size: 0.75em;
              font-weight: 600;
              margin-top: 8px;
            }
            
            .dep-required-yes { background: #d1fae5; color: #065f46; }
            .dep-required-no { background: #fee2e2; color: #991b1b; }
            
            /* Testing & Coverage Styles */
            .testing-section {
              margin: 30px 0;
              padding: 25px;
              background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
              border-radius: 15px;
              box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }
            
            .quality-gate {
              display: inline-flex;
              align-items: center;
              padding: 12px 24px;
              border-radius: 25px;
              font-weight: 700;
              font-size: 1.1em;
              margin: 15px 0;
              box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            }
            
            .quality-gate-passed { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; }
            .quality-gate-warning { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white; }
            .quality-gate-failed { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; }
            .quality-gate-unknown { background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%); color: white; }
            
            .coverage-grid {
              display: grid;
              grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
              gap: 20px;
              margin-top: 20px;
            }
            
            .coverage-item {
              background: white;
              padding: 18px;
              border-radius: 12px;
              box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            }
            
            .coverage-label {
              font-size: 0.85em;
              color: #666;
              text-transform: uppercase;
              letter-spacing: 0.5px;
              margin-bottom: 8px;
            }
            
            .coverage-bar-container {
              width: 100%;
              height: 12px;
              background: #e5e7eb;
              border-radius: 6px;
              overflow: hidden;
              margin: 10px 0;
            }
            
            .coverage-bar {
              height: 100%;
              border-radius: 6px;
              transition: width 1s ease-out;
            }
            
            .coverage-excellent { background: linear-gradient(90deg, #10b981 0%, #059669 100%); }
            .coverage-good { background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%); }
            .coverage-warning { background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%); }
            .coverage-poor { background: linear-gradient(90deg, #ef4444 0%, #dc2626 100%); }
            
            .coverage-value {
              font-size: 1.8em;
              font-weight: 700;
              color: #1f2937;
            }
            
            .test-counts {
              display: flex;
              gap: 15px;
              margin-top: 20px;
              flex-wrap: wrap;
            }
            
            .test-count-badge {
              background: white;
              padding: 12px 20px;
              border-radius: 10px;
              box-shadow: 0 2px 8px rgba(0,0,0,0.08);
              display: flex;
              align-items: center;
              gap: 10px;
            }
            
            .test-count-number {
              font-size: 1.5em;
              font-weight: 700;
              color: #667eea;
            }
            
            .test-count-label {
              font-size: 0.85em;
              color: #666;
            }
            
            /* Build Metrics Styles */
            .build-metrics-section {
              margin: 30px 0;
              padding: 25px;
              background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
              border-radius: 15px;
              box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }
            
            .metrics-grid {
              display: grid;
              grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
              gap: 20px;
              margin-top: 20px;
            }
            
            .metric-card {
              background: white;
              padding: 20px;
              border-radius: 12px;
              box-shadow: 0 2px 10px rgba(0,0,0,0.08);
              text-align: center;
              transition: transform 0.3s;
            }
            
            .metric-card:hover {
              transform: scale(1.05);
            }
            
            .metric-icon {
              font-size: 2.5em;
              margin-bottom: 10px;
            }
            
            .metric-value {
              font-size: 1.8em;
              font-weight: 700;
              color: #1f2937;
              margin: 10px 0;
            }
            
            .metric-label {
              font-size: 0.9em;
              color: #666;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }
            
            .build-success {
              color: #10b981;
            }
            
            .build-failed {
              color: #ef4444;
            }
            
            /* Dark mode support for new sections */
            body.dark-mode .external-deps-section {
              background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
            }
            
            body.dark-mode .testing-section {
              background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            }
            
            body.dark-mode .build-metrics-section {
              background: linear-gradient(135deg, #422006 0%, #78350f 100%);
            }
            
            body.dark-mode .dep-card,
            body.dark-mode .coverage-item,
            body.dark-mode .test-count-badge,
            body.dark-mode .metric-card {
              background: #1e293b;
              color: #e2e8f0;
            }
            
            body.dark-mode .dep-info,
            body.dark-mode .coverage-label,
            body.dark-mode .test-count-label,
            body.dark-mode .metric-label {
              color: #94a3b8;
            }
            
            body.dark-mode .coverage-value,
            body.dark-mode .test-count-number,
            body.dark-mode .metric-value {
              color: #f1f5f9;
            }
            """;
    }
    
    /**
     * Generate HTML for external dependencies section.
     */
    public static String generateExternalDependenciesHtml(DeployableModule module) {
        ExternalDependencies deps = module.getExternalDependencies();
        if (deps == null) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"external-deps-section\">\n");
        html.append("          <h3>🔗 External Dependencies</h3>\n");
        html.append("          <div class=\"deps-grid\">\n");
        
        // Databases
        if (deps.databases() != null && !deps.databases().isEmpty()) {
            for (var db : deps.databases()) {
                html.append("            <div class=\"dep-card\">\n");
                html.append("              <span class=\"dep-type-badge dep-database\">🗄️ Database</span>\n");
                html.append("              <h4>").append(escapeHtml(db.type())).append("</h4>\n");
                if (db.minVersion() != null) {
                    html.append("              <div class=\"dep-info\">📦 Version: ").append(escapeHtml(db.minVersion())).append("</div>\n");
                }
                if (db.connectionPool() != null) {
                    html.append("              <div class=\"dep-info\">🔌 Pool: ").append(escapeHtml(db.connectionPool())).append("</div>\n");
                }
                if (db.driver() != null) {
                    html.append("              <div class=\"dep-info\">🚗 Driver: <code>").append(escapeHtml(db.driver())).append("</code></div>\n");
                }
                if (db.required() != null) {
                    String requiredClass = db.required() ? "dep-required-yes" : "dep-required-no";
                    String requiredText = db.required() ? "✓ Required" : "○ Optional";
                    html.append("              <span class=\"dep-required ").append(requiredClass).append("\">").append(requiredText).append("</span>\n");
                }
                html.append("            </div>\n");
            }
        }
        
        // Message Queues
        if (deps.messageQueues() != null && !deps.messageQueues().isEmpty()) {
            for (var queue : deps.messageQueues()) {
                html.append("            <div class=\"dep-card\">\n");
                html.append("              <span class=\"dep-type-badge dep-queue\">📨 Message Queue</span>\n");
                html.append("              <h4>").append(escapeHtml(queue.type())).append("</h4>\n");
                if (queue.version() != null) {
                    html.append("              <div class=\"dep-info\">📦 Version: ").append(escapeHtml(queue.version())).append("</div>\n");
                }
                if (queue.queues() != null && !queue.queues().isEmpty()) {
                    html.append("              <div class=\"dep-info\">📬 Queues: ").append(String.join(", ", queue.queues())).append("</div>\n");
                }
                if (queue.required() != null) {
                    String requiredClass = queue.required() ? "dep-required-yes" : "dep-required-no";
                    String requiredText = queue.required() ? "✓ Required" : "○ Optional";
                    html.append("              <span class=\"dep-required ").append(requiredClass).append("\">").append(requiredText).append("</span>\n");
                }
                html.append("            </div>\n");
            }
        }
        
        // Caches
        if (deps.caches() != null && !deps.caches().isEmpty()) {
            for (var cache : deps.caches()) {
                html.append("            <div class=\"dep-card\">\n");
                html.append("              <span class=\"dep-type-badge dep-cache\">⚡ Cache</span>\n");
                html.append("              <h4>").append(escapeHtml(cache.type())).append("</h4>\n");
                if (cache.version() != null) {
                    html.append("              <div class=\"dep-info\">📦 Version: ").append(escapeHtml(cache.version())).append("</div>\n");
                }
                if (cache.usage() != null) {
                    html.append("              <div class=\"dep-info\">💡 Usage: ").append(escapeHtml(cache.usage())).append("</div>\n");
                }
                if (cache.required() != null) {
                    String requiredClass = cache.required() ? "dep-required-yes" : "dep-required-no";
                    String requiredText = cache.required() ? "✓ Required" : "○ Optional";
                    html.append("              <span class=\"dep-required ").append(requiredClass).append("\">").append(requiredText).append("</span>\n");
                }
                html.append("            </div>\n");
            }
        }
        
        // External Services
        if (deps.services() != null && !deps.services().isEmpty()) {
            for (var service : deps.services()) {
                html.append("            <div class=\"dep-card\">\n");
                html.append("              <span class=\"dep-type-badge dep-service\">🌐 Service</span>\n");
                html.append("              <h4>").append(escapeHtml(service.name())).append("</h4>\n");
                if (service.type() != null) {
                    html.append("              <div class=\"dep-info\">🏷️ Type: ").append(escapeHtml(service.type())).append("</div>\n");
                }
                if (service.version() != null) {
                    html.append("              <div class=\"dep-info\">📦 Version: ").append(escapeHtml(service.version())).append("</div>\n");
                }
                if (service.required() != null) {
                    String requiredClass = service.required() ? "dep-required-yes" : "dep-required-no";
                    String requiredText = service.required() ? "✓ Required" : "○ Optional";
                    html.append("              <span class=\"dep-required ").append(requiredClass).append("\">").append(requiredText).append("</span>\n");
                }
                html.append("            </div>\n");
            }
        }
        
        html.append("          </div>\n");
        html.append("        </div>\n");
        
        return html.toString();
    }
    
    /**
     * Generate HTML for testing & coverage section.
     */
    public static String generateTestingInfoHtml(DeployableModule module) {
        TestingInfo testing = module.getTesting();
        if (testing == null) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"testing-section\">\n");
        html.append("          <h3>🧪 Testing & Coverage</h3>\n");
        
        // Quality Gate
        if (testing.qualityGate() != null) {
            String gateClass = switch (testing.qualityGate()) {
                case "PASSED" -> "quality-gate-passed";
                case "WARNING" -> "quality-gate-warning";
                case "FAILED" -> "quality-gate-failed";
                default -> "quality-gate-unknown";
            };
            String gateIcon = switch (testing.qualityGate()) {
                case "PASSED" -> "✅";
                case "WARNING" -> "⚠️";
                case "FAILED" -> "❌";
                default -> "❓";
            };
            html.append("          <div class=\"quality-gate ").append(gateClass).append("\">\n");
            html.append("            ").append(gateIcon).append(" Quality Gate: ").append(testing.qualityGate()).append("\n");
            html.append("          </div>\n");
        }
        
        // Coverage Metrics
        if (testing.coverage() != null) {
            html.append("          <h4>📊 Code Coverage</h4>\n");
            html.append("          <div class=\"coverage-grid\">\n");
            
            TestingInfo.CoverageMetrics cov = testing.coverage();
            
            if (cov.line() != null) {
                html.append(generateCoverageItem("Line Coverage", cov.line()));
            }
            if (cov.branch() != null) {
                html.append(generateCoverageItem("Branch Coverage", cov.branch()));
            }
            if (cov.instruction() != null) {
                html.append(generateCoverageItem("Instruction Coverage", cov.instruction()));
            }
            if (cov.method() != null) {
                html.append(generateCoverageItem("Method Coverage", cov.method()));
            }
            if (cov.clazz() != null) {
                html.append(generateCoverageItem("Class Coverage", cov.clazz()));
            }
            
            html.append("          </div>\n");
        }
        
        // Test Counts
        if (testing.testCount() != null) {
            html.append("          <h4>🎯 Test Statistics</h4>\n");
            html.append("          <div class=\"test-counts\">\n");
            
            TestingInfo.TestCount counts = testing.testCount();
            
            if (counts.total() != null) {
                html.append("            <div class=\"test-count-badge\">\n");
                html.append("              <span class=\"test-count-number\">").append(counts.total()).append("</span>\n");
                html.append("              <span class=\"test-count-label\">Total Tests</span>\n");
                html.append("            </div>\n");
            }
            if (counts.unit() != null) {
                html.append("            <div class=\"test-count-badge\">\n");
                html.append("              <span class=\"test-count-number\">").append(counts.unit()).append("</span>\n");
                html.append("              <span class=\"test-count-label\">Unit Tests</span>\n");
                html.append("            </div>\n");
            }
            if (counts.integration() != null) {
                html.append("            <div class=\"test-count-badge\">\n");
                html.append("              <span class=\"test-count-number\">").append(counts.integration()).append("</span>\n");
                html.append("              <span class=\"test-count-label\">Integration Tests</span>\n");
                html.append("            </div>\n");
            }
            if (counts.failed() != null && counts.failed() > 0) {
                html.append("            <div class=\"test-count-badge\" style=\"border: 2px solid #ef4444;\">\n");
                html.append("              <span class=\"test-count-number\" style=\"color: #ef4444;\">").append(counts.failed()).append("</span>\n");
                html.append("              <span class=\"test-count-label\">Failed</span>\n");
                html.append("            </div>\n");
            }
            if (counts.skipped() != null && counts.skipped() > 0) {
                html.append("            <div class=\"test-count-badge\" style=\"border: 2px solid #f59e0b;\">\n");
                html.append("              <span class=\"test-count-number\" style=\"color: #f59e0b;\">").append(counts.skipped()).append("</span>\n");
                html.append("              <span class=\"test-count-label\">Skipped</span>\n");
                html.append("            </div>\n");
            }
            
            html.append("          </div>\n");
        }
        
        // Test Framework Info
        if (testing.testFramework() != null || testing.coverageTool() != null) {
            html.append("          <div style=\"margin-top: 20px; font-size: 0.9em; color: #666;\">\n");
            if (testing.testFramework() != null) {
                html.append("            <span>🧰 Framework: <strong>").append(escapeHtml(testing.testFramework())).append("</strong></span>\n");
            }
            if (testing.coverageTool() != null) {
                html.append("            <span style=\"margin-left: 20px;\">📈 Coverage Tool: <strong>").append(escapeHtml(testing.coverageTool())).append("</strong></span>\n");
            }
            html.append("          </div>\n");
        }
        
        html.append("        </div>\n");
        
        return html.toString();
    }
    
    /**
     * Generate HTML for build metrics section.
     */
    public static String generateBuildMetricsHtml(DeployableModule module) {
        BuildMetrics metrics = module.getBuildMetrics();
        if (metrics == null) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"build-metrics-section\">\n");
        html.append("          <h3>📊 Build Metrics</h3>\n");
        html.append("          <div class=\"metrics-grid\">\n");
        
        // Build Duration
        if (metrics.duration() != null) {
            html.append("            <div class=\"metric-card\">\n");
            html.append("              <div class=\"metric-icon\">⏱️</div>\n");
            html.append("              <div class=\"metric-value\">").append(escapeHtml(metrics.duration())).append("</div>\n");
            html.append("              <div class=\"metric-label\">Build Duration</div>\n");
            html.append("            </div>\n");
        }
        
        // Build Status
        if (metrics.success() != null) {
            String statusIcon = metrics.success() ? "✅" : "❌";
            String statusClass = metrics.success() ? "build-success" : "build-failed";
            String statusText = metrics.success() ? "Success" : "Failed";
            html.append("            <div class=\"metric-card\">\n");
            html.append("              <div class=\"metric-icon\">").append(statusIcon).append("</div>\n");
            html.append("              <div class=\"metric-value ").append(statusClass).append("\">").append(statusText).append("</div>\n");
            html.append("              <div class=\"metric-label\">Build Status</div>\n");
            html.append("            </div>\n");
        }
        
        // Artifact Size
        if (metrics.artifactSize() != null) {
            BuildMetrics.ArtifactSize size = metrics.artifactSize();
            if (size.jar() != null) {
                html.append("            <div class=\"metric-card\">\n");
                html.append("              <div class=\"metric-icon\">📦</div>\n");
                html.append("              <div class=\"metric-value\">").append(escapeHtml(size.jar())).append("</div>\n");
                html.append("              <div class=\"metric-label\">JAR Size</div>\n");
                html.append("            </div>\n");
            }
            if (size.war() != null) {
                html.append("            <div class=\"metric-card\">\n");
                html.append("              <div class=\"metric-icon\">📦</div>\n");
                html.append("              <div class=\"metric-value\">").append(escapeHtml(size.war())).append("</div>\n");
                html.append("              <div class=\"metric-label\">WAR Size</div>\n");
                html.append("            </div>\n");
            }
            if (size.total() != null) {
                html.append("            <div class=\"metric-card\">\n");
                html.append("              <div class=\"metric-icon\">📊</div>\n");
                html.append("              <div class=\"metric-value\">").append(escapeHtml(size.total())).append("</div>\n");
                html.append("              <div class=\"metric-label\">Total Size</div>\n");
                html.append("            </div>\n");
            }
        }
        
        // Build Number
        if (metrics.buildNumber() != null) {
            html.append("            <div class=\"metric-card\">\n");
            html.append("              <div class=\"metric-icon\">#️⃣</div>\n");
            html.append("              <div class=\"metric-value\">").append(escapeHtml(metrics.buildNumber())).append("</div>\n");
            html.append("              <div class=\"metric-label\">Build Number</div>\n");
            html.append("            </div>\n");
        }
        
        // Build Tool
        if (metrics.buildTool() != null) {
            html.append("            <div class=\"metric-card\">\n");
            html.append("              <div class=\"metric-icon\">🔨</div>\n");
            html.append("              <div class=\"metric-value\" style=\"font-size: 1.3em;\">").append(escapeHtml(metrics.buildTool())).append("</div>\n");
            html.append("              <div class=\"metric-label\">Build Tool</div>\n");
            html.append("            </div>\n");
        }
        
        html.append("          </div>\n");
        html.append("        </div>\n");
        
        return html.toString();
    }
    
    // Helper methods
    
    private static String generateCoverageItem(String label, Double percentage) {
        String barClass = getCoverageBarClass(percentage);
        return String.format("""
                    <div class="coverage-item">
                      <div class="coverage-label">%s</div>
                      <div class="coverage-value">%.1f%%</div>
                      <div class="coverage-bar-container">
                        <div class="coverage-bar %s" style="width: %.1f%%"></div>
                      </div>
                    </div>
            """, label, percentage, barClass, percentage);
    }
    
    private static String getCoverageBarClass(Double percentage) {
        if (percentage >= 80) return "coverage-excellent";
        if (percentage >= 60) return "coverage-good";
        if (percentage >= 40) return "coverage-warning";
        return "coverage-poor";
    }
    
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
