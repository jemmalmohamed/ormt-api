# 🚀 Guide Complet - Export Indicateurs avec Postman

## 📋 Collection Postman Complète

La collection `Postman-Complete-Export-Request.json` contient **8 requêtes** qui couvrent **TOUTES** les possibilités d'export.

---

## 🛠️ Configuration Initiale

### 1. Importer la Collection
- Ouvrir Postman
- File → Import
- Sélectionner le fichier `Postman-Complete-Export-Request.json`

### 2. Configurer les Variables
```
base_url: http://localhost:8080
token: YOUR_JWT_TOKEN_HERE
```

⚠️ **IMPORTANT**: Remplacez `YOUR_JWT_TOKEN_HERE` par votre vrai token JWT !

---

## 🎯 Requêtes Disponibles

### 1. 🚀 **EXPORT COMPLET - Toutes Options**
```json
{
  "columnsToExport": [
    "ID", "INDICATEUR", "DESCRIPTION", "CATEGORIE", "SOURCE", 
    "UNITE", "TYPE_GRAPHE", "TYPE_TB", "ACTIF", "HAS_DATA", 
    "ABREVIATION", "REGLE_CALCUL", "DOMAINES", "SOUS_DOMAINES", "ESPACES"
  ],
  "groupBy": "BY_DOMAINE",
  "fileName": "export_complet_{{timestamp}}",
  "activeOnly": false
}
```
**✨ Caractéristiques:**
- ✅ **TOUTES** les colonnes disponibles
- 📊 Groupé par domaine (une feuille par domaine)
- 🗂️ Nom de fichier avec timestamp automatique
- 📈 Tous les indicateurs (actifs et inactifs)

---

### 2. 📊 **Export par SOURCE - Colonnes Métier**
```json
{
  "columnsToExport": [
    "INDICATEUR", "CATEGORIE", "SOURCE", "UNITE", 
    "TYPE_GRAPHE", "REGLE_CALCUL", "ACTIF", "HAS_DATA"
  ],
  "groupBy": "BY_SOURCE",
  "fileName": "indicateurs_par_source_metier",
  "activeOnly": true
}
```
**✨ Caractéristiques:**
- 🎯 Colonnes métier essentielles
- 📊 Groupé par source de données
- ✅ Seulement les indicateurs actifs

---

### 3. 🎯 **Export DOMAINE - Colonnes Essentielles**
```json
{
  "columnsToExport": [
    "ID", "INDICATEUR", "DESCRIPTION", "DOMAINES", 
    "SOUS_DOMAINES", "ACTIF", "HAS_DATA"
  ],
  "groupBy": "BY_DOMAINE",
  "fileName": "indicateurs_par_domaine_essentiels",
  "activeOnly": false
}
```
**✨ Caractéristiques:**
- 🏢 Focus sur la hiérarchie organisationnelle
- 📊 Une feuille par domaine
- 📋 Informations de base

---

### 4. 📋 **Export SIMPLE - Toutes Colonnes**
```json
{
  "columnsToExport": null,
  "groupBy": "NONE",
  "fileName": "export_complet_une_feuille",
  "activeOnly": false
}
```
**✨ Caractéristiques:**
- 📄 Une seule feuille Excel
- 🔄 `columnsToExport: null` = toutes les colonnes
- 📊 Tous les indicateurs

---

### 5. ✅ **Export ACTIFS SEULEMENT - Par Source**
```json
{
  "columnsToExport": [
    "INDICATEUR", "SOURCE", "CATEGORIE", "UNITE", 
    "ACTIF", "HAS_DATA", "DESCRIPTION"
  ],
  "groupBy": "BY_SOURCE",
  "fileName": "indicateurs_actifs_par_source",
  "activeOnly": true
}
```
**✨ Caractéristiques:**
- ✅ Filtrage strict sur les actifs
- 📊 Groupement par source
- 🎯 Colonnes pratiques

---

### 6. 🔧 **Export TECHNIQUE - Infos Système**
```json
{
  "columnsToExport": [
    "ID", "INDICATEUR", "ABREVIATION", "TYPE_TB", 
    "TYPE_GRAPHE", "REGLE_CALCUL", "ACTIF", "HAS_DATA"
  ],
  "groupBy": "NONE",
  "fileName": "indicateurs_infos_techniques",
  "activeOnly": false
}
```
**✨ Caractéristiques:**
- 🔧 Informations techniques pour développeurs
- 📋 Règles de calcul et types
- 💻 Une seule feuille

---

### 7. 🌍 **Export ESPACES et DOMAINES**
```json
{
  "columnsToExport": [
    "INDICATEUR", "ESPACES", "DOMAINES", "SOUS_DOMAINES", 
    "DESCRIPTION", "ACTIF"
  ],
  "groupBy": "BY_DOMAINE",
  "fileName": "indicateurs_espaces_domaines",
  "activeOnly": true
}
```
**✨ Caractéristiques:**
- 🏢 Focus sur la structure organisationnelle
- 🌍 Espaces, domaines, sous-domaines
- ✅ Seulement les actifs

---

### 8. 🧪 **Test avec Colonnes Inexistantes**
```json
{
  "columnsToExport": [
    "ID", "INDICATEUR", "COLONNE_INEXISTANTE", 
    "AUTRE_COLONNE_FAUSSE", "DESCRIPTION", "ACTIF"
  ],
  "groupBy": "NONE",
  "fileName": "test_colonnes_invalides",
  "activeOnly": false
}
```
**✨ Caractéristiques:**
- 🧪 Test de robustesse
- ❌ Colonnes invalides ignorées
- 📝 Pour vérifier la gestion d'erreurs

---

## 📊 Toutes les Colonnes Disponibles

| Nom | Description | Type |
|-----|-------------|------|
| `ESPACES` | Espaces associés | 🌍 Hiérarchie |
| `DOMAINES` | Domaines associés | 🏢 Hiérarchie |
| `SOUS_DOMAINES` | Sous-domaines associés | 📋 Hiérarchie |
| `ID` | Identifiant unique | 🔢 Technique |
| `INDICATEUR` | Nom de l'indicateur | 📝 Base |
| `UNITE` | Unité de mesure | 📏 Métier |
| `CATEGORIE` | Catégorie | 🏷️ Métier |
| `SOURCE` | Source des données | 📊 Métier |
| `ABREVIATION` | Abréviation | 📝 Base |
| `TYPE_TB` | Type tableau de bord | 🔧 Technique |
| `TYPE_GRAPHE` | Type de graphique | 📈 Technique |
| `DESCRIPTION` | Description détaillée | 📝 Base |
| `REGLE_CALCUL` | Règle de calcul | 🧮 Technique |
| `ACTIF` | Statut actif/inactif | ✅ Statut |
| `HAS_DATA` | Présence de données | 📊 Statut |

---

## 🎛️ Options de Groupement

| Valeur | Description | Résultat |
|--------|-------------|----------|
| `NONE` | Pas de groupement | 📄 Une seule feuille |
| `BY_DOMAINE` | Grouper par domaine | 📊 Une feuille par domaine |
| `BY_SOURCE` | Grouper par source | 📈 Une feuille par source |

---

## 🧪 Tests Automatiques Inclus

Chaque requête inclut des tests automatiques :

```javascript
✅ Vérification du status code 200
✅ Vérification du type de fichier Excel
✅ Vérification de l'en-tête Content-Disposition
📊 Log de la taille du fichier
📁 Log du nom du fichier
```

---

## 🔥 Cas d'Usage Recommandés

### 👩‍💼 **Pour les Managers**
- Utilisez `Export DOMAINE - Colonnes Essentielles`
- Vue d'ensemble par domaine d'activité

### 👨‍💻 **Pour les Développeurs**
- Utilisez `Export TECHNIQUE - Infos Système`
- Toutes les informations techniques

### 📊 **Pour l'Analyse de Données**
- Utilisez `EXPORT COMPLET - Toutes Options`
- Maximum d'informations disponibles

### 🎯 **Pour un Rapport Spécifique**
- Créez votre propre requête en copiant une existante
- Personnalisez les colonnes selon vos besoins

---

## 🚨 Gestion d'Erreurs

### Erreur 400 - Paramètres Invalides
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Paramètres d'export invalides"
}
```

### Erreur 403 - Permission Refusée
```json
{
  "status": 403,
  "error": "Forbidden", 
  "message": "Permission denied"
}
```

### Colonnes Invalides
- Les colonnes inexistantes sont **ignorées** (pas d'erreur)
- Le service continue avec les colonnes valides
- Un warning est loggé côté serveur

---

## 💡 Conseils d'Utilisation

1. **Commencez par l'export simple** pour tester la connectivité
2. **Utilisez activeOnly=true** en production pour de meilleures performances
3. **Personnalisez les noms de fichiers** avec des timestamps ou identifiants
4. **Testez avec peu de colonnes** avant d'exporter massivement
5. **Vérifiez les logs serveur** en cas de problème

---

## 🔧 Variables d'Environnement Postman

```json
{
  "base_url": "http://localhost:8080",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "timestamp": "20250612_143022" // Auto-généré
}
```

Cette collection vous permet de tester **TOUTES** les combinaisons possibles d'export avec groupement et colonnes personnalisées ! 🎉
