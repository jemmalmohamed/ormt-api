package ma.org.ormt.modules.analytics.category.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategorieAnalytiqueSectionDto extends BaseDto {

    private String type;

    private String titre;

    private String contentJson;

    private Integer ordre;

    private Boolean actif;
}
