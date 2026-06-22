package ma.org.ormt.modules.analytics.domain.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortailAnalytiqueTransitionSummaryDto {

    private long legacyDomaines;

    private long legacyDomainesAvecContenuEditorial;

    private long legacyDomainesSansMappingAnalytique;

    private long legacyTbDomaines;

    private long legacyTbDomainesSansMappingAnalytique;

    private long legacyEspacesDomaines;

    private long legacyTbGroupDomaines;

    private long legacyTbdCategories;

    private long legacyTbdCategoriesSansMappingAnalytique;

    private long domainesAnalytiques;

    private long domainesAnalytiquesAvecHeroSection;

    private long domainesAnalytiquesSansContenuEditorial;

    private long domainesAnalytiquesPartages;

    private long espaceDomaineAnalytiquesLinks;

    private long tbGroupDomaineAnalytiquesLinks;

    private long categoriesAnalytiques;

    private long categoriesAnalytiquesAvecTbd;

    private long categoriesAnalytiquesSansTbd;

    private long categoriesAnalytiquesAvecSectionTbdEmbed;

    private boolean readyToDeprecateDomaineEditorialFields;

    private boolean readyToDeprecateTbDomaine;

    private boolean readyToDeprecateTbdCategorie;

    private boolean readyToDeprecateLegacyPortalTaxonomy;
}
