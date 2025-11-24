package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for validation behavior.
 */
public class ValidationConfiguration {
    
    private Boolean enabled = true;
    private Boolean failOnError = true;
    private Boolean warnOnDeprecated = true;
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Boolean getFailOnError() {
        return failOnError;
    }
    
    public void setFailOnError(Boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    public Boolean getWarnOnDeprecated() {
        return warnOnDeprecated;
    }
    
    public void setWarnOnDeprecated(Boolean warnOnDeprecated) {
        this.warnOnDeprecated = warnOnDeprecated;
    }
    
    @Override
    public String toString() {
        return "ValidationConfiguration{" +
                "enabled=" + enabled +
                ", failOnError=" + failOnError +
                ", warnOnDeprecated=" + warnOnDeprecated +
                '}';
    }
}
