# API d'Export Indicateur - Exemples Postman

## Configuration de base
- **Base URL**: `http://localhost:8080/api/v1/indicateurs/audit`
- **Headers requis**: 
  - `Authorization: Bearer YOUR_TOKEN`
  - `Content-Type: application/json` (pour les requêtes POST)

---

## 1. Export Simple (GET) - Version par défaut

### Request
```
GET {{base_url}}/export
```

### Headers
```
Authorization: Bearer YOUR_TOKEN
```

### Description
- Exporte tous les indicateurs avec toutes les colonnes
- Une seule feuille Excel
- Nom du fichier: "indicateurs"

---

## 2. Export avec Options GET

### Request
```
GET {{base_url}}/export/with-options?groupBy=domaine&activeOnly=true&fileName=indicateurs-actifs-par-domaine
```

### Headers
```
Authorization: Bearer YOUR_TOKEN
```

### Query Parameters
- `groupBy`: `none` | `domaine` | `source` (défaut: `none`)
- `activeOnly`: `true` | `false` (défaut: `false`)
- `fileName`: nom du fichier sans extension (défaut: `indicateurs-export`)

### Exemples d'URLs
```
# Export groupé par source, tous les indicateurs
GET {{base_url}}/export/with-options?groupBy=source&activeOnly=false&fileName=export-par-source

# Export simple, seulement les actifs
GET {{base_url}}/export/with-options?activeOnly=true&fileName=indicateurs-actifs

# Export groupé par domaine
GET {{base_url}}/export/with-options?groupBy=domaine&fileName=export-domaines
```

---

## 3. Export Personnalisé (POST) - Configuration avancée

### Request
```
POST {{base_url}}/export/custom
```

### Headers
```
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

### Body - Exemple 1: Export basique
```json
{
  "columnsToExport": ["ID", "INDICATEUR", "DESCRIPTION", "ACTIF"],
  "groupBy": "NONE",
  "fileName": "indicateurs-basique",
  "activeOnly": false
}
```

### Body - Exemple 2: Export groupé par domaine avec colonnes spécifiques
```json
{
  "columnsToExport": [
    "ID", 
    "INDICATEUR", 
    "DESCRIPTION", 
    "SOURCE", 
    "ACTIF", 
    "HAS_DATA"
  ],
  "groupBy": "BY_DOMAINE",
  "fileName": "indicateurs-par-domaine",
  "activeOnly": true
}
```

### Body - Exemple 3: Export groupé par source, toutes les colonnes
```json
{
  "columnsToExport": null,
  "groupBy": "BY_SOURCE",
  "fileName": "export-complet-par-source",
  "activeOnly": false
}
```

### Body - Exemple 4: Export avec colonnes métier spécifiques
```json
{
  "columnsToExport": [
    "INDICATEUR",
    "CATEGORIE", 
    "UNITE",
    "TYPE_GRAPHE",
    "REGLE_CALCUL",
    "SOURCE"
  ],
  "groupBy": "NONE",
  "fileName": "indicateurs-metier",
  "activeOnly": true
}
```

---

## 4. Export Détaillé par Feuille (GET)

### Request
```
GET {{base_url}}/export/details
```

### Headers
```
Authorization: Bearer YOUR_TOKEN
```

### Description
- Chaque indicateur dans sa propre feuille
- Informations détaillées avec métadonnées
- Nom du fichier: "indicateurs-details"

---

## Liste des Colonnes Disponibles

Utilisez ces noms dans le tableau `columnsToExport`:

| Nom de la colonne | Description |
|-------------------|-------------|
| `ESPACES` | Espaces associés |
| `DOMAINES` | Domaines associés |
| `SOUS_DOMAINES` | Sous-domaines associés |
| `ID` | Identifiant unique |
| `INDICATEUR` | Nom de l'indicateur |
| `UNITE` | Unité de mesure |
| `CATEGORIE` | Catégorie de l'indicateur |
| `SOURCE` | Source des données |
| `ABREVIATION` | Abréviation |
| `TYPE_TB` | Type de tableau de bord |
| `TYPE_GRAPHE` | Type de graphique |
| `DESCRIPTION` | Description détaillée |
| `REGLE_CALCUL` | Règle de calcul |
| `ACTIF` | Statut actif/inactif |
| `HAS_DATA` | Présence de données |

---

## Types de Groupement

| Valeur | Description |
|--------|-------------|
| `NONE` ou `none` | Une seule feuille avec tous les indicateurs |
| `BY_DOMAINE` ou `domaine` | Une feuille par domaine |
| `BY_SOURCE` ou `source` | Une feuille par source |

---

## Gestion des Erreurs

### Erreur 400 - Paramètres invalides
```json
{
  "timestamp": "2025-06-12T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Paramètres d'export invalides",
  "path": "/api/v1/indicateurs/audit/export/custom"
}
```

### Erreur 403 - Permission refusée
```json
{
  "timestamp": "2025-06-12T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Permission denied",
  "path": "/api/v1/indicateurs/audit/export"
}
```

---

## Collection Postman

Voici une collection Postman complète à importer:

```json
{
  "info": {
    "name": "Indicateur Export API",
    "description": "Collection pour tester l'export des indicateurs"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api/v1/indicateurs/audit"
    },
    {
      "key": "token",
      "value": "YOUR_TOKEN_HERE"
    }
  ],
  "item": [
    {
      "name": "Export Simple",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": "{{base_url}}/export"
      }
    },
    {
      "name": "Export avec Options - Par Domaine",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{base_url}}/export/with-options?groupBy=domaine&activeOnly=true&fileName=export-domaine",
          "query": [
            {
              "key": "groupBy",
              "value": "domaine"
            },
            {
              "key": "activeOnly",
              "value": "true"
            },
            {
              "key": "fileName",
              "value": "export-domaine"
            }
          ]
        }
      }
    },
    {
      "name": "Export Personnalisé - Colonnes Spécifiques",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          },
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"columnsToExport\": [\"ID\", \"INDICATEUR\", \"DESCRIPTION\", \"SOURCE\", \"ACTIF\"],\n  \"groupBy\": \"BY_SOURCE\",\n  \"fileName\": \"indicateurs-par-source\",\n  \"activeOnly\": true\n}"
        },
        "url": "{{base_url}}/export/custom"
      }
    },
    {
      "name": "Export Détaillé",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": "{{base_url}}/export/details"
      }
    }
  ]
}
```

---

## Tests Recommandés

1. **Test de base**: Commencez par l'export simple pour vérifier que l'API fonctionne
2. **Test avec filtrage**: Utilisez `activeOnly=true` pour tester le filtrage
3. **Test de groupement**: Essayez les différents types de groupement
4. **Test de colonnes**: Testez avec différentes combinaisons de colonnes
5. **Test d'erreur**: Testez avec des paramètres invalides pour vérifier la gestion d'erreurs

---

## Pour votre Frontend Angular

Voici des exemples d'appels depuis Angular:

```typescript
// Service Angular
exportIndicateurs(options: any) {
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${this.authService.getToken()}`
  });

  // Export simple
  return this.http.get('/api/v1/indicateurs/audit/export', { 
    headers, 
    responseType: 'blob' 
  });
}

// Export personnalisé
exportIndicateursCustom(exportRequest: any) {
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${this.authService.getToken()}`,
    'Content-Type': 'application/json'
  });

  return this.http.post('/api/v1/indicateurs/audit/export/custom', 
    exportRequest, 
    { headers, responseType: 'blob' }
  );
}
```
