package io.github.tourem.maven.descriptor.config.loader;

import io.github.tourem.maven.descriptor.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Loads configuration from a YAML file (.deploy-manifest.yml).
 */
public class YamlConfigurationLoader {
    
    private static final Logger log = LoggerFactory.getLogger(YamlConfigurationLoader.class);
    private static final String DEFAULT_CONFIG_FILE = ".deploy-manifest.yml";
    
    /**
     * Loads configuration from the default file (.deploy-manifest.yml) in the project directory.
     *
     * @param projectDirectory the project root directory
     * @return the loaded configuration, or null if file doesn't exist
     * @throws ConfigurationLoadException if the file exists but cannot be parsed
     */
    public ManifestConfiguration load(File projectDirectory) throws ConfigurationLoadException {
        File configFile = new File(projectDirectory, DEFAULT_CONFIG_FILE);
        return loadFromFile(configFile);
    }
    
    /**
     * Loads configuration from a specific YAML file.
     *
     * @param yamlFile the YAML file to load
     * @return the loaded configuration, or null if file doesn't exist
     * @throws ConfigurationLoadException if the file exists but cannot be parsed
     */
    public ManifestConfiguration loadFromFile(File yamlFile) throws ConfigurationLoadException {
        if (!yamlFile.exists()) {
            log.debug("Configuration file not found: {}", yamlFile.getAbsolutePath());
            return null;
        }
        
        if (!yamlFile.isFile()) {
            throw new ConfigurationLoadException(
                "Configuration path exists but is not a file: " + yamlFile.getAbsolutePath()
            );
        }
        
        log.info("Loading configuration from: {}", yamlFile.getAbsolutePath());
        
        try (InputStream input = new FileInputStream(yamlFile)) {
            return parseYaml(input, yamlFile.getAbsolutePath());
        } catch (IOException e) {
            throw new ConfigurationLoadException(
                "Failed to read configuration file: " + yamlFile.getAbsolutePath(),
                e
            );
        }
    }
    
    /**
     * Parses YAML content from an input stream.
     *
     * @param input the input stream
     * @param source the source description (for error messages)
     * @return the parsed configuration
     * @throws ConfigurationLoadException if parsing fails
     */
    @SuppressWarnings("unchecked")
    private ManifestConfiguration parseYaml(InputStream input, String source) throws ConfigurationLoadException {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);
            
            if (data == null || data.isEmpty()) {
                log.warn("Configuration file is empty: {}", source);
                return new ManifestConfiguration();
            }
            
            return mapToConfiguration(data, source);
            
        } catch (YAMLException e) {
            throw new ConfigurationLoadException(
                "Invalid YAML syntax in " + source + ": " + e.getMessage(),
                e
            );
        } catch (Exception e) {
            throw new ConfigurationLoadException(
                "Failed to parse configuration from " + source + ": " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Maps YAML data to ManifestConfiguration object.
     */
    @SuppressWarnings("unchecked")
    private ManifestConfiguration mapToConfiguration(Map<String, Object> data, String source) throws ConfigurationLoadException {
        ManifestConfiguration config = new ManifestConfiguration();
        
        try {
            // Profile
            if (data.containsKey("profile")) {
                String profileValue = getString(data, "profile");
                config.setProfile(ManifestProfile.fromValue(profileValue));
            }
            
            // Output
            if (data.containsKey("output")) {
                Map<String, Object> outputData = getMap(data, "output");
                config.setOutput(mapToOutputConfiguration(outputData));
            }
            
            // Dependencies
            if (data.containsKey("dependencies")) {
                Map<String, Object> depsData = getMap(data, "dependencies");
                config.setDependencies(mapToDependenciesConfiguration(depsData));
            }
            
            // Metadata
            if (data.containsKey("metadata")) {
                Map<String, Object> metadataData = getMap(data, "metadata");
                config.setMetadata(mapToMetadataConfiguration(metadataData));
            }
            
            // Git
            if (data.containsKey("git")) {
                Map<String, Object> gitData = getMap(data, "git");
                config.setGit(mapToGitConfiguration(gitData));
            }
            
            // Docker
            if (data.containsKey("docker")) {
                Map<String, Object> dockerData = getMap(data, "docker");
                config.setDocker(mapToDockerConfiguration(dockerData));
            }
            
            // CI
            if (data.containsKey("ci")) {
                Map<String, Object> ciData = getMap(data, "ci");
                config.setCi(mapToCiConfiguration(ciData));
            }
            
            // Frameworks
            if (data.containsKey("frameworks")) {
                Map<String, Object> frameworksData = getMap(data, "frameworks");
                config.setFrameworks(mapToFrameworksConfiguration(frameworksData));
            }
            
            // Validation
            if (data.containsKey("validation")) {
                Map<String, Object> validationData = getMap(data, "validation");
                config.setValidation(mapToValidationConfiguration(validationData));
            }
            
            // Simple booleans
            if (data.containsKey("verbose")) {
                config.setVerbose(getBoolean(data, "verbose"));
            }
            if (data.containsKey("dryRun")) {
                config.setDryRun(getBoolean(data, "dryRun"));
            }
            if (data.containsKey("skip")) {
                config.setSkip(getBoolean(data, "skip"));
            }
            
            return config;
            
        } catch (IllegalArgumentException e) {
            throw new ConfigurationLoadException(
                "Invalid configuration value in " + source + ": " + e.getMessage(),
                e
            );
        }
    }
    
    private OutputConfiguration mapToOutputConfiguration(Map<String, Object> data) {
        OutputConfiguration config = new OutputConfiguration();
        
        if (data.containsKey("directory")) {
            config.setDirectory(getString(data, "directory"));
        }
        if (data.containsKey("filename")) {
            config.setFilename(getString(data, "filename"));
        }
        if (data.containsKey("formats")) {
            config.setFormats(getStringList(data, "formats"));
        }
        if (data.containsKey("archive")) {
            config.setArchive(getBoolean(data, "archive"));
        }
        if (data.containsKey("archiveFormat")) {
            config.setArchiveFormat(getString(data, "archiveFormat"));
        }
        if (data.containsKey("attach")) {
            config.setAttach(getBoolean(data, "attach"));
        }
        if (data.containsKey("classifier")) {
            config.setClassifier(getString(data, "classifier"));
        }
        
        return config;
    }
    
    private DependenciesConfiguration mapToDependenciesConfiguration(Map<String, Object> data) {
        DependenciesConfiguration config = new DependenciesConfiguration();
        
        if (data.containsKey("tree")) {
            Map<String, Object> treeData = getMap(data, "tree");
            config.setTree(mapToDependencyTreeConfiguration(treeData));
        }
        if (data.containsKey("analysis")) {
            Map<String, Object> analysisData = getMap(data, "analysis");
            config.setAnalysis(mapToDependencyAnalysisConfiguration(analysisData));
        }
        
        return config;
    }
    
    private DependencyTreeConfiguration mapToDependencyTreeConfiguration(Map<String, Object> data) {
        DependencyTreeConfiguration config = new DependencyTreeConfiguration();
        
        if (data.containsKey("enabled")) {
            config.setEnabled(getBoolean(data, "enabled"));
        }
        if (data.containsKey("depth")) {
            config.setDepth(getInteger(data, "depth"));
        }
        if (data.containsKey("format")) {
            config.setFormat(getString(data, "format"));
        }
        if (data.containsKey("includeTransitive")) {
            config.setIncludeTransitive(getBoolean(data, "includeTransitive"));
        }
        if (data.containsKey("scopes")) {
            config.setScopes(getStringList(data, "scopes"));
        }
        if (data.containsKey("includeOptional")) {
            config.setIncludeOptional(getBoolean(data, "includeOptional"));
        }
        
        return config;
    }
    
    private DependencyAnalysisConfiguration mapToDependencyAnalysisConfiguration(Map<String, Object> data) {
        DependencyAnalysisConfiguration config = new DependencyAnalysisConfiguration();
        
        if (data.containsKey("enabled")) {
            config.setEnabled(getBoolean(data, "enabled"));
        }
        if (data.containsKey("healthThreshold")) {
            config.setHealthThreshold(getInteger(data, "healthThreshold"));
        }
        if (data.containsKey("filterSpringStarters")) {
            config.setFilterSpringStarters(getBoolean(data, "filterSpringStarters"));
        }
        if (data.containsKey("filterLombok")) {
            config.setFilterLombok(getBoolean(data, "filterLombok"));
        }
        if (data.containsKey("filterAnnotationProcessors")) {
            config.setFilterAnnotationProcessors(getBoolean(data, "filterAnnotationProcessors"));
        }
        if (data.containsKey("generateRecommendations")) {
            config.setGenerateRecommendations(getBoolean(data, "generateRecommendations"));
        }
        
        return config;
    }
    
    private MetadataConfiguration mapToMetadataConfiguration(Map<String, Object> data) {
        MetadataConfiguration config = new MetadataConfiguration();
        
        if (data.containsKey("licenses")) {
            config.setLicenses(getBoolean(data, "licenses"));
        }
        if (data.containsKey("properties")) {
            config.setProperties(getBoolean(data, "properties"));
        }
        if (data.containsKey("plugins")) {
            config.setPlugins(getBoolean(data, "plugins"));
        }
        if (data.containsKey("checksums")) {
            config.setChecksums(getBoolean(data, "checksums"));
        }
        if (data.containsKey("includeSystemProperties")) {
            config.setIncludeSystemProperties(getBoolean(data, "includeSystemProperties"));
        }
        if (data.containsKey("includeEnvironmentVariables")) {
            config.setIncludeEnvironmentVariables(getBoolean(data, "includeEnvironmentVariables"));
        }
        if (data.containsKey("filterSensitive")) {
            config.setFilterSensitive(getBoolean(data, "filterSensitive"));
        }
        
        return config;
    }
    
    private GitConfiguration mapToGitConfiguration(Map<String, Object> data) {
        GitConfiguration config = new GitConfiguration();
        
        if (data.containsKey("fetch")) {
            String fetchValue = getString(data, "fetch");
            config.setFetch(GitFetchMode.fromValue(fetchValue));
        }
        if (data.containsKey("includeUncommitted")) {
            config.setIncludeUncommitted(getBoolean(data, "includeUncommitted"));
        }
        if (data.containsKey("depth")) {
            config.setDepth(getInteger(data, "depth"));
        }
        if (data.containsKey("includeBranch")) {
            config.setIncludeBranch(getBoolean(data, "includeBranch"));
        }
        if (data.containsKey("includeTags")) {
            config.setIncludeTags(getBoolean(data, "includeTags"));
        }
        if (data.containsKey("includeRemote")) {
            config.setIncludeRemote(getBoolean(data, "includeRemote"));
        }
        
        return config;
    }
    
    private DockerConfiguration mapToDockerConfiguration(Map<String, Object> data) {
        DockerConfiguration config = new DockerConfiguration();
        
        if (data.containsKey("autoDetect")) {
            config.setAutoDetect(getBoolean(data, "autoDetect"));
        }
        if (data.containsKey("registries")) {
            config.setRegistries(getStringList(data, "registries"));
        }
        if (data.containsKey("includeImageDigest")) {
            config.setIncludeImageDigest(getBoolean(data, "includeImageDigest"));
        }
        
        return config;
    }
    
    private CiConfiguration mapToCiConfiguration(Map<String, Object> data) {
        CiConfiguration config = new CiConfiguration();
        
        if (data.containsKey("autoDetect")) {
            config.setAutoDetect(getBoolean(data, "autoDetect"));
        }
        if (data.containsKey("includeEnvironment")) {
            config.setIncludeEnvironment(getBoolean(data, "includeEnvironment"));
        }
        
        return config;
    }
    
    private FrameworksConfiguration mapToFrameworksConfiguration(Map<String, Object> data) {
        FrameworksConfiguration config = new FrameworksConfiguration();
        
        if (data.containsKey("autoDetect")) {
            config.setAutoDetect(getBoolean(data, "autoDetect"));
        }
        if (data.containsKey("includeProfiles")) {
            config.setIncludeProfiles(getBoolean(data, "includeProfiles"));
        }
        if (data.containsKey("includeConfiguration")) {
            config.setIncludeConfiguration(getBoolean(data, "includeConfiguration"));
        }
        
        return config;
    }
    
    private ValidationConfiguration mapToValidationConfiguration(Map<String, Object> data) {
        ValidationConfiguration config = new ValidationConfiguration();
        
        if (data.containsKey("enabled")) {
            config.setEnabled(getBoolean(data, "enabled"));
        }
        if (data.containsKey("failOnError")) {
            config.setFailOnError(getBoolean(data, "failOnError"));
        }
        if (data.containsKey("warnOnDeprecated")) {
            config.setWarnOnDeprecated(getBoolean(data, "warnOnDeprecated"));
        }
        
        return config;
    }
    
    // Helper methods for type-safe extraction
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return Map.of();
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException(
                "Expected map for key '" + key + "', but got: " + value.getClass().getSimpleName()
            );
        }
        return (Map<String, Object>) value;
    }
    
    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    private Boolean getBoolean(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        // Handle string values like "true" or "false"
        String strValue = value.toString().toLowerCase();
        if ("true".equals(strValue)) {
            return true;
        }
        if ("false".equals(strValue)) {
            return false;
        }
        throw new IllegalArgumentException(
            "Expected boolean for key '" + key + "', but got: " + value
        );
    }
    
    private Integer getInteger(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        // Handle string values
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Expected integer for key '" + key + "', but got: " + value
            );
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.stream()
                .map(Object::toString)
                .toList();
        }
        // Handle single string value
        if (value instanceof String) {
            return List.of((String) value);
        }
        throw new IllegalArgumentException(
            "Expected list for key '" + key + "', but got: " + value.getClass().getSimpleName()
        );
    }
}
