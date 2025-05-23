import { Indicateur } from '../../../../../../indicateurs/indicateur/models/indicateur.type';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class IndicateurTableOrganizerService {
  constructor() { }

  /**
   * Helper to get unique values for a dimension from data.
   */
  private getUniqueDimensionValues(indicateur: Indicateur, dimensionNom: string): string[] {
    return Array.from(new Set(indicateur.donnees.map(donnee => {
      const vd = donnee.valeurDimensions?.find(vd => vd.dimension.nom === dimensionNom);
      return vd ? vd.valeur : '';
    })));
  }

  /**
   * Helper to compute cartesian product of arrays.
   */
  private cartesian(arrays: string[][]): string[][] {
    return arrays.length === 0 ? [[]] : arrays.reduce((a, b) => a.flatMap(d => b.map(e => [...d, e])), [[]] as string[][]);
  }

  /**
   * Build the classic (main) sheet data.
   */
  buildClassicSheetData(indicateur: Indicateur): { label: string, data: any[][], colCount: number } {
    const headers = [];
    if (indicateur.dimensions && indicateur.dimensions.length > 0) {
      indicateur.dimensions.forEach(dim => headers.push(dim.dimension.libelle || dim.dimension.nom));
    }
    headers.push('Valeur');
    const data: any[][] = [];
    indicateur.donnees.forEach(donnee => {
      const row = [];
      if (indicateur.dimensions && indicateur.dimensions.length > 0) {
        indicateur.dimensions.forEach(dim => {
          const vd = donnee.valeurDimensions?.find(vd => vd.dimension.nom === dim.dimension.nom);
          row.push(vd ? vd.valeur : '');
        });
      }
      row.push(donnee.valeur);
      data.push(row);
    });
    data.unshift(headers);
    return { label: 'Données', data, colCount: headers.length };
  }

  /**
   * Build the pivot worksheet data (headers + rows) for the given indicator.
   */
  buildPivotSheetData(indicateur: Indicateur): { label: string, data: any[][], colCount: number } {
    const principale = indicateur.dimensions.find(dim => dim.principale);
    const autres = indicateur.dimensions.filter(dim => !dim.principale);
    if (!principale || autres.length === 0) return { label: 'Pivot', data: [], colCount: 0 };
    const secondaire = autres[0];
    const reste = autres.slice(1);
    // Identify the temporal dimension using the 'temporelle' attribute
    const temporel = [secondaire, ...reste].find(dim => dim.temporelle) || secondaire;
    const autresSansTemporel = [secondaire, ...reste].filter(dim => dim !== temporel);
    const valeursPrincipale = this.getUniqueDimensionValues(indicateur, principale.dimension.nom);
    const valeursTemporel = this.getUniqueDimensionValues(indicateur, temporel.dimension.nom);
    const autresValeurs = autresSansTemporel.map(dim => this.getUniqueDimensionValues(indicateur, dim.dimension.nom));
    const combinaisonsAutres = this.cartesian(autresValeurs);
    // Build headers
    const header1 = [principale.dimension.libelle || principale.dimension.nom];
    valeursTemporel.forEach(valTemp => {
      combinaisonsAutres.forEach(() => {
        header1.push(valTemp);
      });
    });
    const header2 = [''];
    valeursTemporel.forEach(() => {
      combinaisonsAutres.forEach(comb => {
        header2.push(comb.join(' | '));
      });
    });
    // Build data rows
    const data: any[][] = [];
    valeursPrincipale.forEach(valPrincipale => {
      const row = [valPrincipale];
      valeursTemporel.forEach(valTemp => {
        combinaisonsAutres.forEach(comb => {
          const donnee = indicateur.donnees.find(d => {
            const vdPrincipale = d.valeurDimensions?.find(vd => vd.dimension.nom === principale.dimension.nom);
            if ((vdPrincipale ? vdPrincipale.valeur : '') !== valPrincipale) return false;
            const vdTemporel = d.valeurDimensions?.find(vd => vd.dimension.nom === temporel.dimension.nom);
            if ((vdTemporel ? vdTemporel.valeur : '') !== valTemp) return false;
            return comb.every((val, idx) => {
              const vd = d.valeurDimensions?.find(vd => vd.dimension.nom === autresSansTemporel[idx].dimension.nom);
              return (vd ? vd.valeur : '') === val;
            });
          });
          row.push(donnee ? donnee.valeur : '');
        });
      });
      data.push(row);
    });
    return { label: 'Pivot', data: [header1, header2, ...data], colCount: header1.length };
  }

  /**
   * Build the metadata/information sheet data.
   */
  buildMetadataSheetData(indicateur: Indicateur): { label: string, data: any[][], colCount: number } {
    const metadata = [
      ['Titre', indicateur.nom || ''],
      ['Description', indicateur.description || ''],
      ['Source', indicateur.source?.nom || ''],
      ['Unité', indicateur.unite || ''],
      ['Catégorie', indicateur.categorie || ''],
      ['Type Graphe', indicateur.typeGraphe || ''],
      ['Type Tableau', indicateur.typeTb || ''],
      ['Actif', indicateur.actif ? 'Oui' : 'Non'],
      ['Sous-domaines', (indicateur.sousDomaines || []).map(sd => sd.nom).join(', ')],
      ['Dimensions', (indicateur.dimensions || []).map(d => d.dimension.libelle || d.dimension.nom).join(', ')],
      ["Date d'exportation", new Date().toLocaleString()]
    ];
    return { label: 'Informations', data: metadata, colCount: 2 };
  }
}
