package io.github.tourem.maven.descriptor.util;

import org.apache.maven.model.Model;

/**
 * Utility class for resolving Maven model properties with parent inheritance support.
 * Centralizes the logic for resolving groupId, version, and other inherited properties.
 *
 * @author tourem
 */
public final class MavenModelResolver {

    private MavenModelResolver() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Resolve groupId from model or parent.
     *
     * @param model Maven model
     * @return resolved groupId
     * @throws IllegalStateException if groupId cannot be resolved
     */
    public static String resolveGroupId(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        if (model.getGroupId() != null) {
            return model.getGroupId();
        }

        if (model.getParent() != null && model.getParent().getGroupId() != null) {
            return model.getParent().getGroupId();
        }

        throw new IllegalStateException(
            String.format("Cannot resolve groupId for module: %s", model.getArtifactId())
        );
    }

    /**
     * Resolve version from model or parent.
     *
     * @param model Maven model
     * @return resolved version
     * @throws IllegalStateException if version cannot be resolved
     */
    public static String resolveVersion(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        if (model.getVersion() != null) {
            return model.getVersion();
        }

        if (model.getParent() != null && model.getParent().getVersion() != null) {
            return model.getParent().getVersion();
        }

        throw new IllegalStateException(
            String.format("Cannot resolve version for module: %s", model.getArtifactId())
        );
    }

    /**
     * Resolve a property from model or parent model.
     *
     * @param model Maven model
     * @param parentModel parent Maven model (can be null)
     * @param propertyName property name to resolve
     * @return property value or null if not found
     */
    public static String resolveProperty(Model model, Model parentModel, String propertyName) {
        if (model == null || propertyName == null) {
            return null;
        }

        // Check in current model
        if (model.getProperties() != null) {
            String value = model.getProperties().getProperty(propertyName);
            if (value != null) {
                return value;
            }
        }

        // Check in parent model
        if (parentModel != null && parentModel.getProperties() != null) {
            return parentModel.getProperties().getProperty(propertyName);
        }

        return null;
    }

    /**
     * Safely get groupId or return empty string.
     *
     * @param model Maven model
     * @return groupId or empty string
     */
    public static String getGroupIdOrEmpty(Model model) {
        try {
            return resolveGroupId(model);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Safely get version or return empty string.
     *
     * @param model Maven model
     * @return version or empty string
     */
    public static String getVersionOrEmpty(Model model) {
        try {
            return resolveVersion(model);
        } catch (Exception e) {
            return "";
        }
    }
}
