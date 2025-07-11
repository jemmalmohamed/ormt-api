package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;

/**
 * Specialized builder for creating domaines metadata sections
 */
@Component
public class DomainesMetaDataBuilder {

    /**
     * Builds a comprehensive domaines section with domain hierarchy
     * 
     * @param indicateur The indicator containing domain information
     * @return MetaDataSection containing domains and sub-domains information
     */
    public MetaDataSection buildDomainesSection(Indicateur indicateur) {
        MetaDataSection section = new MetaDataSection("Domaines et sous-domaines");

        if (indicateur == null) {
            section.addRow(new MetaDataRow("Domaines", "Aucune information de domaine"));
            return section;
        }

        // Extract domain information
        String domaineName = extractDomaineName(indicateur);
        String sousDomainesInfo = extractSousDomainesInfo(indicateur);

        section.addRow(new MetaDataRow("Domaine", domaineName));
        section.addRow(new MetaDataRow("Sous domaine", sousDomainesInfo));

        // Add additional domain-related information if available
        addDomainHierarchyInfo(section, indicateur);

        return section;
    }

    /**
     * Builds a simple domaines section with basic information
     * 
     * @param indicateur The indicator containing domain information
     * @return MetaDataSection with essential domain info
     */
    public MetaDataSection buildEssentialDomainesSection(Indicateur indicateur) {
        MetaDataSection section = new MetaDataSection("Domaines");

        if (indicateur == null) {
            section.addRow(new MetaDataRow("Domaine", "Non défini"));
            return section;
        }

        String domaineName = extractDomaineName(indicateur);
        section.addRow(new MetaDataRow("Domaine", domaineName));

        return section;
    }

    /**
     * Extracts the main domain name from the indicator
     */
    private String extractDomaineName(Indicateur indicateur) {
        // Check if the indicator has domain information
        if (indicateur.getSousDomaines() != null && !indicateur.getSousDomaines().isEmpty()) {
            // Collect all unique domain names
            Set<String> domaineNames = new LinkedHashSet<>();

            for (var sousDomaine : indicateur.getSousDomaines()) {
                if (sousDomaine != null && sousDomaine.getDomaine() != null &&
                        sousDomaine.getDomaine().getNom() != null) {
                    domaineNames.add(sousDomaine.getDomaine().getNom());
                }
            }

            if (!domaineNames.isEmpty()) {
                return String.join(", ", domaineNames);
            }
        }

        return "Non défini";
    }

    /**
     * Extracts sub-domains information from the indicator
     */
    private String extractSousDomainesInfo(Indicateur indicateur) {
        List<String> sousDomainesList = new ArrayList<>();

        if (indicateur.getSousDomaines() != null) {
            for (var sousDomaine : indicateur.getSousDomaines()) {
                if (sousDomaine != null && sousDomaine.getNom() != null) {
                    sousDomainesList.add(sousDomaine.getNom());
                }
            }
        }

        if (sousDomainesList.isEmpty()) {
            return "Aucun sous-domaine défini";
        }

        return String.join(", ", sousDomainesList);
    }

    /**
     * Adds additional domain hierarchy information
     */
    private void addDomainHierarchyInfo(MetaDataSection section, Indicateur indicateur) {
        if (indicateur.getSousDomaines() != null && !indicateur.getSousDomaines().isEmpty()) {

            // Collect all unique domain descriptions
            Set<String> domainDescriptions = new LinkedHashSet<>();
            Set<String> subDomainDescriptions = new LinkedHashSet<>();

            for (var sousDomaine : indicateur.getSousDomaines()) {
                // Add domain description if available
                if (sousDomaine != null && sousDomaine.getDomaine() != null &&
                        sousDomaine.getDomaine().getDescription() != null) {
                    domainDescriptions.add(sousDomaine.getDomaine().getDescription());
                }

                // Add sub-domain description if available
                if (sousDomaine != null && sousDomaine.getDescription() != null) {
                    subDomainDescriptions.add(sousDomaine.getDescription());
                }
            }

            // Add domain descriptions if any
            if (!domainDescriptions.isEmpty()) {
                section.addRow(new MetaDataRow("Description(s) du domaine",
                        String.join(" | ", domainDescriptions)));
            }

            // Add sub-domain descriptions if any
            if (!subDomainDescriptions.isEmpty()) {
                section.addRow(new MetaDataRow("Description(s) du sous-domaine",
                        String.join(" | ", subDomainDescriptions)));
            }

            // Add count information
            section.addRow(new MetaDataRow("Nombre de sous-domaines",
                    String.valueOf(indicateur.getSousDomaines().size())));
        }
    }
}
