package io.github.tourem.maven.descriptor.config.loader;

import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.converter.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Loads configuration from environment variables.
 * Environment variables must be prefixed with MANIFEST_ and use UPPER_SNAKE_CASE.
 * <p>
 * Examples:
 * - MANIFEST_PROFILE=standard
 * - MANIFEST_OUTPUT_DIRECTORY=/tmp/reports
 * - MANIFEST_OUTPUT_FORMATS=json,html
 * - MANIFEST_DEPENDENCIES_TREE_ENABLED=true
 * - MANIFEST_DEPENDENCIES_TREE_DEPTH=5
 * </p>
 */
public class EnvironmentConfigurationLoader {
    
    private static final Logger log = LoggerFactory.getLogger(EnvironmentConfigurationLoader.class);
    private static final String ENV_PREFIX = "MANIFEST_";
    
    /**
     * Loads configuration from environment variables.
     *
     * @return the configuration loaded from environment, or an empty configuration if no env vars found
     */
    public ManifestConfiguration load() {
        return load(System.getenv());
    }
    
    /**
     * Loads configuration from a map of environment variables.
     * Package-private for testing.
     *
     * @param envVars the environment variables map
     * @return the configuration loaded from environment
     */
    ManifestConfiguration load(Map<String, String> envVars) {
        ManifestConfiguration config = new ManifestConfiguration();
        boolean hasAnyConfig = false;
        
        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Only process MANIFEST_* variables
            if (!key.startsWith(ENV_PREFIX)) {
                continue;
            }
            
            try {
                String propertyPath = TypeConverter.envVarToPropertyPath(key);
                applyProperty(config, propertyPath, value);
                hasAnyConfig = true;
                log.debug("Loaded from environment: {} = {}", key, value);
            } catch (Exception e) {
                log.warn("Failed to apply environment variable {}: {}", key, e.getMessage());
            }
        }
        
        if (!hasAnyConfig) {
            log.debug("No MANIFEST_* environment variables found");
        }
        
        return config;
    }
    
    /**
     * Applies a property value to the configuration.
     */
    private void applyProperty(ManifestConfiguration config, String propertyPath, String value) {
        String[] parts = propertyPath.split("\\.");
        
        if (parts.length == 0) {
            return;
        }
        
        // Top-level properties
        if (parts.length == 1) {
            applyTopLevelProperty(config, parts[0], value);
            return;
        }
        
        // Nested properties
        String section = parts[0];
        String remaining = String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
        
        switch (section) {
            case "output":
                applyOutputProperty(config.getOutput(), remaining, value);
                break;
            case "dependencies":
                applyDependenciesProperty(config.getDependencies(), remaining, value);
                break;
            case "metadata":
                applyMetadataProperty(config.getMetadata(), remaining, value);
                break;
            case "git":
                applyGitProperty(config.getGit(), remaining, value);
                break;
            case "docker":
                applyDockerProperty(config.getDocker(), remaining, value);
                break;
            case "ci":
                applyCiProperty(config.getCi(), remaining, value);
                break;
            case "frameworks":
                applyFrameworksProperty(config.getFrameworks(), remaining, value);
                break;
            case "validation":
                applyValidationProperty(config.getValidation(), remaining, value);
                break;
            default:
                log.warn("Unknown configuration section: {}", section);
        }
    }
    
    private void applyTopLevelProperty(ManifestConfiguration config, String property, String value) {
        switch (property) {
            case "profile":
                config.setProfile(ManifestProfile.fromValue(value));
                break;
            case "verbose":
                config.setVerbose(TypeConverter.toBoolean(value));
                break;
            case "dryrun":
                config.setDryRun(TypeConverter.toBoolean(value));
                break;
            case "skip":
                config.setSkip(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown top-level property: {}", property);
        }
    }
    
    private void applyOutputProperty(OutputConfiguration output, String property, String value) {
        switch (property) {
            case "directory":
                output.setDirectory(TypeConverter.toString(value));
                break;
            case "filename":
                output.setFilename(TypeConverter.toString(value));
                break;
            case "formats":
                output.setFormats(TypeConverter.toStringList(value));
                break;
            case "archive":
                output.setArchive(TypeConverter.toBoolean(value));
                break;
            case "archiveformat":
                output.setArchiveFormat(TypeConverter.toString(value));
                break;
            case "attach":
                output.setAttach(TypeConverter.toBoolean(value));
                break;
            case "classifier":
                output.setClassifier(TypeConverter.toString(value));
                break;
            default:
                log.warn("Unknown output property: {}", property);
        }
    }
    
    private void applyDependenciesProperty(DependenciesConfiguration deps, String property, String value) {
        if (property.startsWith("tree.")) {
            String treeProp = property.substring(5);
            applyDependencyTreeProperty(deps.getTree(), treeProp, value);
        } else if (property.startsWith("analysis.")) {
            String analysisProp = property.substring(9);
            applyDependencyAnalysisProperty(deps.getAnalysis(), analysisProp, value);
        } else {
            log.warn("Unknown dependencies property: {}", property);
        }
    }
    
    private void applyDependencyTreeProperty(DependencyTreeConfiguration tree, String property, String value) {
        switch (property) {
            case "enabled":
                tree.setEnabled(TypeConverter.toBoolean(value));
                break;
            case "depth":
                tree.setDepth(TypeConverter.toInteger(value));
                break;
            case "format":
                tree.setFormat(TypeConverter.toString(value));
                break;
            case "includetransitive":
                tree.setIncludeTransitive(TypeConverter.toBoolean(value));
                break;
            case "scopes":
                tree.setScopes(TypeConverter.toStringList(value));
                break;
            case "includeoptional":
                tree.setIncludeOptional(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown dependency tree property: {}", property);
        }
    }
    
    private void applyDependencyAnalysisProperty(DependencyAnalysisConfiguration analysis, String property, String value) {
        switch (property) {
            case "enabled":
                analysis.setEnabled(TypeConverter.toBoolean(value));
                break;
            case "healththreshold":
                analysis.setHealthThreshold(TypeConverter.toInteger(value));
                break;
            case "filterspringstarters":
                analysis.setFilterSpringStarters(TypeConverter.toBoolean(value));
                break;
            case "filterlombok":
                analysis.setFilterLombok(TypeConverter.toBoolean(value));
                break;
            case "filterannotationprocessors":
                analysis.setFilterAnnotationProcessors(TypeConverter.toBoolean(value));
                break;
            case "generaterecommendations":
                analysis.setGenerateRecommendations(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown dependency analysis property: {}", property);
        }
    }
    
    private void applyMetadataProperty(MetadataConfiguration metadata, String property, String value) {
        switch (property) {
            case "licenses":
                metadata.setLicenses(TypeConverter.toBoolean(value));
                break;
            case "properties":
                metadata.setProperties(TypeConverter.toBoolean(value));
                break;
            case "plugins":
                metadata.setPlugins(TypeConverter.toBoolean(value));
                break;
            case "checksums":
                metadata.setChecksums(TypeConverter.toBoolean(value));
                break;
            case "includesystemproperties":
                metadata.setIncludeSystemProperties(TypeConverter.toBoolean(value));
                break;
            case "includeenvironmentvariables":
                metadata.setIncludeEnvironmentVariables(TypeConverter.toBoolean(value));
                break;
            case "filtersensitive":
                metadata.setFilterSensitive(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown metadata property: {}", property);
        }
    }
    
    private void applyGitProperty(GitConfiguration git, String property, String value) {
        switch (property) {
            case "fetch":
                git.setFetch(GitFetchMode.fromValue(value));
                break;
            case "includeuncommitted":
                git.setIncludeUncommitted(TypeConverter.toBoolean(value));
                break;
            case "depth":
                git.setDepth(TypeConverter.toInteger(value));
                break;
            case "includebranch":
                git.setIncludeBranch(TypeConverter.toBoolean(value));
                break;
            case "includetags":
                git.setIncludeTags(TypeConverter.toBoolean(value));
                break;
            case "includeremote":
                git.setIncludeRemote(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown git property: {}", property);
        }
    }
    
    private void applyDockerProperty(DockerConfiguration docker, String property, String value) {
        switch (property) {
            case "autodetect":
                docker.setAutoDetect(TypeConverter.toBoolean(value));
                break;
            case "registries":
                docker.setRegistries(TypeConverter.toStringList(value));
                break;
            case "includeimagedigest":
                docker.setIncludeImageDigest(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown docker property: {}", property);
        }
    }
    
    private void applyCiProperty(CiConfiguration ci, String property, String value) {
        switch (property) {
            case "autodetect":
                ci.setAutoDetect(TypeConverter.toBoolean(value));
                break;
            case "includeenvironment":
                ci.setIncludeEnvironment(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown ci property: {}", property);
        }
    }
    
    private void applyFrameworksProperty(FrameworksConfiguration frameworks, String property, String value) {
        switch (property) {
            case "autodetect":
                frameworks.setAutoDetect(TypeConverter.toBoolean(value));
                break;
            case "includeprofiles":
                frameworks.setIncludeProfiles(TypeConverter.toBoolean(value));
                break;
            case "includeconfiguration":
                frameworks.setIncludeConfiguration(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown frameworks property: {}", property);
        }
    }
    
    private void applyValidationProperty(ValidationConfiguration validation, String property, String value) {
        switch (property) {
            case "enabled":
                validation.setEnabled(TypeConverter.toBoolean(value));
                break;
            case "failonerror":
                validation.setFailOnError(TypeConverter.toBoolean(value));
                break;
            case "warnondeprecated":
                validation.setWarnOnDeprecated(TypeConverter.toBoolean(value));
                break;
            default:
                log.warn("Unknown validation property: {}", property);
        }
    }
    
    // Missing import
    private static class Arrays {
        static <T> T[] copyOfRange(T[] original, int from, int to) {
            return java.util.Arrays.copyOfRange(original, from, to);
        }
    }
}
