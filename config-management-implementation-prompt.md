# Configuration Système - Maven Deploy Manifest Plugin
## Prompt d'Implémentation pour IA

---

## 🎯 Objectif

Implémenter un système de configuration simple et flexible basé sur un fichier YAML.

**L'utilisateur doit pouvoir :**
1. Créer un fichier `.deploy-manifest.yml` à la racine de son projet
2. Avoir de l'**autocomplétion** dans son éditeur (VS Code, IntelliJ)
3. Voir les **erreurs en temps réel** dans son éditeur (soulignement rouge)
4. Override la config via **variables d'environnement** ou **ligne de commande**

**Exemple concret :** Si l'utilisateur tape `profile: toto` dans le YAML, l'éditeur doit souligner "toto" en rouge et afficher "Valeur invalide. Valeurs autorisées : basic, standard, full, ci".

---

## 📋 Ce Que l'Utilisateur Doit Vivre

### Scénario 1 : Premier Fichier de Config

**L'utilisateur crée `.deploy-manifest.yml` :**

```yaml
profile: standard
```

**Exécute :**
```bash
mvn deploy-manifest:generate
```

**Résultat attendu :**
- ✅ Génère `target/deployment-manifest-report.json`
- ✅ Génère `target/deployment-manifest-report.html`
- ✅ Pas d'erreur
- ✅ Message dans les logs : "[INFO] Using profile: standard (from .deploy-manifest.yml)"

---

### Scénario 2 : Autocomplétion dans l'Éditeur

**L'utilisateur tape dans VS Code :**

```yaml
prof
```

**Ce qui se passe (temps réel) :**
```
Suggestions (Ctrl+Espace) :
  ▸ profile
```

**L'utilisateur continue :**

```yaml
profile: sta
```

**Ce qui se passe :**
```
Suggestions :
  ▸ standard
```

**L'utilisateur tape Ctrl+Espace après "profile: " :**
```
Suggestions :
  ▸ basic
  ▸ standard
  ▸ full
  ▸ ci
```

---

### Scénario 3 : Détection d'Erreur Temps Réel

**L'utilisateur tape :**

```yaml
profile: toto
```

**Ce qui se passe (immédiatement) :**
- ❌ Le mot "toto" est souligné en rouge
- 💡 Tooltip au survol : "Value is not accepted. Allowed values: basic, standard, full, ci"

**L'utilisateur tape :**

```yaml
output:
  format: pdf
```

**Ce qui se passe :**
- ❌ "pdf" souligné en rouge
- 💡 Tooltip : "Value is not accepted. Allowed values: json, yaml, html, xml"

---

### Scénario 4 : Documentation au Survol

**L'utilisateur hover (survol souris) sur "profile" :**

```
Tooltip affiché :
───────────────────────────────────────
profile (string)

Predefined configuration profile

Values:
  • basic    - Minimal (JSON only)
  • standard - JSON + HTML + dependency tree
  • full     - All formats + metadata
  • ci       - Optimized for CI/CD

Default: basic
───────────────────────────────────────
```

---

### Scénario 5 : Config Détaillée

**L'utilisateur crée :**

```yaml
profile: standard

output:
  formats:
    - json
    - html
  archive: true

dependencies:
  tree:
    enabled: true
    depth: 5

metadata:
  licenses: true
```

**Exécute :**
```bash
mvn deploy-manifest:generate
```

**Résultat attendu :**
- ✅ Génère JSON + HTML
- ✅ Crée une archive ZIP
- ✅ Inclut l'arbre de dépendances (profondeur 5)
- ✅ Inclut les licences

---

### Scénario 6 : Override via Variables d'Environnement

**Fichier `.deploy-manifest.yml` :**
```yaml
profile: basic
```

**L'utilisateur exécute :**
```bash
export MANIFEST_OUTPUT_FORMATS=json,html
export MANIFEST_DEPENDENCIES_TREE_ENABLED=true
mvn deploy-manifest:generate
```

**Résultat attendu :**
- ✅ Génère JSON + HTML (pas juste JSON du profil basic)
- ✅ Arbre de dépendances activé
- ✅ Les variables d'environnement **écrasent** le profil
- 📝 Log : "[INFO] Output formats overridden by environment variable: json, html"

---

### Scénario 7 : Override via Ligne de Commande

**Fichier `.deploy-manifest.yml` :**
```yaml
dependencies:
  tree:
    depth: 3
```

**L'utilisateur exécute :**
```bash
mvn deploy-manifest:generate -Dmanifest.dependencies.tree.depth=10
```

**Résultat attendu :**
- ✅ Profondeur = 10 (pas 3)
- ✅ La ligne de commande **gagne** sur tout
- 📝 Log : "[INFO] Tree depth overridden by command line: 10"

---

### Scénario 8 : Validation des Erreurs

**L'utilisateur tape :**

```yaml
profile: standarrd   # Faute de frappe
output:
  format: pdf       # Format invalide
dependencies:
  tree:
    depth: 50       # Hors limites
```

**Exécute :**
```bash
mvn deploy-manifest:generate
```

**Résultat attendu :**

```
[ERROR] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[ERROR] Configuration validation failed (3 errors)
[ERROR] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[ERROR] 
[ERROR] Error 1: Invalid profile
[ERROR] ────────────────────────────────────────────────────────
[ERROR]   File: .deploy-manifest.yml
[ERROR]   Line: 1
[ERROR]   Value: 'standarrd'
[ERROR]   
[ERROR]   Allowed values: basic, standard, full, ci
[ERROR]   
[ERROR]   💡 Did you mean?
[ERROR]      → standard (1 character difference)
[ERROR] 
[ERROR] Error 2: Invalid output format
[ERROR] ────────────────────────────────────────────────────────
[ERROR]   File: .deploy-manifest.yml
[ERROR]   Line: 3
[ERROR]   Field: output.format
[ERROR]   Value: 'pdf'
[ERROR]   
[ERROR]   Allowed values: json, yaml, html, xml
[ERROR]   
[ERROR]   📝 Example:
[ERROR]      output:
[ERROR]        formats:
[ERROR]          - json
[ERROR]          - html
[ERROR] 
[ERROR] Error 3: Value out of range
[ERROR] ────────────────────────────────────────────────────────
[ERROR]   File: .deploy-manifest.yml
[ERROR]   Line: 6
[ERROR]   Field: dependencies.tree.depth
[ERROR]   Value: 50
[ERROR]   
[ERROR]   Valid range: 1-10
[ERROR]   Reason: Deep trees can cause performance issues
[ERROR]   
[ERROR]   💡 Suggested value: 5
[ERROR] 
[ERROR] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[ERROR] Fix these errors and try again.
[ERROR] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[BUILD FAILURE]
```

---

### Scénario 9 : Voir la Config Résolue

**Fichier `.deploy-manifest.yml` :**
```yaml
profile: standard
output:
  archive: false
```

**L'utilisateur exécute :**
```bash
export MANIFEST_OUTPUT_ARCHIVE=true
export MANIFEST_DEPENDENCIES_TREE_DEPTH=7
mvn deploy-manifest:validate-config -Dmanifest.metadata.licenses=true
```

**Résultat attendu :**

```
[INFO] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[INFO]  Configuration Resolution
[INFO] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[INFO] 
[INFO] Configuration file: .deploy-manifest.yml ✓ FOUND
[INFO] Profile: standard
[INFO] 
[INFO] Resolved Configuration:
[INFO] 
[INFO] ┌────────────────────────────────┬─────────────┬─────────────────────┐
[INFO] │ Option                         │ Value       │ Source              │
[INFO] ├────────────────────────────────┼─────────────┼─────────────────────┤
[INFO] │ profile                        │ standard    │ .yml file           │
[INFO] │ output.directory               │ target      │ default             │
[INFO] │ output.formats                 │ json, html  │ profile "standard"  │
[INFO] │ output.archive                 │ true        │ ENV: MANIFEST_*  🌍 │
[INFO] │ dependencies.tree.enabled      │ true        │ profile "standard"  │
[INFO] │ dependencies.tree.depth        │ 7           │ ENV: MANIFEST_*  🌍 │
[INFO] │ metadata.licenses              │ true        │ -D property      ⌨️  │
[INFO] └────────────────────────────────┴─────────────┴─────────────────────┘
[INFO] 
[INFO] Priority Order (highest first):
[INFO]   1. ⌨️  Command line (-Dmanifest.*)
[INFO]   2. 🌍 Environment variables (MANIFEST_*)
[INFO]   3. 📄 Configuration file (.deploy-manifest.yml)
[INFO]   4. 📦 Profile defaults
[INFO]   5. 🔧 Plugin defaults
[INFO] 
[INFO] ✅ Configuration is VALID
[INFO] 
[INFO] To generate the manifest:
[INFO]   mvn deploy-manifest:generate
[INFO] 
[INFO] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 📄 Structure Complète du Fichier YAML

### Tous les Champs Possibles

```yaml
# .deploy-manifest.yml
# Configuration du Maven Deploy Manifest Plugin

# Profil prédéfini (basic, standard, full, ci)
profile: standard

# Configuration de la sortie
output:
  directory: target
  filename: deployment-manifest-report
  formats:
    - json
    - html
  archive: false
  attach: false

# Configuration des dépendances
dependencies:
  tree:
    enabled: true
    depth: 3
    includeTransitive: true
  analysis:
    enabled: false
    healthThreshold: 80
    filterSpringStarters: true
    filterLombok: true

# Métadonnées à inclure
metadata:
  licenses: false
  properties: false
  plugins: false
  checksums: false

# Configuration Git
git:
  fetch: auto
  includeUncommitted: false
  depth: 50

# Configuration Docker
docker:
  autoDetect: true
  registries:
    - docker.io
    - ghcr.io

# Options de debug
verbose: false
dryRun: false
```

### Valeurs Autorisées (Validation)

| Champ | Type | Valeurs Autorisées | Défaut |
|-------|------|-------------------|--------|
| `profile` | enum | `basic`, `standard`, `full`, `ci` | `basic` |
| `output.directory` | string | Chemin valide | `target` |
| `output.filename` | string | Nom sans extension | `deployment-manifest-report` |
| `output.formats` | array | `json`, `yaml`, `html`, `xml` | `[json]` |
| `output.archive` | boolean | `true`, `false` | `false` |
| `output.attach` | boolean | `true`, `false` | `false` |
| `dependencies.tree.enabled` | boolean | `true`, `false` | `false` |
| `dependencies.tree.depth` | integer | 1 à 10 | `3` |
| `dependencies.tree.includeTransitive` | boolean | `true`, `false` | `true` |
| `dependencies.analysis.enabled` | boolean | `true`, `false` | `false` |
| `dependencies.analysis.healthThreshold` | integer | 0 à 100 | `80` |
| `git.fetch` | enum | `auto`, `always`, `never` | `auto` |
| `git.depth` | integer | 1 à 1000 | `50` |

---

## 🎨 Comment Activer la Validation dans l'Éditeur

### Principe : JSON Schema

**JSON Schema** est un standard qui permet de :
- ✅ Définir la structure d'un fichier YAML/JSON
- ✅ Spécifier les valeurs autorisées
- ✅ Ajouter de la documentation
- ✅ Les éditeurs l'utilisent pour valider en temps réel

### Étape 1 : Créer le Fichier de Schema

**Créer** `.deploy-manifest.schema.json` dans le repository GitHub du plugin

**Structure du schema (les éditeurs utilisent ce fichier pour valider) :**

- Définir chaque propriété avec son type (`string`, `boolean`, `integer`, `array`)
- Pour les enums : lister les valeurs autorisées
- Pour les integers : définir `minimum` et `maximum`
- Ajouter des `description` pour la documentation inline
- Utiliser `additionalProperties: false` pour détecter les champs inconnus

**Exemple de structure attendue dans le schema :**

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Deploy Manifest Configuration",
  "type": "object",
  "properties": {
    "profile": {
      "type": "string",
      "enum": ["basic", "standard", "full", "ci"],
      "default": "basic",
      "description": "Predefined profile:\n• basic: Minimal (JSON only)\n• standard: JSON + HTML + dependency tree\n• full: All formats + metadata\n• ci: Optimized for CI/CD"
    },
    "output": {
      "type": "object",
      "properties": {
        "formats": {
          "type": "array",
          "items": {
            "type": "string",
            "enum": ["json", "yaml", "html", "xml"]
          },
          "uniqueItems": true,
          "description": "Output format(s)"
        },
        "archive": {
          "type": "boolean",
          "default": false,
          "description": "Create a ZIP archive"
        }
      }
    },
    "dependencies": {
      "type": "object",
      "properties": {
        "tree": {
          "type": "object",
          "properties": {
            "depth": {
              "type": "integer",
              "minimum": 1,
              "maximum": 10,
              "default": 3,
              "description": "Maximum depth of dependency tree (1-10)"
            }
          }
        }
      }
    }
  },
  "additionalProperties": false
}
```

### Étape 2 : Héberger le Schema

**Option 1 : GitHub Raw (recommandé)**

Publier le fichier dans le repo, accessible via :
```
https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json
```

**Option 2 : Site Web**

Héberger sur le site du plugin :
```
https://tourem.github.io/deploy-manifest-plugin/schema.json
```

### Étape 3 : L'Utilisateur Active la Validation

**Méthode 1 : Référence dans le YAML (recommandé)**

L'utilisateur ajoute cette ligne **en haut** de son `.deploy-manifest.yml` :

```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json

profile: standard
output:
  formats:
    - json
```

**Méthode 2 : Configuration VS Code**

L'utilisateur crée `.vscode/settings.json` :

```json
{
  "yaml.schemas": {
    "https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json": ".deploy-manifest.yml"
  }
}
```

**Méthode 3 : Configuration IntelliJ**

IntelliJ détecte automatiquement la ligne `# yaml-language-server: $schema=...`

---

## 🔍 Exemples de Validation dans l'Éditeur

### Exemple 1 : Valeur Enum Invalide

**L'utilisateur tape :**
```yaml
profile: standar
```

**L'éditeur affiche :**
```
┌────────────────────────────────────────────┐
│ ⚠️ Value is not accepted                  │
│                                            │
│ Allowed values:                            │
│   • basic                                  │
│   • standard                               │
│   • full                                   │
│   • ci                                     │
└────────────────────────────────────────────┘
```

**Bonus :** L'éditeur peut suggérer "standard" (correction automatique)

---

### Exemple 2 : Type Incorrect

**L'utilisateur tape :**
```yaml
output:
  archive: yes
```

**L'éditeur affiche :**
```
┌────────────────────────────────────────────┐
│ ⚠️ Incorrect type                         │
│                                            │
│ Expected: boolean                          │
│ Got: string                                │
│                                            │
│ Use: true or false                         │
└────────────────────────────────────────────┘
```

---

### Exemple 3 : Valeur Hors Limites

**L'utilisateur tape :**
```yaml
dependencies:
  tree:
    depth: 50
```

**L'éditeur affiche :**
```
┌────────────────────────────────────────────┐
│ ⚠️ Value out of range                     │
│                                            │
│ Expected: 1-10                             │
│ Got: 50                                    │
│                                            │
│ Deep trees can cause performance issues    │
└────────────────────────────────────────────┘
```

---

### Exemple 4 : Champ Inconnu

**L'utilisateur tape :**
```yaml
output:
  arhive: true
```

**L'éditeur affiche :**
```
┌────────────────────────────────────────────┐
│ ⚠️ Property arhive is not allowed         │
│                                            │
│ Did you mean?                              │
│   • archive                                │
│                                            │
│ Valid properties:                          │
│   • directory                              │
│   • filename                               │
│   • formats                                │
│   • archive                                │
│   • attach                                 │
└────────────────────────────────────────────┘
```

---

### Exemple 5 : Autocomplétion

**L'utilisateur tape :**
```yaml
output:
  
```

**Puis Ctrl+Espace, l'éditeur suggère :**
```
┌────────────────────────────────────────────┐
│ Suggestions:                               │
│                                            │
│ ▸ directory                                │
│   Output directory path                    │
│                                            │
│ ▸ filename                                 │
│   Output filename (without extension)      │
│                                            │
│ ▸ formats                                  │
│   Output format(s)                         │
│                                            │
│ ▸ archive                                  │
│   Create a ZIP archive                     │
│                                            │
│ ▸ attach                                   │
│   Attach archive to Maven build            │
└────────────────────────────────────────────┘
```

---

## 🔄 Variables d'Environnement

### Convention de Nommage

**Règle :** Préfixe `MANIFEST_` + chemin en `UPPER_SNAKE_CASE`

| Chemin YAML | Variable d'Environnement |
|-------------|-------------------------|
| `profile` | `MANIFEST_PROFILE` |
| `output.directory` | `MANIFEST_OUTPUT_DIRECTORY` |
| `output.formats` | `MANIFEST_OUTPUT_FORMATS` |
| `dependencies.tree.enabled` | `MANIFEST_DEPENDENCIES_TREE_ENABLED` |
| `dependencies.tree.depth` | `MANIFEST_DEPENDENCIES_TREE_DEPTH` |
| `metadata.licenses` | `MANIFEST_METADATA_LICENSES` |

### Exemples d'Utilisation

**Exemple 1 : CI/CD GitHub Actions**

```yaml
# .github/workflows/build.yml
jobs:
  build:
    runs-on: ubuntu-latest
    env:
      MANIFEST_PROFILE: ci
      MANIFEST_OUTPUT_ARCHIVE: true
      MANIFEST_OUTPUT_ATTACH: true
    steps:
      - uses: actions/checkout@v3
      - name: Generate Manifest
        run: mvn deploy-manifest:generate
```

**Exemple 2 : Override Local**

```bash
# Terminal
export MANIFEST_VERBOSE=true
export MANIFEST_DEPENDENCIES_TREE_DEPTH=10
mvn deploy-manifest:generate
```

**Exemple 3 : Docker**

```dockerfile
ENV MANIFEST_PROFILE=production
ENV MANIFEST_METADATA_LICENSES=true
RUN mvn deploy-manifest:generate
```

### Conversion des Types

| Type | Exemple Variable | Valeur Résultante |
|------|------------------|-------------------|
| Boolean | `MANIFEST_VERBOSE=true` | `true` (boolean) |
| Integer | `MANIFEST_DEPENDENCIES_TREE_DEPTH=5` | `5` (int) |
| String | `MANIFEST_OUTPUT_DIRECTORY=/tmp` | `/tmp` (string) |
| Array | `MANIFEST_OUTPUT_FORMATS=json,html,yaml` | `["json", "html", "yaml"]` |
| Enum | `MANIFEST_PROFILE=full` | `full` (validé) |

---

## 📊 Ordre de Priorité

### Table de Priorité

| Niveau | Source | Symbole | Exemple |
|--------|--------|---------|---------|
| **1** (plus haute) | Ligne de commande | ⌨️ | `-Dmanifest.output.archive=true` |
| **2** | Variables d'environnement | 🌍 | `MANIFEST_OUTPUT_ARCHIVE=true` |
| **3** | Fichier `.deploy-manifest.yml` | 📄 | `output.archive: true` |
| **4** | Profil (dans le YAML) | 📦 | `profile: full` → définit plusieurs options |
| **5** | Configuration `pom.xml` | 🔨 | `<configuration><archive>true</archive>` |
| **6** (plus basse) | Défauts du plugin | 🔧 | Valeurs codées en dur |

### Exemple de Résolution Complexe

**Fichier `.deploy-manifest.yml` :**
```yaml
profile: standard               # Niveau 4
output:
  formats:
    - json
  archive: false                # Niveau 3
dependencies:
  tree:
    depth: 3                    # Niveau 3
```

**Variables d'environnement :**
```bash
export MANIFEST_OUTPUT_FORMATS=yaml,html      # Niveau 2
export MANIFEST_OUTPUT_ARCHIVE=true           # Niveau 2
```

**Ligne de commande :**
```bash
mvn deploy-manifest:generate -Dmanifest.dependencies.tree.depth=10   # Niveau 1
```

**Configuration résolue :**
```
profile = "standard"                    (Niveau 3 - YAML)
output.formats = ["yaml", "html"]       (Niveau 2 - ENV gagne sur YAML)
output.archive = true                   (Niveau 2 - ENV gagne sur YAML)
dependencies.tree.depth = 10            (Niveau 1 - CLI gagne sur tout)
dependencies.tree.enabled = true        (Niveau 4 - Profil "standard")
```

---

## 🎯 Profils Prédéfinis

### Profil `basic` (défaut)

**Objectif :** Génération minimale, rapide

**Options activées :**
```yaml
output:
  formats: [json]
```

**Tout le reste est désactivé.**

---

### Profil `standard`

**Objectif :** Documentation pour l'équipe

**Options activées :**
```yaml
output:
  formats: [json, html]
dependencies:
  tree:
    enabled: true
    depth: 3
```

---

### Profil `full`

**Objectif :** Analyse complète

**Options activées :**
```yaml
output:
  formats: [json, yaml, html]
dependencies:
  tree:
    enabled: true
    depth: 5
  analysis:
    enabled: true
metadata:
  licenses: true
  properties: true
  plugins: true
  checksums: true
```

---

### Profil `ci`

**Objectif :** Optimisé pour CI/CD

**Options activées :**
```yaml
output:
  formats: [json]
  archive: true
  attach: true
dependencies:
  tree:
    enabled: true
    depth: 2
git:
  fetch: always
```

---

## ✅ Tests de Validation

### Test 1 : Fichier Basique

**Given :**
```yaml
profile: standard
```

**When :**
```bash
mvn deploy-manifest:generate
```

**Then :**
- ✅ 2 fichiers générés : JSON + HTML
- ✅ Arbre de dépendances activé (profondeur 3)
- ✅ Pas d'erreur

---

### Test 2 : Override Environnement

**Given :**
```yaml
profile: basic
```

**When :**
```bash
export MANIFEST_OUTPUT_FORMATS=html,yaml
mvn deploy-manifest:generate
```

**Then :**
- ✅ 2 fichiers : HTML + YAML (pas JSON)
- ✅ Log indique : "Output formats overridden by environment variable"

---

### Test 3 : Override Ligne de Commande

**Given :**
```yaml
dependencies:
  tree:
    depth: 3
```

**When :**
```bash
mvn deploy-manifest:generate -Dmanifest.dependencies.tree.depth=8
```

**Then :**
- ✅ Profondeur = 8
- ✅ Log indique : "Tree depth overridden by command line"

---

### Test 4 : Erreur - Profil Invalide

**Given :**
```yaml
profile: invalid
```

**When :**
```bash
mvn deploy-manifest:generate
```

**Then :**
- ❌ Build échoue
- ✅ Message : "Invalid profile 'invalid'"
- ✅ Liste : "Allowed values: basic, standard, full, ci"

---

### Test 5 : Erreur - Valeur Hors Limites

**Given :**
```yaml
dependencies:
  tree:
    depth: 50
```

**When :**
```bash
mvn deploy-manifest:generate
```

**Then :**
- ❌ Build échoue
- ✅ Message : "Value 50 out of range"
- ✅ Indique : "Valid range: 1-10"

---

### Test 6 : Goal validate-config

**Given :**
```yaml
profile: standard
output:
  archive: true
```

**When :**
```bash
export MANIFEST_DEPENDENCIES_TREE_DEPTH=5
mvn deploy-manifest:validate-config -Dmanifest.metadata.licenses=true
```

**Then :**
- ✅ Affiche tableau avec 3 colonnes : Option, Value, Source
- ✅ Indique archive=true (YAML), depth=5 (ENV), licenses=true (CLI)
- ✅ Message final : "✅ Configuration is VALID"

---

### Test 7 : Validation dans l'Éditeur

**Given :** VS Code avec extension YAML

**When :** L'utilisateur crée `.deploy-manifest.yml` avec :
```yaml
# yaml-language-server: $schema=https://...schema.json

profile: 
```

**Then :**
- ✅ Autocomplétion suggère : basic, standard, full, ci

---

### Test 8 : Détection Erreur Éditeur

**Given :** VS Code avec extension YAML

**When :** L'utilisateur tape :
```yaml
profile: toto
```

**Then :**
- ✅ "toto" souligné en rouge
- ✅ Tooltip : "Value not accepted. Allowed: basic, standard, full, ci"

---

### Test 9 : Ordre de Priorité Complexe

**Given :**
```yaml
profile: basic
output:
  formats: [json]
  archive: false
dependencies:
  tree:
    depth: 3
```

**When :**
```bash
export MANIFEST_OUTPUT_FORMATS=yaml
export MANIFEST_OUTPUT_ARCHIVE=true
mvn deploy-manifest:generate \
  -Dmanifest.dependencies.tree.depth=7 \
  -Dmanifest.metadata.licenses=true
```

**Then :**
- ✅ Formats = yaml (ENV)
- ✅ Archive = true (ENV)
- ✅ Depth = 7 (CLI)
- ✅ Licenses = true (CLI)
- ✅ Profile = basic (YAML, pas overridé)

---

### Test 10 : Fichier Absent

**Given :** Pas de fichier `.deploy-manifest.yml`

**When :**
```bash
mvn deploy-manifest:generate
```

**Then :**
- ✅ Utilise profil "basic" par défaut
- ✅ Génère JSON seulement
- ✅ Log : "[INFO] No configuration file found, using defaults"
- ✅ Pas d'erreur

---

## 📚 Documentation Utilisateur

### Section à Ajouter au README

**Titre :** "🔧 Configuration File"

**Contenu :**

```markdown
## 🔧 Configuration File

Create a `.deploy-manifest.yml` file at your project root:

```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json

profile: standard

output:
  formats:
    - json
    - html

dependencies:
  tree:
    enabled: true
    depth: 5

metadata:
  licenses: true
```

### Configuration Priority

Options are resolved in this order (highest priority first):

1. **Command line** (`-Dmanifest.*`)
2. **Environment variables** (`MANIFEST_*`)
3. **Configuration file** (`.deploy-manifest.yml`)
4. **Profile defaults**
5. **Plugin defaults**

### Environment Variables

Set environment variables with `MANIFEST_` prefix:

```bash
export MANIFEST_PROFILE=full
export MANIFEST_OUTPUT_FORMATS=json,html
mvn deploy-manifest:generate
```

### Validate Configuration

See your resolved configuration:

```bash
mvn deploy-manifest:validate-config
```

### Editor Support

Get autocompletion and validation:

1. **VS Code:** Install [YAML extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml)
2. **IntelliJ:** Built-in support

Add this line to your `.deploy-manifest.yml`:

```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json
```
```

---

## 🚀 Checklist d'Implémentation

### Phase 1 : Parsing YAML
- [ ] Lire `.deploy-manifest.yml` (utiliser SnakeYAML ou Jackson)
- [ ] Parser en structure Java
- [ ] Gérer fichier absent (utiliser défauts)

### Phase 2 : Variables d'Environnement
- [ ] Lire toutes variables `MANIFEST_*`
- [ ] Convertir `MANIFEST_OUTPUT_FORMAT` → `output.format`
- [ ] Gérer arrays (`json,html` → `["json", "html"]`)
- [ ] Gérer booleans (`true` → boolean)

### Phase 3 : Ligne de Commande
- [ ] Lire properties `-Dmanifest.*`
- [ ] Convertir en structure Java

### Phase 4 : Fusion (Merge)
- [ ] Implémenter ordre de priorité (1-6)
- [ ] Ne pas écraser avec null
- [ ] Pour arrays : remplacer complètement

### Phase 5 : Validation
- [ ] Valider enums (profile, formats, git.fetch)
- [ ] Valider ranges (depth 1-10, healthThreshold 0-100)
- [ ] Valider types (boolean, int, string)
- [ ] Générer messages d'erreur clairs
- [ ] Implémenter "Did you mean?" (distance Levenshtein)

### Phase 6 : JSON Schema
- [ ] Créer `.deploy-manifest.schema.json`
- [ ] Publier sur GitHub raw ou site web
- [ ] Tester dans VS Code et IntelliJ

### Phase 7 : Goal validate-config
- [ ] Créer nouveau Mojo
- [ ] Afficher tableau (Option | Value | Source)
- [ ] Afficher symboles (⌨️ 🌍 📄 📦 🔧)

### Phase 8 : Tests
- [ ] Tests unitaires (parsing, merge, validation)
- [ ] Tests d'intégration (10 scénarios ci-dessus)
- [ ] Test manuel dans éditeur

### Phase 9 : Documentation
- [ ] Mettre à jour README
- [ ] Ajouter exemples
- [ ] Expliquer JSON Schema

---

## 🎯 Résultat Attendu Final

### Avant

```bash
mvn deploy-manifest:generate \
  -Dmanifest.generateHtml=true \
  -Dmanifest.includeDependencyTree=true \
  -Dmanifest.dependencyTreeDepth=5 \
  -Dmanifest.includeLicenses=true
```

**Problèmes :**
- ❌ Trop long
- ❌ Difficile à retenir
- ❌ Pas de validation
- ❌ Pas réutilisable

### Après

**Fichier `.deploy-manifest.yml` :**
```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json

profile: standard
dependencies:
  tree:
    depth: 5
metadata:
  licenses: true
```

**Commande :**
```bash
mvn deploy-manifest:generate
```

**Avantages :**
- ✅ Simple (un fichier)
- ✅ Validation temps réel dans l'éditeur
- ✅ Autocomplétion
- ✅ Réutilisable
- ✅ Versionnable
- ✅ Override possible

---

## 💡 Points Clés

1. **JSON Schema = Clé de l'UX** - Validation temps réel dans l'éditeur
2. **Ordre de priorité clair** - CLI > ENV > YAML > Défauts
3. **Messages d'erreur utiles** - "Did you mean?" + exemples
4. **Zero breaking changes** - Les anciennes options `-D` fonctionnent toujours
5. **Documentation inline** - Tooltips dans l'éditeur

---

## ✅ Commencez !

Suivez la checklist dans l'ordre. **Commencez par le JSON Schema** - c'est ce qui fera la différence pour l'utilisateur !

**Bon courage ! 🚀**
