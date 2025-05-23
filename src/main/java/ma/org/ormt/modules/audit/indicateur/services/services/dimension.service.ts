// dimension.service.ts
import { Injectable } from '@angular/core';
import { Indicateur } from '../../../../../../indicateurs/indicateur/models/indicateur.type';
import { DimensionConfig, DimensionInfo, DimensionsAnalysis } from '../types/dimension.types';

@Injectable({
    providedIn: 'root'
})
export class DimensionService {
    /**
     * Analyser les dimensions disponibles dans l'indicateur
     */
    analyzeDimensions(indicateur: Indicateur): DimensionsAnalysis {
        const dimensionsInfo: DimensionsAnalysis = {
            principale: null,
            temporelle: null,
            autres: []
        };

        if (indicateur.dimensions && indicateur.dimensions.length > 0) {
            indicateur.dimensions.forEach(dim => {
                if (dim.principale) {
                    dimensionsInfo.principale = {
                        id: dim.dimension.id,
                        nom: dim.dimension.nom,
                        libelle: dim.dimension.libelle,
                        valeurs: this.extractUniqueValues(indicateur, dim.dimension.nom)
                    };
                } else if (dim.temporelle) {
                    dimensionsInfo.temporelle = {
                        id: dim.dimension.id,
                        nom: dim.dimension.nom,
                        libelle: dim.dimension.libelle,
                        valeurs: this.extractUniqueValues(indicateur, dim.dimension.nom)
                    };
                } else {
                    dimensionsInfo.autres.push({
                        id: dim.dimension.id,
                        nom: dim.dimension.nom,
                        libelle: dim.dimension.libelle,
                        valeurs: this.extractUniqueValues(indicateur, dim.dimension.nom)
                    });
                }
            });
        }

        // ADAPTATION: Si pas de principale, créer une principale factice avec le nom de l'indicateur
        if (!dimensionsInfo.principale) {
            dimensionsInfo.principale = {
                id: 'indicateur',
                nom: 'indicateur',
                libelle: indicateur.nom || 'Indicateur',
                valeurs: [indicateur.nom || 'Indicateur']
            };
        }

        return dimensionsInfo;
    }

    /**
     * Extraire les valeurs uniques pour une dimension donnée
     */
    extractUniqueValues(indicateur: Indicateur, dimensionNom: string): string[] {
        const valeurs = new Set<string>();

        if (indicateur.donnees && indicateur.donnees.length > 0) {
            indicateur.donnees.forEach(donnee => {
                if (donnee.valeurDimensions) {
                    donnee.valeurDimensions.forEach(vd => {
                        if (vd.dimension.nom === dimensionNom) {
                            valeurs.add(vd.valeur);
                        }
                    });
                }
            });
        }

        return Array.from(valeurs).sort();
    }

    /**
     * Obtenir une configuration par défaut pour les dimensions
     */
    getDefaultDimensionConfig(dimensions: DimensionsAnalysis): DimensionConfig {
        // Configuration par défaut
        const config: DimensionConfig = {
            xAxis: null,
            series: null,
            group: null,
            defaultGroupValue: null,
            defaultTimeValue: null
        };

        // Dimension pour l'axe X (généralement temporelle)
        if (dimensions.temporelle) {
            config.xAxis = dimensions.temporelle.nom;
        }

        // Dimension pour les séries (généralement principale)
        if (dimensions.principale) {
            config.series = dimensions.principale.nom;
        }

        // Dimension pour le groupement/filtrage
        if (dimensions.autres.length > 0) {
            config.group = dimensions.autres[0].nom;
            if (dimensions.autres[0].valeurs.length > 0) {
                config.defaultGroupValue = dimensions.autres[0].valeurs[0];
            }
        }

        // Valeur par défaut pour la dimension temporelle
        if (dimensions.temporelle && dimensions.temporelle.valeurs.length > 0) {
            config.defaultTimeValue = dimensions.temporelle.valeurs[dimensions.temporelle.valeurs.length - 1];
        }

        return config;
    }

    /**
     * Vérifie si une donnée correspond à une dimension et une valeur
     */
    matchDimension(donnee: any, dimensionName: string, value: string): boolean {
        return donnee.valeurDimensions && donnee.valeurDimensions.some((vd: any) =>
            vd.dimension.nom === dimensionName && vd.valeur === value
        );
    }

    /**
     * Retourne les valeurs d'une dimension spécifique
     */
    getDimensionValues(dimensions: DimensionsAnalysis, dimensionName: string): string[] {
        if (dimensions.principale && dimensions.principale.nom === dimensionName) {
            return dimensions.principale.valeurs;
        } else if (dimensions.temporelle && dimensions.temporelle.nom === dimensionName) {
            return dimensions.temporelle.valeurs;
        } else {
            const autreDim = dimensions.autres.find(d => d.nom === dimensionName);
            return autreDim ? autreDim.valeurs : [];
        }
    }

    /**
     * Retourne la liste des dimensions possibles pour l'axe X,
     * en excluant la dimension sélectionnée pour les séries.
     */
    getAvailableXAxisDimensions(dimensions: DimensionsAnalysis, selectedSeries: string | null): DimensionInfo[] {
        const allDims: DimensionInfo[] = [];
        if (dimensions.principale) allDims.push(dimensions.principale);
        if (dimensions.temporelle) allDims.push(dimensions.temporelle);
        allDims.push(...dimensions.autres);
        return allDims.filter(dim => dim.nom !== selectedSeries);
    }

    /**
     * Retourne la liste des dimensions possibles pour les séries,
     * en excluant la dimension sélectionnée pour l'axe X.
     */
    getAvailableSeriesDimensions(dimensions: DimensionsAnalysis, selectedXAxis: string | null): DimensionInfo[] {
        const allDims: DimensionInfo[] = [];
        if (dimensions.principale) allDims.push(dimensions.principale);
        if (dimensions.temporelle) allDims.push(dimensions.temporelle);
        allDims.push(...dimensions.autres);
        return allDims.filter(dim => dim.nom !== selectedXAxis);
    }
}
