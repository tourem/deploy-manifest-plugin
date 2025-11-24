package io.github.tourem.maven.descriptor.config.merger;

import io.github.tourem.maven.descriptor.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Merges multiple configuration sources according to priority order.
 * <p>
 * Priority order (highest to lowest):
 * 1. Command line (-Dmanifest.*)
 * 2. Environment variables (MANIFEST_*)
 * 3. YAML file (.deploy-manifest.yml)
 * 4. Profile defaults
 * 5. POM configuration
 * 6. Plugin defaults
 * </p>
 */
public class ConfigurationMerger {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationMerger.class);
    
    /**
     * Merges configurations from multiple sources.
     *
     * @param commandLine configuration from command line (highest priority)
     * @param environment configuration from environment variables
     * @param yaml configuration from YAML file
     * @param pom configuration from POM (if any)
     * @return the merged and resolved configuration
     */
    public ResolvedConfiguration merge(
            ManifestConfiguration commandLine,
            ManifestConfiguration environment,
            ManifestConfiguration yaml,
            ManifestConfiguration pom) {
        
        // Start with plugin defaults
        ManifestConfiguration result = new ManifestConfiguration();
        ResolvedConfiguration resolved = new ResolvedConfiguration(result);
        
        // Apply in reverse priority order (lowest to highest)
        // This way, higher priority sources will override lower ones
        
        // 1. Plugin defaults (already set in constructor)
        trackDefaults(resolved);
        
        // 2. POM configuration (if provided)
        if (pom != null) {
            applyConfiguration(result, pom, ConfigurationSource.POM_XML, resolved);
        }
        
        // 3. YAML file
        if (yaml != null) {
            // Apply profile defaults first
            ManifestProfile profile = yaml.getProfile();
            if (profile != null && profile != ManifestProfile.BASIC) {
                ManifestConfiguration profileDefaults = profile.getDefaultConfiguration();
                applyConfiguration(result, profileDefaults, ConfigurationSource.PROFILE, resolved);
            }
            
            // Then apply YAML overrides
            applyConfiguration(result, yaml, ConfigurationSource.YAML_FILE, resolved);
        }
        
        // 4. Environment variables
        if (environment != null) {
            applyConfiguration(result, environment, ConfigurationSource.ENVIRONMENT, resolved);
        }
        
        // 5. Command line (highest priority)
        if (commandLine != null) {
            applyConfiguration(result, commandLine, ConfigurationSource.COMMAND_LINE, resolved);
        }
        
        log.info("Configuration merged from {} sources", 
            countSources(commandLine, environment, yaml, pom));
        
        return resolved;
    }
    
    /**
     * Applies configuration from a source to the result.
     */
    private void applyConfiguration(
            ManifestConfiguration result,
            ManifestConfiguration source,
            ConfigurationSource sourceType,
            ResolvedConfiguration resolved) {
        
        // Top-level properties
        if (source.getProfile() != null && source.getProfile() != result.getProfile()) {
            result.setProfile(source.getProfile());
            resolved.setSource("profile", sourceType);
        }
        if (source.getVerbose() != null && !source.getVerbose().equals(result.getVerbose())) {
            result.setVerbose(source.getVerbose());
            resolved.setSource("verbose", sourceType);
        }
        if (source.getDryRun() != null && !source.getDryRun().equals(result.getDryRun())) {
            result.setDryRun(source.getDryRun());
            resolved.setSource("dryRun", sourceType);
        }
        if (source.getSkip() != null && !source.getSkip().equals(result.getSkip())) {
            result.setSkip(source.getSkip());
            resolved.setSource("skip", sourceType);
        }
        
        // Nested configurations
        mergeOutput(result.getOutput(), source.getOutput(), sourceType, resolved);
        mergeDependencies(result.getDependencies(), source.getDependencies(), sourceType, resolved);
        mergeMetadata(result.getMetadata(), source.getMetadata(), sourceType, resolved);
        mergeGit(result.getGit(), source.getGit(), sourceType, resolved);
        mergeDocker(result.getDocker(), source.getDocker(), sourceType, resolved);
        mergeCi(result.getCi(), source.getCi(), sourceType, resolved);
        mergeFrameworks(result.getFrameworks(), source.getFrameworks(), sourceType, resolved);
        mergeValidation(result.getValidation(), source.getValidation(), sourceType, resolved);
    }
    
    private void mergeOutput(OutputConfiguration result, OutputConfiguration source, 
                            ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getDirectory() != null && !source.getDirectory().equals(result.getDirectory())) {
            result.setDirectory(source.getDirectory());
            resolved.setSource("output.directory", sourceType);
        }
        if (source.getFilename() != null && !source.getFilename().equals(result.getFilename())) {
            result.setFilename(source.getFilename());
            resolved.setSource("output.filename", sourceType);
        }
        if (source.getFormats() != null && !source.getFormats().isEmpty()) {
            result.setFormats(source.getFormats());
            resolved.setSource("output.formats", sourceType);
        }
        if (source.getArchive() != null && !source.getArchive().equals(result.getArchive())) {
            result.setArchive(source.getArchive());
            resolved.setSource("output.archive", sourceType);
        }
        if (source.getArchiveFormat() != null && !source.getArchiveFormat().equals(result.getArchiveFormat())) {
            result.setArchiveFormat(source.getArchiveFormat());
            resolved.setSource("output.archiveFormat", sourceType);
        }
        if (source.getAttach() != null && !source.getAttach().equals(result.getAttach())) {
            result.setAttach(source.getAttach());
            resolved.setSource("output.attach", sourceType);
        }
        if (source.getClassifier() != null && !source.getClassifier().equals(result.getClassifier())) {
            result.setClassifier(source.getClassifier());
            resolved.setSource("output.classifier", sourceType);
        }
    }
    
    private void mergeDependencies(DependenciesConfiguration result, DependenciesConfiguration source,
                                  ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        mergeDependencyTree(result.getTree(), source.getTree(), sourceType, resolved);
        mergeDependencyAnalysis(result.getAnalysis(), source.getAnalysis(), sourceType, resolved);
    }
    
    private void mergeDependencyTree(DependencyTreeConfiguration result, DependencyTreeConfiguration source,
                                     ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getEnabled() != null && !source.getEnabled().equals(result.getEnabled())) {
            result.setEnabled(source.getEnabled());
            resolved.setSource("dependencies.tree.enabled", sourceType);
        }
        if (source.getDepth() != null && !source.getDepth().equals(result.getDepth())) {
            result.setDepth(source.getDepth());
            resolved.setSource("dependencies.tree.depth", sourceType);
        }
        if (source.getFormat() != null && !source.getFormat().equals(result.getFormat())) {
            result.setFormat(source.getFormat());
            resolved.setSource("dependencies.tree.format", sourceType);
        }
        if (source.getIncludeTransitive() != null && !source.getIncludeTransitive().equals(result.getIncludeTransitive())) {
            result.setIncludeTransitive(source.getIncludeTransitive());
            resolved.setSource("dependencies.tree.includeTransitive", sourceType);
        }
        if (source.getScopes() != null && !source.getScopes().isEmpty()) {
            result.setScopes(source.getScopes());
            resolved.setSource("dependencies.tree.scopes", sourceType);
        }
        if (source.getIncludeOptional() != null && !source.getIncludeOptional().equals(result.getIncludeOptional())) {
            result.setIncludeOptional(source.getIncludeOptional());
            resolved.setSource("dependencies.tree.includeOptional", sourceType);
        }
    }
    
    private void mergeDependencyAnalysis(DependencyAnalysisConfiguration result, DependencyAnalysisConfiguration source,
                                        ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getEnabled() != null && !source.getEnabled().equals(result.getEnabled())) {
            result.setEnabled(source.getEnabled());
            resolved.setSource("dependencies.analysis.enabled", sourceType);
        }
        if (source.getHealthThreshold() != null && !source.getHealthThreshold().equals(result.getHealthThreshold())) {
            result.setHealthThreshold(source.getHealthThreshold());
            resolved.setSource("dependencies.analysis.healthThreshold", sourceType);
        }
        if (source.getFilterSpringStarters() != null && !source.getFilterSpringStarters().equals(result.getFilterSpringStarters())) {
            result.setFilterSpringStarters(source.getFilterSpringStarters());
            resolved.setSource("dependencies.analysis.filterSpringStarters", sourceType);
        }
        if (source.getFilterLombok() != null && !source.getFilterLombok().equals(result.getFilterLombok())) {
            result.setFilterLombok(source.getFilterLombok());
            resolved.setSource("dependencies.analysis.filterLombok", sourceType);
        }
        if (source.getFilterAnnotationProcessors() != null && !source.getFilterAnnotationProcessors().equals(result.getFilterAnnotationProcessors())) {
            result.setFilterAnnotationProcessors(source.getFilterAnnotationProcessors());
            resolved.setSource("dependencies.analysis.filterAnnotationProcessors", sourceType);
        }
        if (source.getGenerateRecommendations() != null && !source.getGenerateRecommendations().equals(result.getGenerateRecommendations())) {
            result.setGenerateRecommendations(source.getGenerateRecommendations());
            resolved.setSource("dependencies.analysis.generateRecommendations", sourceType);
        }
    }
    
    private void mergeMetadata(MetadataConfiguration result, MetadataConfiguration source,
                              ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getLicenses() != null && !source.getLicenses().equals(result.getLicenses())) {
            result.setLicenses(source.getLicenses());
            resolved.setSource("metadata.licenses", sourceType);
        }
        if (source.getProperties() != null && !source.getProperties().equals(result.getProperties())) {
            result.setProperties(source.getProperties());
            resolved.setSource("metadata.properties", sourceType);
        }
        if (source.getPlugins() != null && !source.getPlugins().equals(result.getPlugins())) {
            result.setPlugins(source.getPlugins());
            resolved.setSource("metadata.plugins", sourceType);
        }
        if (source.getChecksums() != null && !source.getChecksums().equals(result.getChecksums())) {
            result.setChecksums(source.getChecksums());
            resolved.setSource("metadata.checksums", sourceType);
        }
        if (source.getIncludeSystemProperties() != null && !source.getIncludeSystemProperties().equals(result.getIncludeSystemProperties())) {
            result.setIncludeSystemProperties(source.getIncludeSystemProperties());
            resolved.setSource("metadata.includeSystemProperties", sourceType);
        }
        if (source.getIncludeEnvironmentVariables() != null && !source.getIncludeEnvironmentVariables().equals(result.getIncludeEnvironmentVariables())) {
            result.setIncludeEnvironmentVariables(source.getIncludeEnvironmentVariables());
            resolved.setSource("metadata.includeEnvironmentVariables", sourceType);
        }
        if (source.getFilterSensitive() != null && !source.getFilterSensitive().equals(result.getFilterSensitive())) {
            result.setFilterSensitive(source.getFilterSensitive());
            resolved.setSource("metadata.filterSensitive", sourceType);
        }
    }
    
    private void mergeGit(GitConfiguration result, GitConfiguration source,
                         ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getFetch() != null && source.getFetch() != result.getFetch()) {
            result.setFetch(source.getFetch());
            resolved.setSource("git.fetch", sourceType);
        }
        if (source.getIncludeUncommitted() != null && !source.getIncludeUncommitted().equals(result.getIncludeUncommitted())) {
            result.setIncludeUncommitted(source.getIncludeUncommitted());
            resolved.setSource("git.includeUncommitted", sourceType);
        }
        if (source.getDepth() != null && !source.getDepth().equals(result.getDepth())) {
            result.setDepth(source.getDepth());
            resolved.setSource("git.depth", sourceType);
        }
        if (source.getIncludeBranch() != null && !source.getIncludeBranch().equals(result.getIncludeBranch())) {
            result.setIncludeBranch(source.getIncludeBranch());
            resolved.setSource("git.includeBranch", sourceType);
        }
        if (source.getIncludeTags() != null && !source.getIncludeTags().equals(result.getIncludeTags())) {
            result.setIncludeTags(source.getIncludeTags());
            resolved.setSource("git.includeTags", sourceType);
        }
        if (source.getIncludeRemote() != null && !source.getIncludeRemote().equals(result.getIncludeRemote())) {
            result.setIncludeRemote(source.getIncludeRemote());
            resolved.setSource("git.includeRemote", sourceType);
        }
    }
    
    private void mergeDocker(DockerConfiguration result, DockerConfiguration source,
                            ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getAutoDetect() != null && !source.getAutoDetect().equals(result.getAutoDetect())) {
            result.setAutoDetect(source.getAutoDetect());
            resolved.setSource("docker.autoDetect", sourceType);
        }
        if (source.getRegistries() != null && !source.getRegistries().isEmpty()) {
            result.setRegistries(source.getRegistries());
            resolved.setSource("docker.registries", sourceType);
        }
        if (source.getIncludeImageDigest() != null && !source.getIncludeImageDigest().equals(result.getIncludeImageDigest())) {
            result.setIncludeImageDigest(source.getIncludeImageDigest());
            resolved.setSource("docker.includeImageDigest", sourceType);
        }
    }
    
    private void mergeCi(CiConfiguration result, CiConfiguration source,
                        ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getAutoDetect() != null && !source.getAutoDetect().equals(result.getAutoDetect())) {
            result.setAutoDetect(source.getAutoDetect());
            resolved.setSource("ci.autoDetect", sourceType);
        }
        if (source.getIncludeEnvironment() != null && !source.getIncludeEnvironment().equals(result.getIncludeEnvironment())) {
            result.setIncludeEnvironment(source.getIncludeEnvironment());
            resolved.setSource("ci.includeEnvironment", sourceType);
        }
    }
    
    private void mergeFrameworks(FrameworksConfiguration result, FrameworksConfiguration source,
                                ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getAutoDetect() != null && !source.getAutoDetect().equals(result.getAutoDetect())) {
            result.setAutoDetect(source.getAutoDetect());
            resolved.setSource("frameworks.autoDetect", sourceType);
        }
        if (source.getIncludeProfiles() != null && !source.getIncludeProfiles().equals(result.getIncludeProfiles())) {
            result.setIncludeProfiles(source.getIncludeProfiles());
            resolved.setSource("frameworks.includeProfiles", sourceType);
        }
        if (source.getIncludeConfiguration() != null && !source.getIncludeConfiguration().equals(result.getIncludeConfiguration())) {
            result.setIncludeConfiguration(source.getIncludeConfiguration());
            resolved.setSource("frameworks.includeConfiguration", sourceType);
        }
    }
    
    private void mergeValidation(ValidationConfiguration result, ValidationConfiguration source,
                                ConfigurationSource sourceType, ResolvedConfiguration resolved) {
        if (source == null) return;
        
        if (source.getEnabled() != null && !source.getEnabled().equals(result.getEnabled())) {
            result.setEnabled(source.getEnabled());
            resolved.setSource("validation.enabled", sourceType);
        }
        if (source.getFailOnError() != null && !source.getFailOnError().equals(result.getFailOnError())) {
            result.setFailOnError(source.getFailOnError());
            resolved.setSource("validation.failOnError", sourceType);
        }
        if (source.getWarnOnDeprecated() != null && !source.getWarnOnDeprecated().equals(result.getWarnOnDeprecated())) {
            result.setWarnOnDeprecated(source.getWarnOnDeprecated());
            resolved.setSource("validation.warnOnDeprecated", sourceType);
        }
    }
    
    private void trackDefaults(ResolvedConfiguration resolved) {
        // All properties start with DEFAULT source
        // This is implicit, so we don't need to track every single one
    }
    
    private int countSources(ManifestConfiguration... configs) {
        int count = 0;
        for (ManifestConfiguration config : configs) {
            if (config != null) {
                count++;
            }
        }
        return count;
    }
}
