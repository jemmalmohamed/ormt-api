// chart-factory.service.ts
import { inject, Injectable } from '@angular/core';
import { ColorService } from './color.service';
import { ChartDataset } from '../types/chart.types';

@Injectable({
    providedIn: 'root'
})
export class ChartFactoryService {


    colorService = inject(ColorService);


    /**
     * Crée un dataset de base pour un graphique
     */
    createDataset(label: string, data: (number | null)[], index: number): ChartDataset {
        const color = this.colorService.getColorForIndex(index);
        return {
            label,
            data,
            backgroundColor: color,
            borderColor: color,
            fill: false
        };
    }

    /**
     * Crée un dataset pour les graphiques à barres
     */
    createBarDataset(label: string, data: (number | null)[], index: number): ChartDataset {
        const dataset = this.createDataset(label, data, index);
        dataset.borderWidth = 1;
        return dataset;
    }

    /**
     * Crée un dataset pour les graphiques à barres groupées/empilées avec variation par groupe
     */
    createGroupedBarDataset(
        label: string,
        data: (number | null)[],
        seriesIndex: number,
        groupIndex: number,
        totalGroups: number
    ): ChartDataset {
        const baseColor = this.colorService.getColorForIndex(seriesIndex);
        const color = this.colorService.adjustColor(baseColor, totalGroups, groupIndex);

        return {
            label,
            data,
            backgroundColor: color,
            borderColor: this.colorService.darkenColor(color),
            borderWidth: 1
        };
    }

    /**
     * Crée un dataset pour les graphiques radar
     */
    createRadarDataset(label: string, data: (number | null)[], index: number): ChartDataset {
        const dataset = this.createDataset(label, data, index);
        const color = dataset.borderColor as string;

        return {
            ...dataset,
            backgroundColor: this.colorService.hexToRgba(color, 0.2),
            pointBackgroundColor: color,
            pointHoverBackgroundColor: "#fff",
            pointHoverBorderColor: color
        };
    }


}
