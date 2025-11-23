package io.github.tourem.maven.descriptor.service;

import io.github.tourem.maven.descriptor.model.TestingInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects testing and coverage information from Maven reports.
 * 
 * @author tourem
 */
@Slf4j
public class TestingInfoCollector {
    
    /**
     * Collect testing information from Surefire/Failsafe and JaCoCo reports.
     */
    public TestingInfo collect(Model model, Path modulePath) {
        TestingInfo.CoverageMetrics coverage = collectCoverageMetrics(modulePath);
        TestingInfo.TestCount testCount = collectTestCount(modulePath);
        String qualityGate = determineQualityGate(coverage, testCount);
        String testFramework = detectTestFramework(model);
        String coverageTool = detectCoverageTool(model);
        
        // Return null if no testing info found
        if (coverage == null && testCount == null) {
            return null;
        }
        
        return TestingInfo.builder()
            .coverage(coverage)
            .testCount(testCount)
            .qualityGate(qualityGate)
            .testFramework(testFramework)
            .coverageTool(coverageTool)
            .build();
    }
    
    /**
     * Collect coverage metrics from JaCoCo XML report.
     */
    private TestingInfo.CoverageMetrics collectCoverageMetrics(Path modulePath) {
        Path jacocoXml = modulePath.resolve("target/site/jacoco/jacoco.xml");
        
        if (!Files.exists(jacocoXml)) {
            log.debug("JaCoCo report not found at: {}", jacocoXml);
            return null;
        }
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(jacocoXml.toFile());
            
            Element report = doc.getDocumentElement();
            NodeList counters = report.getElementsByTagName("counter");
            
            Double line = null, branch = null, instruction = null, complexity = null, method = null, clazz = null;
            
            for (int i = 0; i < counters.getLength(); i++) {
                Element counter = (Element) counters.item(i);
                String type = counter.getAttribute("type");
                int covered = Integer.parseInt(counter.getAttribute("covered"));
                int missed = Integer.parseInt(counter.getAttribute("missed"));
                int total = covered + missed;
                
                if (total > 0) {
                    double percentage = (covered * 100.0) / total;
                    
                    switch (type) {
                        case "LINE" -> line = Math.round(percentage * 10.0) / 10.0;
                        case "BRANCH" -> branch = Math.round(percentage * 10.0) / 10.0;
                        case "INSTRUCTION" -> instruction = Math.round(percentage * 10.0) / 10.0;
                        case "COMPLEXITY" -> complexity = Math.round(percentage * 10.0) / 10.0;
                        case "METHOD" -> method = Math.round(percentage * 10.0) / 10.0;
                        case "CLASS" -> clazz = Math.round(percentage * 10.0) / 10.0;
                    }
                }
            }
            
            return TestingInfo.CoverageMetrics.builder()
                .line(line)
                .branch(branch)
                .instruction(instruction)
                .complexity(complexity)
                .method(method)
                .clazz(clazz)
                .build();
                
        } catch (Exception e) {
            log.warn("Failed to parse JaCoCo report: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Collect test count from Surefire/Failsafe reports.
     */
    private TestingInfo.TestCount collectTestCount(Path modulePath) {
        int unit = 0, integration = 0, e2e = 0, skipped = 0, failed = 0;
        
        // Surefire reports (unit tests)
        Path surefireDir = modulePath.resolve("target/surefire-reports");
        if (Files.exists(surefireDir)) {
            TestCounts unitCounts = countTestsInDirectory(surefireDir);
            unit = unitCounts.total;
            skipped += unitCounts.skipped;
            failed += unitCounts.failed;
        }
        
        // Failsafe reports (integration tests)
        Path failsafeDir = modulePath.resolve("target/failsafe-reports");
        if (Files.exists(failsafeDir)) {
            TestCounts integrationCounts = countTestsInDirectory(failsafeDir);
            integration = integrationCounts.total;
            skipped += integrationCounts.skipped;
            failed += integrationCounts.failed;
        }
        
        int total = unit + integration + e2e;
        
        if (total == 0) {
            return null;
        }
        
        return TestingInfo.TestCount.builder()
            .unit(unit > 0 ? unit : null)
            .integration(integration > 0 ? integration : null)
            .e2e(e2e > 0 ? e2e : null)
            .total(total)
            .skipped(skipped > 0 ? skipped : null)
            .failed(failed > 0 ? failed : null)
            .build();
    }
    
    /**
     * Count tests in a directory of XML reports.
     */
    private TestCounts countTestsInDirectory(Path directory) {
        int total = 0, skipped = 0, failed = 0;
        
        try {
            List<Path> xmlFiles = new ArrayList<>();
            Files.walk(directory, 1)
                .filter(p -> p.toString().endsWith(".xml"))
                .filter(p -> p.getFileName().toString().startsWith("TEST-"))
                .forEach(xmlFiles::add);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            for (Path xmlFile : xmlFiles) {
                try {
                    Document doc = builder.parse(xmlFile.toFile());
                    Element testsuite = doc.getDocumentElement();
                    
                    if ("testsuite".equals(testsuite.getNodeName())) {
                        String testsAttr = testsuite.getAttribute("tests");
                        String skippedAttr = testsuite.getAttribute("skipped");
                        String failuresAttr = testsuite.getAttribute("failures");
                        String errorsAttr = testsuite.getAttribute("errors");
                        
                        if (!testsAttr.isEmpty()) {
                            total += Integer.parseInt(testsAttr);
                        }
                        if (!skippedAttr.isEmpty()) {
                            skipped += Integer.parseInt(skippedAttr);
                        }
                        if (!failuresAttr.isEmpty()) {
                            failed += Integer.parseInt(failuresAttr);
                        }
                        if (!errorsAttr.isEmpty()) {
                            failed += Integer.parseInt(errorsAttr);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to parse test report {}: {}", xmlFile.getFileName(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.debug("Failed to read test reports from {}: {}", directory, e.getMessage());
        }
        
        return new TestCounts(total, skipped, failed);
    }
    
    /**
     * Determine quality gate status based on coverage and tests.
     */
    private String determineQualityGate(TestingInfo.CoverageMetrics coverage, TestingInfo.TestCount testCount) {
        // If no data, return null
        if (coverage == null && testCount == null) {
            return null;
        }
        
        // Check for failed tests
        if (testCount != null && testCount.failed() != null && testCount.failed() > 0) {
            return "FAILED";
        }
        
        // Check coverage thresholds (80% line coverage)
        if (coverage != null && coverage.line() != null) {
            if (coverage.line() >= 80.0) {
                return "PASSED";
            } else if (coverage.line() >= 60.0) {
                return "WARNING";
            } else {
                return "FAILED";
            }
        }
        
        // Default to PASSED if tests exist and none failed
        if (testCount != null && testCount.total() != null && testCount.total() > 0) {
            return "PASSED";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Detect test framework from dependencies.
     */
    private String detectTestFramework(Model model) {
        if (model.getDependencies() == null) {
            return null;
        }
        
        for (var dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            if (artifactId.contains("junit-jupiter")) {
                return "JUnit 5";
            } else if (artifactId.contains("junit") && !artifactId.contains("vintage")) {
                return "JUnit 4";
            } else if (artifactId.contains("testng")) {
                return "TestNG";
            } else if (artifactId.contains("spock")) {
                return "Spock";
            }
        }
        
        return null;
    }
    
    /**
     * Detect coverage tool from plugins.
     */
    private String detectCoverageTool(Model model) {
        if (model.getBuild() == null || model.getBuild().getPlugins() == null) {
            return null;
        }
        
        for (Plugin plugin : model.getBuild().getPlugins()) {
            String artifactId = plugin.getArtifactId();
            if (artifactId.contains("jacoco")) {
                return "JaCoCo";
            } else if (artifactId.contains("cobertura")) {
                return "Cobertura";
            } else if (artifactId.contains("clover")) {
                return "Clover";
            }
        }
        
        return null;
    }
    
    /**
     * Helper record for test counts.
     */
    private record TestCounts(int total, int skipped, int failed) {}
}
