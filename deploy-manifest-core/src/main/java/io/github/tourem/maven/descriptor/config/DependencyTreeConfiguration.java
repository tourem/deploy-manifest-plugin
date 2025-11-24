package io.github.tourem.maven.descriptor.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration for dependency tree collection.
 */
public class DependencyTreeConfiguration {
    
    private Boolean enabled = false;
    private Integer depth = 3;
    private String format = "flat";
    private Boolean includeTransitive = true;
    private List<String> scopes = new ArrayList<>(Arrays.asList("compile", "runtime"));
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
