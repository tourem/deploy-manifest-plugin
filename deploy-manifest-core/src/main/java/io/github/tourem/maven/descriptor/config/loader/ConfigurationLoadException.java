package io.github.tourem.maven.descriptor.config.loader;

/**
 * Exception thrown when configuration loading fails.
 */
public class ConfigurationLoadException extends Exception {
    
    public ConfigurationLoadException(String message) {
        super(message);
    }
    
    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
