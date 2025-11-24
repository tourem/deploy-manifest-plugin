package io.github.tourem.maven.descriptor.config.loader;

import io.github.tourem.maven.descriptor.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for YamlConfigurationLoader.
 */
class YamlConfigurationLoaderTest {
    
    private YamlConfigurationLoader loader;
    
    @BeforeEach
    void setUp() {
        loader = new YamlConfigurationLoader();
    }
    
    @Test
    void shouldLoadMinimalConfiguration() throws Exception {
        // Given
        File yamlFile = getResourceFile("config/minimal.yml");
        
        // When
        ManifestConfiguration config = loader.loadFromFile(yamlFile);
        
        // Then
        assertThat(config).isNotNull();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.BASIC);
    }
    
    @Test
    void shouldLoadCompleteConfiguration() throws Exception {
        // Given
        File yamlFile = getResourceFile("config/complete.yml");
        
        // When
        ManifestConfiguration config = loader.loadFromFile(yamlFile);
        
        // Then
        assertThat(config).isNotNull();
        
        // Profile
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.FULL);
        
        // Output
        assertThat(config.getOutput().getDirectory()).isEqualTo("target/reports");
        assertThat(config.getOutput().getFilename()).isEqualTo("my-manifest");
        assertThat(config.getOutput().getFormats()).containsExactly("json", "html", "yaml");
        assertThat(config.getOutput().getArchive()).isTrue();
        assertThat(config.getOutput().getArchiveFormat()).isEqualTo("zip");
        assertThat(config.getOutput().getAttach()).isTrue();
        assertThat(config.getOutput().getClassifier()).isEqualTo("docs");
        
        // Dependencies - Tree
        assertThat(config.getDependencies().getTree().getEnabled()).isTrue();
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(5);
        assertThat(config.getDependencies().getTree().getFormat()).isEqualTo("both");
        assertThat(config.getDependencies().getTree().getIncludeTransitive()).isTrue();
        assertThat(config.getDependencies().getTree().getScopes()).containsExactly("compile", "runtime", "test");
        assertThat(config.getDependencies().getTree().getIncludeOptional()).isFalse();
        
        // Dependencies - Analysis
        assertThat(config.getDependencies().getAnalysis().getEnabled()).isTrue();
        assertThat(config.getDependencies().getAnalysis().getHealthThreshold()).isEqualTo(90);
        assertThat(config.getDependencies().getAnalysis().getFilterSpringStarters()).isTrue();
        assertThat(config.getDependencies().getAnalysis().getFilterLombok()).isTrue();
        assertThat(config.getDependencies().getAnalysis().getFilterAnnotationProcessors()).isTrue();
        assertThat(config.getDependencies().getAnalysis().getGenerateRecommendations()).isTrue();
        
        // Metadata
        assertThat(config.getMetadata().getLicenses()).isTrue();
        assertThat(config.getMetadata().getProperties()).isTrue();
        assertThat(config.getMetadata().getPlugins()).isTrue();
        assertThat(config.getMetadata().getChecksums()).isTrue();
        assertThat(config.getMetadata().getIncludeSystemProperties()).isTrue();
        assertThat(config.getMetadata().getIncludeEnvironmentVariables()).isFalse();
        assertThat(config.getMetadata().getFilterSensitive()).isTrue();
        
        // Git
        assertThat(config.getGit().getFetch()).isEqualTo(GitFetchMode.ALWAYS);
        assertThat(config.getGit().getIncludeUncommitted()).isTrue();
        assertThat(config.getGit().getDepth()).isEqualTo(100);
        assertThat(config.getGit().getIncludeBranch()).isTrue();
        assertThat(config.getGit().getIncludeTags()).isTrue();
        assertThat(config.getGit().getIncludeRemote()).isTrue();
        
        // Docker
        assertThat(config.getDocker().getAutoDetect()).isTrue();
        assertThat(config.getDocker().getRegistries()).containsExactly("docker.io", "ghcr.io", "quay.io");
        assertThat(config.getDocker().getIncludeImageDigest()).isTrue();
        
        // CI
        assertThat(config.getCi().getAutoDetect()).isTrue();
        assertThat(config.getCi().getIncludeEnvironment()).isTrue();
        
        // Frameworks
        assertThat(config.getFrameworks().getAutoDetect()).isTrue();
        assertThat(config.getFrameworks().getIncludeProfiles()).isTrue();
        assertThat(config.getFrameworks().getIncludeConfiguration()).isFalse();
        
        // Validation
        assertThat(config.getValidation().getEnabled()).isTrue();
        assertThat(config.getValidation().getFailOnError()).isTrue();
        assertThat(config.getValidation().getWarnOnDeprecated()).isTrue();
        
        // Simple booleans
        assertThat(config.getVerbose()).isTrue();
        assertThat(config.getDryRun()).isFalse();
        assertThat(config.getSkip()).isFalse();
    }
    
    @Test
    void shouldReturnNullWhenFileDoesNotExist(@TempDir Path tempDir) throws Exception {
        // Given
        File nonExistentFile = tempDir.resolve("non-existent.yml").toFile();
        
        // When
        ManifestConfiguration config = loader.loadFromFile(nonExistentFile);
        
        // Then
        assertThat(config).isNull();
    }
    
    @Test
    void shouldReturnDefaultConfigurationWhenFileIsEmpty() throws Exception {
        // Given
        File yamlFile = getResourceFile("config/empty.yml");
        
        // When
        ManifestConfiguration config = loader.loadFromFile(yamlFile);
        
        // Then
        assertThat(config).isNotNull();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.BASIC);
    }
    
    @Test
    void shouldThrowExceptionForInvalidYamlSyntax() {
        // Given
        File yamlFile = getResourceFile("config/invalid-syntax.yml");
        
        // When/Then
        assertThatThrownBy(() -> loader.loadFromFile(yamlFile))
            .isInstanceOf(ConfigurationLoadException.class)
            .hasMessageContaining("Invalid YAML syntax");
    }
    
    @Test
    void shouldThrowExceptionForInvalidProfile() {
        // Given
        File yamlFile = getResourceFile("config/invalid-profile.yml");
        
        // When/Then
        assertThatThrownBy(() -> loader.loadFromFile(yamlFile))
            .isInstanceOf(ConfigurationLoadException.class)
            .hasMessageContaining("Invalid configuration value");
    }
    
    @Test
    void shouldThrowExceptionForInvalidType() {
        // Given
        File yamlFile = getResourceFile("config/invalid-type.yml");
        
        // When/Then
        assertThatThrownBy(() -> loader.loadFromFile(yamlFile))
            .isInstanceOf(ConfigurationLoadException.class)
            .hasMessageContaining("Invalid configuration value");
    }
    
    @Test
    void shouldThrowExceptionWhenPathIsDirectory(@TempDir Path tempDir) {
        // Given
        File directory = tempDir.toFile();
        
        // When/Then
        assertThatThrownBy(() -> loader.loadFromFile(directory))
            .isInstanceOf(ConfigurationLoadException.class)
            .hasMessageContaining("not a file");
    }
    
    @Test
    void shouldLoadFromProjectDirectory(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve(".deploy-manifest.yml");
        Files.writeString(configFile, "profile: standard\n");
        
        // When
        ManifestConfiguration config = loader.load(tempDir.toFile());
        
        // Then
        assertThat(config).isNotNull();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.STANDARD);
    }
    
    @Test
    void shouldHandleStringBooleanValues(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, 
            "verbose: \"true\"\n" +
            "dryRun: \"false\"\n"
        );
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        assertThat(config.getVerbose()).isTrue();
        assertThat(config.getDryRun()).isFalse();
    }
    
    @Test
    void shouldHandleStringIntegerValues(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, 
            "dependencies:\n" +
            "  tree:\n" +
            "    depth: \"7\"\n"
        );
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(7);
    }
    
    @Test
    void shouldHandleSingleStringAsListValue(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, 
            "output:\n" +
            "  formats: json\n"  // Single value instead of list
        );
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        assertThat(config.getOutput().getFormats()).containsExactly("json");
    }
    
    @Test
    void shouldHandleGitFetchModeEnum(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, 
            "git:\n" +
            "  fetch: never\n"
        );
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        assertThat(config.getGit().getFetch()).isEqualTo(GitFetchMode.NEVER);
    }
    
    @Test
    void shouldHandlePartialConfiguration(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, 
            "profile: standard\n" +
            "output:\n" +
            "  formats:\n" +
            "    - json\n" +
            "    - html\n" +
            "metadata:\n" +
            "  licenses: true\n"
        );
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.STANDARD);
        assertThat(config.getOutput().getFormats()).containsExactly("json", "html");
        assertThat(config.getMetadata().getLicenses()).isTrue();
        
        // Defaults should still be present
        assertThat(config.getOutput().getDirectory()).isEqualTo("target");
        assertThat(config.getMetadata().getProperties()).isFalse();
    }
    
    @Test
    void shouldPreserveDefaultsWhenNotSpecified(@TempDir Path tempDir) throws Exception {
        // Given
        Path configFile = tempDir.resolve("test.yml");
        Files.writeString(configFile, "profile: basic\n");
        
        // When
        ManifestConfiguration config = loader.loadFromFile(configFile.toFile());
        
        // Then
        // Check that defaults are preserved
        assertThat(config.getOutput().getDirectory()).isEqualTo("target");
        assertThat(config.getOutput().getFilename()).isEqualTo("deployment-manifest-report");
        assertThat(config.getOutput().getArchive()).isFalse();
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(3);
        assertThat(config.getGit().getFetch()).isEqualTo(GitFetchMode.AUTO);
        assertThat(config.getGit().getDepth()).isEqualTo(50);
    }
    
    // Helper method to get resource file
    private File getResourceFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(path).getFile());
    }
}
