package io.github.tourem.maven.descriptor.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration for Docker/Container detection.
 */
public class DockerConfiguration {
    
    private Boolean autoDetect = true;
    private List<String> registries = new ArrayList<>(Arrays.asList("docker.io", "ghcr.io"));
    private Boolean includeImageDigest = false;
    
    public Boolean getAutoDetect() {
        return autoDetect;
    }
    
    public void setAutoDetect(Boolean autoDetect) {
        this.autoDetect = autoDetect;
    }
    
    public List<String> getRegistries() {
        return registries;
    }
    
    public void setRegistries(List<String> registries) {
        this.registries = registries;
    }
    
    public Boolean getIncludeImageDigest() {
        return includeImageDigest;
    }
    
    public void setIncludeImageDigest(Boolean includeImageDigest) {
        this.includeImageDigest = includeImageDigest;
    }
    
    @Override
    public String toString() {
        return "DockerConfiguration{" +
                "autoDetect=" + autoDetect +
                ", registries=" + registries +
                ", includeImageDigest=" + includeImageDigest +
                '}';
    }
}
