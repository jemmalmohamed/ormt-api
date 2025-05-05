package ma.org.ormt.modules.domaines.domaine.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.users.AdminRoleFilter;
import ma.org.ormt.modules.users.roleacces.dtos.summary.RoleAccesSummaryDto;

@Setter
@Getter
@Schema(name = "Domaine")
@JsonIgnoreProperties(value = { "domaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DomaineDto extends BaseDto {

    private String nom;

    private String description;

    private String imageUrl;

    private Boolean actif;

    private String apropos;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;
}