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
public class HealthScore {
    private Integer overall;       // 0..100
    private String grade;          // A, B+, C, ...
    private Breakdown breakdown;   // optional category breakdown
    private List<ActionableImprovement> actionableImprovements; // optional quick wins

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Breakdown {
        private Category cleanliness;
        private Category security;
        private Category maintainability;
        private Category licenses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Category {
        private Integer score;   // 0..100
        private Integer outOf;   // usually 100
        private Double weight;   // e.g., 0.4 for cleanliness
        private String details;  // summary text for the category
        private List<Factor> factors; // optional
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Factor {
        private String factor;   // e.g., "5 unused dependencies"
        private Integer impact;  // e.g., -10
        private String details;  // explanation
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActionableImprovement {
        private String action;       // e.g., "Remove 5 unused dependencies"
        private Integer scoreImpact; // e.g., +8
        private String effort;       // LOW/MEDIUM/HIGH
        private Integer priority;    // 1..n
    }
}

