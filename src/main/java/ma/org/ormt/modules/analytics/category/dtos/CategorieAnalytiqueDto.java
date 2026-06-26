package ma.org.ormt.modules.analytics.category.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardSummaryDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategorieAnalytiqueDto extends BaseDto {

    private Long domaineAnalytiqueId;

    private String nom;

    private String libelle;

    private String description;

    private String slug;

    private Integer ordre;

    private Boolean actif;

    private Long tbdDashboardId;

    private TbdDashboardSummaryDto tbdPrincipal;

    private List<CategorieAnalytiqueSectionDto> sections;
}
