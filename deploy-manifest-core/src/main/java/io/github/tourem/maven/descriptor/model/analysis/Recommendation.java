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
public class Recommendation {
    public enum Type { REMOVE_DEPENDENCY, CHANGE_SCOPE }

    private Type type;
    private String groupId;
    private String artifactId;
    private String version;
    private String targetScope;

    private String pomPatch;
    private List<String> verifyCommands;
    private List<String> rollbackCommands;

    @Builder.Default
    private Impact impact = null;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Impact {
        private Long sizeSavingsBytes;
        private Double sizeSavingsKB;
        private Double sizeSavingsMB;
        private String securityImpact; // optional textual assessment
    }
}

