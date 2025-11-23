package io.github.tourem.maven.plugin;

/**
 * Predefined profiles for manifest generation.
 * Each profile defines a set of default options for common use cases.
 *
 * <p>Profiles can be overridden by specifying individual options on the command line.</p>
 *
 * @since 2.8.0
 */
public enum ManifestProfile {

    /**
     * Basic profile (default).
     * <p>Generates only JSON with essential information:</p>
     * <ul>
     *   <li>Module basic info</li>
     *   <li>External dependencies</li>
     *   <li>Testing info</li>
     *   <li>Build metrics</li>
     * </ul>
     * <p>Does NOT include: dependency tree, licenses, properties, plugins</p>
     *
     * <p>Usage: {@code mvn deploy-manifest:generate} (default)</p>
     */
    BASIC,

    /**
     * Standard profile.
     * <p>Generates JSON + HTML with moderate detail:</p>
     * <ul>
     *   <li>All from BASIC</li>
     *   <li>HTML visualization</li>
     *   <li>Dependency tree (depth=2)</li>
     * </ul>
     *
     * <p>Usage: {@code mvn deploy-manifest:generate -Dmanifest.profile=standard}</p>
     */
    STANDARD,

    /**
     * Full profile.
     * <p>Generates JSON + YAML + HTML with complete information:</p>
     * <ul>
     *   <li>All from STANDARD</li>
     *   <li>YAML format</li>
     *   <li>Licenses</li>
     *   <li>Properties</li>
     *   <li>Plugins</li>
     *   <li>Full dependency tree (depth=5)</li>
     * </ul>
     *
     * <p>Usage: {@code mvn deploy-manifest:generate -Dmanifest.profile=full}</p>
     */
    FULL,

    /**
     * CI/CD profile.
     * <p>Optimized for continuous integration pipelines:</p>
     * <ul>
     *   <li>All from STANDARD</li>
     *   <li>ZIP archive with all reports</li>
     *   <li>Archive attached to build</li>
     *   <li>Compression enabled</li>
     *   <li>All reports included</li>
     * </ul>
     *
     * <p>Usage: {@code mvn deploy-manifest:generate -Dmanifest.profile=ci}</p>
     */
    CI;

    /**
     * Apply this profile's default settings to the given mojo.
     *
     * @param mojo the mojo to configure
     */
    public void applyDefaults(GenerateDescriptorMojo mojo) {
        switch (this) {
            case BASIC:
                applyBasicDefaults(mojo);
                break;
            case STANDARD:
                applyStandardDefaults(mojo);
                break;
            case FULL:
                applyFullDefaults(mojo);
                break;
            case CI:
                applyCiDefaults(mojo);
                break;
        }
    }

    private void applyBasicDefaults(GenerateDescriptorMojo mojo) {
        // JSON only (default)
        mojo.setExportFormatIfNotSet("json");
        mojo.setGenerateHtmlIfNotSet(false);
        
        // No additional metadata
        mojo.setIncludeDependencyTreeIfNotSet(false);
        mojo.setIncludeLicensesIfNotSet(false);
        mojo.setIncludePropertiesIfNotSet(false);
        mojo.setIncludePluginsIfNotSet(false);
    }

    private void applyStandardDefaults(GenerateDescriptorMojo mojo) {
        // JSON + HTML
        mojo.setExportFormatIfNotSet("json");
        mojo.setGenerateHtmlIfNotSet(true);
        
        // Dependency tree with moderate depth
        mojo.setIncludeDependencyTreeIfNotSet(true);
        mojo.setDependencyTreeMaxDepthIfNotSet(2);
        
        // No licenses, properties, plugins
        mojo.setIncludeLicensesIfNotSet(false);
        mojo.setIncludePropertiesIfNotSet(false);
        mojo.setIncludePluginsIfNotSet(false);
    }

    private void applyFullDefaults(GenerateDescriptorMojo mojo) {
        // JSON + YAML + HTML
        mojo.setExportFormatIfNotSet("both");
        mojo.setGenerateHtmlIfNotSet(true);
        
        // All metadata
        mojo.setIncludeDependencyTreeIfNotSet(true);
        mojo.setDependencyTreeMaxDepthIfNotSet(5);
        mojo.setIncludeLicensesIfNotSet(true);
        mojo.setIncludePropertiesIfNotSet(true);
        mojo.setIncludePluginsIfNotSet(true);
    }

    private void applyCiDefaults(GenerateDescriptorMojo mojo) {
        // JSON + HTML
        mojo.setExportFormatIfNotSet("json");
        mojo.setGenerateHtmlIfNotSet(true);
        
        // Dependency tree
        mojo.setIncludeDependencyTreeIfNotSet(true);
        mojo.setDependencyTreeMaxDepthIfNotSet(3);
        
        // Archive for CI
        mojo.setFormatIfNotSet("zip");
        mojo.setAttachIfNotSet(true);
        mojo.setCompressIfNotSet(true);
        mojo.setIncludeAllReportsIfNotSet(true);
        
        // No licenses, properties, plugins (keep CI fast)
        mojo.setIncludeLicensesIfNotSet(false);
        mojo.setIncludePropertiesIfNotSet(false);
        mojo.setIncludePluginsIfNotSet(false);
    }

    /**
     * Parse a profile from string (case-insensitive).
     *
     * @param value the profile name
     * @return the profile, or BASIC if invalid
     */
    public static ManifestProfile fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BASIC;
        }
        
        try {
            return ManifestProfile.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return BASIC;
        }
    }
}
