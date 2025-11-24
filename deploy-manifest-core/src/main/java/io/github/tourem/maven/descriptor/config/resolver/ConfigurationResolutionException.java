package io.github.tourem.maven.descriptor.config.resolver;

/**
 * Exception thrown when configuration resolution fails.
 */
public class ConfigurationResolutionException extends Exception {
    
    public ConfigurationResolutionException(String message) {
        super(message);
    }
    
    public ConfigurationResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
