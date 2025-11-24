# JSON Schema Testing Guide

**Phase**: 1.4 - Test JSON Schema in Editors  
**Date**: 24 novembre 2025

---

## 🎯 Objectif

Vérifier que le JSON Schema fonctionne correctement dans les éditeurs (VS Code et IntelliJ IDEA) pour fournir:
- ✅ Autocomplétion
- ✅ Validation temps réel
- ✅ Documentation inline
- ✅ Détection d'erreurs

---

## 📋 Tests à Effectuer

### Test 1: Autocomplétion des Propriétés

**Dans VS Code ou IntelliJ:**

1. Ouvrir `examples/.deploy-manifest-minimal.yml`
2. Ajouter une nouvelle ligne après `profile: basic`
3. Taper quelques lettres (ex: `out`)
4. Appuyer sur `Ctrl+Space` (ou `Cmd+Space` sur Mac)

**Résultat attendu:**
```
Suggestions affichées:
  ▸ output
```

5. Sélectionner `output` et appuyer sur `Enter`
6. Taper `:` et `Enter`
7. Taper quelques lettres (ex: `for`)
8. Appuyer sur `Ctrl+Space`

**Résultat attendu:**
```
Suggestions affichées:
  ▸ formats
  ▸ filename
```

---

### Test 2: Autocomplétion des Valeurs Enum

**Dans l'éditeur:**

1. Ouvrir un nouveau fichier `.deploy-manifest-test.yml`
2. Ajouter la ligne de schéma:
   ```yaml
   # yaml-language-server: $schema=../.deploy-manifest.schema.json
   ```
3. Taper:
   ```yaml
   profile: 
   ```
4. Appuyer sur `Ctrl+Space` après les deux points

**Résultat attendu:**
```
Suggestions affichées:
  ▸ basic
  ▸ standard
  ▸ full
  ▸ ci
```

---

### Test 3: Validation - Valeur Enum Invalide

**Dans l'éditeur:**

1. Taper:
   ```yaml
   profile: toto
   ```

**Résultat attendu:**
- ❌ Le mot "toto" est souligné en rouge (ou avec une couleur d'erreur)
- 💡 Au survol, un tooltip s'affiche:
  ```
  Value is not accepted. Allowed values: basic, standard, full, ci
  ```

---

### Test 4: Validation - Type Incorrect

**Dans l'éditeur:**

1. Taper:
   ```yaml
   output:
     archive: yes
   ```

**Résultat attendu:**
- ❌ "yes" souligné (devrait être `true` ou `false`)
- 💡 Tooltip: "Incorrect type. Expected: boolean"

---

### Test 5: Validation - Valeur Hors Limites

**Dans l'éditeur:**

1. Taper:
   ```yaml
   dependencies:
     tree:
       depth: 50
   ```

**Résultat attendu:**
- ❌ "50" souligné
- 💡 Tooltip: "Value must be between 1 and 10" (ou similaire)

---

### Test 6: Validation - Propriété Inconnue

**Dans l'éditeur:**

1. Taper:
   ```yaml
   output:
     arhive: true
   ```
   (faute de frappe: "arhive" au lieu de "archive")

**Résultat attendu:**
- ❌ "arhive" souligné
- 💡 Tooltip: "Property arhive is not allowed" ou "Unknown property"

---

### Test 7: Documentation au Survol

**Dans l'éditeur:**

1. Taper:
   ```yaml
   profile: standard
   ```
2. Passer la souris sur le mot "profile"

**Résultat attendu:**
- 💡 Tooltip s'affiche avec la description:
  ```
  Predefined configuration profile:
  • basic - Minimal (JSON only)
  • standard - JSON + HTML + dependency tree
  • full - All formats + metadata
  • ci - Optimized for CI/CD
  ```

---

### Test 8: Autocomplétion dans un Tableau

**Dans l'éditeur:**

1. Taper:
   ```yaml
   output:
     formats:
       - 
   ```
2. Appuyer sur `Ctrl+Space` après le tiret

**Résultat attendu:**
```
Suggestions affichées:
  ▸ json
  ▸ yaml
  ▸ html
  ▸ xml
```

---

### Test 9: Validation - Valeurs Dupliquées dans Tableau

**Dans l'éditeur:**

1. Taper:
   ```yaml
   output:
     formats:
       - json
       - html
       - json
   ```

**Résultat attendu:**
- ❌ Le deuxième "json" souligné
- 💡 Tooltip: "Duplicate items are not allowed" (car `uniqueItems: true`)

---

### Test 10: Validation Complète d'un Fichier

**Dans l'éditeur:**

1. Ouvrir `examples/.deploy-manifest-complete.yml`
2. Vérifier qu'il n'y a aucune erreur soulignée
3. Modifier quelques valeurs pour tester la validation

**Résultat attendu:**
- ✅ Aucune erreur dans le fichier d'origine
- ❌ Erreurs apparaissent quand on met des valeurs invalides

---

## 🔧 Configuration Requise

### VS Code

1. **Installer l'extension YAML**:
   - Nom: "YAML" par Red Hat
   - ID: `redhat.vscode-yaml`
   - [Lien Marketplace](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-yaml)

2. **Vérifier la configuration**:
   - Ouvrir Settings (Cmd+,)
   - Chercher "yaml schemas"
   - Vérifier que l'extension est activée

3. **Redémarrer VS Code** après installation

### IntelliJ IDEA

1. **Support intégré** - Pas d'extension nécessaire
2. **Vérifier**:
   - Settings → Languages & Frameworks → Schemas and DTDs → JSON Schema Mappings
   - Devrait détecter automatiquement le schéma via le commentaire `# yaml-language-server`

---

## ✅ Checklist de Validation

Cocher chaque test réussi:

- [ ] Test 1: Autocomplétion des propriétés
- [ ] Test 2: Autocomplétion des valeurs enum
- [ ] Test 3: Validation - Valeur enum invalide
- [ ] Test 4: Validation - Type incorrect
- [ ] Test 5: Validation - Valeur hors limites
- [ ] Test 6: Validation - Propriété inconnue
- [ ] Test 7: Documentation au survol
- [ ] Test 8: Autocomplétion dans un tableau
- [ ] Test 9: Validation - Valeurs dupliquées
- [ ] Test 10: Validation complète d'un fichier

**Éditeurs testés:**
- [ ] VS Code
- [ ] IntelliJ IDEA

---

## 🐛 Troubleshooting

### Le schéma n'est pas détecté

**Solution 1**: Vérifier la ligne de commentaire
```yaml
# yaml-language-server: $schema=https://raw.githubusercontent.com/tourem/deploy-manifest-plugin/main/.deploy-manifest.schema.json
```

**Solution 2**: Utiliser un chemin relatif pour les tests locaux
```yaml
# yaml-language-server: $schema=../.deploy-manifest.schema.json
```

**Solution 3**: Configuration manuelle dans VS Code
- Créer `.vscode/settings.json`:
```json
{
  "yaml.schemas": {
    "./.deploy-manifest.schema.json": ".deploy-manifest*.yml"
  }
}
```

### L'autocomplétion ne fonctionne pas

1. Vérifier que l'extension YAML est installée (VS Code)
2. Redémarrer l'éditeur
3. Vérifier qu'il n'y a pas d'erreurs de syntaxe YAML
4. Vérifier que le fichier a l'extension `.yml` ou `.yaml`

### Les erreurs ne s'affichent pas

1. Vérifier que le schéma JSON est valide (utiliser un validateur en ligne)
2. Vérifier les logs de l'extension YAML (VS Code: Output → YAML Support)
3. Essayer de fermer et rouvrir le fichier

---

## 📊 Résultats Attendus

Après avoir complété tous les tests:

**✅ Autocomplétion**: Fonctionne pour propriétés et valeurs  
**✅ Validation**: Détecte les erreurs en temps réel  
**✅ Documentation**: Tooltips informatifs au survol  
**✅ Expérience**: Fluide et intuitive

**Si tous les tests passent**: Phase 1 complète ! ✨

**Prochaine étape**: Phase 2 - Créer le modèle Java

---

## 📝 Notes de Test

Utiliser cette section pour noter les observations:

```
Date: _______________
Éditeur: _______________

Test 1: ☐ OK  ☐ KO  Notes: _______________________
Test 2: ☐ OK  ☐ KO  Notes: _______________________
Test 3: ☐ OK  ☐ KO  Notes: _______________________
Test 4: ☐ OK  ☐ KO  Notes: _______________________
Test 5: ☐ OK  ☐ KO  Notes: _______________________
Test 6: ☐ OK  ☐ KO  Notes: _______________________
Test 7: ☐ OK  ☐ KO  Notes: _______________________
Test 8: ☐ OK  ☐ KO  Notes: _______________________
Test 9: ☐ OK  ☐ KO  Notes: _______________________
Test 10: ☐ OK  ☐ KO  Notes: _______________________

Problèmes rencontrés:
_________________________________________________
_________________________________________________
_________________________________________________

Solutions appliquées:
_________________________________________________
_________________________________________________
_________________________________________________
```
