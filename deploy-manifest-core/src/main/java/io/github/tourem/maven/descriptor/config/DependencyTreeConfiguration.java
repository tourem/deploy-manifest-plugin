package io.github.tourem.maven.descriptor.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration for dependency tree collection.
 */
public class DependencyTreeConfiguration {
    
    @NotNull
    private Boolean enabled = false;
    
    @NotNull
    @Min(value = 1, message = "Dependency tree depth must be at least 1")
    @Max(value = 10, message = "Dependency tree depth must be at most 10 (deep trees can cause performance issues)")
    private Integer depth = 3;
    
    @NotNull
    @Pattern(regexp = "flat|tree|both", message = "Dependency tree format must be one of: flat, tree, both")
    private String format = "flat";
    
    @NotNull
    private Boolean includeTransitive = true;
    
    @NotNull
    private List<String> scopes = new ArrayList<>(Arrays.asList("compile", "runtime"));
    
    @NotNull
    private Boolean includeOptional = false;
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Integer getDepth() {
        return depth;
    }
    
    public void setDepth(Integer depth) {
        this.depth = depth;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public Boolean getIncludeTransitive() {
        return includeTransitive;
    }
    
    public void setIncludeTransitive(Boolean includeTransitive) {
        this.includeTransitive = includeTransitive;
    }
    
    public List<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
    
    public Boolean getIncludeOptional() {
        return includeOptional;
    }
    
    public void setIncludeOptional(Boolean includeOptional) {
        this.includeOptional = includeOptional;
    }
    
    @Override
    public String toString() {
        return "DependencyTreeConfiguration{" +
                "enabled=" + enabled +
                ", depth=" + depth +
                ", format='" + format + '\'' +
                ", includeTransitive=" + includeTransitive +
                ", scopes=" + scopes +
                ", includeOptional=" + includeOptional +
                '}';
    }
}
