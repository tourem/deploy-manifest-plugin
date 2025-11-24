package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for dependency analysis (unused/undeclared detection).
 */
public class DependencyAnalysisConfiguration {
    
    private Boolean enabled = false;
    private Integer healthThreshold = 80;
    private Boolean filterSpringStarters = true;
    private Boolean filterLombok = true;
    private Boolean filterAnnotationProcessors = true;
    private Boolean generateRecommendations = true;
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Integer getHealthThreshold() {
        return healthThreshold;
    }
    
    public void setHealthThreshold(Integer healthThreshold) {
        this.healthThreshold = healthThreshold;
    }
    
    public Boolean getFilterSpringStarters() {
        return filterSpringStarters;
    }
    
    public void setFilterSpringStarters(Boolean filterSpringStarters) {
        this.filterSpringStarters = filterSpringStarters;
    }
    
    public Boolean getFilterLombok() {
        return filterLombok;
    }
    
    public void setFilterLombok(Boolean filterLombok) {
        this.filterLombok = filterLombok;
    }
    
    public Boolean getFilterAnnotationProcessors() {
        return filterAnnotationProcessors;
    }
    
    public void setFilterAnnotationProcessors(Boolean filterAnnotationProcessors) {
        this.filterAnnotationProcessors = filterAnnotationProcessors;
    }
    
    public Boolean getGenerateRecommendations() {
        return generateRecommendations;
    }
    
    public void setGenerateRecommendations(Boolean generateRecommendations) {
        this.generateRecommendations = generateRecommendations;
    }
    
    @Override
    public String toString() {
        return "DependencyAnalysisConfiguration{" +
                "enabled=" + enabled +
                ", healthThreshold=" + healthThreshold +
                ", filterSpringStarters=" + filterSpringStarters +
                ", filterLombok=" + filterLombok +
                ", filterAnnotationProcessors=" + filterAnnotationProcessors +
                ", generateRecommendations=" + generateRecommendations +
                '}';
    }
}
