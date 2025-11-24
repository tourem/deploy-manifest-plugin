package io.github.tourem.maven.descriptor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a resolved configuration with source tracking.
 * This class wraps a ManifestConfiguration and tracks where each value came from.
 */
public class ResolvedConfiguration {
    
    private final ManifestConfiguration configuration;
    private final Map<String, ConfigurationSource> sources;
    
    public ResolvedConfiguration(ManifestConfiguration configuration) {
        this.configuration = configuration;
        this.sources = new HashMap<>();
    }
    
    /**
     * Gets the resolved configuration.
     *
     * @return the manifest configuration
     */
    public ManifestConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Gets the source of a specific property.
     *
     * @param propertyPath the property path (e.g., "output.directory")
     * @return the source, or DEFAULT if not tracked
     */
    public ConfigurationSource getSource(String propertyPath) {
        return sources.getOrDefault(propertyPath, ConfigurationSource.DEFAULT);
    }
    
    /**
     * Sets the source of a property.
     *
     * @param propertyPath the property path
     * @param source the source
     */
    public void setSource(String propertyPath, ConfigurationSource source) {
        sources.put(propertyPath, source);
    }
    
    /**
     * Gets all tracked sources.
     *
     * @return the map of property paths to sources
     */
    public Map<String, ConfigurationSource> getAllSources() {
        return new HashMap<>(sources);
    }
    
    /**
     * Checks if a property has been set from a non-default source.
     *
     * @param propertyPath the property path
     * @return true if the property was explicitly set
     */
    public boolean isExplicitlySet(String propertyPath) {
        ConfigurationSource source = getSource(propertyPath);
        return source != ConfigurationSource.DEFAULT;
    }
}
