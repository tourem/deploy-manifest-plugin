package io.github.tourem.maven.descriptor.config.converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting string values to typed values.
 * Used by environment variable and command line loaders.
 */
public class TypeConverter {
    
    /**
     * Converts a string value to a Boolean.
     *
     * @param value the string value
     * @return the boolean value, or null if value is null
     * @throws IllegalArgumentException if value cannot be converted
     */
    public static Boolean toBoolean(String value) {
        if (value == null) {
            return null;
        }
        
        String normalized = value.trim().toLowerCase();
        
        if ("true".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized)) {
            return true;
        }
        if ("false".equals(normalized) || "no".equals(normalized) || "0".equals(normalized)) {
            return false;
        }
        
        throw new IllegalArgumentException(
            "Cannot convert '" + value + "' to boolean. Expected: true/false, yes/no, or 1/0"
        );
    }
    
    /**
     * Converts a string value to an Integer.
     *
     * @param value the string value
     * @return the integer value, or null if value is null
     * @throws IllegalArgumentException if value cannot be converted
     */
    public static Integer toInteger(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Cannot convert '" + value + "' to integer: " + e.getMessage()
            );
        }
    }
    
    /**
     * Converts a string value to a String (trimmed).
     *
     * @param value the string value
     * @return the trimmed string, or null if value is null
     */
    public static String toString(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
    
    /**
     * Converts a comma-separated string to a list of strings.
     * Example: "json,html,yaml" → ["json", "html", "yaml"]
     *
     * @param value the comma-separated string
     * @return the list of strings, or empty list if value is null/empty
     */
    public static List<String> toStringList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.stream(value.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
    
    /**
     * Converts a property path to a nested property name.
     * Examples:
     * - "manifest.output.directory" → "output.directory"
     * - "MANIFEST_OUTPUT_DIRECTORY" → "output.directory"
     *
     * @param propertyPath the property path
     * @param prefix the prefix to remove (e.g., "manifest." or "MANIFEST_")
     * @return the nested property name
     */
    public static String toNestedPropertyName(String propertyPath, String prefix) {
        if (propertyPath == null) {
            return null;
        }
        
        String normalized = propertyPath;
        
        // Remove prefix if present
        if (prefix != null && propertyPath.startsWith(prefix)) {
            normalized = propertyPath.substring(prefix.length());
        }
        
        // Convert UPPER_SNAKE_CASE to lower.dot.case
        if (normalized.contains("_")) {
            normalized = normalized.toLowerCase().replace("_", ".");
        }
        
        return normalized;
    }
    
    /**
     * Converts an environment variable name to a property path.
     * Example: "MANIFEST_OUTPUT_DIRECTORY" → "output.directory"
     *
     * @param envVarName the environment variable name
     * @return the property path
     */
    public static String envVarToPropertyPath(String envVarName) {
        return toNestedPropertyName(envVarName, "MANIFEST_");
    }
    
    /**
     * Converts a command line property to a property path.
     * Example: "manifest.output.directory" → "output.directory"
     *
     * @param cmdLineProperty the command line property
     * @return the property path
     */
    public static String cmdLineToPropertyPath(String cmdLineProperty) {
        return toNestedPropertyName(cmdLineProperty, "manifest.");
    }
}
