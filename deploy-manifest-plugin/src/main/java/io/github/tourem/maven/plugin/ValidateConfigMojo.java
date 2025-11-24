package io.github.tourem.maven.plugin;

import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.resolver.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Map;

/**
 * Validates and displays the resolved configuration.
 * <p>
 * This goal shows how configuration is resolved from all sources
 * (YAML file, environment variables, command line, POM, defaults)
 * and displays it in a readable table format.
 * </p>
 * <p>
 * Usage:
 * <pre>
 * mvn deploy-manifest:validate-config
 * </pre>
 * </p>
 *
 * @author tourem
 * @since 3.0.0
 */
@Mojo(name = "validate-config", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class ValidateConfigMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("");
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        getLog().info("  Deploy Manifest Plugin - Configuration Validation");
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        getLog().info("");
        
        try {
            // Resolve configuration from all sources
            ConfigurationResolver resolver = new ConfigurationResolver();
            ResolvedConfiguration resolved = resolver.resolve(project.getBasedir());
            
            ManifestConfiguration config = resolved.getConfiguration();
            
            // Display configuration
            displayConfiguration(config, resolved);
            
            getLog().info("");
            getLog().info("✅ Configuration is VALID");
            getLog().info("");
            
        } catch (ConfigurationResolutionException e) {
            getLog().error("");
            getLog().error("❌ Configuration validation FAILED");
            getLog().error("");
            getLog().error(e.getMessage());
            getLog().error("");
            throw new MojoFailureException("Configuration validation failed", e);
        }
    }
    
    private void displayConfiguration(ManifestConfiguration config, ResolvedConfiguration resolved) {
        // Header
        getLog().info("Configuration Summary:");
        getLog().info("");
        
        // Profile
        displayProperty("Profile", config.getProfile().toString(), resolved.getSource("profile"));
        
        // Top-level
        displayProperty("Verbose", config.getVerbose().toString(), resolved.getSource("verbose"));
        displayProperty("Dry Run", config.getDryRun().toString(), resolved.getSource("dryRun"));
        displayProperty("Skip", config.getSkip().toString(), resolved.getSource("skip"));
        
        getLog().info("");
        getLog().info("Output Configuration:");
        getLog().info("────────────────────────────────────────────────────────");
        
        // Output
        displayProperty("  Directory", config.getOutput().getDirectory(), resolved.getSource("output.directory"));
        displayProperty("  Filename", config.getOutput().getFilename(), resolved.getSource("output.filename"));
        displayProperty("  Formats", config.getOutput().getFormats().toString(), resolved.getSource("output.formats"));
        displayProperty("  Archive", config.getOutput().getArchive().toString(), resolved.getSource("output.archive"));
        
        if (config.getOutput().getArchive()) {
            displayProperty("  Archive Format", config.getOutput().getArchiveFormat(), resolved.getSource("output.archiveFormat"));
            displayProperty("  Attach", config.getOutput().getAttach().toString(), resolved.getSource("output.attach"));
            displayProperty("  Classifier", config.getOutput().getClassifier(), resolved.getSource("output.classifier"));
        }
        
        getLog().info("");
        getLog().info("Dependencies Configuration:");
        getLog().info("────────────────────────────────────────────────────────");
        
        // Dependencies - Tree
        displayProperty("  Tree Enabled", config.getDependencies().getTree().getEnabled().toString(), 
                       resolved.getSource("dependencies.tree.enabled"));
        
        if (config.getDependencies().getTree().getEnabled()) {
            displayProperty("  Tree Depth", config.getDependencies().getTree().getDepth().toString(), 
                           resolved.getSource("dependencies.tree.depth"));
            displayProperty("  Tree Format", config.getDependencies().getTree().getFormat(), 
                           resolved.getSource("dependencies.tree.format"));
            displayProperty("  Scopes", config.getDependencies().getTree().getScopes().toString(), 
                           resolved.getSource("dependencies.tree.scopes"));
        }
        
        // Dependencies - Analysis
        displayProperty("  Analysis Enabled", config.getDependencies().getAnalysis().getEnabled().toString(), 
                       resolved.getSource("dependencies.analysis.enabled"));
        
        if (config.getDependencies().getAnalysis().getEnabled()) {
            displayProperty("  Health Threshold", config.getDependencies().getAnalysis().getHealthThreshold().toString(), 
                           resolved.getSource("dependencies.analysis.healthThreshold"));
        }
        
        getLog().info("");
        getLog().info("Metadata Configuration:");
        getLog().info("────────────────────────────────────────────────────────");
        
        // Metadata
        displayProperty("  Licenses", config.getMetadata().getLicenses().toString(), resolved.getSource("metadata.licenses"));
        displayProperty("  Properties", config.getMetadata().getProperties().toString(), resolved.getSource("metadata.properties"));
        displayProperty("  Plugins", config.getMetadata().getPlugins().toString(), resolved.getSource("metadata.plugins"));
        displayProperty("  Checksums", config.getMetadata().getChecksums().toString(), resolved.getSource("metadata.checksums"));
        
        getLog().info("");
        getLog().info("Git Configuration:");
        getLog().info("────────────────────────────────────────────────────────");
        
        // Git
        displayProperty("  Fetch Mode", config.getGit().getFetch().toString(), resolved.getSource("git.fetch"));
        displayProperty("  Depth", config.getGit().getDepth().toString(), resolved.getSource("git.depth"));
        displayProperty("  Include Branch", config.getGit().getIncludeBranch().toString(), resolved.getSource("git.includeBranch"));
        displayProperty("  Include Tags", config.getGit().getIncludeTags().toString(), resolved.getSource("git.includeTags"));
        
        getLog().info("");
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        getLog().info("");
        getLog().info("Configuration Priority Order:");
        getLog().info("  1. ⌨️  Command Line (-Dmanifest.*)");
        getLog().info("  2. 🌍 Environment (MANIFEST_*)");
        getLog().info("  3. 📄 YAML File (.deploy-manifest.yml)");
        getLog().info("  4. 📦 Profile (profile defaults)");
        getLog().info("  5. 🔨 POM (pom.xml configuration)");
        getLog().info("  6. 🔧 Default (plugin defaults)");
        getLog().info("");
    }
    
    private void displayProperty(String name, String value, ConfigurationSource source) {
        String sourceSymbol = getSourceSymbol(source);
        String paddedName = String.format("%-30s", name + ":");
        getLog().info("  " + paddedName + " " + value + " " + sourceSymbol);
    }
    
    private String getSourceSymbol(ConfigurationSource source) {
        if (source == null) {
            return "(🔧 Default)";
        }
        
        switch (source) {
            case COMMAND_LINE:
                return "(⌨️  CLI)";
            case ENVIRONMENT:
                return "(🌍 ENV)";
            case YAML_FILE:
                return "(📄 YAML)";
            case PROFILE:
                return "(📦 Profile)";
            case POM_XML:
                return "(🔨 POM)";
            case DEFAULT:
                return "(🔧 Default)";
            default:
                return "(" + source.getDisplayName() + ")";
        }
    }
}
