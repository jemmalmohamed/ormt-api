package ma.org.ormt.modules.chiffres.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.AdminRoleFilter;

@Setter
@Getter
@Schema(name = "ChiffreCle")
@JsonIgnoreProperties(value = { "chiffrecle.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChiffreCleDto extends Dto {

    private String libelle;

    private String valeur;

    private String unite;

    private String description;

    private Boolean actif;

    private Boolean afficherDate;

    private String accessType;

    private DonneeIndicateurDto donneeIndicateur;

    private Dto indicateur;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;

}
