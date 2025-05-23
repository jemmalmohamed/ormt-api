package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Service
public class IndicateurExportCellCreationService {

        public static void createSousDomainesCell(Row row, int colIdx, Indicateur indicateur) {
                String sousDomaines = indicateur.getSousDomaines() != null
                                ? indicateur.getSousDomaines().stream()
                                                .map(sd -> sd.getNom() != null ? sd.getNom() : "")
                                                .filter(nom -> !nom.isEmpty())
                                                .reduce((a, b) -> a + ", " + b).orElse("")
                                : "";
                ExcelUtils.createCell(row, colIdx, sousDomaines);
        }

        public static void createDomainesCell(Row row, int colIdx, Indicateur indicateur) {
                String domaines = indicateur.getSousDomaines() != null
                                ? indicateur.getSousDomaines().stream()
                                                .map(sd -> sd.getDomaine() != null && sd.getDomaine().getNom() != null
                                                                ? sd.getDomaine().getNom()
                                                                : "")
                                                .distinct()
                                                .filter(nom -> !nom.isEmpty())
                                                .reduce((a, b) -> a + ", " + b).orElse("")
                                : "";
                ExcelUtils.createCell(row, colIdx, domaines);
        }

        public static void createEspacesCell(Row row, int colIdx, Indicateur indicateur) {
                String espaces = indicateur.getSousDomaines() != null
                                ? indicateur.getSousDomaines().stream()
                                                .filter(sd -> sd.getDomaine() != null
                                                                && sd.getDomaine().getEspaceDomaines() != null)
                                                .flatMap(sd -> sd.getDomaine().getEspaceDomaines().stream())
                                                .filter(ed -> ed.getEspace() != null && ed.getEspace().getNom() != null)
                                                .map(ed -> ed.getEspace().getNom())
                                                .distinct()
                                                .filter(nom -> !nom.isEmpty())
                                                .reduce((a, b) -> a + " - " + b).orElse("")
                                : "";
                ExcelUtils.createCell(row, colIdx, espaces);
        }
}
