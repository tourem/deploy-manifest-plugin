package io.github.tourem.maven.descriptor.model;

import lombok.Builder;

/**
 * Testing and code coverage information.
 * 
 * @author tourem
 */
@Builder
public record TestingInfo(
    CoverageMetrics coverage,
    TestCount testCount,
    String qualityGate,
    String testFramework,
    String coverageTool
) {
    
    /**
     * Code coverage metrics.
     */
    @Builder
    public record CoverageMetrics(
        Double line,
        Double branch,
        Double instruction,
        Double complexity,
        Double method,
        Double clazz
    ) {}
    
    /**
     * Test count by type.
     */
    @Builder
    public record TestCount(
        Integer unit,
        Integer integration,
        Integer e2e,
        Integer total,
        Integer skipped,
        Integer failed
    ) {}
}
