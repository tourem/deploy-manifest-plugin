package io.github.tourem.maven.descriptor.config.validator;

/**
 * Represents a configuration validation error.
 */
public class ValidationError {
    
    private final String field;
    private final Object value;
    private final String message;
    private final String suggestion;
    
    public ValidationError(String field, Object value, String message) {
        this(field, value, message, null);
    }
    
    public ValidationError(String field, Object value, String message, String suggestion) {
        this.field = field;
        this.value = value;
        this.message = message;
        this.suggestion = suggestion;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public boolean hasSuggestion() {
        return suggestion != null && !suggestion.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field: ").append(field).append("\n");
        sb.append("Value: ").append(value).append("\n");
        sb.append("Error: ").append(message);
        if (hasSuggestion()) {
            sb.append("\n").append("Suggestion: ").append(suggestion);
        }
        return sb.toString();
    }
}
