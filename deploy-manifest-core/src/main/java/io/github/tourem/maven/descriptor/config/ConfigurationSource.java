package io.github.tourem.maven.descriptor.config;

/**
 * Represents the source of a configuration value.
 * Used to track where each configuration value comes from for debugging and display.
 */
public enum ConfigurationSource {
    
    /**
     * Value comes from command line (-Dmanifest.*).
     * Highest priority.
     */
    COMMAND_LINE("CLI", "⌨️", 1),
    
    /**
     * Value comes from environment variable (MANIFEST_*).
     * Second highest priority.
     */
    ENVIRONMENT("ENV", "🌍", 2),
    
    /**
     * Value comes from .deploy-manifest.yml file.
     * Third priority.
     */
    YAML_FILE("YAML", "📄", 3),
    
    /**
     * Value comes from profile defaults.
     * Fourth priority.
     */
    PROFILE("Profile", "📦", 4),
    
    /**
     * Value comes from pom.xml configuration.
     * Fifth priority.
     */
    POM_XML("POM", "🔨", 5),
    
    /**
     * Value is the plugin default.
     * Lowest priority.
     */
    DEFAULT("Default", "🔧", 6);
    
    private final String displayName;
    private final String symbol;
    private final int priority;
    
    ConfigurationSource(String displayName, String symbol, int priority) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.priority = priority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public int getPriority() {
        return priority;
    }
    
    /**
     * Checks if this source has higher priority than another source.
     *
     * @param other the other source to compare
     * @return true if this source has higher priority (lower priority number)
     */
    public boolean hasHigherPriorityThan(ConfigurationSource other) {
        return this.priority < other.priority;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
