package io.github.tourem.maven.descriptor.config.validator;

import io.github.tourem.maven.descriptor.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for ConfigurationValidator.
 */
class ConfigurationValidatorTest {
    
    private ConfigurationValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new ConfigurationValidator();
    }
    
    @Test
    void shouldValidateValidConfiguration() {
        // Given
        ManifestConfiguration config = new ManifestConfiguration();
        config.setProfile(ManifestProfile.STANDARD);
        
        // When
        ValidationResult result = validator.validate(config);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrorCount()).isZero();
    }
    
    @Test
    void shouldDetectInvalidOutputFormat() {
        // Given
        ManifestConfiguration config = new ManifestConfiguration();
        config.getOutput().setFormats(Arrays.asList("jsn")); // Typo
        
        // When
        ValidationResult result = validator.validate(config);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCount()).isEqualTo(1);
        assertThat(result.getErrors().get(0).getField()).isEqualTo("output.formats");
        assertThat(result.getErrors().get(0).getValue()).isEqualTo("jsn");
        assertThat(result.getErrors().get(0).getSuggestion()).isEqualTo("json");
    }
    
    @Test
    void shouldDetectInvalidTreeDepth() {
        // Given
        ManifestConfiguration config = new ManifestConfiguration();
        config.getDependencies().getTree().setDepth(50); // Too deep
        
        // When
        ValidationResult result = validator.validate(config);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCount()).isGreaterThanOrEqualTo(1);
        assertThat(result.getErrors().stream().anyMatch(e -> e.getField().contains("depth"))).isTrue();
    }
    
    @Test
    void shouldDetectInvalidHealthThreshold() {
        // Given
        ManifestConfiguration config = new ManifestConfiguration();
        config.getDependencies().getAnalysis().setHealthThreshold(150); // Out of range
        
        // When
        ValidationResult result = validator.validate(config);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCount()).isGreaterThanOrEqualTo(1);
        assertThat(result.getErrors().stream().anyMatch(e -> e.getField().contains("healthThreshold"))).isTrue();
    }
    
    @Test
    void shouldFormatErrorsNicely() {
        // Given
        ManifestConfiguration config = new ManifestConfiguration();
        config.getOutput().setFormats(Arrays.asList("jsn"));
        
        // When
        ValidationResult result = validator.validate(config);
        String formatted = result.formatErrors();
        
        // Then
        assertThat(formatted).contains("Configuration validation failed");
        assertThat(formatted).contains("output.formats");
        assertThat(formatted).contains("jsn");
        assertThat(formatted).contains("Did you mean");
    }
}
