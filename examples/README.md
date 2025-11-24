# Configuration Examples

This directory contains example configuration files for the Maven Deploy Manifest Plugin.

## Files

### Minimal Configuration
**File**: `.deploy-manifest-minimal.yml`

Simplest configuration using the default `basic` profile.

```yaml
profile: basic
```

### Profile-Based Configurations

#### Standard Profile
**File**: `.deploy-manifest-standard-profile.yml`

Recommended for team documentation. Generates JSON + HTML with dependency tree.

```yaml
profile: standard
dependencies:
  tree:
    depth: 5
metadata:
  licenses: true
```

#### Full Profile
**File**: `.deploy-manifest-full-profile.yml`

Complete analysis with all formats and metadata.

```yaml
profile: full
output:
  archive: true
dependencies:
  tree:
    depth: 7
  analysis:
    enabled: true
```

#### CI Profile
**File**: `.deploy-manifest-ci-profile.yml`

Optimized for CI/CD pipelines with archive attachment.

```yaml
profile: ci
output:
  attach: true
git:
  fetch: always
```

### Complete Reference
**File**: `.deploy-manifest-complete.yml`

Shows all available configuration options. Use as a reference.

## Usage

1. **Copy** one of these files to your project root
2. **Rename** it to `.deploy-manifest.yml`
3. **Customize** as needed
4. **Run** the plugin:
   ```bash
   mvn deploy-manifest:generate
   ```

## Editor Support

For autocompletion and validation in your editor:

### VS Code
1. Install the [YAML extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml)
2. The schema reference in the file header will be automatically detected

### IntelliJ IDEA
Built-in support - the schema reference will be automatically detected

## Schema Reference

All example files include this header:
```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json
```

This enables:
- ✅ Autocompletion (Ctrl+Space)
- ✅ Real-time validation
- ✅ Inline documentation
- ✅ Error detection

## Override with Environment Variables

You can override any configuration value using environment variables:

```bash
export MANIFEST_PROFILE=full
export MANIFEST_OUTPUT_FORMATS=json,html
export MANIFEST_DEPENDENCIES_TREE_DEPTH=10
mvn deploy-manifest:generate
```

## Override with Command Line

Command line options have the highest priority:

```bash
mvn deploy-manifest:generate \
  -Dmanifest.profile=standard \
  -Dmanifest.dependencies.tree.depth=5 \
  -Dmanifest.metadata.licenses=true
```

## Priority Order

Configuration is resolved in this order (highest first):

1. ⌨️  Command line (`-Dmanifest.*`)
2. 🌍 Environment variables (`MANIFEST_*`)
3. 📄 Configuration file (`.deploy-manifest.yml`)
4. 📦 Profile defaults
5. 🔧 Plugin defaults

## Validate Configuration

See your resolved configuration:

```bash
mvn deploy-manifest:validate-config
```

This shows a table with all options, their values, and sources.
