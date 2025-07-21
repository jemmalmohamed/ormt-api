# Système de Configuration Dynamique des Graphiques

## Vue d'ensemble

Ce document présente l'architecture et l'implémentation d'un système de configuration dynamique des graphiques pour les indicateurs de l'application ORMT. Le système permet aux utilisateurs de créer, personnaliser et sauvegarder des configurations de graphiques basées sur les dimensions de chaque indicateur.

## Contexte et Objectifs

### Problématique
- Chaque indicateur possède des dimensions différentes (temporelle, géographique, catégorielle)
- Les utilisateurs ont besoin de visualiser les données sous différents formats graphiques
- Nécessité d'une solution flexible permettant le mapping dynamique des dimensions aux axes des graphiques

### Objectifs
- **Flexibilité** : Permettre le mapping dynamique des dimensions vers les axes/séries des graphiques
- **Personnalisation** : Sauvegarder des configurations personnalisées par utilisateur
- **Validation intelligente** : Proposer uniquement les types de graphiques compatibles
- **Réutilisabilité** : Partager des configurations entre utilisateurs
- **Performance** : Optimiser les requêtes pour les gros volumes de données

## Architecture du Système

### 1. Structure de Base de Données

#### Table `chart_type`
Table de référence contenant les types de graphiques disponibles :
- **Code unique** : Identifiant du type (CAMEMBERT, HISTOGRAMME, etc.)
- **Métadonnées** : Nom, description, type Chart.js correspondant
- **Contraintes** : Nombre min/max de dimensions, nécessité de données temporelles
- **Activation** : Possibilité de désactiver certains types

#### Table `chart_mapping_rule` 
Règles de mapping spécifiques à chaque type de graphique :
- **Mapping keys** : xAxis, yAxis, series, filter, labels, values, etc.
- **Contraintes** : Obligatoire, interdit, temporel requis, géographique requis
- **Limites** : Nombre maximum de valeurs par mapping
- **Validation** : Règles métier pour chaque type de graphique

#### Table `chart_configuration`
Configurations sauvegardées par les utilisateurs :
- **Mapping JSON** : Configuration des dimensions vers les axes
- **Options Chart.js** : Personnalisation visuelle (couleurs, légendes, etc.)
- **Partage** : Configurations publiques/privées
- **Défaut** : Configuration par défaut par indicateur

### 2. Types de Graphiques Supportés

| Type | Code | Chart.js | Dimensions | Temporel | Spécificités |
|------|------|----------|------------|----------|--------------|
| Camembert | CAMEMBERT | pie | 1 | Non | labels + values uniquement |
| Histogramme | HISTOGRAMME | bar | 1-10 | Non | xAxis + yAxis + series optionnel |
| Courbes | COURBES | line | 1-10 | Non | Flexible avec séries multiples |
| Pyramide des âges | PYRAMIDE_AGES | bar | 2 exactement | Non | ageAxis + genderAxis |
| Carte | CARTE | choropleth | 1-3 | Non | Dimension géographique obligatoire |
| Courbe linéaire | COURBE_LINEAIRE | line | 1-8 | Oui | xAxis temporel obligatoire |
| Histogramme évolution | HISTOGRAMME_EVOLUTION | bar | 1-8 | Oui | Empilé pour évolutions |
| Courbe évolution | COURBE_EVOLUTION | line | 1-8 | Oui | Multiples séries temporelles |

### 3. Système de Validation

#### Règles de Compatibilité
- **Nombre de dimensions** : Vérification min/max par type
- **Type de données** : Validation temporelle/géographique
- **Mappings obligatoires** : Contrôle des champs requis
- **Mappings interdits** : Exclusion des mappings incompatibles
- **Limites de séries** : Contrôle du nombre max de séries

#### Exemple de Validation
```sql
-- CAMEMBERT : Interdit xAxis, yAxis, series
-- CARTE : Nécessite dimension géographique
-- COURBE_EVOLUTION : Nécessite dimension temporelle sur xAxis
```

## Intégration avec le DTO

### Extension d'IndicateurDetailDto
```java
// Métadonnées pour configuration dynamique
private List<Object> availableChartTypes;     // Types compatibles
private List<Object> chartConfigurations;     // Configs sauvegardées
private List<Object> dimensionMetadata;       // Métadonnées des dimensions
```

### Structure des Métadonnées
```json
{
  "dimensionMetadata": [
    {
      "id": "dimension_temps",
      "name": "Temps", 
      "type": "temporal",
      "canBeXAxis": true,
      "canBeSeries": false,
      "dataType": "date"
    }
  ]
}
```

## Workflow Utilisateur

### 1. Sélection du Type de Graphique
- Le système analyse les dimensions de l'indicateur
- Propose uniquement les types compatibles
- Affiche les contraintes et limitations

### 2. Configuration du Mapping
- Interface drag & drop pour mapper les dimensions
- Validation en temps réel des mappings
- Prévisualisation immédiate du résultat

### 3. Personnalisation Visuelle
- Configuration des couleurs, légendes, axes
- Options spécifiques au type de graphique
- Templates prédéfinis par organisation

### 4. Sauvegarde et Partage
- Sauvegarde de configurations personnalisées
- Possibilité de partage public
- Configuration par défaut par indicateur

## Exemples de Configuration

### Camembert - Répartition par Région
```json
{
  "chartType": "CAMEMBERT",
  "dimensionMapping": {
    "labels": "dimension_region",
    "values": "valeur"
  },
  "chartOptions": {
    "showLegend": true,
    "showPercentages": true
  }
}
```

### Courbe Évolution - Ventes dans le Temps
```json
{
  "chartType": "COURBE_EVOLUTION", 
  "dimensionMapping": {
    "xAxis": "dimension_temps",
    "yAxis": "valeur",
    "series": "dimension_region"
  },
  "chartOptions": {
    "tension": 0.4,
    "showPoints": true
  }
}
```

### Histogramme - Comparaison Multi-critères
```json
{
  "chartType": "HISTOGRAMME",
  "dimensionMapping": {
    "xAxis": "dimension_categorie", 
    "yAxis": "valeur",
    "series": "dimension_annee",
    "filter": "dimension_region"
  },
  "chartOptions": {
    "stacked": false,
    "showValues": true
  }
}
```

## Avantages de l'Architecture

### Flexibilité
- **Mapping dynamique** : Adaptation automatique aux dimensions de chaque indicateur
- **Extensibilité** : Ajout facile de nouveaux types de graphiques
- **Personnalisation** : Configuration fine de chaque aspect visuel

### Performance
- **Index optimisés** : Requêtes rapides sur les configurations fréquentes
- **Cache intelligent** : Mémorisation des configurations populaires
- **Validation côté client** : Réduction des aller-retours serveur

### Maintenabilité
- **Séparation des responsabilités** : Logique métier vs configuration visuelle
- **Règles centralisées** : Gestion unifiée des contraintes
- **Tests automatisés** : Validation des règles de mapping

## Technologies Utilisées

### Backend
- **Base de données** : PostgreSQL avec colonnes JSON
- **Framework** : Spring Boot avec JPA/Hibernate
- **Validation** : Règles métier en base + validation Java

### Frontend
- **Framework** : Angular
- **Graphiques** : Chart.js
- **Interface** : Drag & drop pour mapping des dimensions

## Prochaines Étapes

### Phase 1 : Implémentation de Base
1. ✅ Création des tables et migrations
2. 🔄 Développement des entités Java
3. 🔄 Services de validation et recommandation

### Phase 2 : Interface Utilisateur
1. 🔄 Extension du DTO avec métadonnées
2. 🔄 API endpoints pour configuration
3. 🔄 Interface Angular de configuration

### Phase 3 : Fonctionnalités Avancées
1. 🔄 Templates réutilisables
2. 🔄 Export/Import de configurations
3. 🔄 Analytics sur l'utilisation des graphiques

## Conclusion

Ce système de configuration dynamique des graphiques offre une solution complète et flexible pour la visualisation des données d'indicateurs. L'architecture proposée permet une adaptation automatique aux spécificités de chaque indicateur tout en offrant une personnalisation poussée aux utilisateurs.

La séparation claire entre les règles métier, les configurations utilisateur et l'interface garantit une maintenance aisée et une évolutivité optimale du système.
