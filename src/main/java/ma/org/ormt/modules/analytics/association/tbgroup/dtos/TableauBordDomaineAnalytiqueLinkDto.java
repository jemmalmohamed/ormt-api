package ma.org.ormt.modules.analytics.association.tbgroup.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableauBordDomaineAnalytiqueLinkDto {

    private Long id;

    private Integer ordre;

    private Long tbGroupId;

    private String tbGroupNom;

    private Long domaineAnalytiqueId;

    private String domaineAnalytiqueNom;

    private String domaineAnalytiqueTitre;

    private String domaineAnalytiqueSlug;

    private Boolean domaineAnalytiqueActif;
}
