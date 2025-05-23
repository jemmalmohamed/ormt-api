import { Injectable } from '@angular/core';
import { DimensionsAnalysis } from '../types/dimension.types';

@Injectable({
    providedIn: 'root'
})
export class ChartTypeCompatibilityService {
    chartTypes = [
        { value: 'LINE', label: 'Ligne' },
        { value: 'BAR', label: 'Barres' },
        { value: 'GROUPED_BAR', label: 'Barres groupées' },
        { value: 'STACKED_BAR', label: 'Barres empilées' },
        { value: 'PIE', label: 'Camembert' },
        { value: 'RADAR', label: 'Radar' },
    ];

    private isLineCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.temporelle && (!dims.autres || dims.autres.length === 0);
    }

    private isBarCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.temporelle && (!dims.autres || dims.autres.length === 0);
    }

    private isPieCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.principale && dims.principale.valeurs.length <= 7;
    }

    private isGroupedBarCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.temporelle && !!dims.autres && dims.autres.length > 0;
    }

    private isStackedBarCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.temporelle && !!dims.autres && dims.autres.length > 0;
    }

    private isRadarCompatible(dims: DimensionsAnalysis): boolean {
        return !!dims.principale && dims.principale.valeurs.length >= 3;
    }

    getCompatibleChartTypes(dimensionsAnalysis: DimensionsAnalysis | null) {
        if (!dimensionsAnalysis) return this.chartTypes;
        const dims = dimensionsAnalysis;
        return this.chartTypes.filter(type => {
            switch (type.value) {
                case 'LINE':
                    return this.isLineCompatible(dims);
                case 'BAR':
                    return this.isBarCompatible(dims);
                case 'PIE':
                    return this.isPieCompatible(dims);
                case 'GROUPED_BAR':
                    return this.isGroupedBarCompatible(dims);
                case 'STACKED_BAR':
                    return this.isStackedBarCompatible(dims);
                case 'RADAR':
                    return this.isRadarCompatible(dims);
                default:
                    return true;
            }
        });
    }
}
