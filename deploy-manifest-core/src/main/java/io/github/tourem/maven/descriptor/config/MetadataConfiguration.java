package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for metadata inclusion.
 */
public class MetadataConfiguration {
    
    private Boolean licenses = false;
    private Boolean properties = false;
    private Boolean plugins = false;
    private Boolean checksums = false;
    private Boolean includeSystemProperties = true;
    private Boolean includeEnvironmentVariables = false;
    private Boolean filterSensitive = true;
    
    public Boolean getLicenses() {
        return licenses;
    }
    
    public void setLicenses(Boolean licenses) {
        this.licenses = licenses;
    }
    
    public Boolean getProperties() {
        return properties;
    }
    
    public void setProperties(Boolean properties) {
        this.properties = properties;
    }
    
    public Boolean getPlugins() {
        return plugins;
    }
    
    public void setPlugins(Boolean plugins) {
        this.plugins = plugins;
    }
    
    public Boolean getChecksums() {
        return checksums;
    }
    
    public void setChecksums(Boolean checksums) {
        this.checksums = checksums;
    }
    
    public Boolean getIncludeSystemProperties() {
        return includeSystemProperties;
    }
    
    public void setIncludeSystemProperties(Boolean includeSystemProperties) {
        this.includeSystemProperties = includeSystemProperties;
    }
    
    public Boolean getIncludeEnvironmentVariables() {
        return includeEnvironmentVariables;
    }
    
    public void setIncludeEnvironmentVariables(Boolean includeEnvironmentVariables) {
        this.includeEnvironmentVariables = includeEnvironmentVariables;
    }
    
    public Boolean getFilterSensitive() {
        return filterSensitive;
    }
    
    public void setFilterSensitive(Boolean filterSensitive) {
        this.filterSensitive = filterSensitive;
    }
    
    @Override
    public String toString() {
        return "MetadataConfiguration{" +
                "licenses=" + licenses +
                ", properties=" + properties +
                ", plugins=" + plugins +
                ", checksums=" + checksums +
                ", includeSystemProperties=" + includeSystemProperties +
                ", includeEnvironmentVariables=" + includeEnvironmentVariables +
                ", filterSensitive=" + filterSensitive +
                '}';
    }
}
