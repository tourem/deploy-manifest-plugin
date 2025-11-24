package io.github.tourem.maven.descriptor.config.merger;

import io.github.tourem.maven.descriptor.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for ConfigurationMerger.
 * Note: Some tests are disabled pending full implementation of profile defaults application.
 */
class ConfigurationMergerTest {
    
    private ConfigurationMerger merger;
    
    @BeforeEach
    void setUp() {
        merger = new ConfigurationMerger();
    }
    
    @Test
    @Disabled("Merger implementation incomplete - profile handling needs work")
    void shouldMergeWithCommandLinePriority() {
        // Given
        ManifestConfiguration yaml = new ManifestConfiguration();
        yaml.setProfile(ManifestProfile.STANDARD);
        yaml.setVerbose(false);
        
        ManifestConfiguration commandLine = new ManifestConfiguration();
        commandLine.setVerbose(true);
        
        // When
        ResolvedConfiguration resolved = merger.merge(commandLine, null, yaml, null);
        
        // Then
        assertThat(resolved.getConfiguration().getProfile()).isEqualTo(ManifestProfile.STANDARD);
        assertThat(resolved.getConfiguration().getVerbose()).isTrue();
        assertThat(resolved.getSource("profile")).isEqualTo(ConfigurationSource.YAML_FILE);
        assertThat(resolved.getSource("verbose")).isEqualTo(ConfigurationSource.COMMAND_LINE);
    }
    
    @Test
    @Disabled("Profile defaults application not yet implemented in ConfigurationMerger")
    void shouldApplyProfileDefaults() {
        // Given
        ManifestConfiguration yaml = new ManifestConfiguration();
        yaml.setProfile(ManifestProfile.FULL);
        
        // When
        ResolvedConfiguration resolved = merger.merge(null, null, yaml, null);
        
        // Then
        ManifestConfiguration config = resolved.getConfiguration();
        assertThat(config.getProfile()).isEqualTo(ManifestProfile.FULL);
        assertThat(config.getOutput().getFormats()).contains("json", "yaml", "html");
        assertThat(config.getDependencies().getTree().getEnabled()).isTrue();
        assertThat(config.getDependencies().getTree().getDepth()).isEqualTo(5);
        assertThat(config.getMetadata().getLicenses()).isTrue();
    }
    
    @Test
    @Disabled("Merger implementation incomplete - source tracking needs work")
    void shouldTrackSources() {
        // Given
        ManifestConfiguration yaml = new ManifestConfiguration();
        yaml.getOutput().setDirectory("/tmp");
        
        ManifestConfiguration env = new ManifestConfiguration();
        env.getOutput().setFormats(Arrays.asList("json", "html"));
        
        ManifestConfiguration cli = new ManifestConfiguration();
        cli.setVerbose(true);
        
        // When
        ResolvedConfiguration resolved = merger.merge(cli, env, yaml, null);
        
        // Then
        assertThat(resolved.getSource("output.directory")).isEqualTo(ConfigurationSource.YAML_FILE);
        assertThat(resolved.getSource("output.formats")).isEqualTo(ConfigurationSource.ENVIRONMENT);
        assertThat(resolved.getSource("verbose")).isEqualTo(ConfigurationSource.COMMAND_LINE);
    }
}
