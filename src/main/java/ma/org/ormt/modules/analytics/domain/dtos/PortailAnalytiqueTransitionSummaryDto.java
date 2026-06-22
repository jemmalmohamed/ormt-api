package ma.org.ormt.modules.analytics.domain.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortailAnalytiqueTransitionSummaryDto {

    private long legacyDomaines;

    private long legacyTbDomaines;

    private long legacyEspacesDomaines;

    private long legacyTableauBordDomaines;

    private long legacyTbdCategories;

    private long domainesAnalytiques;

    private long domainesAnalytiquesAvecHeroSection;

    private long domainesAnalytiquesPartages;

    private long espaceDomaineAnalytiquesLinks;

    private long tbGroupDomaineAnalytiquesLinks;

    private long categoriesAnalytiques;

    private long categoriesAnalytiquesAvecTbd;

    private long categoriesAnalytiquesSansTbd;

    private long categoriesAnalytiquesAvecSectionTbdEmbed;
}
