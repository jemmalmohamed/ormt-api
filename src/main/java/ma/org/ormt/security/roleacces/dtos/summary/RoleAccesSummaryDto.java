package ma.org.ormt.security.roleacces.dtos.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "roleAccesDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "roleAcces.id" }, allowGetters = true)
public class RoleAccesSummaryDto extends Dto {

    private String roleCode;

    private String niveauAcces;

}
