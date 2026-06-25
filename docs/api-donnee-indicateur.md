# API Documentation — Module Donnée Indicateur

> **Audience :** Équipe frontend  
> **Version API :** v1  
> **Base URL :** `http://<host>/api/v1`

---

## Table des matières

1. [Architecture des données](#1-architecture-des-données)
2. [Modèle de données](#2-modèle-de-données)
3. [Formats de table générés par le backend](#3-formats-de-table-générés-par-le-backend)
4. [Endpoints — Lecture (Admin & Public)](#4-endpoints--lecture-admin--public)
5. [Endpoints — CRUD](#5-endpoints--crud)
6. [Endpoints — Import Excel](#6-endpoints--import-excel)
7. [DTOs de référence](#7-dtos-de-référence)
8. [Gestion des erreurs](#8-gestion-des-erreurs)
9. [Flux recommandés pour le frontend](#9-flux-recommandés-pour-le-frontend)

---

## 1. Architecture des données

### Concept

Un **Indicateur** possède :
- **N Dimensions** — les axes de décomposition (ex. Région, Année, Secteur)
- **N DonnéesIndicateur** — les enregistrements de valeurs

Chaque `DonneeIndicateur` contient :
- une **valeur** (ex. `"1500"`, `"42.7"`)
- une liste de **ValeurDimension** qui situent cette valeur sur chaque axe

```
Indicateur
├── IndicateurDimension[Région]   (principale = true)
├── IndicateurDimension[Année]    (temporelle = true)
└── IndicateurDimension[Secteur]

DonneeIndicateur #1
├── valeur = "100"
├── ValeurDimension → Région = "Nord"
├── ValeurDimension → Année  = "2023"
└── ValeurDimension → Secteur = "Santé"

DonneeIndicateur #2
├── valeur = "95"
├── ValeurDimension → Région = "Sud"
├── ValeurDimension → Année  = "2023"
└── ValeurDimension → Secteur = "Santé"
```

### Types de dimensions

| Attribut | Effet |
|---|---|
| `principale = true` | Dimension utilisée comme axe des lignes dans le tableau pivot |
| `temporelle = true` | Valeurs triées naturellement (2021, 2022, 2023…) |
| autres | Dimensions utilisées comme axe des colonnes dans le pivot |

---

## 2. Modèle de données

### DonneeIndicateurDto

```json
{
  "id": 42,
  "valeur": "100",
  "valeurDimensions": [
    {
      "id": 101,
      "valeur": "Nord",
      "dimension": {
        "id": 1,
        "nom": "region",
        "libelle": "Région"
      }
    },
    {
      "id": 102,
      "valeur": "2023",
      "dimension": {
        "id": 2,
        "nom": "annee",
        "libelle": "Année"
      }
    }
  ]
}
```

### DonneeIndicateurRequestDto (création / mise à jour)

```json
{
  "valeur": "100",
  "valeurDimensions": [
    {
      "valeur": "Nord",
      "dimension": { "id": 1, "nom": "region" }
    },
    {
      "valeur": "2023",
      "dimension": { "id": 2, "nom": "annee" }
    }
  ]
}
```

---

## 3. Formats de table générés par le backend

Le paramètre `tableFormat` sur les endpoints GET détermine la structure retournée.

### 3.1 Format `pivot` — Pour les charts

Le backend génère automatiquement un tableau croisé à partir des données brutes.

**Exemple avec dimensions [Région (principale), Année (temporelle), Secteur]:**

```
[
  ["",       "Secteur", "Santé",  "Santé"  ],  ← header dim Secteur
  ["",       "Année",   "2022",   "2023"   ],  ← header dim Année (temporelle)
  ["Région", "",        "",       ""       ],  ← header dim principale
  ["Nord",   "",        "110",    "100"    ],  ← ligne données
  ["Sud",    "",        "",       "95"     ],  ← ligne données (cellule vide = pas de données)
]
```

**Règles de construction :**
- Lignes d'en-tête : une ligne par dimension non-principale, dans l'ordre
- Les dimensions temporelles sont triées naturellement (2021 < 2022 < 2023)
- Les colonnes correspondent au produit cartésien des valeurs des dimensions non-principales
- Les cellules vides (`""`) indiquent l'absence de données pour cette combinaison

**Réponse avec métadonnées (`PivotTableWithMetadataDto`) :**

```json
{
  "table": [["", "Année", "2022", "2023"], ["Région", "", "", ""], ...],
  "metadata": {
    "headerRowCount": 2,
    "dataStartRow": 2,
    "dataStartColumn": 1,
    "totalRows": 4,
    "totalColumns": 3,
    "dimensions": [
      {
        "nom": "region",
        "libelle": "Région",
        "axis": "row",
        "headerRowIndex": null,
        "columnStart": null,
        "columnEnd": null
      },
      {
        "nom": "annee",
        "libelle": "Année",
        "axis": "column",
        "headerRowIndex": 0,
        "columnStart": 1,
        "columnEnd": 2
      }
    ]
  }
}
```

**Usage frontend :** Récupérer `table` puis utiliser `metadata.dataStartRow` et `metadata.dataStartColumn` pour localiser les données réelles. Utiliser `metadata.dimensions` pour construire les libellés d'axes de charts.

---

### 3.2 Format `flat` — Pour l'affichage tabulaire

Tableau dénormalisé avec toutes les combinaisons possibles. Les cellules sans données sont vides.

```json
[
  ["Région", "Année", "Valeur"],
  ["Nord",   "2022",  "110"  ],
  ["Nord",   "2023",  "100"  ],
  ["Sud",    "2022",  ""     ],
  ["Sud",    "2023",  "95"   ]
]
```

---

### 3.3 Format `crud` — Pour les tableaux éditables

Identique au format flat mais inclut l'`id` en première colonne (pour édition/suppression).

```json
[
  ["id",  "Région", "Année", "Valeur"],
  ["42",  "Nord",   "2022",  "110"  ],
  ["43",  "Nord",   "2023",  "100"  ],
  ["45",  "Sud",    "2023",  "95"   ]
]
```

---

### 3.4 Format `create-template` — Pour les formulaires d'import vierges

Toutes les combinaisons de dimensions **sans** données existantes (cases à remplir).

```json
[
  ["Région", "Année", "Valeur"],
  ["Nord",   "2021",  ""     ],
  ["Sud",    "2021",  ""     ],
  ["Sud",    "2022",  ""     ]
]
```

---

## 4. Endpoints — Lecture (Admin & Public)

### GET Liste des données d'un indicateur

**Admin :** `GET /api/v1/admin/indicateurs/{indicateurId}/donnees`  
**Public :** `GET /api/v1/public/indicateurs/{indicateurId}/donnees`

**Permissions requises (admin) :** `indicateur:list`

**Query parameters :**

| Paramètre | Type | Défaut | Description |
|---|---|---|---|
| `pageIndex` | integer | 0 | Index de la page (0-based) |
| `pageSize` | integer | 20 | Nombre d'éléments par page |
| `sortField` | string | — | Champ de tri |
| `sortDirection` | `ASC` \| `DESC` | `ASC` | Direction du tri |
| `filters` | string (JSON) | — | Filtres par champ |
| `globalFilter` | string | — | Recherche texte globale |
| `tableFormat` | `pivot` \| `flat` \| `crud` \| `create-template` | — | Si présent, retourne un tableau formaté au lieu de la liste DTO |

**Réponse sans `tableFormat` :**

```json
{
  "success": true,
  "data": [
    {
      "id": 42,
      "valeur": "100",
      "valeurDimensions": [...]
    }
  ],
  "pagination": {
    "totalElements": 50,
    "totalPages": 3,
    "pageIndex": 0,
    "pageSize": 20
  }
}
```

**Réponse avec `tableFormat=pivot` :**

```json
{
  "success": true,
  "data": {
    "table": [["", "2022", "2023"], ["Nord", "110", "100"], ...],
    "metadata": { ... }
  }
}
```

---

### GET Détail d'une donnée

**Admin :** `GET /api/v1/admin/indicateurs/{indicateurId}/donnees/{id}`  
**Public :** `GET /api/v1/public/indicateurs/{indicateurId}/donnees/{id}`

**Permissions requises (admin) :** `indicateur:read`

**Réponse :**

```json
{
  "success": true,
  "data": {
    "id": 42,
    "valeur": "100",
    "valeurDimensions": [...],
    "indicateur": {
      "id": 10,
      "nom": "Taux de couverture"
    }
  }
}
```

---

## 5. Endpoints — CRUD

### POST Créer une donnée

`POST /api/v1/admin/indicateurs/{indicateurId}/donnees`

**Permission :** `domaine:create`

**Request body :**

```json
{
  "valeur": "100",
  "valeurDimensions": [
    { "valeur": "Nord",  "dimension": { "id": 1 } },
    { "valeur": "2023",  "dimension": { "id": 2 } }
  ]
}
```

**Réponse 200 :**

```json
{
  "success": true,
  "data": { "id": 42, "valeur": "100", "valeurDimensions": [...] }
}
```

**Erreurs :** `402` validation, `403` droits insuffisants

---

### POST Création en masse

`POST /api/v1/admin/indicateurs/{indicateurId}/donnees/bulk`

**Permission :** `domaine:create`

**Request body :** `Array<DonneeIndicateurRequestDto>`

**Réponse 201 :** `Array<DonneeIndicateurDto>`

---

### PUT Mettre à jour une donnée

`PUT /api/v1/admin/indicateurs/{indicateurId}/donnees/{id}`

**Permission :** `domaine:edit`

**Request body :** `DonneeIndicateurRequestDto`

**Réponse 200 :** `DonneeIndicateurDto`

**Erreurs :** `402` validation, `404` non trouvé, `403` droits insuffisants

---

### DELETE Supprimer une donnée

`DELETE /api/v1/admin/indicateurs/{indicateurId}/donnees/{id}`

**Permission :** `domaine:delete`

**Réponse 204 :** (vide)

---

### DELETE Supprimer plusieurs données

`DELETE /api/v1/admin/indicateurs/{indicateurId}/donnees/bulk`

**Permission :** `domaine:delete`

**Request body :** `Array<Long>` (liste d'IDs)

**Réponse 204 :** (vide) ou 409 si conflit partiel

---

### DELETE Supprimer toutes les données

`DELETE /api/v1/admin/indicateurs/{indicateurId}/donnees/all`

**Permission :** `domaine:delete`

**Réponse 204 :** (vide)

---

### DELETE Supprimer tout sauf une liste

`DELETE /api/v1/admin/indicateurs/{indicateurId}/donnees/exclude`

**Permission :** `domaine:delete`

**Request body :** `Array<Long>` (IDs à **conserver**)

**Réponse 204 :** (vide)

---

## 6. Endpoints — Import Excel

### Flux import en 2 étapes (recommandé)

```
1. POST /parse-donnee-excel  →  fichier Excel → List<DonneeIndicateurRequestDto>
2. POST /donnees/import-preview  →  diagnostic sans écriture
3. POST /donnees/import-commit   →  écriture en base avec stratégie de conflit
```

---

### POST Parser un fichier Excel

`POST /api/v1/admin/indicateurs/{indicateurId}/parse-donnee-excel`

**Permission :** `indicateur:create`

**Content-Type :** `multipart/form-data`

| Champ | Type | Obligatoire | Description |
|---|---|---|---|
| `file` | File | oui | Fichier `.xlsx` ou `.xls` |
| `sheet` | string | non | Nom ou index de l'onglet |

**Réponse 200 :**

```json
{
  "success": true,
  "data": {
    "rows": [
      {
        "valeur": "100",
        "valeurDimensions": [
          { "valeur": "Nord", "dimension": { "id": 1, "nom": "region" } }
        ]
      }
    ],
    "errors": []
  }
}
```

---

### POST Prévisualisation de l'import

`POST /api/v1/admin/indicateurs/{indicateurId}/donnees/import-preview`

**Permission :** `domaine:create`

Analyse les lignes sans rien écrire en base. Retourne un diagnostic complet ligne par ligne.

**Request body :**

```json
{
  "rows": [
    {
      "valeur": "100",
      "valeurDimensions": [
        { "valeur": "Nord", "dimension": { "id": 1 } },
        { "valeur": "2023", "dimension": { "id": 2 } }
      ]
    }
  ]
}
```

**Réponse 200 — `DonneeImportPreviewDto` :**

```json
{
  "success": true,
  "data": {
    "rowsReceived": 5,
    "validRows": 4,
    "dimensionsMapped": 2,
    "newRows": 2,
    "duplicateRows": 1,
    "conflictRows": 1,
    "rejectedRows": 1,
    "missingValueRows": 1,
    "missingDimensionsRows": 0,
    "minValue": 95.0,
    "maxValue": 110.0,
    "hasConflicts": true,
    "hasBlockingErrors": true,
    "diagnosticRows": [
      {
        "rowNumber": 1,
        "status": "new",
        "reason": null,
        "dimensionsLabel": "Région: Nord | Année: 2023",
        "valeur": "100",
        "existingDonneeId": null,
        "existingValeur": null,
        "importedValeur": "100"
      },
      {
        "rowNumber": 2,
        "status": "conflict",
        "reason": "Valeur différente pour la même combinaison de dimensions",
        "dimensionsLabel": "Région: Sud | Année: 2023",
        "valeur": "80",
        "existingDonneeId": 45,
        "existingValeur": "95",
        "importedValeur": "80"
      },
      {
        "rowNumber": 3,
        "status": "duplicate",
        "reason": "Identique à l'enregistrement existant",
        "dimensionsLabel": "Région: Nord | Année: 2022",
        "valeur": "110",
        "existingDonneeId": 42,
        "existingValeur": "110",
        "importedValeur": "110"
      },
      {
        "rowNumber": 4,
        "status": "rejected",
        "reason": "Valeur manquante",
        "dimensionsLabel": "Région: Est | Année: 2023",
        "valeur": null,
        "existingDonneeId": null,
        "existingValeur": null,
        "importedValeur": null
      }
    ],
    "conflicts": [...],
    "duplicates": [...],
    "rejected": [...]
  }
}
```

**Statuts possibles pour `diagnosticRows[].status` :**

| Statut | Description |
|---|---|
| `new` | Combinaison de dimensions inexistante en base → sera insérée |
| `duplicate` | Même combinaison + même valeur qu'en base → sera ignorée |
| `conflict` | Même combinaison, valeur différente → dépend de la stratégie choisie |
| `rejected` | Erreur de validation → toujours ignorée |

---

### POST Confirmer l'import

`POST /api/v1/admin/indicateurs/{indicateurId}/donnees/import-commit`

**Permission :** `domaine:create`

**Request body — `DonneeImportCommitRequest` :**

```json
{
  "rows": [...],
  "overwriteConflicts": true,
  "replaceExistingData": false,
  "selectedRowNumbers": [1, 2, 3]
}
```

**Paramètres de stratégie :**

| Paramètre | Type | Description |
|---|---|---|
| `overwriteConflicts` | boolean | `true` → les lignes en conflit écrasent les valeurs existantes |
| `replaceExistingData` | boolean | `true` → supprime TOUTES les données existantes avant d'importer |
| `selectedRowNumbers` | `Array<int>` | Numéros de lignes (1-indexé) à traiter. Les autres sont ignorées. |

**Matrice de comportement :**

| `replaceExistingData` | `overwriteConflicts` | Résultat |
|---|---|---|
| `true` | — | Supprime tout, puis insère toutes les lignes sélectionnées |
| `false` | `true` | Insère les nouvelles, écrase les conflits, ignore les duplicates |
| `false` | `false` | Insère les nouvelles uniquement, ignore conflits et duplicates |

**Réponse 200 — `DonneeImportCommitDto` :**

```json
{
  "success": true,
  "data": {
    "importedRows": 2,
    "overwrittenRows": 1,
    "skippedConflictRows": 0,
    "beforeCount": 10,
    "afterCount": 12,
    "rowsReceived": 5,
    "validRows": 4,
    "newRows": 2,
    "duplicateRows": 1,
    "conflictRows": 1,
    "rejectedRows": 1,
    "hasConflicts": false,
    "hasBlockingErrors": false,
    "diagnosticRows": [...]
  }
}
```

---

## 7. DTOs de référence

### DonneeImportPreviewDto (structure complète)

```typescript
interface DonneeImportPreviewDto {
  rowsReceived: number;
  validRows: number;
  dimensionsMapped: number;
  newRows: number;
  duplicateRows: number;
  conflictRows: number;
  rejectedRows: number;
  missingValueRows: number;
  missingDimensionsRows: number;
  minValue: number | null;
  maxValue: number | null;
  hasConflicts: boolean;
  hasBlockingErrors: boolean;
  diagnosticRows: DonneeImportDiagnosedRowDto[];
  conflicts: DonneeImportConflictDto[];
  duplicates: DonneeImportRowIssueDto[];
  rejected: DonneeImportRowIssueDto[];
}

interface DonneeImportDiagnosedRowDto {
  rowNumber: number;          // 1-indexé
  status: 'new' | 'duplicate' | 'conflict' | 'rejected';
  reason: string | null;
  dimensionsLabel: string;    // ex: "Région: Nord | Année: 2023"
  valeur: string | null;
  existingDonneeId: number | null;
  existingValeur: string | null;
  importedValeur: string | null;
}

interface DonneeImportConflictDto extends DonneeImportRowIssueDto {
  existingDonneeId: number;
  existingValeur: string;
  importedValeur: string;
}

interface DonneeImportRowIssueDto {
  rowNumber: number;
  reason: string;
  dimensionsLabel: string;
  valeur: string | null;
}
```

### DonneeImportCommitDto (étend DonneeImportPreviewDto)

```typescript
interface DonneeImportCommitDto extends DonneeImportPreviewDto {
  importedRows: number;
  overwrittenRows: number;
  skippedConflictRows: number;
  beforeCount: number;
  afterCount: number;
}
```

---

## 8. Gestion des erreurs

### Structure d'erreur standard

```json
{
  "success": false,
  "error": "MESSAGE_CODE",
  "message": "Description lisible"
}
```

### Codes HTTP

| Code | Signification |
|---|---|
| `200` | Succès |
| `201` | Création en masse réussie |
| `204` | Suppression réussie (corps vide) |
| `402` | Erreur de validation des données envoyées |
| `403` | Droits insuffisants |
| `404` | Ressource non trouvée |
| `409` | Conflit (suppression bulk partielle) |
| `500` | Erreur serveur inattendue |

---

## 9. Flux recommandés pour le frontend

### Affichage d'un chart

```
1. GET /{indicateurId}/donnees?tableFormat=pivot
   → récupérer table[] et metadata
2. Utiliser metadata.dimensions pour identifier les axes
3. Parser table[] à partir de metadata.dataStartRow / dataStartColumn
4. Mapper les données dans la librairie de charts
```

### Tableau de données éditables

```
1. GET /{indicateurId}/donnees?tableFormat=crud
   → récupérer tableau avec IDs
2. Afficher avec colonnes = première ligne
3. Pour éditer une ligne : PUT /{indicateurId}/donnees/{id}
4. Pour supprimer : DELETE /{indicateurId}/donnees/{id}
```

### Import de données depuis Excel

```
1. Upload → POST /{indicateurId}/parse-donnee-excel
   → récupérer { rows: [...] }

2. Prévisualisation → POST /{indicateurId}/donnees/import-preview
   body: { rows: [...] }
   → afficher le résumé (newRows, conflictRows, rejectedRows)
   → afficher diagnosticRows pour feedback ligne par ligne

3. L'utilisateur choisit sa stratégie :
   - overwriteConflicts: true/false
   - replaceExistingData: true/false
   - selectedRowNumbers: [1, 2, 3, ...]  ← peut filtrer certaines lignes

4. Confirmation → POST /{indicateurId}/donnees/import-commit
   body: { rows, overwriteConflicts, replaceExistingData, selectedRowNumbers }
   → afficher importedRows, overwrittenRows, afterCount
```

### Normalisation des valeurs (règles backend)

- Les valeurs sont comparées en **minuscules sans espaces de bord** (pour la détection de conflits/doublons)
- Les valeurs sont **stockées avec leur casse d'origine** mais les espaces de bord sont supprimés
- Les nombres acceptent la virgule ou le point décimal (`"1.500"` = `"1,500"`)
- Une combinaison de dimensions est identifiée par la clé `dimensionId:valeurNormalisée|...` (ordre des dimensions inclus)
