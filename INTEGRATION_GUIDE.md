# Guide d'Intégration du Système de Configuration YAML

**Date**: 24 novembre 2025  
**Version**: 1.0  
**Branche**: `feature/yaml-config-management`

---

## 📋 Vue d'Ensemble

Ce document explique comment intégrer le nouveau système de configuration YAML dans le plugin Maven `deploy-manifest-plugin`.

### ✅ Ce qui a été implémenté

1. **JSON Schema** (`.deploy-manifest.schema.json`)
   - Autocomplétion dans les éditeurs
   - Validation temps réel
   - Documentation inline

2. **Modèle Java** (14 classes)
   - `ManifestConfiguration` - Configuration principale
   - Sous-configurations (Output, Dependencies, Metadata, Git, Docker, CI, Frameworks, Validation)
   - Enums type-safe (ManifestProfile, GitFetchMode, ConfigurationSource)
   - Bean Validation annotations

3. **Chargement Multi-Sources**
   - `YamlConfigurationLoader` - Fichier `.deploy-manifest.yml`
   - `EnvironmentConfigurationLoader` - Variables `MANIFEST_*`
   - `CommandLineConfigurationLoader` - Propriétés `manifest.*`

4. **Fusion Intelligente**
   - `ConfigurationMerger` - Fusion avec priorités
   - `ResolvedConfiguration` - Tracking des sources
   - Ordre: CLI > ENV > YAML > Profile > POM > Default

5. **Validation**
   - `ConfigurationValidator` - Validation complète
   - `ValidationResult` - Collection d'erreurs
   - `LevenshteinDistance` - Suggestions "Did you mean?"

6. **Orchestration**
   - `ConfigurationResolver` - Point d'entrée principal

---

## 🎯 Intégration dans GenerateDescriptorMojo

### Étape 1: Ajouter le ConfigurationResolver

```java
package io.github.tourem.maven.plugin;

import io.github.tourem.maven.descriptor.config.*;
import io.github.tourem.maven.descriptor.config.resolver.*;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class GenerateDescriptorMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    // Garder les paramètres existants pour rétrocompatibilité
    @Parameter(property = "manifest.outputFile", defaultValue = "deployment-manifest-report.json")
    private String outputFile;
    
    @Parameter(property = "manifest.outputDirectory", defaultValue = "${project.build.directory}")
    private String outputDirectory;
    
    @Parameter(property = "manifest.skip", defaultValue = "false")
    private boolean skip;
    
    // ... autres paramètres existants ...
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // 1. Résoudre la configuration depuis toutes les sources
            ConfigurationResolver resolver = new ConfigurationResolver();
            ResolvedConfiguration resolved = resolver.resolve(
                project.getBasedir(),
                buildPomConfiguration() // Configuration depuis les paramètres Maven
            );
            
            ManifestConfiguration config = resolved.getConfiguration();
            
            // 2. Vérifier skip
            if (config.getSkip()) {
                getLog().info("Skipping manifest generation (skip=true)");
                return;
            }
            
            // 3. Logger la configuration résolue
            logConfiguration(config, resolved);
            
            // 4. Utiliser la configuration pour la génération
            generateManifest(config);
            
        } catch (ConfigurationResolutionException e) {
            throw new MojoExecutionException("Configuration error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Construit la configuration depuis les paramètres Maven (POM).
     * Cela assure la rétrocompatibilité avec les anciennes configurations.
     */
    private ManifestConfiguration buildPomConfiguration() {
        ManifestConfiguration config = new ManifestConfiguration();
        
        // Mapper les anciens paramètres vers la nouvelle configuration
        if (outputDirectory != null) {
            config.getOutput().setDirectory(outputDirectory);
        }
        
        if (skip) {
            config.setSkip(skip);
        }
        
        if (profile != null) {
            try {
                config.setProfile(ManifestProfile.fromValue(profile));
            } catch (IllegalArgumentException e) {
                getLog().warn("Invalid profile: " + profile + ", using default");
            }
        }
        
        // ... mapper les autres paramètres ...
        
        return config;
    }
    
    /**
     * Log la configuration résolue avec les sources.
     */
    private void logConfiguration(ManifestConfiguration config, ResolvedConfiguration resolved) {
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        getLog().info("Configuration Resolved");
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        getLog().info("");
        getLog().info("Profile: " + config.getProfile() + 
                     " (from " + resolved.getSource("profile") + ")");
        getLog().info("Output directory: " + config.getOutput().getDirectory() + 
                     " (from " + resolved.getSource("output.directory") + ")");
        getLog().info("Output formats: " + config.getOutput().getFormats() + 
                     " (from " + resolved.getSource("output.formats") + ")");
        
        if (config.getDependencies().getTree().getEnabled()) {
            getLog().info("Dependency tree: enabled (depth=" + 
                         config.getDependencies().getTree().getDepth() + ")");
        }
        
        getLog().info("");
        getLog().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Génère le manifest en utilisant la configuration.
     */
    private void generateManifest(ManifestConfiguration config) throws MojoExecutionException {
        // Utiliser config.getOutput().getDirectory() au lieu de outputDirectory
        // Utiliser config.getOutput().getFormats() pour savoir quels formats générer
        // Utiliser config.getDependencies().getTree() pour la configuration de l'arbre
        // etc.
        
        // Votre logique de génération existante, mais en utilisant 'config'
    }
}
```

---

## 🔄 Migration des Paramètres Existants

### Mapping Ancien → Nouveau

| Ancien Paramètre Maven | Nouvelle Configuration | Notes |
|------------------------|------------------------|-------|
| `manifest.outputFile` | `output.filename` | Sans extension |
| `manifest.outputDirectory` | `output.directory` | Chemin |
| `manifest.skip` | `skip` | Boolean |
| `manifest.profile` | `profile` | basic/standard/full/ci |
| `manifest.format` | `output.archiveFormat` | zip/tar.gz/tar.bz2/jar |
| `manifest.attach` | `output.attach` | Boolean |
| `manifest.classifier` | `output.classifier` | String |
| `manifest.exportFormat` | `output.formats` | Liste: json, yaml, html |
| `manifest.generateHtml` | `output.formats` | Ajouter "html" |
| `manifest.includeDependencyTree` | `dependencies.tree.enabled` | Boolean |
| `manifest.dependencyTreeDepth` | `dependencies.tree.depth` | 1-10 |
| `manifest.includeLicenses` | `metadata.licenses` | Boolean |
| `manifest.includeProperties` | `metadata.properties` | Boolean |
| `manifest.includePlugins` | `metadata.plugins` | Boolean |

---

## 📝 Exemples d'Utilisation

### Exemple 1: Configuration Minimale (YAML)

```yaml
# .deploy-manifest.yml
profile: basic
```

### Exemple 2: Configuration Standard (YAML)

```yaml
# .deploy-manifest.yml
profile: standard

output:
  directory: target/reports
  formats:
    - json
    - html

dependencies:
  tree:
    depth: 5

metadata:
  licenses: true
```

### Exemple 3: Override avec Variables d'Environnement

```bash
# .deploy-manifest.yml
profile: standard

# Terminal
export MANIFEST_OUTPUT_FORMATS=json,yaml,html
export MANIFEST_DEPENDENCIES_TREE_DEPTH=10
export MANIFEST_VERBOSE=true

mvn deploy-manifest:generate
```

### Exemple 4: Override avec Ligne de Commande

```bash
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.output.formats=json,yaml \
  -Dmanifest.dependencies.tree.depth=7 \
  -Dmanifest.metadata.licenses=true
```

### Exemple 5: CI/CD

```yaml
# .github/workflows/build.yml
jobs:
  build:
    env:
      MANIFEST_PROFILE: ci
      MANIFEST_OUTPUT_ATTACH: true
      MANIFEST_GIT_FETCH: always
    steps:
      - run: mvn deploy-manifest:generate
```

---

## 🎯 Ordre de Priorité

Les valeurs sont résolues dans cet ordre (du plus prioritaire au moins prioritaire):

1. **⌨️  Command Line** (`-Dmanifest.*`) - Priorité MAX
2. **🌍 Environment** (`MANIFEST_*`)
3. **📄 YAML File** (`.deploy-manifest.yml`)
4. **📦 Profile** (defaults du profil choisi)
5. **🔨 POM** (paramètres dans `pom.xml`)
6. **🔧 Default** (valeurs par défaut du plugin)

---

## ✅ Checklist d'Intégration

- [ ] Ajouter `ConfigurationResolver` dans le Mojo
- [ ] Créer la méthode `buildPomConfiguration()`
- [ ] Remplacer les accès directs aux paramètres par `config.getXxx()`
- [ ] Ajouter le logging de la configuration
- [ ] Tester la rétrocompatibilité (anciennes configurations POM)
- [ ] Tester les nouvelles configurations YAML
- [ ] Tester les overrides ENV et CLI
- [ ] Mettre à jour la documentation utilisateur

---

## 🧪 Tests de Rétrocompatibilité

### Test 1: Ancienne Configuration POM

```xml
<plugin>
    <groupId>io.github.tourem</groupId>
    <artifactId>deploy-manifest-plugin</artifactId>
    <configuration>
        <outputDirectory>target/manifests</outputDirectory>
        <generateHtml>true</generateHtml>
        <includeDependencyTree>true</includeDependencyTree>
        <dependencyTreeDepth>5</dependencyTreeDepth>
    </configuration>
</plugin>
```

**Résultat attendu**: Doit fonctionner exactement comme avant.

### Test 2: Nouvelle Configuration YAML

```yaml
profile: standard
output:
  directory: target/manifests
  formats:
    - json
    - html
dependencies:
  tree:
    enabled: true
    depth: 5
```

**Résultat attendu**: Même résultat que Test 1.

### Test 3: Mix POM + YAML

```xml
<!-- pom.xml -->
<configuration>
    <outputDirectory>target/manifests</outputDirectory>
</configuration>
```

```yaml
# .deploy-manifest.yml
profile: standard
output:
  formats:
    - json
    - html
```

**Résultat attendu**: 
- `outputDirectory` vient du POM
- `formats` vient du YAML
- Fusion correcte

---

## 📚 Ressources

- **JSON Schema**: `.deploy-manifest.schema.json`
- **Exemples**: `examples/` directory
- **Guide de test**: `SCHEMA_TESTING_GUIDE.md`
- **Checklist**: `TASKS_CHECKLIST.md`

---

## 🚀 Prochaines Étapes

1. Intégrer dans `GenerateDescriptorMojo`
2. Créer le goal `validate-config`
3. Mettre à jour la documentation utilisateur
4. Ajouter des tests d'intégration
5. Publier le schéma JSON sur GitHub

---

## 💡 Conseils

- **Commencez petit**: Intégrez d'abord juste le `ConfigurationResolver`
- **Testez progressivement**: Un paramètre à la fois
- **Gardez la rétrocompatibilité**: Les anciennes configurations doivent continuer à fonctionner
- **Loggez tout**: Les utilisateurs doivent voir d'où viennent les valeurs
- **Documentez**: Mettez à jour le README avec les nouveaux exemples

---

**Bon courage pour l'intégration !** 🎉
