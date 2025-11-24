package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for framework detection (Spring Boot, Quarkus, Micronaut).
 */
public class FrameworksConfiguration {
    
    private Boolean autoDetect = true;
    private Boolean includeProfiles = true;
    private Boolean includeConfiguration = false;
    
    public Boolean getAutoDetect() {
        return autoDetect;
    }
    
    public void setAutoDetect(Boolean autoDetect) {
        this.autoDetect = autoDetect;
    }
    
    public Boolean getIncludeProfiles() {
        return includeProfiles;
    }
    
    public void setIncludeProfiles(Boolean includeProfiles) {
        this.includeProfiles = includeProfiles;
    }
    
    public Boolean getIncludeConfiguration() {
        return includeConfiguration;
    }
    
    public void setIncludeConfiguration(Boolean includeConfiguration) {
        this.includeConfiguration = includeConfiguration;
    }
    
    @Override
    public String toString() {
        return "FrameworksConfiguration{" +
                "autoDetect=" + autoDetect +
                ", includeProfiles=" + includeProfiles +
                ", includeConfiguration=" + includeConfiguration +
                '}';
    }
}
