# 📋 Guide Frontend - Formulaire d'Export des Indicateurs

## 🎯 Objectif
Créer un formulaire moderne et intuitif pour configurer l'export des indicateurs basé sur le DTO `IndicateurExportRequestDto`.

## 📊 Structure du Formulaire

### 1. **Sélection des Indicateurs** 
```typescript
indicateurIds?: number[]
```
- **Type** : Multi-sélection (optionnel)
- **Interface** : Select avec recherche ou liste avec checkboxes
- **Comportement** : 
  - Si vide → exporte TOUS les indicateurs
  - Si rempli → exporte uniquement les IDs sélectionnés
- **UI suggérée** : 
  ```jsx
  <MultiSelect
    label="Indicateurs à exporter"
    placeholder="Tous les indicateurs (par défaut)"
    options={indicateursList}
    value={formData.indicateurIds}
    onChange={handleIndicateursChange}
    searchable
    clearable
  />
  ```

### 2. **Colonnes à Inclure**
```typescript
columnsToExport?: string[]
```
- **Type** : Multi-sélection (optionnel) 
- **Options disponibles** :
  - `"Espaces"`, `"Domaines"`, `"Sous domaines"`
  - `"id"`, `"Nom"`, `"Unité"`, `"Catégorie"`
  - `"Source"`, `"Abréviation"`, `"Type TB"`, `"Type Graphe"`
  - `"Description"`, `"Règle de calcul"`, `"Actif"`, `"A des données"`
- **Interface** : Groupe de checkboxes ou liste déroulante multiple
- **Comportement** : Si vide → inclut TOUTES les colonnes

### 3. **Type de Groupement**
```typescript
groupBy: 'none' | 'domaine' | 'source'
```
- **Type** : Radio buttons ou Select simple
- **Valeur par défaut** : `'none'`
- **Options** :
  ```typescript
  const groupingOptions = [
    { value: 'NONE', label: 'Tout dans une feuille', description: 'Tous les indicateurs dans un seul onglet' },
    { value: 'BY_DOMAINE', label: 'Grouper par domaine', description: 'Un onglet par domaine' },
    { value: 'BY_SOURCE', label: 'Grouper par source', description: 'Un onglet par source de données' }
  ]
  ```

### 4. **Nom du Fichier**
```typescript
fileName: string
```
- **Type** : Input text
- **Valeur par défaut** : `"indicateurs-export"`
- **Validation** : Caractères alphanumériques + tirets/underscores
- **Interface** : 
  ```jsx
  <TextInput
    label="Nom du fichier"
    value={formData.fileName}
    placeholder="indicateurs-export"
    onChange={handleFileNameChange}
  />
  ```

### 5. **Format d'Export** ⭐ *NOUVEAU*
```typescript
format: 'EXCEL' | 'CSV'
```
- **Type** : Radio buttons ou Toggle
- **Valeur par défaut** : `'EXCEL'`
- **Interface** suggérée :
  ```jsx
  <SegmentedControl
    data={[
      { label: '📊 Excel (.xlsx)', value: 'EXCEL' },
      { label: '📄 CSV (.csv)', value: 'CSV' }
    ]}
    value={formData.format}
    onChange={setFormat}
  />
  ```

## 🔧 Configuration Avancée (Section Collapsible)

### 6. **Sections à Exporter** *(pour export détaillé)*
```typescript
sectionsToExport?: string[]
```
- **Type** : Multi-checkboxes
- **Options** :
  ```typescript
  const sectionOptions = [
    { value: 'META', label: 'Informations générales', icon: '📋' },
    { value: 'DOMAINES', label: 'Domaines et sous-domaines', icon: '🏷️' },
    { value: 'DIMENSIONS', label: 'Dimensions de l\'indicateur', icon: '📐' },
     { value: 'PIVOT_DATA', label: 'Données au format pivot', icon: '🔄' },
    { value: 'FLAT_DATA', label: 'Données au format plat', icon: '📝' }
  ]
  ```

### 7. **Type de Tables de Données**
```typescript
dataTableType: 'PIVOT' | 'FLAT' | 'BOTH' | 'NONE'
```
- **Type** : Radio buttons
- **Valeur par défaut** : `'BOTH'`
- **Options** :
  ```typescript
  const dataTableOptions = [
    { value: 'BOTH', label: 'Les deux formats', description: 'Tableau croisé + format plat' },
    { value: 'PIVOT', label: 'Tableau croisé seulement', description: 'Données au format pivot' },
    { value: 'FLAT', label: 'Format plat seulement', description: 'Une ligne par combinaison' },
    { value: 'NONE', label: 'Aucune donnée', description: 'Métadonnées uniquement' }
  ]
  ```

### 8. **Inclure les Statistiques**
```typescript
includeDataStats: boolean
```
- **Type** : Switch/Toggle
- **Valeur par défaut** : `true`
- **Interface** :
  ```jsx
  <Switch
    label="Inclure les statistiques des données"
    description="Ajoute un résumé statistique des données"
    />
  ```

## 🎨 Structure du Formulaire Recommandée

```jsx
<Form>
  {/* Section Principale */}
  <Card title="Configuration de l'export">
    <Grid cols={2}>
      <IndicateurSelector />
      <FormatSelector />
    </Grid>
    
    <Grid cols={2}>
      <ColumnSelector />
      <GroupingSelector />
    </Grid>
    
    <FileNameInput />
  </Card>

  {/* Section Avancée (Collapsible) */}
  <Collapsible title="Options avancées" defaultOpen={false}>
    <Card>
      <SectionSelector />
      <DataTableTypeSelector />
      <StatsToggle />
    </Card>
  </Collapsible>

  {/* Actions */}
  <Group position="right">
    <Button variant="outline" onClick={handleCancel}>
      Annuler
    </Button>
    <Button 
      type="submit" 
      loading={isExporting}
      leftSection={<IconDownload />}
    >
      Exporter {getFileExtension()}
    </Button>
  </Group>
</Form>
```

## 🔄 Interface TypeScript

```typescript
export interface IndicateurExportRequest {
  indicateurIds?: number[];
  columnsToExport?: string[];
  groupBy: 'NONE' | 'BY_DOMAINE' | 'BY_SOURCE';
  format: 'EXCEL' | 'CSV';
  fileName: string;
  sectionsToExport?: string[];
  dataTableType: 'PIVOT' | 'FLAT' | 'BOTH' | 'NONE';
 }

// Valeurs par défaut
export const defaultExportConfig: IndicateurExportRequest = {
  indicateurIds: undefined, // Tous les indicateurs
  columnsToExport: undefined, // Toutes les colonnes
  groupBy: 'NONE',
  format: 'EXCEL',
  fileName: 'indicateurs-export',
  sectionsToExport: undefined, // Toutes les sections
  dataTableType: 'BOTH'
 
};
```

## 🚀 Fonctions d'aide

```typescript
// Validation du formulaire
export const validateExportForm = (data: IndicateurExportRequest): string[] => {
  const errors: string[] = [];
  
  if (!data.fileName.trim()) {
    errors.push('Le nom du fichier est obligatoire');
  }
  
  if (!/^[a-zA-Z0-9_-]+$/.test(data.fileName)) {
    errors.push('Le nom du fichier contient des caractères invalides');
  }
  
  return errors;
};

// Génération de l'URL d'export
export const getExportUrl = (config: IndicateurExportRequest): string => {
  const endpoint = config.sectionsToExport?.length 
    ? '/api/v1/admin/indicateurs/audit/export/details/with-options'
    : '/api/v1/admin/indicateurs/audit/export/with-options';
    
  return endpoint;
};

// Preview du nom de fichier final
export const getFullFileName = (config: IndicateurExportRequest): string => {
  const extension = config.format === 'EXCEL' ? '.xlsx' : '.csv';
  return `${config.fileName}${extension}`;
};
```

## 💡 Suggestions UX

1. **Prévisualisation** : Afficher le nom complet du fichier avec extension
2. **Compteurs** : Indiquer le nombre d'indicateurs/colonnes sélectionnés
3. **Progressive Disclosure** : Masquer les options avancées par défaut
4. **Smart Defaults** : Proposer des configurations prédéfinies ("Rapide", "Complet", "Personnalisé")
5. **Validation en temps réel** : Vérifier la validité des champs pendant la saisie

Cette structure offre une interface flexible et intuitive pour configurer tous les aspects de l'export des indicateurs ! 🎯
