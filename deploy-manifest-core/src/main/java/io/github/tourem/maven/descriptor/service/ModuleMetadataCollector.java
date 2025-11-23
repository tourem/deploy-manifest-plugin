package io.github.tourem.maven.descriptor.service;

import io.github.tourem.maven.descriptor.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;

import java.nio.file.Path;

/**
 * Helper class to collect optional metadata for a module.
 * Centralizes the collection logic with consistent error handling.
 *
 * @author tourem
 */
@Slf4j
public class ModuleMetadataCollector {

    private final DependencyTreeCollector dependencyTreeCollector;
    private final LicenseCollector licenseCollector;
    private final PropertyCollector propertyCollector;
    private final PluginCollector pluginCollector;

    public ModuleMetadataCollector(
            DependencyTreeCollector dependencyTreeCollector,
            LicenseCollector licenseCollector,
            PropertyCollector propertyCollector,
            PluginCollector pluginCollector) {
        this.dependencyTreeCollector = dependencyTreeCollector;
        this.licenseCollector = licenseCollector;
        this.propertyCollector = propertyCollector;
        this.pluginCollector = pluginCollector;
    }

    /**
     * Collect dependency tree information if enabled.
     */
    public DependencyTreeInfo collectDependencyTree(
            Model model,
            Path modulePath,
            DependencyTreeOptions options,
            String groupId,
            String artifactId) {
        
        if (options == null || !options.isInclude()) {
            return null;
        }

        try {
            return dependencyTreeCollector.collect(model, modulePath, options);
        } catch (Exception e) {
            log.debug("Dependency tree collection failed for {}:{} - {}", 
                     groupId, artifactId, e.getMessage());
            return null;
        }
    }

    /**
     * Collect license information if enabled.
     */
    public LicenseInfo collectLicenses(
            Model model,
            Path modulePath,
            LicenseOptions options,
            String groupId,
            String artifactId) {
        
        if (options == null || !options.isInclude()) {
            return null;
        }

        try {
            return licenseCollector.collect(model, modulePath, options);
        } catch (Exception e) {
            log.debug("License collection failed for {}:{} - {}", 
                     groupId, artifactId, e.getMessage());
            return null;
        }
    }

    /**
     * Collect build properties if enabled.
     */
    public BuildProperties collectProperties(
            Model model,
            Path modulePath,
            PropertyOptions options,
            String groupId,
            String artifactId) {
        
        if (options == null || !options.isInclude()) {
            return null;
        }

        try {
            var result = propertyCollector.collect(model, modulePath, options);
            return result.properties();
        } catch (Exception e) {
            log.debug("Property collection failed for {}:{} - {}", 
                     groupId, artifactId, e.getMessage());
            return null;
        }
    }

    /**
     * Collect plugin information if enabled.
     */
    public PluginInfo collectPlugins(
            Model model,
            Path modulePath,
            PluginOptions options,
            String groupId,
            String artifactId) {
        
        if (options == null || !options.isInclude()) {
            return null;
        }

        try {
            return pluginCollector.collect(model, modulePath, options);
        } catch (Exception e) {
            log.debug("Plugin collection failed for {}:{} - {}", 
                     groupId, artifactId, e.getMessage());
            return null;
        }
    }
}
