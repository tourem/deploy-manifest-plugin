# Plan d'Implémentation - Système de Configuration YAML

**Branche**: `feature/yaml-config-management`  
**Date de création**: 24 novembre 2025  
**Objectif**: Implémenter un système de configuration flexible basé sur YAML avec validation temps réel

---

## 📋 Vue d'Ensemble

### Objectifs Principaux

1. ✅ Fichier de configuration `.deploy-manifest.yml` à la racine du projet
2. ✅ Autocomplétion dans les éditeurs (VS Code, IntelliJ)
3. ✅ Validation temps réel avec erreurs soulignées
4. ✅ Override via variables d'environnement et ligne de commande
5. ✅ Messages d'erreur clairs et utiles
6. ✅ Rétrocompatibilité avec les options `-D` existantes

### Ordre de Priorité des Configurations

```
1. ⌨️  Ligne de commande (-Dmanifest.*)
2. 🌍 Variables d'environnement (MANIFEST_*)
3. 📄 Fichier .deploy-manifest.yml
4. 📦 Profil (défini dans le YAML)
5. 🔨 Configuration pom.xml
6. 🔧 Défauts du plugin
```

---

## 🎯 Phase 1: JSON Schema (Priorité HAUTE)

**Objectif**: Créer le schéma JSON pour la validation dans les éditeurs

### Tâches

- [ ] **1.1** Créer le fichier `.deploy-manifest.schema.json`
  - Structure complète avec tous les champs
  - Définir les types (string, boolean, integer, array, object)
  - Ajouter les enums pour les valeurs autorisées
  - Définir les ranges (min/max) pour les integers
  - Ajouter les descriptions pour chaque propriété
  - Utiliser `additionalProperties: false` pour détecter les champs inconnus

- [ ] **1.2** Définir les propriétés principales
  ```json
  - profile (enum: basic, standard, full, ci)
  - output (object)
    - directory (string)
    - filename (string)
    - formats (array of enum: json, yaml, html, xml)
    - archive (boolean)
    - attach (boolean)
  - dependencies (object)
    - tree (object)
      - enabled (boolean)
      - depth (integer, 1-10)
      - includeTransitive (boolean)
    - analysis (object)
      - enabled (boolean)
      - healthThreshold (integer, 0-100)
      - filterSpringStarters (boolean)
      - filterLombok (boolean)
  - metadata (object)
    - licenses (boolean)
    - properties (boolean)
    - plugins (boolean)
    - checksums (boolean)
  - git (object)
    - fetch (enum: auto, always, never)
    - includeUncommitted (boolean)
    - depth (integer, 1-1000)
  - docker (object)
    - autoDetect (boolean)
    - registries (array of string)
  - verbose (boolean)
  - dryRun (boolean)
  ```

- [ ] **1.3** Ajouter des exemples dans le schéma
  - Exemple complet de configuration
  - Exemples pour chaque section

- [ ] **1.4** Publier le schéma
  - Committer le fichier dans le repo
  - Vérifier l'URL GitHub raw
  - Tester l'accès au schéma

**Fichiers à créer**:
- `.deploy-manifest.schema.json`

**Tests**:
- Valider le schéma JSON avec un validateur en ligne
- Tester dans VS Code avec l'extension YAML
- Tester dans IntelliJ IDEA

---

## 🎯 Phase 2: Modèle de Configuration Java

**Objectif**: Créer les classes Java pour représenter la configuration

### Tâches

- [ ] **2.1** Créer la classe `ManifestConfiguration`
  - Package: `io.github.tourem.maven.descriptor.config`
  - Propriétés correspondant au schéma JSON
  - Getters/Setters
  - Builder pattern
  - Validation interne

- [ ] **2.2** Créer les sous-classes de configuration
  - `OutputConfiguration`
  - `DependenciesConfiguration`
    - `DependencyTreeConfiguration`
    - `DependencyAnalysisConfiguration`
  - `MetadataConfiguration`
  - `GitConfiguration`
  - `DockerConfiguration`

- [ ] **2.3** Créer l'enum `ManifestProfile`
  - BASIC, STANDARD, FULL, CI
  - Méthode `getDefaultConfiguration()` pour chaque profil

- [ ] **2.4** Ajouter les annotations de validation
  - Utiliser Bean Validation (javax.validation)
  - `@NotNull`, `@Min`, `@Max`, `@Pattern`
  - Créer des validateurs personnalisés si nécessaire

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/ManifestConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/OutputConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/DependenciesConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/DependencyTreeConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/DependencyAnalysisConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/MetadataConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/GitConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/DockerConfiguration.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/ManifestProfile.java`

**Tests**:
- Tests unitaires pour chaque classe
- Tests de validation

---

## 🎯 Phase 3: Parsing YAML

**Objectif**: Lire et parser le fichier `.deploy-manifest.yml`

### Tâches

- [ ] **3.1** Ajouter la dépendance YAML
  - Choisir entre SnakeYAML ou Jackson YAML
  - Ajouter au `pom.xml` du core

- [ ] **3.2** Créer `YamlConfigurationLoader`
  - Package: `io.github.tourem.maven.descriptor.config.loader`
  - Méthode `load(File yamlFile)` → `ManifestConfiguration`
  - Gérer le fichier absent (retourner null ou config vide)
  - Gérer les erreurs de parsing (YAML invalide)

- [ ] **3.3** Gérer les erreurs de parsing
  - Capturer les exceptions YAML
  - Convertir en messages d'erreur clairs
  - Indiquer la ligne et la colonne de l'erreur

- [ ] **3.4** Tester avec différents fichiers YAML
  - Fichier minimal (juste profile)
  - Fichier complet (tous les champs)
  - Fichier avec erreurs de syntaxe
  - Fichier absent

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/loader/YamlConfigurationLoader.java`

**Tests**:
- Tests unitaires avec différents fichiers YAML
- Tests d'erreurs (YAML invalide, fichier absent)

---

## 🎯 Phase 4: Variables d'Environnement

**Objectif**: Lire et convertir les variables d'environnement `MANIFEST_*`

### Tâches

- [ ] **4.1** Créer `EnvironmentConfigurationLoader`
  - Package: `io.github.tourem.maven.descriptor.config.loader`
  - Méthode `load()` → `ManifestConfiguration`
  - Lire toutes les variables `MANIFEST_*`

- [ ] **4.2** Implémenter la conversion de noms
  - `MANIFEST_PROFILE` → `profile`
  - `MANIFEST_OUTPUT_DIRECTORY` → `output.directory`
  - `MANIFEST_DEPENDENCIES_TREE_ENABLED` → `dependencies.tree.enabled`
  - Utiliser une stratégie de mapping

- [ ] **4.3** Implémenter la conversion de types
  - String → String (direct)
  - "true"/"false" → Boolean
  - "5" → Integer
  - "json,html,yaml" → List<String>
  - Gérer les erreurs de conversion

- [ ] **4.4** Créer des tests
  - Test avec différentes variables
  - Test de conversion de types
  - Test avec valeurs invalides

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/loader/EnvironmentConfigurationLoader.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/converter/TypeConverter.java`

**Tests**:
- Tests unitaires pour la conversion de noms
- Tests unitaires pour la conversion de types
- Tests d'intégration avec variables d'environnement

---

## 🎯 Phase 5: Ligne de Commande

**Objectif**: Lire et convertir les propriétés `-Dmanifest.*`

### Tâches

- [ ] **5.1** Créer `CommandLineConfigurationLoader`
  - Package: `io.github.tourem.maven.descriptor.config.loader`
  - Méthode `load(Properties properties)` → `ManifestConfiguration`
  - Filtrer les propriétés qui commencent par `manifest.`

- [ ] **5.2** Implémenter la conversion de noms
  - `manifest.profile` → `profile`
  - `manifest.output.directory` → `output.directory`
  - `manifest.dependencies.tree.depth` → `dependencies.tree.depth`

- [ ] **5.3** Réutiliser le convertisseur de types
  - Utiliser `TypeConverter` de la phase 4
  - Gérer les erreurs de conversion

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/loader/CommandLineConfigurationLoader.java`

**Tests**:
- Tests unitaires avec différentes propriétés
- Tests de conversion

---

## 🎯 Phase 6: Fusion des Configurations (Merge)

**Objectif**: Fusionner les configurations selon l'ordre de priorité

### Tâches

- [ ] **6.1** Créer `ConfigurationMerger`
  - Package: `io.github.tourem.maven.descriptor.config.merger`
  - Méthode `merge(List<ManifestConfiguration>)` → `ManifestConfiguration`
  - Implémenter l'ordre de priorité (CLI > ENV > YAML > Profile > Defaults)

- [ ] **6.2** Implémenter la logique de fusion
  - Ne pas écraser avec null
  - Pour les primitives: prendre la première valeur non-null
  - Pour les arrays: remplacer complètement (pas de merge)
  - Pour les objets: merger récursivement

- [ ] **6.3** Tracker la source de chaque valeur
  - Créer `ConfigurationSource` enum (CLI, ENV, YAML, PROFILE, DEFAULT)
  - Stocker la source pour chaque propriété
  - Utiliser pour l'affichage dans `validate-config`

- [ ] **6.4** Appliquer les profils
  - Charger la configuration par défaut du profil
  - Fusionner avec les autres sources
  - Le profil a la priorité la plus basse (après YAML)

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/merger/ConfigurationMerger.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/ConfigurationSource.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/ResolvedConfiguration.java`

**Tests**:
- Tests unitaires pour chaque règle de fusion
- Tests d'intégration avec toutes les sources
- Tests de l'ordre de priorité

---

## 🎯 Phase 7: Validation

**Objectif**: Valider la configuration et générer des messages d'erreur clairs

### Tâches

- [ ] **7.1** Créer `ConfigurationValidator`
  - Package: `io.github.tourem.maven.descriptor.config.validator`
  - Méthode `validate(ManifestConfiguration)` → `ValidationResult`
  - Collecter toutes les erreurs (pas juste la première)

- [ ] **7.2** Implémenter les validations
  - Valider les enums (profile, formats, git.fetch)
  - Valider les ranges (depth 1-10, healthThreshold 0-100)
  - Valider les types (boolean, int, string)
  - Valider les chemins de fichiers
  - Valider les URLs

- [ ] **7.3** Créer des messages d'erreur clairs
  - Indiquer le fichier et la ligne (si YAML)
  - Indiquer le champ et la valeur invalide
  - Lister les valeurs autorisées
  - Suggérer des corrections ("Did you mean?")
  - Fournir des exemples

- [ ] **7.4** Implémenter "Did you mean?"
  - Utiliser l'algorithme de distance de Levenshtein
  - Suggérer la valeur la plus proche
  - Seuil de similarité configurable

- [ ] **7.5** Créer `ValidationResult` et `ValidationError`
  - Stocker toutes les erreurs
  - Méthodes pour formater les erreurs
  - Support pour différents formats (console, HTML, JSON)

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/validator/ConfigurationValidator.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/validator/ValidationResult.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/validator/ValidationError.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/validator/LevenshteinDistance.java`

**Tests**:
- Tests unitaires pour chaque type de validation
- Tests de "Did you mean?"
- Tests de formatage des erreurs

---

## 🎯 Phase 8: Intégration dans le Mojo

**Objectif**: Intégrer le système de configuration dans `GenerateDescriptorMojo`

### Tâches

- [ ] **8.1** Créer `ConfigurationResolver`
  - Package: `io.github.tourem.maven.descriptor.config`
  - Orchestrer toutes les étapes (load, merge, validate)
  - Méthode `resolve(File projectDir, Properties systemProperties)` → `ResolvedConfiguration`

- [ ] **8.2** Modifier `GenerateDescriptorMojo`
  - Ajouter un champ `private ConfigurationResolver configResolver`
  - Dans `execute()`, résoudre la configuration en premier
  - Utiliser la configuration résolue au lieu des paramètres Maven
  - Garder les paramètres Maven pour la rétrocompatibilité

- [ ] **8.3** Logger les informations de configuration
  - "[INFO] Configuration file: .deploy-manifest.yml ✓ FOUND"
  - "[INFO] Using profile: standard (from .deploy-manifest.yml)"
  - "[INFO] Output formats overridden by environment variable: json, html"
  - "[INFO] Tree depth overridden by command line: 10"

- [ ] **8.4** Gérer les erreurs de validation
  - Afficher toutes les erreurs avec un format clair
  - Utiliser des séparateurs visuels (━━━━━━━)
  - Faire échouer le build si validation échoue
  - Fournir des suggestions de correction

**Fichiers à modifier**:
- `deploy-manifest-plugin/src/main/java/io/github/tourem/maven/plugin/GenerateDescriptorMojo.java`

**Fichiers à créer**:
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/ConfigurationResolver.java`

**Tests**:
- Tests d'intégration avec le Mojo
- Tests avec différentes configurations
- Tests d'erreurs

---

## 🎯 Phase 9: Goal validate-config

**Objectif**: Créer un nouveau goal pour afficher la configuration résolue

### Tâches

- [ ] **9.1** Créer `ValidateConfigMojo`
  - Package: `io.github.tourem.maven.plugin`
  - Annoter avec `@Mojo(name = "validate-config")`
  - Résoudre la configuration
  - Afficher un tableau avec 3 colonnes: Option, Value, Source

- [ ] **9.2** Implémenter l'affichage du tableau
  - Utiliser des caractères Unicode pour le tableau (┌─┬─┐)
  - Afficher les symboles pour chaque source (⌨️ 🌍 📄 📦 🔧)
  - Aligner les colonnes correctement
  - Colorier si le terminal le supporte

- [ ] **9.3** Afficher les informations supplémentaires
  - Fichier de configuration trouvé ou non
  - Profil utilisé
  - Ordre de priorité
  - Statut de validation (✅ VALID ou ❌ INVALID)

- [ ] **9.4** Ajouter des exemples d'utilisation
  - Dans le log de sortie
  - Dans la documentation

**Fichiers à créer**:
- `deploy-manifest-plugin/src/main/java/io/github/tourem/maven/plugin/ValidateConfigMojo.java`
- `deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/formatter/ConfigurationTableFormatter.java`

**Tests**:
- Tests du Mojo
- Tests de formatage du tableau

---

## 🎯 Phase 10: Tests d'Intégration

**Objectif**: Tester tous les scénarios décrits dans le document

### Tâches

- [ ] **10.1** Test 1: Fichier basique
  - Créer `.deploy-manifest.yml` avec `profile: standard`
  - Exécuter `mvn deploy-manifest:generate`
  - Vérifier JSON + HTML générés

- [ ] **10.2** Test 2: Override environnement
  - Fichier avec `profile: basic`
  - Variable `MANIFEST_OUTPUT_FORMATS=html,yaml`
  - Vérifier HTML + YAML générés (pas JSON)

- [ ] **10.3** Test 3: Override ligne de commande
  - Fichier avec `dependencies.tree.depth: 3`
  - Commande avec `-Dmanifest.dependencies.tree.depth=8`
  - Vérifier profondeur = 8

- [ ] **10.4** Test 4: Erreur - Profil invalide
  - Fichier avec `profile: invalid`
  - Vérifier échec du build
  - Vérifier message d'erreur clair

- [ ] **10.5** Test 5: Erreur - Valeur hors limites
  - Fichier avec `dependencies.tree.depth: 50`
  - Vérifier échec du build
  - Vérifier message avec range valide

- [ ] **10.6** Test 6: Goal validate-config
  - Fichier + ENV + CLI
  - Exécuter `mvn deploy-manifest:validate-config`
  - Vérifier tableau avec 3 colonnes
  - Vérifier sources correctes

- [ ] **10.7** Test 7: Autocomplétion éditeur
  - Créer fichier avec référence au schéma
  - Tester dans VS Code
  - Vérifier autocomplétion

- [ ] **10.8** Test 8: Détection erreur éditeur
  - Taper `profile: toto`
  - Vérifier soulignement rouge
  - Vérifier tooltip

- [ ] **10.9** Test 9: Ordre de priorité complexe
  - YAML + ENV + CLI
  - Vérifier que chaque source gagne au bon niveau

- [ ] **10.10** Test 10: Fichier absent
  - Pas de fichier `.deploy-manifest.yml`
  - Vérifier utilisation des défauts
  - Vérifier log approprié

**Fichiers à créer**:
- `deploy-manifest-plugin/src/test/java/io/github/tourem/maven/plugin/integration/YamlConfigurationIT.java`
- Fichiers de test YAML dans `src/test/resources/`

---

## 🎯 Phase 11: Documentation

**Objectif**: Documenter le nouveau système de configuration

### Tâches

- [ ] **11.1** Mettre à jour le README
  - Ajouter section "🔧 Configuration File"
  - Expliquer l'ordre de priorité
  - Montrer des exemples
  - Expliquer les variables d'environnement
  - Documenter le goal `validate-config`

- [ ] **11.2** Créer un guide de configuration
  - Fichier `CONFIGURATION_GUIDE.md`
  - Tous les champs disponibles
  - Exemples pour chaque section
  - Cas d'usage courants

- [ ] **11.3** Documenter le JSON Schema
  - Comment activer la validation dans VS Code
  - Comment activer la validation dans IntelliJ
  - Troubleshooting

- [ ] **11.4** Créer des exemples
  - `.deploy-manifest.yml` minimal
  - `.deploy-manifest.yml` complet
  - Exemples pour chaque profil
  - Exemples de CI/CD (GitHub Actions, GitLab CI)

- [ ] **11.5** Mettre à jour le CHANGELOG
  - Nouvelle fonctionnalité: Configuration YAML
  - Breaking changes (si applicable)
  - Migration guide

**Fichiers à modifier**:
- `README.md`
- `CHANGELOG.md`

**Fichiers à créer**:
- `CONFIGURATION_GUIDE.md`
- `examples/.deploy-manifest-minimal.yml`
- `examples/.deploy-manifest-complete.yml`
- `examples/.deploy-manifest-basic-profile.yml`
- `examples/.deploy-manifest-standard-profile.yml`
- `examples/.deploy-manifest-full-profile.yml`
- `examples/.deploy-manifest-ci-profile.yml`

---

## 🎯 Phase 12: Rétrocompatibilité

**Objectif**: S'assurer que les anciennes options `-D` fonctionnent toujours

### Tâches

- [ ] **12.1** Tester toutes les anciennes options
  - Créer des tests pour chaque option `-Dmanifest.*`
  - Vérifier qu'elles fonctionnent toujours
  - Vérifier qu'elles ont la priorité la plus haute

- [ ] **12.2** Ajouter des warnings de dépréciation (optionnel)
  - Si on veut encourager l'utilisation du YAML
  - "[WARN] Using -Dmanifest.* is deprecated. Consider using .deploy-manifest.yml"
  - Configurable (peut être désactivé)

- [ ] **12.3** Documenter la migration
  - Guide de migration des options `-D` vers YAML
  - Script de conversion (optionnel)

**Tests**:
- Tests de rétrocompatibilité
- Tests de migration

---

## 📊 Résumé des Livrables

### Fichiers à Créer (Core)

```
deploy-manifest-core/src/main/java/io/github/tourem/maven/descriptor/config/
├── ManifestConfiguration.java
├── OutputConfiguration.java
├── DependenciesConfiguration.java
├── DependencyTreeConfiguration.java
├── DependencyAnalysisConfiguration.java
├── MetadataConfiguration.java
├── GitConfiguration.java
├── DockerConfiguration.java
├── ManifestProfile.java
├── ConfigurationSource.java
├── ResolvedConfiguration.java
├── ConfigurationResolver.java
├── loader/
│   ├── YamlConfigurationLoader.java
│   ├── EnvironmentConfigurationLoader.java
│   └── CommandLineConfigurationLoader.java
├── converter/
│   └── TypeConverter.java
├── merger/
│   └── ConfigurationMerger.java
├── validator/
│   ├── ConfigurationValidator.java
│   ├── ValidationResult.java
│   ├── ValidationError.java
│   └── LevenshteinDistance.java
└── formatter/
    └── ConfigurationTableFormatter.java
```

### Fichiers à Créer (Plugin)

```
deploy-manifest-plugin/src/main/java/io/github/tourem/maven/plugin/
└── ValidateConfigMojo.java
```

### Fichiers à Créer (Racine)

```
.deploy-manifest.schema.json
CONFIGURATION_GUIDE.md
examples/
├── .deploy-manifest-minimal.yml
├── .deploy-manifest-complete.yml
├── .deploy-manifest-basic-profile.yml
├── .deploy-manifest-standard-profile.yml
├── .deploy-manifest-full-profile.yml
└── .deploy-manifest-ci-profile.yml
```

### Fichiers à Modifier

```
README.md
CHANGELOG.md
deploy-manifest-plugin/src/main/java/io/github/tourem/maven/plugin/GenerateDescriptorMojo.java
pom.xml (ajouter dépendance YAML)
```

---

## 🎯 Ordre d'Exécution Recommandé

### Sprint 1: Fondations (2-3 jours)
1. Phase 1: JSON Schema ⭐ **PRIORITÉ**
2. Phase 2: Modèle de Configuration Java
3. Phase 3: Parsing YAML

### Sprint 2: Sources de Configuration (2-3 jours)
4. Phase 4: Variables d'Environnement
5. Phase 5: Ligne de Commande
6. Phase 6: Fusion des Configurations

### Sprint 3: Validation (2 jours)
7. Phase 7: Validation

### Sprint 4: Intégration (2 jours)
8. Phase 8: Intégration dans le Mojo
9. Phase 9: Goal validate-config

### Sprint 5: Tests et Documentation (2-3 jours)
10. Phase 10: Tests d'Intégration
11. Phase 11: Documentation
12. Phase 12: Rétrocompatibilité

**Durée totale estimée**: 10-13 jours

---

## ✅ Critères de Succès

### Fonctionnels

- [ ] L'utilisateur peut créer `.deploy-manifest.yml` et l'utiliser
- [ ] Autocomplétion fonctionne dans VS Code et IntelliJ
- [ ] Erreurs soulignées en rouge dans l'éditeur
- [ ] Variables d'environnement `MANIFEST_*` fonctionnent
- [ ] Options `-Dmanifest.*` fonctionnent (rétrocompatibilité)
- [ ] Ordre de priorité respecté (CLI > ENV > YAML > Profile > Default)
- [ ] Messages d'erreur clairs avec suggestions
- [ ] Goal `validate-config` affiche la configuration résolue

### Techniques

- [ ] Tous les tests passent (unitaires + intégration)
- [ ] Couverture de code > 80%
- [ ] Pas de régression sur les fonctionnalités existantes
- [ ] Performance acceptable (< 100ms pour charger la config)
- [ ] Documentation complète et à jour

### UX

- [ ] Expérience fluide dans l'éditeur
- [ ] Messages d'erreur utiles (pas juste "invalid value")
- [ ] Suggestions de correction ("Did you mean?")
- [ ] Exemples fournis dans les messages d'erreur
- [ ] Logs clairs indiquant la source de chaque valeur

---

## 🚀 Prêt à Commencer !

**Prochaine étape**: Commencer par la Phase 1 (JSON Schema)

C'est la phase la plus importante car elle détermine l'expérience utilisateur dans l'éditeur. Une fois le schéma créé, les utilisateurs pourront immédiatement bénéficier de l'autocomplétion et de la validation.

**Commande pour démarrer**:
```bash
# Créer le fichier JSON Schema
touch .deploy-manifest.schema.json

# Ouvrir dans l'éditeur
code .deploy-manifest.schema.json
```

Bon courage ! 🎉
