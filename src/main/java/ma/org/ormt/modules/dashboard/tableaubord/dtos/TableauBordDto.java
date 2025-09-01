package ma.org.ormt.modules.dashboard.tableaubord.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.TableauBordDomaineDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.AdminRoleFilter;

@Setter
@Getter
@Schema(name = "TableauBord")
@JsonIgnoreProperties(value = { "tableauBord.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordDto extends BaseDto {

    private String nom;

    private String description;

    private Boolean actif;

    private List<TableauBordDomaineDto> tableauBordDomaines;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;
}
