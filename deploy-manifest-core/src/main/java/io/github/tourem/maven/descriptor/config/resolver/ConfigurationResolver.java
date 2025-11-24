package io.github.tourem.maven.descriptor.config.resolver;

import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.loader.*;
import io.github.tourem.maven.descriptor.config.merger.ConfigurationMerger;
import io.github.tourem.maven.descriptor.config.validator.ConfigurationValidator;
import io.github.tourem.maven.descriptor.config.validator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Resolves configuration from all sources and validates it.
 * This is the main entry point for configuration loading.
 */
public class ConfigurationResolver {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationResolver.class);
    
    private final YamlConfigurationLoader yamlLoader;
    private final EnvironmentConfigurationLoader envLoader;
    private final CommandLineConfigurationLoader cliLoader;
    private final ConfigurationMerger merger;
    private final ConfigurationValidator validator;
    
    public ConfigurationResolver() {
        this.yamlLoader = new YamlConfigurationLoader();
        this.envLoader = new EnvironmentConfigurationLoader();
        this.cliLoader = new CommandLineConfigurationLoader();
        this.merger = new ConfigurationMerger();
        this.validator = new ConfigurationValidator();
    }
    
    /**
     * Resolves configuration from all sources.
     *
     * @param projectDirectory the project root directory
     * @return the resolved configuration
     * @throws ConfigurationResolutionException if configuration cannot be resolved or is invalid
     */
    public ResolvedConfiguration resolve(File projectDirectory) throws ConfigurationResolutionException {
        return resolve(projectDirectory, null);
    }
    
    /**
     * Resolves configuration from all sources with optional POM configuration.
     *
     * @param projectDirectory the project root directory
     * @param pomConfig configuration from POM (optional)
     * @return the resolved configuration
     * @throws ConfigurationResolutionException if configuration cannot be resolved or is invalid
     */
    public ResolvedConfiguration resolve(File projectDirectory, ManifestConfiguration pomConfig) 
            throws ConfigurationResolutionException {
        
        log.info("Resolving configuration from all sources...");
        
        try {
            // Load from all sources
            ManifestConfiguration yamlConfig = loadYamlConfig(projectDirectory);
            ManifestConfiguration envConfig = loadEnvironmentConfig();
            ManifestConfiguration cliConfig = loadCommandLineConfig();
            
            // Merge configurations
            ResolvedConfiguration resolved = merger.merge(cliConfig, envConfig, yamlConfig, pomConfig);
            
            // Log sources
            logConfigurationSources(resolved);
            
            // Validate
            ValidationResult validation = validator.validate(resolved.getConfiguration());
            if (!validation.isValid()) {
                String errorMessage = validation.formatErrors();
                log.error("Configuration validation failed:\n{}", errorMessage);
                throw new ConfigurationResolutionException(
                    "Configuration validation failed with " + validation.getErrorCount() + " error(s). " +
                    "See details above."
                );
            }
            
            log.info("Configuration resolved and validated successfully");
            return resolved;
            
        } catch (ConfigurationLoadException e) {
            throw new ConfigurationResolutionException("Failed to load configuration: " + e.getMessage(), e);
        }
    }
    
    private ManifestConfiguration loadYamlConfig(File projectDirectory) throws ConfigurationLoadException {
        log.debug("Loading YAML configuration from {}", projectDirectory);
        ManifestConfiguration config = yamlLoader.load(projectDirectory);
        if (config != null) {
            log.info("✅ Loaded configuration from .deploy-manifest.yml");
        } else {
            log.debug("No .deploy-manifest.yml file found, using defaults");
        }
        return config;
    }
    
    private ManifestConfiguration loadEnvironmentConfig() {
        log.debug("Loading environment variables");
        ManifestConfiguration config = envLoader.load();
        // Count how many env vars were loaded (we can enhance this later)
        log.debug("Environment variables loaded");
        return config;
    }
    
    private ManifestConfiguration loadCommandLineConfig() {
        log.debug("Loading command line properties");
        ManifestConfiguration config = cliLoader.load();
        log.debug("Command line properties loaded");
        return config;
    }
    
    private void logConfigurationSources(ResolvedConfiguration resolved) {
        log.info("Configuration sources:");
        log.info("  Profile: {} (from {})", 
            resolved.getConfiguration().getProfile(), 
            resolved.getSource("profile"));
        
        if (resolved.isExplicitlySet("verbose")) {
            log.info("  Verbose: {} (from {})", 
                resolved.getConfiguration().getVerbose(), 
                resolved.getSource("verbose"));
        }
        
        if (resolved.isExplicitlySet("output.directory")) {
            log.info("  Output directory: {} (from {})", 
                resolved.getConfiguration().getOutput().getDirectory(), 
                resolved.getSource("output.directory"));
        }
        
        if (resolved.isExplicitlySet("output.formats")) {
            log.info("  Output formats: {} (from {})", 
                resolved.getConfiguration().getOutput().getFormats(), 
                resolved.getSource("output.formats"));
        }
        
        if (resolved.isExplicitlySet("dependencies.tree.depth")) {
            log.info("  Dependency tree depth: {} (from {})", 
                resolved.getConfiguration().getDependencies().getTree().getDepth(), 
                resolved.getSource("dependencies.tree.depth"));
        }
    }
}
