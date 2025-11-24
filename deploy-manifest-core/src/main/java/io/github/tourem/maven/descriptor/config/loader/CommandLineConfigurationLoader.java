package io.github.tourem.maven.descriptor.config.loader;

import io.github.tourem.maven.descriptor.config.ManifestConfiguration;
import io.github.tourem.maven.descriptor.config.converter.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Loads configuration from command line properties (-Dmanifest.*).
 * <p>
 * Examples:
 * - -Dmanifest.profile=standard
 * - -Dmanifest.output.directory=/tmp/reports
 * - -Dmanifest.output.formats=json,html
 * - -Dmanifest.dependencies.tree.enabled=true
 * - -Dmanifest.dependencies.tree.depth=5
 * </p>
 */
public class CommandLineConfigurationLoader {
    
    private static final Logger log = LoggerFactory.getLogger(CommandLineConfigurationLoader.class);
    private static final String PROPERTY_PREFIX = "manifest.";
    
    private final EnvironmentConfigurationLoader envLoader;
    
    public CommandLineConfigurationLoader() {
        this.envLoader = new EnvironmentConfigurationLoader();
    }
    
    /**
     * Loads configuration from system properties.
     *
     * @return the configuration loaded from command line properties
     */
    public ManifestConfiguration load() {
        return load(System.getProperties());
    }
    
    /**
     * Loads configuration from properties.
     * Package-private for testing.
     *
     * @param properties the properties
     * @return the configuration loaded from properties
     */
    ManifestConfiguration load(Properties properties) {
        // Convert properties to environment-style map for reuse
        java.util.Map<String, String> envStyleMap = new java.util.HashMap<>();
        
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(PROPERTY_PREFIX)) {
                continue;
            }
            
            // Convert manifest.output.directory → MANIFEST_OUTPUT_DIRECTORY
            String propertyPath = TypeConverter.cmdLineToPropertyPath(key);
            String envKey = "MANIFEST_" + propertyPath.toUpperCase().replace(".", "_");
            String value = properties.getProperty(key);
            
            envStyleMap.put(envKey, value);
            log.debug("Loaded from command line: {} = {}", key, value);
        }
        
        if (envStyleMap.isEmpty()) {
            log.debug("No manifest.* properties found");
        }
        
        // Reuse environment loader logic
        return envLoader.load(envStyleMap);
    }
}
