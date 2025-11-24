package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for CI/CD environment detection.
 */
public class CiConfiguration {
    
    private Boolean autoDetect = true;
    private Boolean includeEnvironment = true;
    
    public Boolean getAutoDetect() {
        return autoDetect;
    }
    
    public void setAutoDetect(Boolean autoDetect) {
        this.autoDetect = autoDetect;
    }
    
    public Boolean getIncludeEnvironment() {
        return includeEnvironment;
    }
    
    public void setIncludeEnvironment(Boolean includeEnvironment) {
        this.includeEnvironment = includeEnvironment;
    }
    
    @Override
    public String toString() {
        return "CiConfiguration{" +
                "autoDetect=" + autoDetect +
                ", includeEnvironment=" + includeEnvironment +
                '}';
    }
}
