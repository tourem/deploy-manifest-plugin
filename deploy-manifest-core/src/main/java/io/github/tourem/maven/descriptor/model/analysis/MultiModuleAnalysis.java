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
public class MultiModuleAnalysis {
    private Integer moduleCount;
    private Integer analyzedModuleCount;
    private List<CommonUnused> commonUnused;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CommonUnused {
        private String groupId;
        private String artifactId;
        private List<String> modules; // artifactIds or module paths
    }
}

