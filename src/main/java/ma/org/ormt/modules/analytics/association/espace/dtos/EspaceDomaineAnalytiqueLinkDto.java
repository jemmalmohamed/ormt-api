package ma.org.ormt.modules.analytics.association.espace.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EspaceDomaineAnalytiqueLinkDto {

    private Long id;

    private Integer ordre;

    private Long espaceId;

    private String espaceNom;

    private Long domaineAnalytiqueId;

    private String domaineAnalytiqueNom;

    private String domaineAnalytiqueTitre;

    private String domaineAnalytiqueSlug;

    private Boolean domaineAnalytiqueActif;
}
