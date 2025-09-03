package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.RoleAccesContentFilter;

@Setter
@Getter
@Schema(name = "TBDomaine")
@JsonIgnoreProperties(value = { "TBDomaine.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class TBDomaineDto extends BaseDto {

    private String nom;

    private String description;

    private Boolean actif;

    @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = RoleAccesContentFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;
}