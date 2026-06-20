package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2DataSourceType;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2WidgetType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2WidgetRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis.")
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

    private Boolean actif;

    private List<TableauBordV2WidgetItemRequestDto> items;
}
