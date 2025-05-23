package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import java.util.ArrayList;
import java.util.List;

public class IndicateurMetaDataTable {
    /**
     * Builds the metadata table as a list of rows (label, value) for the given
     * Indicateur.
     */
    public static List<List<String>> buildMetaTableData(Indicateur indicateur) {
        List<List<String>> meta = new ArrayList<>();
        meta.add(List.of("Id",
                indicateur != null && indicateur.getId() != null ? String.valueOf(indicateur.getId()) : ""));
        meta.add(List.of("Indicateur", indicateur != null && indicateur.getNom() != null ? indicateur.getNom() : ""));
        meta.add(List.of("Unité", indicateur != null && indicateur.getUnite() != null ? indicateur.getUnite() : ""));
        meta.add(List.of("Catégorie",
                indicateur != null && indicateur.getCategorie() != null ? indicateur.getCategorie() : ""));
        meta.add(List.of("Source",
                (indicateur != null && indicateur.getSource() != null && indicateur.getSource().getNom() != null)
                        ? indicateur.getSource().getNom()
                        : ""));
        meta.add(
                List.of("Type TB", indicateur != null && indicateur.getTypeTb() != null ? indicateur.getTypeTb() : ""));
        meta.add(List.of("Abréviation",
                indicateur != null && indicateur.getAbreviation() != null ? indicateur.getAbreviation() : ""));
        meta.add(List.of("Description",
                indicateur != null && indicateur.getDescription() != null ? indicateur.getDescription() : ""));
        meta.add(List.of("Type Graphe",
                indicateur != null && indicateur.getTypeGraphe() != null ? indicateur.getTypeGraphe() : ""));
        meta.add(List.of("Règle de calcul",
                indicateur != null && indicateur.getRegleCalcul() != null ? indicateur.getRegleCalcul() : ""));
        meta.add(List.of("Actif",
                (indicateur != null && indicateur.getActif() != null) ? (indicateur.getActif() ? "Oui" : "Non") : ""));
        return meta;
    }
}
