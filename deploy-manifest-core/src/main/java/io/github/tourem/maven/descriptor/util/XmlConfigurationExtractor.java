package io.github.tourem.maven.descriptor.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Utility class for extracting values from XML configuration (Xpp3Dom).
 * Centralizes XML parsing logic used across different detectors and collectors.
 *
 * @author tourem
 */
@Slf4j
public final class XmlConfigurationExtractor {

    private XmlConfigurationExtractor() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Extract a child node value from XML configuration.
     *
     * @param configuration XML configuration object
     * @param childName name of the child node
     * @return child node value or null if not found
     */
    public static String extractChildValue(Object configuration, String childName) {
        if (configuration == null || childName == null) {
            return null;
        }

        if (!(configuration instanceof Xpp3Dom)) {
            log.debug("Configuration is not Xpp3Dom type: {}", configuration.getClass());
            return null;
        }

        try {
            Xpp3Dom config = (Xpp3Dom) configuration;
            Xpp3Dom childNode = config.getChild(childName);
            return childNode != null ? childNode.getValue() : null;
        } catch (Exception e) {
            log.warn("Error extracting child value '{}' from configuration: {}", 
                     childName, e.getMessage());
            return null;
        }
    }

    /**
     * Extract a nested child node value from XML configuration.
     *
     * @param configuration XML configuration object
     * @param path path to the child node (e.g., "execution", "configuration", "mainClass")
     * @return nested child node value or null if not found
     */
    public static String extractNestedValue(Object configuration, String... path) {
        if (configuration == null || path == null || path.length == 0) {
            return null;
        }

        if (!(configuration instanceof Xpp3Dom)) {
            return null;
        }

        try {
            Xpp3Dom current = (Xpp3Dom) configuration;
            
            for (int i = 0; i < path.length - 1; i++) {
                current = current.getChild(path[i]);
                if (current == null) {
                    return null;
                }
            }
            
            Xpp3Dom finalNode = current.getChild(path[path.length - 1]);
            return finalNode != null ? finalNode.getValue() : null;
        } catch (Exception e) {
            log.warn("Error extracting nested value from configuration: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if a child node exists in XML configuration.
     *
     * @param configuration XML configuration object
     * @param childName name of the child node
     * @return true if child exists, false otherwise
     */
    public static boolean hasChild(Object configuration, String childName) {
        if (configuration == null || childName == null) {
            return false;
        }

        if (!(configuration instanceof Xpp3Dom)) {
            return false;
        }

        try {
            Xpp3Dom config = (Xpp3Dom) configuration;
            return config.getChild(childName) != null;
        } catch (Exception e) {
            log.warn("Error checking child existence '{}': {}", childName, e.getMessage());
            return false;
        }
    }

    /**
     * Extract a boolean value from XML configuration.
     *
     * @param configuration XML configuration object
     * @param childName name of the child node
     * @param defaultValue default value if not found or invalid
     * @return boolean value
     */
    public static boolean extractBooleanValue(Object configuration, String childName, boolean defaultValue) {
        String value = extractChildValue(configuration, childName);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        
        try {
            return Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            log.warn("Invalid boolean value for '{}': {}", childName, value);
            return defaultValue;
        }
    }
}
