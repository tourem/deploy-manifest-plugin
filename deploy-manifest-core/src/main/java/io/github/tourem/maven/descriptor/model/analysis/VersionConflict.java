package io.github.tourem.maven.descriptor.model.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionConflict {
    public enum RiskLevel { LOW, MEDIUM, HIGH }

    private String groupId;
    private String artifactId;
    private List<String> versions; // distinct versions found in the graph
    private String selectedVersion; // version chosen by Maven resolution
    private RiskLevel riskLevel;
}

