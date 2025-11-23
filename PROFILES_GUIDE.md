# 📋 Guide des Profils - Maven Deploy Manifest Plugin

## 🎯 Vue d'Ensemble

Les **profils** simplifient l'utilisation du plugin en fournissant des configurations prédéfinies pour les cas d'usage courants. Au lieu de spécifier de nombreuses options, vous choisissez simplement un profil adapté à votre besoin.

---

## 🔖 Profils Disponibles

### 1. **basic** (par défaut)

**Usage** : Développement local, génération rapide

**Génère** :
- ✅ `deployment-manifest-report.json`

**Inclut** :
- ✅ Informations de base du module
- ✅ External dependencies (DB, Cache, MQ, Services)
- ✅ Testing info (tests count, quality gate)
- ✅ Build metrics (duration, size, timestamp)

**N'inclut PAS** :
- ❌ HTML
- ❌ YAML
- ❌ Dependency tree
- ❌ Licenses
- ❌ Properties
- ❌ Plugins

**Commande** :
```bash
# Profil par défaut - pas besoin de le spécifier
mvn deploy-manifest:generate
```

---

### 2. **standard**

**Usage** : Développement avec visualisation, documentation d'équipe

**Génère** :
- ✅ `deployment-manifest-report.json`
- ✅ `deployment-manifest-report.html` (avec UI moderne)

**Inclut** :
- ✅ Tout de **basic**
- ✅ HTML avec sections visuelles
- ✅ Dependency tree (profondeur = 2)

**N'inclut PAS** :
- ❌ YAML
- ❌ Licenses
- ❌ Properties
- ❌ Plugins

**Commande** :
```bash
mvn deploy-manifest:generate -Dmanifest.profile=standard
```

---

### 3. **full**

**Usage** : Documentation complète, releases, audits

**Génère** :
- ✅ `deployment-manifest-report.json`
- ✅ `deployment-manifest-report.yaml`
- ✅ `deployment-manifest-report.html`

**Inclut** :
- ✅ Tout de **standard**
- ✅ YAML format
- ✅ Licenses complètes
- ✅ Properties Maven
- ✅ Plugins Maven
- ✅ Dependency tree complet (profondeur = 5)

**Commande** :
```bash
mvn deploy-manifest:generate -Dmanifest.profile=full
```

---

### 4. **ci**

**Usage** : Pipelines CI/CD, builds automatisés

**Génère** :
- ✅ `deployment-manifest-report.json`
- ✅ `deployment-manifest-report.html`
- ✅ Archive ZIP avec tous les rapports

**Inclut** :
- ✅ Tout de **standard**
- ✅ Dependency tree (profondeur = 3)
- ✅ Archive ZIP attachée au build
- ✅ Compression activée
- ✅ Tous les rapports inclus dans l'archive

**Commande** :
```bash
mvn deploy-manifest:generate -Dmanifest.profile=ci
```

---

## 🎨 Personnalisation des Profils

### Principe

Les profils définissent des **valeurs par défaut**, mais vous pouvez les **surcharger** avec des options spécifiques.

### Ordre de Priorité

1. **Options CLI explicites** (priorité maximale)
2. **Profil sélectionné**
3. **Valeurs par défaut du plugin**

### Exemples de Personnalisation

#### Exemple 1 : Standard + Licenses

```bash
# Profil standard + ajout des licenses
mvn deploy-manifest:generate \
  -Dmanifest.profile=standard \
  -Dmanifest.includeLicenses=true
```

**Résultat** :
- JSON + HTML (du profil standard)
- Dependency tree depth=2 (du profil standard)
- **+ Licenses** (override)

---

#### Exemple 2 : Basic + HTML

```bash
# Profil basic + génération HTML
mvn deploy-manifest:generate \
  -Dmanifest.profile=basic \
  -Dmanifest.generateHtml=true
```

**Résultat** :
- JSON (du profil basic)
- **+ HTML** (override)
- Pas de dependency tree (du profil basic)

---

#### Exemple 3 : Full + Dependency Tree Limité

```bash
# Profil full mais avec dependency tree moins profond
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.dependencyTree.maxDepth=2
```

**Résultat** :
- JSON + YAML + HTML (du profil full)
- Licenses + Properties + Plugins (du profil full)
- **Dependency tree depth=2** (override au lieu de 5)

---

#### Exemple 4 : CI + YAML

```bash
# Profil CI + ajout du format YAML
mvn deploy-manifest:generate \
  -Dmanifest.profile=ci \
  -Dmanifest.exportFormat=both
```

**Résultat** :
- JSON + **YAML** (override) + HTML
- Archive ZIP (du profil ci)
- Tous les rapports inclus (du profil ci)

---

## 📊 Tableau Comparatif

| Fonctionnalité | basic | standard | full | ci |
|----------------|-------|----------|------|-----|
| **JSON** | ✅ | ✅ | ✅ | ✅ |
| **HTML** | ❌ | ✅ | ✅ | ✅ |
| **YAML** | ❌ | ❌ | ✅ | ❌ |
| **External Deps** | ✅ | ✅ | ✅ | ✅ |
| **Testing Info** | ✅ | ✅ | ✅ | ✅ |
| **Build Metrics** | ✅ | ✅ | ✅ | ✅ |
| **Dependency Tree** | ❌ | ✅ (depth=2) | ✅ (depth=5) | ✅ (depth=3) |
| **Licenses** | ❌ | ❌ | ✅ | ❌ |
| **Properties** | ❌ | ❌ | ✅ | ❌ |
| **Plugins** | ❌ | ❌ | ✅ | ❌ |
| **Archive ZIP** | ❌ | ❌ | ❌ | ✅ |
| **Attach to Build** | ❌ | ❌ | ❌ | ✅ |
| **Compression** | ❌ | ❌ | ❌ | ✅ |

---

## 🚀 Cas d'Usage Recommandés

### Développement Local
```bash
# Rapide, juste le JSON
mvn deploy-manifest:generate
```

### Review de Code / Documentation d'Équipe
```bash
# Avec HTML pour visualisation
mvn deploy-manifest:generate -Dmanifest.profile=standard
```

### Release / Documentation Officielle
```bash
# Tout inclus
mvn deploy-manifest:generate -Dmanifest.profile=full
```

### Pipeline CI/CD (GitHub Actions, Jenkins, GitLab CI)
```bash
# Archive pour artefacts
mvn deploy-manifest:generate -Dmanifest.profile=ci
```

### Audit de Sécurité / Conformité
```bash
# Full avec focus sur licenses
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.includeLicenses=true
```

---

## 🔧 Options Disponibles pour Override

### Formats de Sortie
```bash
-Dmanifest.exportFormat=json|yaml|both
-Dmanifest.generateHtml=true|false
```

### Contenu
```bash
-Dmanifest.includeDependencyTree=true|false
-Dmanifest.dependencyTree.maxDepth=<number>
-Dmanifest.includeLicenses=true|false
-Dmanifest.includeProperties=true|false
-Dmanifest.includePlugins=true|false
```

### Archive
```bash
-Dmanifest.format=zip|tar.gz|tar.bz2
-Dmanifest.attach=true|false
-Dmanifest.compress=true|false
-Dmanifest.includeAllReports=true|false
```

### Autres
```bash
-Dmanifest.outputFile=<filename>
-Dmanifest.outputDirectory=<path>
-Dmanifest.prettyPrint=true|false
```

---

## 💡 Conseils et Bonnes Pratiques

### 1. Commencez Simple
```bash
# Utilisez basic pour découvrir
mvn deploy-manifest:generate
```

### 2. Ajoutez HTML pour Visualiser
```bash
# Passez à standard quand vous êtes à l'aise
mvn deploy-manifest:generate -Dmanifest.profile=standard
```

### 3. Utilisez CI dans vos Pipelines
```yaml
# GitHub Actions example
- name: Generate Deployment Manifest
  run: mvn deploy-manifest:generate -Dmanifest.profile=ci
```

### 4. Personnalisez Progressivement
```bash
# Ajoutez des options au fur et à mesure
mvn deploy-manifest:generate \
  -Dmanifest.profile=standard \
  -Dmanifest.includeLicenses=true
```

### 5. Documentez Votre Configuration
```xml
<!-- Dans votre pom.xml -->
<plugin>
  <groupId>io.github.tourem</groupId>
  <artifactId>deploy-manifest-plugin</artifactId>
  <configuration>
    <profile>standard</profile>
    <includeLicenses>true</includeLicenses>
  </configuration>
</plugin>
```

---

## 📖 Exemples Complets

### Exemple 1 : Projet Simple
```bash
# Juste le JSON
mvn deploy-manifest:generate
```

### Exemple 2 : Projet avec Documentation
```bash
# JSON + HTML + dependency tree
mvn deploy-manifest:generate -Dmanifest.profile=standard
```

### Exemple 3 : Release Officielle
```bash
# Tout inclus avec archive
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.format=zip
```

### Exemple 4 : CI/CD Pipeline
```bash
# Archive attachée pour artefacts
mvn deploy-manifest:generate -Dmanifest.profile=ci
```

### Exemple 5 : Audit Complet
```bash
# Full avec toutes les options
mvn deploy-manifest:generate \
  -Dmanifest.profile=full \
  -Dmanifest.dependencyTree.maxDepth=10 \
  -Dmanifest.dependencyTree.includeOptional=true \
  -Dmanifest.dependencyTree.includeTest=true
```

---

## ❓ FAQ

### Q: Puis-je créer mon propre profil ?
**R:** Les profils sont prédéfinis, mais vous pouvez créer une configuration dans votre `pom.xml` :
```xml
<configuration>
  <profile>standard</profile>
  <includeLicenses>true</includeLicenses>
  <includeProperties>true</includeProperties>
</configuration>
```

### Q: Comment savoir quel profil est utilisé ?
**R:** Le plugin affiche le profil au démarrage :
```
[INFO] Using profile: standard
```

### Q: Les options CLI ont-elles priorité sur le profil ?
**R:** Oui, toujours. Les options CLI surchargent le profil.

### Q: Puis-je désactiver un profil ?
**R:** Oui, utilisez `basic` (c'est le profil minimal) ou spécifiez toutes les options manuellement.

---

## 🎉 Résumé

Les profils simplifient l'utilisation du plugin :

1. **basic** : Rapide et minimal
2. **standard** : Équilibré avec HTML
3. **full** : Complet pour documentation
4. **ci** : Optimisé pour CI/CD

Vous pouvez toujours **personnaliser** avec des options supplémentaires !

---

**Version** : 2.8.0+  
**Date** : Novembre 2025
