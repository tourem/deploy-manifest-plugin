package io.github.tourem.maven.descriptor.config.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of configuration validation.
 */
public class ValidationResult {
    
    private final List<ValidationError> errors;
    
    public ValidationResult() {
        this.errors = new ArrayList<>();
    }
    
    /**
     * Adds a validation error.
     *
     * @param error the error to add
     */
    public void addError(ValidationError error) {
        errors.add(error);
    }
    
    /**
     * Adds a validation error with field, value, and message.
     *
     * @param field the field name
     * @param value the invalid value
     * @param message the error message
     */
    public void addError(String field, Object value, String message) {
        errors.add(new ValidationError(field, value, message));
    }
    
    /**
     * Adds a validation error with field, value, message, and suggestion.
     *
     * @param field the field name
     * @param value the invalid value
     * @param message the error message
     * @param suggestion the suggestion
     */
    public void addError(String field, Object value, String message, String suggestion) {
        errors.add(new ValidationError(field, value, message, suggestion));
    }
    
    /**
     * Checks if validation passed (no errors).
     *
     * @return true if valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Gets all validation errors.
     *
     * @return unmodifiable list of errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Gets the number of errors.
     *
     * @return error count
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Formats errors as a readable string.
     *
     * @return formatted error message
     */
    public String formatErrors() {
        if (isValid()) {
            return "✅ Configuration is VALID";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Configuration validation failed (").append(errors.size()).append(" error");
        if (errors.size() > 1) sb.append("s");
        sb.append(")\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        for (int i = 0; i < errors.size(); i++) {
            ValidationError error = errors.get(i);
            sb.append("Error ").append(i + 1).append(": ").append(error.getField()).append("\n");
            sb.append("────────────────────────────────────────────────────────\n");
            sb.append("  Value: '").append(error.getValue()).append("'\n");
            sb.append("  \n");
            sb.append("  ").append(error.getMessage()).append("\n");
            
            if (error.hasSuggestion()) {
                sb.append("  \n");
                sb.append("  💡 ").append(error.getSuggestion()).append("\n");
            }
            
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }
        
        sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Fix these errors and try again.\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return formatErrors();
    }
}
