package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2DataSourceType;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2WidgetType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2WidgetDto extends BaseDto {

    private TableauBordV2WidgetType type;

    private String titre;

    private String sousTitre;

    private String description;

    private Integer ordre;

    private String section;

    private Integer x;

    private Integer y;

    private Integer w;

    private Integer h;

    private String configJson;

    private String styleJson;

    private TableauBordV2DataSourceType dataSourceType;

    private Long indicateurId;

    private Long grapheConfigurationId;

    private Long chiffreCleId;

    private TableauBordV2SourceRefDto indicateur;

    private TableauBordV2SourceRefDto grapheConfiguration;

    private TableauBordV2SourceRefDto chiffreCle;

    private Boolean actif;

    private List<TableauBordV2WidgetItemDto> items;
}
