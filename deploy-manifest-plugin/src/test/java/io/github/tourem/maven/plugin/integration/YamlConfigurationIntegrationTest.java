package io.github.tourem.maven.plugin.integration;

import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.resolver.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for YAML configuration system.
 * Tests the complete flow from YAML file to resolved configuration.
 */
class YamlConfigurationIntegrationTest {
    
    @TempDir
    Path tempDir;
    
    private ConfigurationResolver resolver;
    
    @BeforeEach
    void setUp() {
        resolver = new ConfigurationResolver();
    }
    
    @Test
    void shouldLoadMinimalYamlConfiguration() throws Exception {
        // Given
        File projectDir = tempDir.toFile();
        createYamlFile(projectDir, "profile: basic\n");
        
        // When
        ResolvedConfiguration resolved = resolver.resolve(projectDir);
        
        // Then
        assertThat(resolved).isNotNull();
        assertThat(resolved.getConfiguration().getProfile()).isEqualTo(ManifestProfile.BASIC);
        assertThat(resolved.getSource("profile")).isEqualTo(ConfigurationSource.YAML_FILE);
    }
    
    @Test
    void shouldLoadCompleteYamlConfiguration() throws Exception {
        // Given
        File projectDir = tempDir.toFile();
        String yaml = """
            profile: standard
            
            output:
              directory: target/reports
              formats:
                - json
                - html
            
            dependencies:
              tree:
                enabled: true
                depth: 5
            
            metadata:
              licenses: true
            """;
        createYamlFile(projectDir, yaml);
        
        // When
        ResolvedConfiguration resolved = resolver.resolve(projectDir);
        
        // Then
        ManifestConfiguration config = resolved.getConfiguration();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.STANDARD);
        assertThat(config.getOutput().getDirectory()).isEqualTo("target/reports");
        assertThat(config.getOutput().getFormats()).containsExactly("json", "html");
        assertThat(config.getDependencies().getTree().getEnabled()).isTrue();
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(5);
        assertThat(config.getMetadata().getLicenses()).isTrue();
    }
    
    @Test
    void shouldApplyProfileDefaults() throws Exception {
        // Given
        File projectDir = tempDir.toFile();
        createYamlFile(projectDir, "profile: full\n");
        
        // When
        ResolvedConfiguration resolved = resolver.resolve(projectDir);
        
        // Then
        ManifestConfiguration config = resolved.getConfiguration();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.FULL);
        assertThat(config.getOutput().getFormats()).contains("json", "yaml", "html");
        assertThat(config.getDependencies().getTree().getEnabled()).isTrue();
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(5);
        assertThat(config.getMetadata().getLicenses()).isTrue();
    }
    
    @Test
    void shouldHandleNoYamlFile() throws Exception {
        // Given
        File projectDir = tempDir.toFile();
        // No YAML file created
        
        // When
        ResolvedConfiguration resolved = resolver.resolve(projectDir);
        
        // Then
        assertThat(resolved).isNotNull();
        assertThat(resolved.getConfiguration()).isNotNull();
        // Should use defaults
        assertThat(resolved.getConfiguration().getProfile()).isEqualTo(ManifestProfile.BASIC);
    }
    
    @Test
    void shouldValidateInvalidConfiguration() {
        // Given
        File projectDir = tempDir.toFile();
        String invalidYaml = """
            output:
              formats:
                - invalid_format
            """;
        createYamlFile(projectDir, invalidYaml);
        
        // When/Then
        assertThatThrownBy(() -> resolver.resolve(projectDir))
            .isInstanceOf(ConfigurationResolutionException.class)
            .hasMessageContaining("validation failed");
    }
    
    @Test
    void shouldTrackConfigurationSources() throws Exception {
        // Given
        File projectDir = tempDir.toFile();
        String yaml = """
            profile: standard
            output:
              directory: target/custom
            """;
        createYamlFile(projectDir, yaml);
        
        // When
        ResolvedConfiguration resolved = resolver.resolve(projectDir);
        
        // Then
        assertThat(resolved.getSource("profile")).isEqualTo(ConfigurationSource.YAML_FILE);
        assertThat(resolved.getSource("output.directory")).isEqualTo(ConfigurationSource.YAML_FILE);
        assertThat(resolved.getSource("output.formats")).isEqualTo(ConfigurationSource.PROFILE);
    }
    
    // Helper methods
    
    private void createYamlFile(File projectDir, String content) {
        try {
            File yamlFile = new File(projectDir, ".deploy-manifest.yml");
            try (FileWriter writer = new FileWriter(yamlFile)) {
                writer.write(content);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create YAML file", e);
        }
    }
}
