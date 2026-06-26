package ma.org.ormt.modules.indicateurs.indicateur.dtos.link;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurLinkedAnalyticsCategoryDto {

    private Long categorieAnalytiqueId;
    private String categorieAnalytiqueNom;
    private String categorieAnalytiqueLibelle;
    private Long domaineAnalytiqueId;
    private String domaineAnalytiqueNom;
    private String domaineAnalytiqueTitre;
    private Long tbdDashboardId;
}
