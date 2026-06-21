package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2Status;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2Dto extends BaseDto {

    private String nom;

    private String titre;

    private String sousTitre;

    private String description;

    private String source;

    private Boolean actif;

    private TableauBordV2Status status;

    private Long categorieId;

    private TableauBordV2CategorieDto categorie;

    private List<RoleAccesSummaryDto> roleAcces;

    private String themeJson;

    private String settingsJson;

    private List<TableauBordV2WidgetDto> widgets;
}
