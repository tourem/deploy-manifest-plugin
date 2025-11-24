# Checklist des Tâches - Configuration YAML

**Branche**: `feature/yaml-config-management`  
**Mise à jour**: 24 novembre 2025

---

## 🎯 Sprint 1: Fondations (2-3 jours)

### Phase 1: JSON Schema ⭐ PRIORITÉ
- [x] 1.1 Créer `.deploy-manifest.schema.json`
- [x] 1.2 Définir toutes les propriétés avec types et enums
- [x] 1.3 Ajouter descriptions et exemples
- [ ] 1.4 Publier et tester dans VS Code/IntelliJ

### Phase 2: Modèle Java
- [x] 2.1 Créer `ManifestConfiguration.java`
- [x] 2.2 Créer sous-classes (Output, Dependencies, Metadata, Git, Docker)
- [x] 2.3 Créer enum `ManifestProfile`
- [x] 2.4 Ajouter annotations de validation

### Phase 3: Parsing YAML
- [x] 3.1 Ajouter dépendance YAML au pom.xml
- [x] 3.2 Créer `YamlConfigurationLoader`
- [x] 3.3 Gérer erreurs de parsing
- [x] 3.4 Tests avec différents fichiers YAML

---

## 🎯 Sprint 2: Sources de Configuration (2-3 jours)

### Phase 4: Variables d'Environnement
- [x] 4.1 Créer `EnvironmentConfigurationLoader`
- [x] 4.2 Implémenter conversion de noms (MANIFEST_* → config)
- [x] 4.3 Implémenter conversion de types
- [ ] 4.4 Tests unitaires

### Phase 5: Ligne de Commande
- [x] 5.1 Créer `CommandLineConfigurationLoader`
- [x] 5.2 Implémenter conversion de noms (manifest.* → config)
- [x] 5.3 Réutiliser convertisseur de types
- [ ] 5.4 Tests unitaires

### Phase 6: Fusion
- [ ] 6.1 Créer `ConfigurationMerger`
- [ ] 6.2 Implémenter logique de fusion (ordre de priorité)
- [ ] 6.3 Tracker source de chaque valeur
- [ ] 6.4 Appliquer profils
- [ ] 6.5 Tests de fusion

---

## 🎯 Sprint 3: Validation (2 jours)

### Phase 7: Validation
- [ ] 7.1 Créer `ConfigurationValidator`
- [ ] 7.2 Implémenter validations (enums, ranges, types)
- [ ] 7.3 Créer messages d'erreur clairs
- [ ] 7.4 Implémenter "Did you mean?" (Levenshtein)
- [ ] 7.5 Créer `ValidationResult` et `ValidationError`
- [ ] 7.6 Tests de validation

---

## 🎯 Sprint 4: Intégration (2 jours)

### Phase 8: Intégration Mojo
- [ ] 8.1 Créer `ConfigurationResolver`
- [ ] 8.2 Modifier `GenerateDescriptorMojo`
- [ ] 8.3 Logger informations de configuration
- [ ] 8.4 Gérer erreurs de validation
- [ ] 8.5 Tests d'intégration

### Phase 9: Goal validate-config
- [ ] 9.1 Créer `ValidateConfigMojo`
- [ ] 9.2 Implémenter affichage tableau
- [ ] 9.3 Afficher informations supplémentaires
- [ ] 9.4 Ajouter exemples d'utilisation
- [ ] 9.5 Tests du Mojo

---

## 🎯 Sprint 5: Tests et Documentation (2-3 jours)

### Phase 10: Tests d'Intégration
- [ ] 10.1 Test: Fichier basique
- [ ] 10.2 Test: Override environnement
- [ ] 10.3 Test: Override ligne de commande
- [ ] 10.4 Test: Erreur profil invalide
- [ ] 10.5 Test: Erreur valeur hors limites
- [ ] 10.6 Test: Goal validate-config
- [ ] 10.7 Test: Autocomplétion éditeur
- [ ] 10.8 Test: Détection erreur éditeur
- [ ] 10.9 Test: Ordre de priorité complexe
- [ ] 10.10 Test: Fichier absent

### Phase 11: Documentation
- [ ] 11.1 Mettre à jour README
- [ ] 11.2 Créer CONFIGURATION_GUIDE.md
- [ ] 11.3 Documenter JSON Schema
- [ ] 11.4 Créer exemples YAML
- [ ] 11.5 Mettre à jour CHANGELOG

### Phase 12: Rétrocompatibilité
- [ ] 12.1 Tester toutes anciennes options -D
- [ ] 12.2 Ajouter warnings dépréciation (optionnel)
- [ ] 12.3 Documenter migration

---

## 📊 Progression Globale

```
Sprint 1: [▓▓▓▓▓▓▓▓▓▓░░] 11/12 tâches (92%)
Sprint 2: [▓▓▓▓▓▓░░░░░░░] 6/13 tâches (46%)
Sprint 3: [ ] 0/6 tâches
Sprint 4: [ ] 0/9 tâches
Sprint 5: [ ] 0/25 tâches

TOTAL: [▓▓▓░░░░░░░] 17/65 tâches (26%)
```

---

## 🎯 Tâche Actuelle

**✅ Complété**: 
- Sprint 1: 11/12 tâches (92%)
- Phase 4: Variables d'environnement (3/4 tâches) - 75%
- Phase 5: Ligne de commande (3/4 tâches) - 75%

**Sprint 2: 46% complété** (6/13 tâches)

**Fichiers créés (Phases 4 & 5)**:
- `TypeConverter.java` - Utilitaire de conversion
  * toBoolean() - Supporte true/false, yes/no, 1/0
  * toInteger() - Parse string vers integer
  * toString() - Trim string
  * toStringList() - Parse "a,b,c" → ["a", "b", "c"]
  * envVarToPropertyPath() - MANIFEST_OUTPUT_DIRECTORY → output.directory
  * cmdLineToPropertyPath() - manifest.output.directory → output.directory

- `EnvironmentConfigurationLoader.java` (300+ lignes)
  * Charge depuis variables MANIFEST_*
  * Conversion UPPER_SNAKE_CASE → lower.dot.case
  * Applique toutes les propriétés
  * Gestion d'erreurs avec warnings

- `CommandLineConfigurationLoader.java`
  * Charge depuis propriétés manifest.*
  * Réutilise EnvironmentConfigurationLoader
  * Conversion automatique vers format ENV

**Fonctionnalités**:
- ✅ Lecture variables d'environnement MANIFEST_*
- ✅ Lecture propriétés ligne de commande manifest.*
- ✅ Conversion de noms (UPPER_SNAKE_CASE ↔ lower.dot.case)
- ✅ Conversion de types (Boolean, Integer, String, List)
- ✅ Support valeurs multiples (json,html,yaml)
- ✅ Gestion erreurs avec logs

**Prochaine étape**: Tests unitaires pour Phase 4 et 5

---

## 📝 Notes

- Commencer par le JSON Schema (Phase 1) - c'est la clé de l'UX
- Tester dans VS Code après chaque ajout au schéma
- Garder la rétrocompatibilité en tête
- Documenter au fur et à mesure

---

## ✅ Critères de Validation

Avant de passer au sprint suivant:
- [ ] Tous les tests du sprint passent
- [ ] Code review effectué
- [ ] Documentation à jour
- [ ] Pas de régression

---

**Dernière mise à jour**: 24 novembre 2025  
**Prochaine revue**: Fin du Sprint 1
