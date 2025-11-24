package io.github.tourem.maven.descriptor.config;

/**
 * Defines when to fetch Git information.
 */
public enum GitFetchMode {
    
    /**
     * Automatically fetch Git information if .git directory exists.
     */
    AUTO("auto"),
    
    /**
     * Always fetch Git information (fails if not a Git repository).
     */
    ALWAYS("always"),
    
    /**
     * Never fetch Git information.
     */
    NEVER("never");
    
    private final String value;
    
    GitFetchMode(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static GitFetchMode fromValue(String value) {
        if (value == null) {
            return AUTO;
        }
        
        for (GitFetchMode mode : values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        
        throw new IllegalArgumentException(
            "Invalid git.fetch mode: '" + value + "'. Allowed values: auto, always, never"
        );
    }
    
    @Override
    public String toString() {
        return value;
    }
}
