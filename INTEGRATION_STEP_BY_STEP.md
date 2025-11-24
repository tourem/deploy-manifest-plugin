# Integration Step-by-Step Guide

**Goal**: Integrate YAML configuration system into GenerateDescriptorMojo  
**Time**: 1-2 hours  
**Difficulty**: Medium

---

## 📋 Overview

This guide provides detailed steps to integrate the new YAML configuration system into the existing `GenerateDescriptorMojo` while maintaining 100% backward compatibility.

---

## Step 1: Add Configuration Resolver (5 minutes)

### 1.1 Add Import Statements

Add these imports at the top of `GenerateDescriptorMojo.java`:

```java
import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.resolver.*;
```

### 1.2 Add ConfigurationResolver Field

Add this field after the existing `@Component` declarations:

```java
/**
 * Configuration resolver for loading and merging configuration from all sources.
 */
private final ConfigurationResolver configResolver = new ConfigurationResolver();
```

---

## Step 2: Create POM Configuration Builder (15 minutes)

### 2.1 Add Method to Build POM Configuration

Add this method to `GenerateDescriptorMojo`:

```java
/**
 * Builds configuration from POM parameters for backward compatibility.
 * Maps old Maven parameters to new configuration model.
 *
 * @return configuration from POM parameters
 */
private ManifestConfiguration buildPomConfiguration() {
    ManifestConfiguration config = new ManifestConfiguration();
    
    // Top-level properties
    if (skip) {
        config.setSkip(skip);
    }
    
    if (profile != null && !profile.isEmpty()) {
        try {
            config.setProfile(ManifestProfile.fromValue(profile));
        } catch (IllegalArgumentException e) {
            getLog().warn("Invalid profile '" + profile + "', using default");
        }
    }
    
    // Output configuration
    if (outputDirectory != null) {
        config.getOutput().setDirectory(outputDirectory);
    }
    
    if (outputFile != null) {
        config.getOutput().setFilename(outputFile);
    }
    
    // Map exportFormat to formats list
    if (exportFormat != null) {
        List<String> formats = new ArrayList<>();
        if ("json".equals(exportFormat) || "both".equals(exportFormat)) {
            formats.add("json");
        }
        if ("yaml".equals(exportFormat) || "both".equals(exportFormat)) {
            formats.add("yaml");
        }
        if (generateHtml) {
            formats.add("html");
        }
        if (!formats.isEmpty()) {
            config.getOutput().setFormats(formats);
        }
    }
    
    if (format != null) {
        config.getOutput().setArchive(true);
        config.getOutput().setArchiveFormat(format);
    }
    
    if (attach) {
        config.getOutput().setAttach(attach);
    }
    
    if (classifier != null) {
        config.getOutput().setClassifier(classifier);
    }
    
    // Dependency tree configuration
    if (includeDependencyTree) {
        config.getDependencies().getTree().setEnabled(true);
        
        if (dependencyTreeDepth > 0) {
            config.getDependencies().getTree().setDepth(dependencyTreeDepth);
        }
    }
    
    // Metadata configuration
    if (includeLicenses) {
        config.getMetadata().setLicenses(true);
    }
    
    if (includeProperties) {
        config.getMetadata().setProperties(true);
    }
    
    if (includePlugins) {
        config.getMetadata().setPlugins(true);
    }
    
    return config;
}
```

### 2.2 Add Missing Field Declarations

Make sure these fields exist in the Mojo (they should already be there):

```java
private boolean includeDependencyTree;
private int dependencyTreeDepth;
private boolean includeLicenses;
private boolean includeProperties;
private boolean includePlugins;
```

---

## Step 3: Modify execute() Method (20 minutes)

### 3.1 Replace Current execute() Logic

Find the current `execute()` method and modify it:

```java
@Override
public void execute() throws MojoExecutionException, MojoFailureException {
    try {
        // 1. Resolve configuration from all sources
        getLog().info("Resolving configuration from all sources...");
        
        ResolvedConfiguration resolved = configResolver.resolve(
            project.getBasedir(),
            buildPomConfiguration()
        );
        
        ManifestConfiguration config = resolved.getConfiguration();
        
        // 2. Check skip flag
        if (config.getSkip()) {
            getLog().info("Skipping manifest generation (skip=true)");
            return;
        }
        
        // 3. Log resolved configuration
        logResolvedConfiguration(config, resolved);
        
        // 4. Generate manifest using resolved configuration
        generateManifest(config);
        
    } catch (ConfigurationResolutionException e) {
        throw new MojoExecutionException("Configuration error: " + e.getMessage(), e);
    } catch (Exception e) {
        throw new MojoExecutionException("Failed to generate manifest: " + e.getMessage(), e);
    }
}
```

---

## Step 4: Add Configuration Logging (10 minutes)

### 4.1 Add Logging Method

Add this method to log the resolved configuration:

```java
/**
 * Logs the resolved configuration with sources.
 *
 * @param config the resolved configuration
 * @param resolved the resolved configuration with source tracking
 */
private void logResolvedConfiguration(ManifestConfiguration config, ResolvedConfiguration resolved) {
    getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    getLog().info("Configuration Resolved");
    getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    getLog().info("");
    
    // Profile
    getLog().info("Profile: " + config.getProfile() + 
                 " (from " + resolved.getSource("profile").getDisplayName() + ")");
    
    // Output
    getLog().info("Output directory: " + config.getOutput().getDirectory() + 
                 " (from " + resolved.getSource("output.directory").getDisplayName() + ")");
    getLog().info("Output formats: " + config.getOutput().getFormats() + 
                 " (from " + resolved.getSource("output.formats").getDisplayName() + ")");
    
    // Dependency tree
    if (config.getDependencies().getTree().getEnabled()) {
        getLog().info("Dependency tree: enabled (depth=" + 
                     config.getDependencies().getTree().getDepth() + 
                     ", from " + resolved.getSource("dependencies.tree.depth").getDisplayName() + ")");
    }
    
    // Metadata
    if (config.getMetadata().getLicenses()) {
        getLog().info("Licenses: enabled (from " + 
                     resolved.getSource("metadata.licenses").getDisplayName() + ")");
    }
    
    getLog().info("");
    getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
}
```

---

## Step 5: Refactor Generation Logic (20 minutes)

### 5.1 Extract Generation to Separate Method

Create a new method that uses the configuration:

```java
/**
 * Generates the manifest using the resolved configuration.
 *
 * @param config the resolved configuration
 * @throws MojoExecutionException if generation fails
 */
private void generateManifest(ManifestConfiguration config) throws MojoExecutionException {
    try {
        // Use config instead of direct field access
        String outputDir = config.getOutput().getDirectory();
        String filename = config.getOutput().getFilename();
        List<String> formats = config.getOutput().getFormats();
        
        // Create output directory
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        
        // Generate descriptor (your existing logic)
        ProjectDescriptor descriptor = analyzeProject(config);
        
        // Write outputs based on formats
        for (String format : formats) {
            switch (format.toLowerCase()) {
                case "json":
                    writeJsonOutput(descriptor, outputDirectory, filename);
                    break;
                case "yaml":
                    writeYamlOutput(descriptor, outputDirectory, filename);
                    break;
                case "html":
                    writeHtmlOutput(descriptor, outputDirectory, filename);
                    break;
                default:
                    getLog().warn("Unknown format: " + format);
            }
        }
        
        // Handle archiving if enabled
        if (config.getOutput().getArchive()) {
            createArchive(config, outputDirectory);
        }
        
        // Handle attachment if enabled
        if (config.getOutput().getAttach()) {
            attachArtifact(config, outputDirectory);
        }
        
        getLog().info("Manifest generated successfully");
        
    } catch (Exception e) {
        throw new MojoExecutionException("Failed to generate manifest", e);
    }
}
```

### 5.2 Add Helper Methods

Add these helper methods (adapt to your existing code):

```java
private ProjectDescriptor analyzeProject(ManifestConfiguration config) {
    // Your existing project analysis logic
    // Use config.getDependencies().getTree().getEnabled() instead of includeDependencyTree
    // Use config.getMetadata().getLicenses() instead of includeLicenses
    // etc.
    return null; // Replace with actual implementation
}

private void writeJsonOutput(ProjectDescriptor descriptor, File outputDir, String filename) {
    // Your existing JSON writing logic
}

private void writeYamlOutput(ProjectDescriptor descriptor, File outputDir, String filename) {
    // Your existing YAML writing logic
}

private void writeHtmlOutput(ProjectDescriptor descriptor, File outputDir, String filename) {
    // Your existing HTML writing logic
}

private void createArchive(ManifestConfiguration config, File outputDir) {
    // Your existing archive creation logic
}

private void attachArtifact(ManifestConfiguration config, File outputDir) {
    // Your existing artifact attachment logic
}
```

---

## Step 6: Update Field Access Throughout (15 minutes)

### 6.1 Replace Direct Field Access

Throughout your existing code, replace direct field access with config access:

**Before**:
```java
if (includeDependencyTree) {
    // ...
}
```

**After**:
```java
if (config.getDependencies().getTree().getEnabled()) {
    // ...
}
```

**Common Replacements**:
- `includeDependencyTree` → `config.getDependencies().getTree().getEnabled()`
- `dependencyTreeDepth` → `config.getDependencies().getTree().getDepth()`
- `includeLicenses` → `config.getMetadata().getLicenses()`
- `includeProperties` → `config.getMetadata().getProperties()`
- `includePlugins` → `config.getMetadata().getPlugins()`
- `outputDirectory` → `config.getOutput().getDirectory()`
- `outputFile` → `config.getOutput().getFilename()`
- `generateHtml` → `config.getOutput().getFormats().contains("html")`

---

## Step 7: Test Integration (10 minutes)

### 7.1 Test with POM Configuration

```xml
<plugin>
    <groupId>io.github.tourem</groupId>
    <artifactId>deploy-manifest-plugin</artifactId>
    <configuration>
        <outputDirectory>target/manifests</outputDirectory>
        <generateHtml>true</generateHtml>
    </configuration>
</plugin>
```

Run: `mvn deploy-manifest:generate`

### 7.2 Test with YAML Configuration

Create `.deploy-manifest.yml`:
```yaml
profile: standard
output:
  directory: target/reports
```

Run: `mvn deploy-manifest:generate`

### 7.3 Test with Environment Variables

```bash
export MANIFEST_OUTPUT_FORMATS=json,yaml
mvn deploy-manifest:generate
```

### 7.4 Test with Command Line

```bash
mvn deploy-manifest:generate -Dmanifest.profile=full
```

---

## Step 8: Handle Edge Cases (5 minutes)

### 8.1 Add Null Checks

Ensure null safety:

```java
if (config != null && config.getOutput() != null) {
    String dir = config.getOutput().getDirectory();
    // ...
}
```

### 8.2 Add Default Values

Ensure defaults are set:

```java
String outputDir = config.getOutput().getDirectory();
if (outputDir == null || outputDir.isEmpty()) {
    outputDir = project.getBuild().getDirectory();
}
```

---

## ✅ Integration Checklist

- [ ] Step 1: Add ConfigurationResolver
- [ ] Step 2: Create buildPomConfiguration()
- [ ] Step 3: Modify execute() method
- [ ] Step 4: Add logging method
- [ ] Step 5: Refactor generation logic
- [ ] Step 6: Update field access
- [ ] Step 7: Test all scenarios
- [ ] Step 8: Handle edge cases

---

## 🧪 Testing Checklist

- [ ] Test with POM configuration only
- [ ] Test with YAML configuration only
- [ ] Test with environment variables
- [ ] Test with command line options
- [ ] Test with mixed sources (YAML + CLI)
- [ ] Test backward compatibility (old configs)
- [ ] Test validate-config goal
- [ ] Test error handling

---

## 📝 Notes

1. **Backward Compatibility**: All old configurations must continue to work
2. **Gradual Migration**: Users can migrate progressively
3. **Clear Logging**: Show where each value comes from
4. **Error Handling**: Provide clear error messages
5. **Documentation**: Update javadoc for modified methods

---

## 🎯 Expected Result

After integration:
- ✅ Old POM configurations work unchanged
- ✅ New YAML configurations work
- ✅ Environment variables work
- ✅ Command line options work
- ✅ Configuration priority is respected
- ✅ Sources are logged clearly
- ✅ validate-config goal shows correct info

---

**Time**: ~1-2 hours  
**Difficulty**: Medium  
**Impact**: High (enables all new features)

**Good luck with the integration! 🚀**
