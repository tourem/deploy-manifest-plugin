package io.github.tourem.maven.descriptor.service;

import io.github.tourem.maven.descriptor.model.BuildMetrics;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects build metrics and artifact information.
 * 
 * @author tourem
 */
@Slf4j
public class BuildMetricsCollector {
    
    private final LocalDateTime buildStartTime;
    
    public BuildMetricsCollector() {
        this.buildStartTime = LocalDateTime.now();
    }
    
    /**
     * Collect build metrics for a module.
     */
    public BuildMetrics collect(Model model, Path modulePath, LocalDateTime buildTimestamp) {
        String duration = calculateDuration();
        BuildMetrics.ArtifactSize artifactSize = collectArtifactSizes(model, modulePath);
        String buildNumber = System.getenv("BUILD_NUMBER"); // Jenkins/CI
        if (buildNumber == null) {
            buildNumber = System.getenv("GITHUB_RUN_NUMBER"); // GitHub Actions
        }
        if (buildNumber == null) {
            buildNumber = System.getenv("CI_PIPELINE_ID"); // GitLab CI
        }
        
        Map<String, String> additionalMetrics = new HashMap<>();
        
        // Add CI/CD specific metrics
        String ciProvider = detectCIProvider();
        if (ciProvider != null) {
            additionalMetrics.put("ciProvider", ciProvider);
        }
        
        return BuildMetrics.builder()
            .duration(duration)
            .timestamp(buildTimestamp != null ? buildTimestamp : LocalDateTime.now())
            .success(true) // Assume success if we're generating the descriptor
            .artifactSize(artifactSize)
            .buildNumber(buildNumber)
            .buildTool("Maven")
            .buildToolVersion(System.getProperty("maven.version"))
            .additionalMetrics(additionalMetrics.isEmpty() ? null : additionalMetrics)
            .build();
    }
    
    /**
     * Calculate build duration.
     */
    private String calculateDuration() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(buildStartTime, now).getSeconds();
        
        long minutes = seconds / 60;
        long secs = seconds % 60;
        
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
    
    /**
     * Collect artifact sizes from target directory.
     */
    private BuildMetrics.ArtifactSize collectArtifactSizes(Model model, Path modulePath) {
        Path targetDir = modulePath.resolve("target");
        
        if (!Files.exists(targetDir)) {
            return null;
        }
        
        String artifactId = model.getArtifactId();
        String version = model.getVersion();
        if (version == null && model.getParent() != null) {
            version = model.getParent().getVersion();
        }
        
        String packaging = model.getPackaging() != null ? model.getPackaging() : "jar";
        
        String jarSize = null, warSize = null, dockerSize = null;
        Long jarBytes = null, warBytes = null, dockerBytes = null;
        
        // Look for JAR file
        if ("jar".equals(packaging)) {
            Path jarFile = targetDir.resolve(artifactId + "-" + version + ".jar");
            if (Files.exists(jarFile)) {
                try {
                    jarBytes = Files.size(jarFile);
                    jarSize = formatSize(jarBytes);
                } catch (Exception e) {
                    log.debug("Failed to get JAR size: {}", e.getMessage());
                }
            }
        }
        
        // Look for WAR file
        if ("war".equals(packaging)) {
            Path warFile = targetDir.resolve(artifactId + "-" + version + ".war");
            if (Files.exists(warFile)) {
                try {
                    warBytes = Files.size(warFile);
                    warSize = formatSize(warBytes);
                } catch (Exception e) {
                    log.debug("Failed to get WAR size: {}", e.getMessage());
                }
            }
        }
        
        // Calculate total
        long totalBytes = 0;
        if (jarBytes != null) totalBytes += jarBytes;
        if (warBytes != null) totalBytes += warBytes;
        if (dockerBytes != null) totalBytes += dockerBytes;
        
        String totalSize = totalBytes > 0 ? formatSize(totalBytes) : null;
        
        // Return null if no artifacts found
        if (jarSize == null && warSize == null && dockerSize == null) {
            return null;
        }
        
        return BuildMetrics.ArtifactSize.builder()
            .jar(jarSize)
            .war(warSize)
            .docker(dockerSize)
            .total(totalSize)
            .jarBytes(jarBytes)
            .warBytes(warBytes)
            .dockerBytes(dockerBytes)
            .totalBytes(totalBytes > 0 ? totalBytes : null)
            .build();
    }
    
    /**
     * Format file size in human-readable format.
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Detect CI provider from environment variables.
     */
    private String detectCIProvider() {
        if (System.getenv("JENKINS_HOME") != null) {
            return "Jenkins";
        } else if (System.getenv("GITHUB_ACTIONS") != null) {
            return "GitHub Actions";
        } else if (System.getenv("GITLAB_CI") != null) {
            return "GitLab CI";
        } else if (System.getenv("CIRCLECI") != null) {
            return "CircleCI";
        } else if (System.getenv("TRAVIS") != null) {
            return "Travis CI";
        }
        return null;
    }
}
