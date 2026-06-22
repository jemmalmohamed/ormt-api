package ma.org.ormt.modules.analytics.domain.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DomaineAnalytiqueDto extends BaseDto {

    private String nom;

    private String titre;

    private String description;

    private String apropos;

    private String imageUrl;

    private String slug;

    private String sourceThemeKey;

    private String metadataJson;

    private Boolean actif;

    private List<DomaineAnalytiqueSectionDto> sections;

    private List<CategorieAnalytiqueDto> categories;

    private List<RoleAccesSummaryDto> roleAcces;
}
