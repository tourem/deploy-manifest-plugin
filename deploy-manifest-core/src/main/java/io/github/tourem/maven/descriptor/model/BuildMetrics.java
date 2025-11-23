package io.github.tourem.maven.descriptor.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Build metrics and artifact information.
 * 
 * @author tourem
 */
@Builder
public record BuildMetrics(
    String duration,
    LocalDateTime timestamp,
    Boolean success,
    ArtifactSize artifactSize,
    String buildNumber,
    String buildTool,
    String buildToolVersion,
    Map<String, String> additionalMetrics
) {
    
    /**
     * Artifact size information.
     */
    @Builder
    public record ArtifactSize(
        String jar,
        String war,
        String docker,
        String total,
        Long jarBytes,
        Long warBytes,
        Long dockerBytes,
        Long totalBytes
    ) {}
}
