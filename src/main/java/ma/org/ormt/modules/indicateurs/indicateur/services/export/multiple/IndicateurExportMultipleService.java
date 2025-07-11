package ma.org.ormt.modules.indicateurs.indicateur.services.export.multiple;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;

public interface IndicateurExportMultipleService {

        /**
         * Exporte les indicateurs selon les options spécifiées (format tableau simple)
         * 
         * @param indicateurs   Liste des indicateurs à exporter
         * @param exportRequest DTO contenant les options d'export (filtres, colonnes,
         *                      groupement, etc.)
         * @return ResponseEntity contenant le fichier Excel généré
         * @throws IOException si une erreur survient lors de la génération
         */
        ResponseEntity<byte[]> exportIndicateurListWithOptions(List<Indicateur> indicateurs,
                        IndicateurExportRequestDto exportRequest) throws IOException;

        /**
         * Export détaillé avec options (un sheet par indicateur)
         * 
         * @param indicateurs   Liste des indicateurs à exporter
         * @param exportRequest DTO contenant les options d'export (filtres, sections,
         *                      etc.)
         * @return ResponseEntity contenant le fichier Excel généré avec un sheet
         *         détaillé par indicateur
         * @throws IOException si une erreur survient lors de la génération
         */
        ResponseEntity<byte[]> exportIndicateursParSheetWithOptions(List<Indicateur> indicateurs,
                        IndicateurExportRequestDto exportRequest) throws IOException;
}
