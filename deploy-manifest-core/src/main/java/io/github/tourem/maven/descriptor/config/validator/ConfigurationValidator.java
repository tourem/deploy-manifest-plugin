package io.github.tourem.maven.descriptor.config.validator;

import io.github.tourem.maven.descriptor.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Validates ManifestConfiguration and provides helpful error messages.
 */
public class ConfigurationValidator {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationValidator.class);
    
    private static final String[] VALID_PROFILES = {"basic", "standard", "full", "ci"};
    private static final String[] VALID_FORMATS = {"json", "yaml", "html", "xml"};
    private static final String[] VALID_ARCHIVE_FORMATS = {"zip", "tar.gz", "tar.bz2", "jar"};
    private static final String[] VALID_TREE_FORMATS = {"flat", "tree", "both"};
    private static final String[] VALID_GIT_FETCH_MODES = {"auto", "always", "never"};
    private static final String[] VALID_SCOPES = {"compile", "runtime", "test", "provided", "system"};
    
    private final Validator beanValidator;
    
    public ConfigurationValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.beanValidator = factory.getValidator();
    }
    
    /**
     * Validates a configuration.
     *
     * @param config the configuration to validate
     * @return the validation result
     */
    public ValidationResult validate(ManifestConfiguration config) {
        ValidationResult result = new ValidationResult();
        
        if (config == null) {
            result.addError("configuration", null, "Configuration cannot be null");
            return result;
        }
        
        // Bean Validation
        Set<ConstraintViolation<ManifestConfiguration>> violations = beanValidator.validate(config);
        for (ConstraintViolation<ManifestConfiguration> violation : violations) {
            String field = violation.getPropertyPath().toString();
            Object value = violation.getInvalidValue();
            String message = violation.getMessage();
            result.addError(field, value, message);
        }
        
        // Custom validations
        validateOutput(config.getOutput(), result);
        validateDependencies(config.getDependencies(), result);
        validateGit(config.getGit(), result);
        
        if (result.isValid()) {
            log.debug("Configuration validation passed");
        } else {
            log.warn("Configuration validation failed with {} error(s)", result.getErrorCount());
        }
        
        return result;
    }
    
    private void validateOutput(OutputConfiguration output, ValidationResult result) {
        if (output == null) return;
        
        // Validate formats
        if (output.getFormats() != null) {
            for (String format : output.getFormats()) {
                if (!isValidFormat(format)) {
                    String suggestion = LevenshteinDistance.findClosestMatch(format, VALID_FORMATS);
                    String message = "Invalid output format. Allowed values: " + String.join(", ", VALID_FORMATS);
                    if (suggestion != null) {
                        message = message + "\nDid you mean '" + suggestion + "'?";
                    }
                    result.addError("output.formats", format, message, suggestion);
                }
            }
        }
        
        // Validate archive format
        if (output.getArchiveFormat() != null && !isValidArchiveFormat(output.getArchiveFormat())) {
            String suggestion = LevenshteinDistance.findClosestMatch(output.getArchiveFormat(), VALID_ARCHIVE_FORMATS);
            String message = "Invalid archive format. Allowed values: " + String.join(", ", VALID_ARCHIVE_FORMATS);
            result.addError("output.archiveFormat", output.getArchiveFormat(), message, suggestion);
        }
    }
    
    private void validateDependencies(DependenciesConfiguration deps, ValidationResult result) {
        if (deps == null) return;
        
        validateDependencyTree(deps.getTree(), result);
        validateDependencyAnalysis(deps.getAnalysis(), result);
    }
    
    private void validateDependencyTree(DependencyTreeConfiguration tree, ValidationResult result) {
        if (tree == null) return;
        
        // Validate depth
        if (tree.getDepth() != null) {
            if (tree.getDepth() < 1 || tree.getDepth() > 10) {
                result.addError(
                    "dependencies.tree.depth",
                    tree.getDepth(),
                    "Dependency tree depth must be between 1 and 10 (deep trees can cause performance issues)",
                    "Suggested value: 5"
                );
            }
        }
        
        // Validate format
        if (tree.getFormat() != null && !isValidTreeFormat(tree.getFormat())) {
            String suggestion = LevenshteinDistance.findClosestMatch(tree.getFormat(), VALID_TREE_FORMATS);
            String message = "Invalid tree format. Allowed values: " + String.join(", ", VALID_TREE_FORMATS);
            result.addError("dependencies.tree.format", tree.getFormat(), message, suggestion);
        }
        
        // Validate scopes
        if (tree.getScopes() != null) {
            for (String scope : tree.getScopes()) {
                if (!isValidScope(scope)) {
                    String suggestion = LevenshteinDistance.findClosestMatch(scope, VALID_SCOPES);
                    String message = "Invalid dependency scope. Allowed values: " + String.join(", ", VALID_SCOPES);
                    result.addError("dependencies.tree.scopes", scope, message, suggestion);
                }
            }
        }
    }
    
    private void validateDependencyAnalysis(DependencyAnalysisConfiguration analysis, ValidationResult result) {
        if (analysis == null) return;
        
        // Validate health threshold
        if (analysis.getHealthThreshold() != null) {
            if (analysis.getHealthThreshold() < 0 || analysis.getHealthThreshold() > 100) {
                result.addError(
                    "dependencies.analysis.healthThreshold",
                    analysis.getHealthThreshold(),
                    "Health threshold must be between 0 and 100",
                    "Suggested value: 80"
                );
            }
        }
    }
    
    private void validateGit(GitConfiguration git, ValidationResult result) {
        if (git == null) return;
        
        // Validate depth
        if (git.getDepth() != null) {
            if (git.getDepth() < 1 || git.getDepth() > 1000) {
                result.addError(
                    "git.depth",
                    git.getDepth(),
                    "Git history depth must be between 1 and 1000",
                    "Suggested value: 50"
                );
            }
        }
    }
    
    // Helper methods
    
    private boolean isValidFormat(String format) {
        for (String valid : VALID_FORMATS) {
            if (valid.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidArchiveFormat(String format) {
        for (String valid : VALID_ARCHIVE_FORMATS) {
            if (valid.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidTreeFormat(String format) {
        for (String valid : VALID_TREE_FORMATS) {
            if (valid.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidScope(String scope) {
        for (String valid : VALID_SCOPES) {
            if (valid.equalsIgnoreCase(scope)) {
                return true;
            }
        }
        return false;
    }
}
