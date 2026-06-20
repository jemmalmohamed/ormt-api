package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2WidgetItemDto extends BaseDto {

    private String libelle;

    private String valeur;

    private String unite;

    private String description;

    private Integer ordre;

    private String configJson;

    private String styleJson;

    private Boolean actif;
}
