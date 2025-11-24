package io.github.tourem.maven.descriptor.config;

/**
 * Predefined configuration profiles for the Maven Deploy Manifest Plugin.
 * Each profile provides sensible defaults for common use cases.
 */
public enum ManifestProfile {
    
    /**
     * Basic profile - Minimal configuration.
     * <p>
     * Generates JSON only with essential information.
     * Suitable for quick manifest generation.
     * </p>
     * <ul>
     *   <li>Output: JSON only</li>
     *   <li>Dependency tree: disabled</li>
     *   <li>Metadata: minimal</li>
     * </ul>
     */
    BASIC("basic"),
    
    /**
     * Standard profile - Team documentation.
     * <p>
     * Generates JSON + HTML with dependency tree.
     * Suitable for team collaboration and code reviews.
     * </p>
     * <ul>
     *   <li>Output: JSON + HTML</li>
     *   <li>Dependency tree: enabled (depth=3)</li>
     *   <li>Metadata: standard</li>
     * </ul>
     */
    STANDARD("standard"),
    
    /**
     * Full profile - Complete analysis.
     * <p>
     * Generates all formats with complete metadata.
     * Suitable for security audits and compliance reviews.
     * </p>
     * <ul>
     *   <li>Output: JSON + YAML + HTML</li>
     *   <li>Dependency tree: enabled (depth=5)</li>
     *   <li>Dependency analysis: enabled</li>
     *   <li>Metadata: licenses, properties, plugins, checksums</li>
     * </ul>
     */
    FULL("full"),
    
    /**
     * CI profile - Optimized for CI/CD.
     * <p>
     * Generates JSON with archive, optimized for automated builds.
     * Suitable for CI/CD pipelines and artifact repositories.
     * </p>
     * <ul>
     *   <li>Output: JSON with ZIP archive</li>
     *   <li>Archive: attached to Maven build</li>
     *   <li>Dependency tree: enabled (depth=2)</li>
     *   <li>Git: always fetch</li>
     * </ul>
     */
    CI("ci");
    
    private final String value;
    
    ManifestProfile(String value) {
        this.value = value;
    }
    
    /**
     * Gets the string value of this profile.
     *
     * @return the profile value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Parses a string value to a ManifestProfile enum.
     *
     * @param value the string value
     * @return the corresponding ManifestProfile
     * @throws IllegalArgumentException if the value is not a valid profile
     */
    public static ManifestProfile fromValue(String value) {
        if (value == null) {
            return BASIC; // Default profile
        }
        
        for (ManifestProfile profile : values()) {
            if (profile.value.equalsIgnoreCase(value)) {
                return profile;
            }
        }
        
        throw new IllegalArgumentException(
            "Invalid profile: '" + value + "'. Allowed values: basic, standard, full, ci"
        );
    }
    
    /**
     * Gets the default configuration for this profile.
     *
     * @return a ManifestConfiguration with profile defaults applied
     */
    public ManifestConfiguration getDefaultConfiguration() {
        ManifestConfiguration config = new ManifestConfiguration();
        config.setProfile(this);
        
        switch (this) {
            case BASIC:
                applyBasicDefaults(config);
                break;
            case STANDARD:
                applyStandardDefaults(config);
                break;
            case FULL:
                applyFullDefaults(config);
                break;
            case CI:
                applyCiDefaults(config);
                break;
        }
        
        return config;
    }
    
    private void applyBasicDefaults(ManifestConfiguration config) {
        // Output: JSON only
        OutputConfiguration output = new OutputConfiguration();
        output.setFormats(java.util.Arrays.asList("json"));
        config.setOutput(output);
        
        // Dependencies: tree disabled
        DependenciesConfiguration deps = new DependenciesConfiguration();
        DependencyTreeConfiguration tree = new DependencyTreeConfiguration();
        tree.setEnabled(false);
        deps.setTree(tree);
        config.setDependencies(deps);
        
        // Metadata: minimal
        MetadataConfiguration metadata = new MetadataConfiguration();
        metadata.setLicenses(false);
        metadata.setProperties(false);
        metadata.setPlugins(false);
        config.setMetadata(metadata);
    }
    
    private void applyStandardDefaults(ManifestConfiguration config) {
        // Output: JSON + HTML
        OutputConfiguration output = new OutputConfiguration();
        output.setFormats(java.util.Arrays.asList("json", "html"));
        config.setOutput(output);
        
        // Dependencies: tree enabled (depth=3)
        DependenciesConfiguration deps = new DependenciesConfiguration();
        DependencyTreeConfiguration tree = new DependencyTreeConfiguration();
        tree.setEnabled(true);
        tree.setDepth(3);
        deps.setTree(tree);
        config.setDependencies(deps);
        
        // Metadata: standard
        MetadataConfiguration metadata = new MetadataConfiguration();
        metadata.setLicenses(false);
        metadata.setProperties(false);
        metadata.setPlugins(false);
        config.setMetadata(metadata);
    }
    
    private void applyFullDefaults(ManifestConfiguration config) {
        // Output: JSON + YAML + HTML
        OutputConfiguration output = new OutputConfiguration();
        output.setFormats(java.util.Arrays.asList("json", "yaml", "html"));
        config.setOutput(output);
        
        // Dependencies: tree enabled (depth=5) + analysis
        DependenciesConfiguration deps = new DependenciesConfiguration();
        
        DependencyTreeConfiguration tree = new DependencyTreeConfiguration();
        tree.setEnabled(true);
        tree.setDepth(5);
        deps.setTree(tree);
        
        DependencyAnalysisConfiguration analysis = new DependencyAnalysisConfiguration();
        analysis.setEnabled(true);
        analysis.setHealthThreshold(80);
        deps.setAnalysis(analysis);
        
        config.setDependencies(deps);
        
        // Metadata: complete
        MetadataConfiguration metadata = new MetadataConfiguration();
        metadata.setLicenses(true);
        metadata.setProperties(true);
        metadata.setPlugins(true);
        metadata.setChecksums(true);
        config.setMetadata(metadata);
    }
    
    private void applyCiDefaults(ManifestConfiguration config) {
        // Output: JSON with archive
        OutputConfiguration output = new OutputConfiguration();
        output.setFormats(java.util.Arrays.asList("json"));
        output.setArchive(true);
        output.setAttach(true);
        config.setOutput(output);
        
        // Dependencies: tree enabled (depth=2)
        DependenciesConfiguration deps = new DependenciesConfiguration();
        DependencyTreeConfiguration tree = new DependencyTreeConfiguration();
        tree.setEnabled(true);
        tree.setDepth(2);
        deps.setTree(tree);
        config.setDependencies(deps);
        
        // Git: always fetch
        GitConfiguration git = new GitConfiguration();
        git.setFetch(GitFetchMode.ALWAYS);
        config.setGit(git);
        
        // Metadata: minimal
        MetadataConfiguration metadata = new MetadataConfiguration();
        metadata.setLicenses(false);
        metadata.setProperties(false);
        metadata.setPlugins(false);
        config.setMetadata(metadata);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
