# 🚀 Quick Start - YAML Configuration

Get started with YAML configuration in 5 minutes!

---

## Step 1: Create Configuration File

Create `.deploy-manifest.yml` in your project root:

```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json

profile: standard
```

That's it! You now have a working configuration.

---

## Step 2: Validate Configuration

```bash
mvn deploy-manifest:validate-config
```

You'll see:
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Deploy Manifest Plugin - Configuration Validation
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Configuration Summary:
  Profile:                       standard (📄 YAML)
  Output directory:              target (🔧 Default)
  Output formats:                [json, html] (📦 Profile)
  
✅ Configuration is VALID
```

---

## Step 3: Generate Manifest

```bash
mvn deploy-manifest:generate
```

Your manifest will be generated using the YAML configuration!

---

## 🎯 Common Configurations

### Minimal (Development)
```yaml
profile: basic
```

### Standard (Team Documentation)
```yaml
profile: standard

output:
  formats:
    - json
    - html

dependencies:
  tree:
    depth: 5

metadata:
  licenses: true
```

### Full (Complete Analysis)
```yaml
profile: full

output:
  directory: target/reports
  formats:
    - json
    - yaml
    - html

dependencies:
  tree:
    enabled: true
    depth: 7
  analysis:
    enabled: true
    healthThreshold: 80

metadata:
  licenses: true
  properties: true
  plugins: true
```

### CI/CD (Automated Builds)
```yaml
profile: ci

output:
  archive: true
  attach: true

git:
  fetch: always

ci:
  autoDetect: true
  includeEnvironment: true
```

---

## 🌍 Environment Variables

Override any setting with environment variables:

```bash
# Set profile
export MANIFEST_PROFILE=full

# Set output formats
export MANIFEST_OUTPUT_FORMATS=json,yaml,html

# Set dependency tree depth
export MANIFEST_DEPENDENCIES_TREE_DEPTH=10

# Enable verbose mode
export MANIFEST_VERBOSE=true

# Run
mvn deploy-manifest:generate
```

---

## ⌨️  Command Line

Override with command line options:

```bash
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.output.formats=json,yaml \
  -Dmanifest.dependencies.tree.depth=10 \
  -Dmanifest.verbose=true
```

---

## 🎨 IDE Setup

### VS Code

1. Install **YAML extension** by Red Hat
2. Open `.deploy-manifest.yml`
3. Enjoy autocompletion! (Ctrl+Space)

### IntelliJ IDEA

1. YAML support is built-in
2. Open `.deploy-manifest.yml`
3. Enjoy autocompletion! (Ctrl+Space)

---

## 🔍 Configuration Priority

When the same property is set in multiple places:

1. ⌨️  **Command Line** wins (highest priority)
2. 🌍 **Environment** variables
3. 📄 **YAML File**
4. 📦 **Profile** defaults
5. 🔨 **POM** configuration
6. 🔧 **Plugin** defaults (lowest priority)

Example:
```yaml
# .deploy-manifest.yml
output:
  formats: [json]
```

```bash
# Override with environment
export MANIFEST_OUTPUT_FORMATS=json,html

# Override with command line (wins!)
mvn deploy-manifest:generate -Dmanifest.output.formats=json,yaml,html
```

Result: `[json, yaml, html]` (from command line)

---

## 📚 Examples

See `examples/` directory for complete examples:

- `.deploy-manifest-minimal.yml` - Basic setup
- `.deploy-manifest-standard-profile.yml` - Team docs
- `.deploy-manifest-full-profile.yml` - Complete analysis
- `.deploy-manifest-ci-profile.yml` - CI/CD optimized
- `.deploy-manifest-complete.yml` - All options

---

## ❓ Common Questions

### Q: Do I need to use YAML?
**A:** No! YAML is optional. Old POM configurations still work.

### Q: Can I mix YAML and command line?
**A:** Yes! Command line overrides YAML.

### Q: How do I know which profile to use?
**A:** 
- `basic` - Quick local development
- `standard` - Team documentation
- `full` - Complete analysis
- `ci` - CI/CD pipelines

### Q: What if I make a typo?
**A:** The validator will suggest corrections:
```
Error: output.formats
  Value: 'jsn'
  💡 Did you mean 'json'?
```

---

## 🆘 Troubleshooting

### Configuration not loading?
```bash
# Check if file exists
ls -la .deploy-manifest.yml

# Validate configuration
mvn deploy-manifest:validate-config
```

### Autocompletion not working?
1. Check the `$schema` line at the top of your YAML file
2. Restart your IDE
3. Make sure YAML extension is installed

### Want to see where values come from?
```bash
mvn deploy-manifest:validate-config
```

Shows source for each property (YAML, ENV, CLI, etc.)

---

## 🎉 Next Steps

1. ✅ Create `.deploy-manifest.yml`
2. ✅ Run `mvn deploy-manifest:validate-config`
3. ✅ Run `mvn deploy-manifest:generate`
4. 📚 Explore examples in `examples/` directory
5. 📖 Read full documentation in `README.md`

---

**Happy configuring! 🚀**
